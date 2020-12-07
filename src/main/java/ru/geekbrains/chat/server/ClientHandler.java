package ru.geekbrains.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.lang.Byte;
import java.util.ArrayList;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private Socket fileSocket;
    private DataOutputStream out;
    private DataOutputStream fout;
    private DataInputStream in;
    private DataInputStream fin;
    private String nick;
    private static int maxSessionId;
    private int sessionId;

    List<String> blackList;

    public String getNick() {
        return nick;
    }

    public ClientHandler(Server server, Socket socket, Socket fileSocket) {
        try {
            this.server = server;
            this.socket = socket;
            this.fileSocket = fileSocket;

            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.fin = new DataInputStream(fileSocket.getInputStream());
            this.fout = new DataOutputStream(fileSocket.getOutputStream());

            this.blackList = new ArrayList<>();
            new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/auth")) { // /auth login1 pass1
                            String[] tokens = str.split(" ");
                            AuthService authService = new AuthService(server.connection);
                            String newNick = authService.getNickByLoginAndPass(tokens[1], tokens[2]);
                            if (newNick != null) {
                                if (!server.isNickBusy(newNick)) {
                                    sendMsg("/authok");
                                    nick = newNick;

                                    this.sessionId = server.logNewClientSessionId(nick);
                                    server.subscribe(this);
									fillMessagePane();
                                    break;
                                } else {
                                    sendMsg("Учетная запись уже используется");
                                }
                            } else {
                                sendMsg("Неверный логин/пароль");
                            }
                        }
                    else if (str.startsWith("/register")) {
                            String[] tokens = str.split(" ");
                            AuthService authService = new AuthService(server.connection);
                            String isNickExists = authService.getNickByLoginAndPass(tokens[1], tokens[2]);
                            if (isNickExists != null) {
                                sendMsg("Учетная запись уже существует");
                            }

                            if (tokens[2].equals(tokens[3])) {
                                if (authService.addUser(tokens[1], tokens[2], tokens[4])) {
                                    sendMsg("/regok");
                                }
                                else sendMsg("Ошибка на стороне БД");
                            }
                            else sendMsg("Пароли не соответствуют друг другу");
                        }
                    }

                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/")) {
                            if (str.equals("/end")) {
                                out.writeUTF("/serverclosed");
                                break;
                            }
                            if (str.startsWith("/w ")) { // /w nick3 lsdfhldf sdkfjhsdf wkerhwr
                                String[] tokens = str.split(" ", 3);
                                server.sendPersonalMsg(this, tokens[1], tokens[2]);
                            }
                            if (str.startsWith("/putFile")) { // /w nick3 lsdfhldf sdkfjhsdf wkerhwr
                                String[] tokens = str.split(" ", 2);
                                sentPersonalFile(tokens[1]);
                            }
                            if (str.startsWith("/blacklist ")) { // /blacklist nick3
                                String[] tokens = str.split(" ");
                                blackList.add(tokens[1]);
                                sendMsg("Вы добавили пользователя " + tokens[1] + " в черный список");
                            }
                        } else {
                            server.broadcastMsg(this, nick + ": " + str);
                        }
                        System.out.println("Client: " + str);
                    }
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fin.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fout.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fileSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        server.unsubscribe(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            /*
            //поток отвечающий за обмен файлами
            new Thread( () -> {
                try {
                    byte b[];                                   // буфер для обмена файлом
                    try {
                        b = new byte[fin.available()];
                        fin.read(b);                           // считываем в буфер данные из сокета
                        fout.write(b);                          //
                    } catch (IOException exc) {
                        exc.printStackTrace();
                    }
                }
                finally {
                    try {
                        fin.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
             */

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkBlackList(String nick) {
        return blackList.contains(nick);
    }

    public void sendMsg(String msg) throws IOException {
        out.writeUTF(msg);
    }

    public void sentPersonalFile(String nickto) {
        new Thread( () -> {
            try {
                byte[] b;
                int bLen;
                ArrayList<Byte> bFull = new ArrayList<Byte>();
                try {
                    while ((bLen = fin.available()) > 0) {
                        b = new byte[bLen];                                // буфер для обмена файлом
                        fin.read(b);         // считываем в буфер данные из сокета
                        bFull.ensureCapacity(bFull.size() + bLen);
                        for (byte b1 : b) {
                            bFull.add(b1);
                        }
                    }
                    bLen = bFull.size();
                    b = new byte[bLen];
                    for (int n = 0; n < bFull.size(); n++) {
                        b[n] = bFull.get(n);
                    }
                    server.sentPersonalFile(nick, nickto, b);
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            }
            finally {
                try {
                    fin.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sentFile( byte[] file) {
        try {
            fout.write(file);
            fout.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillBroadcastMessagePane()  throws SQLException, IOException {
        ResultSet rs = server.getBroadcastMessagesHistory(nick);
        while (rs.next()) {
            out.writeUTF(rs.getString("message"));
        }
    }

	private void fillMessagePane()  throws SQLException, IOException {
		ResultSet rs = server.getMessagesHistory(nick);
		while (rs.next()) {
			String message = rs.getString(1);
//			if (message.startsWith("to " + nick + ": ")		|| message.startsWith("from " + nick + ": "))
//				continue;
			out.writeUTF(message);
		}
	}

    public int getSessionId () {
    	return this.sessionId;
	}
}
