package com.ric.rest.resources;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.ric.rest.providers.Logged;

/**
 * @author siva
 *
 */

@Path("/")
public class ReferralJobResource {

	@POST
	@Logged
	// NameBinding for this method only
	@Path("/postjob")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createJob(@Context SecurityContext sc) {
		if (sc.isSecure() && sc.isUserInRole("User"))
			return Response.ok(
					sc.getUserPrincipal().getName() + "has created a job")
					.build();
		return Response.status(200).entity("job not created ").build();

	}

	@DELETE
	@Path("/deletejob")
	@Produces(MediaType.APPLICATION_JSON)
	@Logged
	// Named binding for this method only
	public Response deleteJob(@Context SecurityContext sc) {

		if (sc.isSecure() && sc.isUserInRole("Admin"))
			return Response.status(200).entity("deletejob executed").build();
		return Response.status(200).entity("deletejob  not executed").build();
	}

}
