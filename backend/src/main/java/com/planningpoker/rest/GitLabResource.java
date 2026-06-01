package com.planningpoker.rest;

import com.planningpoker.gitlab.GitLabClient;
import com.planningpoker.gitlab.GitLabIssue;
import com.planningpoker.service.SessionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/api/sessions/{sessionId}/gitlab")
@Produces(MediaType.APPLICATION_JSON)
public class GitLabResource {

    @Inject
    SessionService sessionService;

    @Inject
    @RestClient
    GitLabClient gitLabClient;

    @GET
    @Path("/issues")
    @Operation(operationId = "getIssues")
    public List<GitLabIssue> getIssues(@PathParam("sessionId") String sessionId) {
        var session = sessionService.getSession(sessionId);
        return gitLabClient.getIssues(session.getGitlabProjectId(), "opened", session.getGitlabToken());
    }

    @GET
    @Path("/issues/{iid}")
    @Operation(operationId = "getIssue")
    public GitLabIssue getIssue(
        @PathParam("sessionId") String sessionId,
        @PathParam("iid") int iid
    ) {
        var session = sessionService.getSession(sessionId);
        return gitLabClient.getIssue(session.getGitlabProjectId(), iid, session.getGitlabToken());
    }
}
