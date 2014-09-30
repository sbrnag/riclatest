package com.ric.services.impl;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.ric.domain.Session;
import com.ric.domain.User;
import com.ric.mongodb.repository.SessionRepository;
import com.ric.mongodb.repository.UserRepository;
import com.ric.services.LoginService;
import com.ric.util.AppConstants;

@Service("loginService")
public class LoginServiceImpl implements LoginService {

	private static final JsonObjectBuilder jsonBuilder = Json
			.createObjectBuilder();

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SessionRepository sessionRepository;

	/**
	 * @return the userRepository
	 */
	public UserRepository getUserRepository() {
		return userRepository;
	}

	/**
	 * @param userRepository
	 *            the userRepository to set
	 */
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * @return the sessionRepository
	 */
	public SessionRepository getSessionRepository() {
		return sessionRepository;
	}

	/**
	 * @param sessionRepository
	 *            the sessionRepository to set
	 */
	public void setSessionRepository(SessionRepository sessionRepository) {
		this.sessionRepository = sessionRepository;
	}

	@Override
	public JsonObject save(User user) {

		JsonObject json;

		try {
			String mailId = user.getPersonalEmail();
			String[] str = mailId.split("@");
			String userName = str[0];
			user.setUserName(userName);
			userRepository.save(user);

			Session session = new Session();
			session.setUsername(userName);
			sessionRepository.save(session);

			String sessionID = session.getSessionId();
			

			if (sessionID != null && !sessionID.isEmpty()) {
				json = getJsonObj("secretKey", session.getSessionId());
			} else {
				userRepository.delete(user);
				json = getJsonObj(AppConstants.ERROR, AppConstants.INTERNAL_SERVER_ERROR);
			}

		} catch (DataAccessResourceFailureException darfe) {
			json = getJsonObj(AppConstants.ERROR, AppConstants.INTERNAL_SERVER_ERROR);
		} catch (DuplicateKeyException de) {
			json = getJsonObj(AppConstants.ERROR,
					AppConstants.MAIL_ALREADY_REGISTERED);
		}
		return json;
	}

	@Override
	//public JsonObject authenticate(String userName, String password) {
	public Response authenticate(String userName, String password) {	
		//JsonObject json;
		Response response;

		try {
			if(userRepository.authenticate(userName, password)) {
				Session session = sessionRepository.getSessionByUserName(userName);
				if(session != null) {
					//json = getJsonObj(AppConstants.SECRET_KEY, session.getSessionId());
					response = getResponse(200, MediaType.APPLICATION_JSON, session.getSessionId());
				} else {
					session = new Session();
					session.setUsername(userName);
					sessionRepository.save(session);
					//json = getJsonObj(AppConstants.SECRET_KEY, session.getSessionId());
					response = getResponse(200, MediaType.APPLICATION_JSON, session.getSessionId());
				}
			} else{
				//json = getJsonObj(AppConstants.ERROR, AppConstants.INVALID_USER_CREDENTIALS_ERROR);
				response = getResponse(404, MediaType.APPLICATION_JSON, AppConstants.INVALID_USER_CREDENTIALS_ERROR);
			}
		} catch (DataAccessResourceFailureException darfe) {
			//json = getJsonObj(AppConstants.ERROR, AppConstants.INTERNAL_SERVER_ERROR);
			response = getResponse(500, MediaType.APPLICATION_JSON, AppConstants.INTERNAL_SERVER_ERROR);
		} 
		//return json;
		return response;
	}

	@Override
	public void deleteSession(String key) {
		sessionRepository.delete(key);

	}

	private JsonObject getJsonObj(final String key, final String message) {
		jsonBuilder.add(key, message);
		JsonObject json = jsonBuilder.build();
		return json;
	}
	
	private Response getResponse(int statusCode, String mediaType, Object obj) {
		return Response.status(statusCode).type(mediaType).entity(obj).build(); 
	}

}
