package com.github.folkies.matt.server;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RequestScoped
@Path("/match")
@Produces(MediaType.APPLICATION_JSON)
public class MatchApi {

	@Inject
	TuneMatcher tuneMatcher;

	@GET
	@Path("/{transcription}")
	public List<MatchedTune> findMatches(@PathParam("transcription") String transcription) {
		return tuneMatcher.findBestMatches(transcription);
	}
}
