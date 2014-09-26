package com.ric.services;

import javax.json.JsonObject;
import javax.ws.rs.core.Response;

import com.ric.domain.User;

public interface LoginService {

	public JsonObject save(User user);

	public Response authenticate(String userName, String password);

	public void deleteSession(String key);

}
