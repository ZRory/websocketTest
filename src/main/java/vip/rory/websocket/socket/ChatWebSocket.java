package vip.rory.websocket.socket;

import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

@Component
@ServerEndpoint("/chat/{nickname}")
public class ChatWebSocket {

	// 用来存放每个客户端对应的MyWebSocket对象。
	private static CopyOnWriteArraySet<ChatWebSocket> webSocketSet = new CopyOnWriteArraySet<ChatWebSocket>();

	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;
	// 昵称
	private String nickname;

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("nickname") String nickname) {
		this.session = session;
		this.nickname = nickname;
		webSocketSet.add(this); // 加入set中
		System.out.println(this.nickname + "加入！当前在线人数为" + webSocketSet.size());
		this.session.getAsyncRemote().sendText("恭喜您成功连接上WebSocket-->当前在线人数为：" + webSocketSet.size());
	}

	/**
	 * 连接接关闭时调用的方法
	 * 
	 * @param session
	 */
	@OnClose
	public void onClose() {
		webSocketSet.remove(this);
		System.out.println(this.nickname + "退出！当前在线人数为" + webSocketSet.size());
	}

	/**
	 * 收到消息方法
	 * 
	 * @param message
	 * @param session
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("来自" + this.nickname + "的消息:" + message);
		// 群发消息
		message = this.nickname + ":" + message;
		for (ChatWebSocket chatWebSocket : webSocketSet) {
			chatWebSocket.session.getAsyncRemote().sendText(message);// 异步发送消息.
		}
	}

	@OnError
	public void onError(Session session, Throwable error) {
		System.out.println("发生错误");
		error.printStackTrace();
	}

}
