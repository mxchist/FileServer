package ru.geekbrains.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.Vector;

public class Server {
	private Vector<ClientHandler> clients;
	public Connection connection;

	private final int SERVER_PORT = 8189;
	private final int FILE_SERVER_PORT = 8190;

	private int sessionId;
	private String folderToStore;
	private String delimiter;

	public Server() {
		clients = new Vector<>();
		ServerSocket server = null;
		ServerSocket fileServer = null;
		Socket socket = null;
		Socket fileSocket = null;
		try {
			connect();
			this.folderToStore= getServerOptions("folder_to_store_files");
			this.delimiter= getServerOptions("delimiter");
			server = new ServerSocket(SERVER_PORT);
			fileServer = new ServerSocket(FILE_SERVER_PORT);
			System.out.println("Сервер запущен. Ожидаем клиентов...");
			logNewSessionId();
			while (true) {
				socket = server.accept();
				fileSocket = fileServer.accept();
				System.out.println("Клиент подключился");
				new ClientHandler(this, socket, fileSocket);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fileSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fileServer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			disconnect();
		}
	}

	public void connect() {
		try {
			Class.forName("org.sqlite.JDBC");
			this.connection = DriverManager.getConnection("jdbc:sqlite:backend_DB.db");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void logNewSessionId() {
		try {
			Statement stmt = this.connection.createStatement();
			stmt.executeUpdate(
					"insert into main.server_session\n" +
							"default values;\n" +
							"\n");
			ResultSet rs = stmt.executeQuery("select last_insert_rowid();");
			if (rs.next()) {
				this.sessionId = rs.getInt(1);
			}
		} catch (SQLException exc) {
			exc.printStackTrace();
		}
	}

	public int logNewClientSessionId(String nickname) throws SQLException {
		PreparedStatement ps;
		ps = this.connection.prepareStatement(
				"insert into main.user_session(server_session_id, nickname)\n" +
						"values(?, ?);"
		);
		ps.setInt(1, this.sessionId);
		ps.setString(2, nickname);
		ps.executeUpdate();
		ResultSet rs = this.connection.createStatement().executeQuery("select last_insert_rowid();");
		if (rs.next()) {
			return rs.getInt(1);
		}
		return 0;
	}

	public void sendPersonalMsg(ClientHandler from, String nickTo, String msg) throws SQLException, IOException {
		for (ClientHandler to : clients) {
			if (to.getNick().equals(nickTo)) {
				String messageTo = "from " + from.getNick() + ": " + msg, messageFrom = "to " + nickTo + ": " + msg;
				to.sendMsg(messageTo);
				from.sendMsg(messageFrom);

				PreparedStatement ps;
				ps = this.connection.prepareStatement(
						"insert into main.messages_personal (sent_user_session_id, recieved_user_session_id, message)\n" +
								"values(?, ?, ?);"
				);
				ps.setInt(1, from.getSessionId());
				ps.setInt(2, to.getSessionId());
				ps.setString(3, messageTo);
				ps.executeUpdate();

				ps.setInt(1, to.getSessionId());
				ps.setInt(2, from.getSessionId());
				ps.setString(3, messageFrom);
				ps.executeUpdate();

				return;
			}
		}
		from.sendMsg("Клиент с ником " + nickTo + " не найден в чате");
	}

	public ResultSet getMessagesHistory(String nickname) throws SQLException {
		PreparedStatement ps;
		ps = this.connection.prepareStatement("select message from (\n" +
				"select\n" +
				"mb.message, mb.creation_time, us.nickname\n" +
				"from main.user_session  as us\n" +
				"inner join main.messages_broadcast as mb on mb.server_session_id = us.server_session_id\n" +
				"\n" +
				"union\n" +
				"select\n" +
				"mp.message, mp.creation_time, us.nickname\n" +
				"from main.user_session  as us\n" +
				"inner join main.messages_personal as mp on mp.sent_user_session_id = us.user_session_id\n" +
				"or mp.recieved_user_session_id = us.user_session_id\n" +
				") as t\n" +
				"where t.nickname = ?\n" +
				"order by creation_time"
		);
		ps.setString(1, nickname);
		return ps.executeQuery();
	}

	public void broadcastMsg(ClientHandler from, String msg) throws SQLException, IOException {
		for (ClientHandler cl : clients) {
			if (!cl.checkBlackList(from.getNick())) {
				cl.sendMsg(msg);
			}
		}
		PreparedStatement ps;
		ps = this.connection.prepareStatement("insert into main.messages_broadcast(server_session_id, message)" +
				"values(?, ?)");
		ps.setInt(1, this.sessionId);
		ps.setString(2, msg);
		ps.executeUpdate();
	}

	public ResultSet getBroadcastMessagesHistory(String nickname) throws SQLException {
		PreparedStatement ps;
		ps = this.connection.prepareStatement("select\n" +
				"mb.message\n" +
				"from main.user_session  as us\n" +
				"inner join main.messages_broadcast as mb on mb.server_session_id = us.server_session_id\n" +
				"where us.nickname = ?" +
				"order by mb.server_session_id asc\n" +
				"    , us.creation_time asc ");
		ps.setString(1, nickname);
		return ps.executeQuery();
	}

	public boolean isNickBusy(String nick) {
		for (ClientHandler cl : clients) {
			if (cl.getNick().equals(nick)) {
				return true;
			}
		}
		return false;
	}

	public void broadcastClientsList() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("/clientslist ");
		for (ClientHandler cl : clients) {
			sb.append(cl.getNick() + " ");
		}
		String out = sb.toString();
		for (ClientHandler cl : clients) {
			cl.sendMsg(out);
		}
	}

	public void subscribe(ClientHandler client) throws IOException {
		clients.add(client);
		broadcastClientsList();
	}

	public void unsubscribe(ClientHandler client) throws IOException {
		clients.remove(client);
		broadcastClientsList();
	}

	public void sentPersonalFile(String nickFrom, String nickTo, byte[] file) throws IOException {
		for (ClientHandler to : clients) {
			if (to.getNick().equals(nickTo)) {
				to.sendMsg("/putFile " + nickFrom);
				to.sentFile(file);
			}
		}
	}

	private String getServerOptions(String option_name) {
		try {
			Statement st = this.connection.createStatement();
			PreparedStatement ps = this.connection.prepareStatement("select value from server_options where option_name = ?");
			ps.setString(1, option_name);
			ResultSet rs = ps.executeQuery();
			return rs.getString("value");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.printf("Are you call the server option which is not define? option name: %s %n", option_name);
		}
		return null;
	}

	public String getFolderToStore() {
		return this.folderToStore;
	}

	public String getDelimiter() {return this.delimiter;}

}
