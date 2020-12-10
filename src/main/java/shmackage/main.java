package shmackage;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.lang.Byte;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


class Server {
	private final int SERVER_PORT = 8189;
	private final int FILE_SERVER_PORT = 8190;


	final String folderToStore = "d:\\Max\\Documents\\Учёба\\GeekBrains\\2019\\FileServer\\fileServerStorage";

	public static void main(String[]args){
		Thread t1 = new Thread( () -> {
			Server s = new Server();
		});

		Thread t2 = new Thread( () -> {
			Controller c = new Controller();
		});

		t1.start();
		t2.start();
	}

	public Server() {
		ServerSocket server = null;
		ServerSocket fileServer = null;
		Socket socket = null;
		Socket fileSocket = null;
		try {
			server = new ServerSocket(SERVER_PORT);
			fileServer = new ServerSocket(FILE_SERVER_PORT);
			System.out.println("Сервер запущен. Ожидаем клиентов...");

			while (true) {
				// в качестве клиента -- класс Controller
				socket = server.accept();
				fileSocket = fileServer.accept();
				System.out.println("Клиент подключился");

				//после того, как симмиритуется подключение от клиента, запущу класс, в котором осуществляется передача файла через байтовый поток
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
				fileServer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String getFolderToStore() {
		return this.folderToStore;
	}
}



class ClientHandler {
	private Server server;
	private Socket fileSocket;
	private DataOutputStream fout;
	private DataInputStream fin;
	final public String login = "test_login";
	final String INPUT_FILE = "D:\\Max\\Pictures\\Screenshots\\cv.doc";

	public ClientHandler(Server server, Socket socket, Socket fileSocket) {
		try {
			this.server = server;
			this.fileSocket = fileSocket;

			this.fin = new DataInputStream(fileSocket.getInputStream());
			this.fout = new DataOutputStream(fileSocket.getOutputStream());

			new Thread(() -> {
				try {
					testSaveFileToStorage();
//					File file = new File("Z:");
//					file.createNewFile();

				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void saveFileToStorage(String filename) throws IOException {
		String personalPath = server.getFolderToStore() +"\\" + this.login;
		File personalFolder = new File(personalPath);
		if (!personalFolder.exists()) {
			System.out.printf("A personal folder %s %n", (personalFolder.mkdir() ? "was created" : "was not created because something goes wrong"));
		}
		final File fileToSave = new File(personalPath + "\\" + filename );

		new Thread( () -> {
			try (FileOutputStream fos = new FileOutputStream( fileToSave)) {
				byte[] b;
				int bLen;

				//читаю записанный в testSaveFileToStorage() байтовый поток из того же самого сокет-сервера
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
					if (fileToSave.exists())
						fileToSave.delete();

					fileToSave.createNewFile();
					fos.write(b);
                    fos.close();
				} catch (IOException exc) {
					exc.printStackTrace();
				}
				finally {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	public void testSaveFileToStorage() throws IOException {
		System.out.println("Testing can I use method saveFileToStorage");
		File file = new File(INPUT_FILE);
		FileInputStream fis = new FileInputStream(file);
		System.out.printf("A bytes available: %d %n", fis.available());
		byte[] b = new byte[fis.available()];

		//записываю прочитанный байтовый поток из файла в выходной байтовый поток того же самого сокет-сервера
		fout.write(b);
		fout.flush();

		this.saveFileToStorage(file.getName());
	}
}

class Controller {
	Socket socket;
	Socket fileSocket;
	DataInputStream in;
	DataInputStream fin;

	final String IP_ADDRESS = "localhost";
	private final int SERVER_PORT = 8189;
	private final int FILE_SERVER_PORT = 8190;

	public Controller() {
		if (socket == null || socket.isClosed()) {
			connect();
		}
	}

	public void connect() {
		try {
			//Имитирую подключение к сервер сокету со стороны клиента

			socket = new Socket(IP_ADDRESS, SERVER_PORT);
			in = new DataInputStream(socket.getInputStream());
			fileSocket = new Socket(IP_ADDRESS, FILE_SERVER_PORT);
			fin = new DataInputStream(fileSocket.getInputStream());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
