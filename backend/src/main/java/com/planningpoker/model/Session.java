package com.planningpoker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private final String id;
    private final String scrumMasterId;
    private final String gitlabProjectId;

    @JsonIgnore
    private final String gitlabToken;

    private final List<Participant> participants = Collections.synchronizedList(new ArrayList<>());
    private final List<Round> completedRounds = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, Vote> votes = new ConcurrentHashMap<>();

    private Integer currentIssueIid;
    private SessionStatus status = SessionStatus.WAITING;

    public Session(String id, String scrumMasterId, String gitlabProjectId, String gitlabToken) {
        this.id = id;
        this.scrumMasterId = scrumMasterId;
        this.gitlabProjectId = gitlabProjectId;
        this.gitlabToken = gitlabToken;
    }

    public String getId() {
        return id;
    }

    public String getScrumMasterId() {
        return scrumMasterId;
    }

    public String getGitlabProjectId() {
        return gitlabProjectId;
    }

    @JsonIgnore
    public String getGitlabToken() {
        return gitlabToken;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public List<Round> getCompletedRounds() {
        return completedRounds;
    }

    public Map<String, Vote> getVotes() {
        return votes;
    }

    public Integer getCurrentIssueIid() {
        return currentIssueIid;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setCurrentIssueIid(Integer currentIssueIid) {
        this.currentIssueIid = currentIssueIid;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }
}
