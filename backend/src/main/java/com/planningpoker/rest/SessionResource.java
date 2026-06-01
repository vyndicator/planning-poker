package com.planningpoker.rest;

import com.planningpoker.dto.*;
import com.planningpoker.model.Session;
import com.planningpoker.service.SessionService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/api/sessions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SessionResource {

    @Inject
    SessionService sessionService;
    @POST
    @Operation(operationId = "createSession")
    public Response createSession(@Valid CreateSessionRequest request) {
        var response = sessionService.createSession(
            request.scrumMasterName(),
            request.gitlabProjectId(),
            request.gitlabToken()
        );
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Path("/{sessionId}")
    @Operation(operationId = "getSession")
    public Session getSession(@PathParam("sessionId") String sessionId) {
        return sessionService.getSession(sessionId);
    }

    @POST
    @Path("/{sessionId}/join")
    @Operation(operationId = "joinSession")
    public Response joinSession(
        @PathParam("sessionId") String sessionId,
        @Valid JoinRequest request
    ) {
        var response = sessionService.joinSession(sessionId, request.name());
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/{sessionId}/leave")
    @Operation(operationId = "leaveSession")
    public Session leaveSession(
        @PathParam("sessionId") String sessionId,
        @HeaderParam("X-Participant-Id") String participantId
    ) {
        return sessionService.leaveSession(sessionId, participantId);
    }

    @DELETE
    @Path("/{sessionId}")
    @Operation(operationId = "deleteSession")
    public Response deleteSession(
        @PathParam("sessionId") String sessionId,
        @HeaderParam("X-Participant-Id") String participantId
    ) {
        sessionService.deleteSession(sessionId, participantId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("/{sessionId}/issue")
    @Operation(operationId = "selectIssue")
    public Session selectIssue(
        @PathParam("sessionId") String sessionId,
        @HeaderParam("X-Participant-Id") String participantId,
        @Valid SelectIssueRequest request
    ) {
        return sessionService.selectIssue(sessionId, participantId, request.issueIid());
    }

    @POST
    @Path("/{sessionId}/vote")
    @Operation(operationId = "castVote")
    public Session castVote(
        @PathParam("sessionId") String sessionId,
        @HeaderParam("X-Participant-Id") String participantId,
        @Valid VoteRequest request
    ) {
        return sessionService.castVote(sessionId, participantId, request.value());
    }

    @POST
    @Path("/{sessionId}/reveal")
    @Operation(operationId = "revealCards")
    public Session revealCards(
        @PathParam("sessionId") String sessionId,
        @HeaderParam("X-Participant-Id") String participantId
    ) {
        return sessionService.revealCards(sessionId, participantId);
    }

    @POST
    @Path("/{sessionId}/new-round")
    @Operation(operationId = "startNewRound")
    public Session startNewRound(
        @PathParam("sessionId") String sessionId,
        @HeaderParam("X-Participant-Id") String participantId
    ) {
        return sessionService.startNewRound(sessionId, participantId);
    }

    @POST
    @Path("/{sessionId}/finalize")
    @Operation(operationId = "finalizeRound")
    public Session finalizeRound(
        @PathParam("sessionId") String sessionId,
        @HeaderParam("X-Participant-Id") String participantId,
        @Valid FinalizeRequest request
    ) {
        return sessionService.finalizeRound(sessionId, participantId, request.finalValue());
    }
}
