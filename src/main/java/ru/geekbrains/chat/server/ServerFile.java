package ru.geekbrains.chat.server;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.Vector;

public class ServerFile {
	/*
	private Vector<ClientHandler> clients;
	public Connection connection;

	private final int SERVER_PORT = 8190;

	private int sessionId;

	public ServerFile() {
		clients = new Vector<>();
		ServerSocket server = null;
		Socket socket = null;
		try {
			connect();
			server = new ServerSocket(SERVER_PORT);
			System.out.println("Файловый сервер запущен");
			while (true) {
				socket = server.accept();
				System.out.println("Клиент подключился");
				new ClientHandler(this, socket);
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
			disconnect();
		}
	}

	public void connect() {
		try {
//			Class.forName("org.sqlite.JDBC");
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

	public boolean isNickBusy(String nick) {
		for (ClientHandler cl : clients) {
			if (cl.getNick().equals(nick)) {
				return true;
			}
		}
		return false;
	}

	public void sentPersonalFile(ClientHandler from, String nickTo, PipedInputStream fin) throws SQLException, IOException {
		PipedOutputStream fout = new PipedOutputStream();
		for (ClientHandler to : clients) {
			if (to.getNick().equals(nickTo)) {
				to.sentPersonalFile(fin);
			}
		}
	}
	
	 */
}
