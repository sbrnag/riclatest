package com.ric.rest.providers;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/")
public class RICApplication extends ResourceConfig {

	public RICApplication() {

		packages("com.ric.rest.resources", "com.ric.rest.providers");

		register(RICSecurityContextFilter.class);
		register(RICSecurityContext.class);

	}
}