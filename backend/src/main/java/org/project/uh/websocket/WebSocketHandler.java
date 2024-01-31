package org.project.uh.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.project.uh.user.dto.UserDto;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpSession;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

	private static final Map<WebSocketSession, HttpSession> CLIENTS = new ConcurrentHashMap<>();
	private static final Map<String, WebSocketSession> connectionIds = new ConcurrentHashMap<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		HttpSession httpSession = (HttpSession)session.getAttributes().get("httpSession");
		// if (isHttpSessionAlreadyConnected(httpSession)) {
		// 	// 이미 연결된 경우 접속을 막고 메시지를 보내고 연결을 종료
		// 	TextMessage errorMessage = new TextMessage("You are already connected from another session.");
		// 	session.sendMessage(errorMessage);
		// 	session.close(CloseStatus.POLICY_VIOLATION);
		// 	return;
		// }
		CLIENTS.put(session, httpSession);
		connectionIds.put(session.getId(), session);
		// 클라이언트 접속 시 모든 클라이언트에게 접속 유저 리스트를 전송
		sendConnectors();
	}

	//동시 접속 여부 확인
	// private boolean isHttpSessionAlreadyConnected(HttpSession httpSession) {
	// 	return CLIENTS.values().contains(httpSession);
	// }

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		connectionIds.remove(session.getId());
		CLIENTS.remove(session);
		// 클라이언트 연결 종료 시 모든 클라이언트에게 접속 유저 리스트를 전송 다시 전송
		sendConnectors();
	}

	private void sendConnectors() throws IOException {
		// 현재 접속한 모든 클라이언트의 connectionId와 닉네임 전송
		List<String[]> connectors = new ArrayList<>();
		for (WebSocketSession client : CLIENTS.keySet()) {
			HttpSession session = (HttpSession)client.getAttributes().get("httpSession");
			UserDto dto = (UserDto)session.getAttribute("loginUser");

			//테스트 코드 - 회원 연결 시 변경
			connectors.add(new String[] {client.getId(), "닉네임"});
			//실제 코드
			// connectors.add(new String[]{client.getId(),dto.getUserNickname()});
		}

		// JSON 형식으로 구성
		JsonArray connectorsArray = new JsonArray();
		for (String[] connector : connectors) {
			JsonObject connectorObject = new JsonObject();
			connectorObject.addProperty("connectionId", connector[0]);
			connectorObject.addProperty("nickname", connector[1]);
			connectorsArray.add(connectorObject);
		}

		JsonObject jsonObject = new JsonObject();
		jsonObject.add("connectors", connectorsArray);

		// 모든 클라이언트에게 전송
		for (WebSocketSession client : CLIENTS.keySet()) {
			client.sendMessage(new TextMessage(jsonObject.toString()));
		}
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// 수신된 메시지 파싱
		JsonObject jsonMessage = JsonParser.parseString(message.getPayload()).getAsJsonObject();

		// 친구 초대(invite)
		if (jsonMessage.has("type") && jsonMessage.get("type").getAsString().equals("invite")) {
			System.out.println("invite");
			// 초대 받는 대상의 connectionId 추출
			String toConnectionId = jsonMessage.get("toConnectionId").getAsString();
			jsonMessage.remove(toConnectionId);
			HttpSession httpSession = CLIENTS.get(session);

			//httpSession을 통해 초대를 보낸 유저의 roomId 추출
			String roomId = (String)httpSession.getAttribute("roomId");
			jsonMessage.add("roomId", JsonParser.parseString(roomId));

			handleInvite(toConnectionId, jsonMessage);
		} else if (jsonMessage.has("type") && jsonMessage.get("type").getAsString().equals("follow")) {
			System.out.println("follow");
			// 따라가는 대상의 connectionId 추출
			String connectionId = jsonMessage.get("connectionId").getAsString();
			jsonMessage.remove(connectionId);

			//따라가는 대상의 roomId 추출 후 메시지에 담음
			WebSocketSession followSession = connectionIds.get(connectionId);
			HttpSession followHttpSession = CLIENTS.get(followSession);
			String roomId = (String)followHttpSession.getAttribute("roomId");
			jsonMessage.add("roomId", JsonParser.parseString(roomId));

			handleFollow(session, jsonMessage);
		}
	}

	private void handleInvite(String toConnectionId, JsonObject jsonMessage) throws IOException {
		// 초대를 받은 세션에 메시지 전송
		TextMessage inviteMessage = new TextMessage(jsonMessage.toString());
		WebSocketSession session = connectionIds.get(toConnectionId);
		session.sendMessage(inviteMessage);
	}

	private void handleFollow(WebSocketSession session, JsonObject jsonMessage) throws IOException {
		//따라가기를 요청한 세션에 메시시 전송
		TextMessage followMessage = new TextMessage(jsonMessage.toString());
		session.sendMessage(followMessage);
	}
}