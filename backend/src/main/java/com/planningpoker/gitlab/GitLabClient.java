package com.planningpoker.gitlab;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "gitlab-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GitLabClient {

    @GET
    @Path("/projects/{projectId}/issues")
    List<GitLabIssue> getIssues(
        @PathParam("projectId") String projectId,
        @QueryParam("state") String state,
        @HeaderParam("PRIVATE-TOKEN") String token
    );

    @GET
    @Path("/projects/{projectId}/issues/{iid}")
    GitLabIssue getIssue(
        @PathParam("projectId") String projectId,
        @PathParam("iid") int iid,
        @HeaderParam("PRIVATE-TOKEN") String token
    );

    @POST
    @Path("/projects/{projectId}/issues/{iid}/notes")
    void createNote(
        @PathParam("projectId") String projectId,
        @PathParam("iid") int iid,
        @HeaderParam("PRIVATE-TOKEN") String token,
        NoteRequest body
    );
}
