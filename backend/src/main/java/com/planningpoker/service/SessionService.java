package com.planningpoker.service;

import com.planningpoker.dto.JoinResponse;
import com.planningpoker.gitlab.GitLabClient;
import com.planningpoker.gitlab.NoteRequest;
import com.planningpoker.model.*;
import com.planningpoker.websocket.SessionSocket;
import com.planningpoker.websocket.WsEvent;
import com.planningpoker.websocket.WsEventType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class SessionService {

    private static final Logger LOG = Logger.getLogger(SessionService.class.getName());

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Inject
    SessionSocket sessionSocket;

    @Inject
    @RestClient
    GitLabClient gitLabClient;

    public JoinResponse createSession(String scrumMasterName, String gitlabProjectId, String gitlabToken) {
        String sessionId = UUID.randomUUID().toString();
        String participantId = UUID.randomUUID().toString();

        Session session = new Session(sessionId, participantId, gitlabProjectId, gitlabToken);
        session.getParticipants().add(new Participant(participantId, scrumMasterName, Role.SCRUM_MASTER));
        sessions.put(sessionId, session);

        return new JoinResponse(sessionId, participantId, Role.SCRUM_MASTER);
    }

    public JoinResponse joinSession(String sessionId, String name) {
        Session session = getSessionOrThrow(sessionId);

        String participantId = UUID.randomUUID().toString();
        session.getParticipants().add(new Participant(participantId, name, Role.DEVELOPER));

        sessionSocket.broadcast(sessionId, new WsEvent(WsEventType.PARTICIPANT_JOINED,
            Map.of("id", participantId, "name", name, "role", Role.DEVELOPER)));

        return new JoinResponse(sessionId, participantId, Role.DEVELOPER);
    }

    public Session getSession(String sessionId) {
        return getSessionOrThrow(sessionId);
    }

    public void deleteSession(String sessionId, String participantId) {
        Session session = getSessionOrThrow(sessionId);
        requireScrumMaster(session, participantId);
        sessions.remove(sessionId);
        sessionSocket.broadcast(sessionId, new WsEvent(WsEventType.SESSION_DELETED, null));
    }

    public Session leaveSession(String sessionId, String participantId) {
        Session session = getSessionOrThrow(sessionId);
        String leavingName = resolveParticipantName(session, participantId);
        session.getParticipants().removeIf(p -> p.id().equals(participantId));
        session.getVotes().remove(participantId);

        sessionSocket.broadcast(sessionId, new WsEvent(WsEventType.PARTICIPANT_LEFT,
            Map.of("participantId", participantId, "participantName", leavingName)));

        return session;
    }

    public Session selectIssue(String sessionId, String participantId, Integer issueIid) {
        Session session = getSessionOrThrow(sessionId);
        requireScrumMaster(session, participantId);

        session.setCurrentIssueIid(issueIid);
        session.getVotes().clear();
        session.setStatus(SessionStatus.VOTING);

        sessionSocket.broadcast(sessionId, new WsEvent(WsEventType.ISSUE_SELECTED,
            Map.of("issueIid", issueIid)));

        return session;
    }

    public Session castVote(String sessionId, String participantId, CardValue value) {
        Session session = getSessionOrThrow(sessionId);
        requireStatus(session, SessionStatus.VOTING);
        requireParticipant(session, participantId);

        String participantName = resolveParticipantName(session, participantId);
        session.getVotes().put(participantId, new Vote(participantId, participantName, value));

        // Broadcast name only — vote value stays hidden until reveal
        sessionSocket.broadcast(sessionId, new WsEvent(WsEventType.VOTE_CAST,
            Map.of("participantId", participantId, "participantName", participantName)));

        return session;
    }

    public Session revealCards(String sessionId, String participantId) {
        Session session = getSessionOrThrow(sessionId);
        requireScrumMaster(session, participantId);
        requireStatus(session, SessionStatus.VOTING);

        session.setStatus(SessionStatus.REVEALED);

        sessionSocket.broadcast(sessionId, new WsEvent(WsEventType.CARDS_REVEALED,
            Map.of("votes", session.getVotes().values())));

        return session;
    }

    public Session startNewRound(String sessionId, String participantId) {
        Session session = getSessionOrThrow(sessionId);
        requireScrumMaster(session, participantId);
        requireStatus(session, SessionStatus.REVEALED);

        session.getVotes().clear();
        session.setStatus(SessionStatus.VOTING);

        sessionSocket.broadcast(sessionId, new WsEvent(WsEventType.ROUND_RESET, null));

        return session;
    }

    public Session finalizeRound(String sessionId, String participantId, CardValue finalValue) {
        Session session = getSessionOrThrow(sessionId);
        requireScrumMaster(session, participantId);
        requireStatus(session, SessionStatus.REVEALED);

        Round round = new Round(session.getCurrentIssueIid(), List.copyOf(session.getVotes().values()), finalValue);
        session.getCompletedRounds().add(round);

        session.getVotes().clear();
        session.setCurrentIssueIid(null);
        session.setStatus(SessionStatus.WAITING);

        sessionSocket.broadcast(sessionId, new WsEvent(WsEventType.ROUND_FINALIZED,
            Map.of("issueIid", round.issueIid(), "finalValue", round.finalValue())));

        // GitLab failure must not undo the finalized round
        try {
            String comment = String.format(
                "Planning Poker estimate: **%s**\n\nAgreed in session `%s`.",
                finalValue.getDisplay(), sessionId
            );
            gitLabClient.createNote(session.getGitlabProjectId(), round.issueIid(), session.getGitlabToken(), new NoteRequest(comment));
        } catch (Exception e) {
            LOG.warning(String.format("Failed to post GitLab comment for session %s, issue %d: %s",
                sessionId, round.issueIid(), e.getMessage()));
        }

        return session;
    }

    private Session getSessionOrThrow(String sessionId) {
        Session session = sessions.get(sessionId);
        if (session == null) {
            throw new WebApplicationException("Session not found: " + sessionId, Response.Status.NOT_FOUND);
        }
        return session;
    }

    private void requireScrumMaster(Session session, String participantId) {
        if (!session.getScrumMasterId().equals(participantId)) {
            throw new WebApplicationException("Only the Scrum Master can perform this action", Response.Status.FORBIDDEN);
        }
    }

    private void requireStatus(Session session, SessionStatus required) {
        if (session.getStatus() != required) {
            throw new WebApplicationException(
                "Action not allowed in current state '" + session.getStatus() + "' (expected: " + required + ")",
                Response.Status.CONFLICT
            );
        }
    }

    private void requireParticipant(Session session, String participantId) {
        boolean exists = session.getParticipants().stream().anyMatch(p -> p.id().equals(participantId));
        if (!exists) {
            throw new WebApplicationException("Participant not found in session", Response.Status.FORBIDDEN);
        }
    }

    private String resolveParticipantName(Session session, String participantId) {
        return session.getParticipants().stream()
            .filter(p -> p.id().equals(participantId))
            .map(Participant::name)
            .findFirst()
            .orElse("Unknown");
    }
}
