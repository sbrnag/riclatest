package com.ric.rest.resources;

import java.util.Set;
import java.util.TreeSet;

import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.ric.domain.User;
import com.ric.rest.providers.Logged;
import com.ric.services.LoginService;

@Path("/")
public class LoginResource {

	@Autowired
	private LoginService loginService;

	/**
	 * @return the loginService
	 */
	public LoginService getLoginService() {
		return loginService;
	}

	/**
	 * @param loginService
	 *            the loginService to set
	 */
	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}

	/**
	 * Method handling HTTP POST requests to signup.
	 *
	 * @return JsonObject
	 */
	@POST()
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/signup")
	public JsonObject signup(User user) {
		Set<User.Role> userroles = new TreeSet<User.Role>();
		userroles.add(User.Role.User);
		user.setRoles(userroles);
		return loginService.save(user);
	}

	/**
	 * Method handling HTTP GET requests to login.
	 *
	 * @return JsonObject
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/login")
	public Response login(@QueryParam("username") String userName,
			@QueryParam("password") String password) {

		return loginService.authenticate(userName, password);
	}

	/**
	 * Method handling HTTP GET requests to login.
	 *
	 * @return JsonObject
	 */
	@DELETE
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/logout")
	public String logout(@HeaderParam("secretKey") String key,
			@Context SecurityContext sc) {

		loginService.deleteSession(key);
		return "bye; see you soon";

	}

}
