package ru.geekbrains.chat.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    TextField msgField;

    @FXML
    TextArea chatArea;

    @FXML
    HBox bottomPanel;

    @FXML
    HBox upperPanel;

    @FXML
    TextField loginField;

    @FXML
    PasswordField passwordField;

    @FXML
    HBox loginPanel;

    @FXML
    private Label userName;

    @FXML
    ListView<String> clientsList;

    @FXML
    TextField newLoginField;

    @FXML
    TextField newPasswordField;

    @FXML
    TextField newPasswordFieldAgain;

    @FXML
    TextField newNickField;

    @FXML private void updateTitle(String newTitle) {
        Stage primStage = (Stage) loginField.getScene().getWindow();
        primStage.setTitle(newTitle);
    }

    Socket socket;
    Socket fileSocket;
    DataInputStream in;
    DataInputStream fin;
    DataOutputStream out;
    DataOutputStream fout;

    final String IP_ADDRESS = "localhost";
    private final int SERVER_PORT = 8189;
    private final int FILE_SERVER_PORT = 8190;

    private boolean isAuthorized;

    ArrayList<TextArea> textAreas;
    ArrayList<String> files = new ArrayList<String>();;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthorized(false);
        textAreas = new ArrayList<>();
        textAreas.add(chatArea);
    }

    public void setAuthorized(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
        if (!isAuthorized) {
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
            loginPanel.setVisible(false);
            loginPanel.setManaged(false);
            clientsList.setVisible(false);
            clientsList.setManaged(false);
        } else {
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
            loginPanel.setVisible(false);
            loginPanel.setManaged(false);
            clientsList.setVisible(true);
            clientsList.setManaged(true);
        }
    }

    public void showNewLoginPanel(boolean expose) {
        loginPanel.setVisible(expose);
        loginPanel.setManaged(expose);

        if (expose) {
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
        } else {
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
        }
    }

    public void showNewLoginPanel() {
        showNewLoginPanel(true);
    }

    public void connect() {
        try {
            userName.setText("test");
            socket = new Socket(IP_ADDRESS, SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            fileSocket = new Socket(IP_ADDRESS, FILE_SERVER_PORT);
            setAuthorized(false);
            Thread thread = new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/authok")) {
                            setAuthorized(true);
                            out.writeUTF("/listFiles");
                            break;
                        } else if (str.startsWith("/regok")) {
                            showNewLoginPanel(false);
                        }
                        else {
                            for (TextArea o : textAreas) {
                                o.appendText(str + "\n");
                            }
                        }
                    }
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/")) {
                            if (str.equals("/serverclosed")) break;
                            if (str.startsWith("/clientslist ")) {
                                String[] tokens = str.split(" ");
                                Platform.runLater(() -> {
                                    clientsList.getItems().clear();
                                    for (int i = 1; i < tokens.length; i++) {
                                        clientsList.getItems().add(tokens[i]);
                                    }
                                });
                            }
                        }
                        if (str.startsWith("/putFile")) {
                            fin = new DataInputStream(fileSocket.getInputStream());
                            final String[] tokens = str.split(" ");
                            Platform.runLater( () -> {
                                FileDownloadStage fds = new FileDownloadStage(tokens[1], fin);
                                fds.show();
                                fds.requestFocus();
                                fds.setAlwaysOnTop(true);
                            });
                        }
                        if (str.startsWith("/listFiles")) {
                            files.clear();
                            ArrayList<String> tempfiles = new ArrayList<String>();
                            final String[] tokens = str.split(",");
                            for (String s : tokens) {
                                files.add(s);
                            };
                        }
                        else {
                            chatArea.appendText(str + "\n");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
//                } finally {
//                    try {
//                        in.close();
//                    }
//                    catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        fin.close();
//                    }
//                    catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        socket.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        fileSocket.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        out.close();
//                    }
//                    catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        fout.close();
//                    }
//                    catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    setAuthorized(false);
                }
            });
            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg() {
        try {
            out.writeUTF(msgField.getText());
            msgField.clear();
            msgField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToAuth() {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            out.writeUTF("/auth " + loginField.getText() + " " + passwordField.getText());
            updateTitle(loginField.getText());
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createNewUser() {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            updateTitle("Регистрация");
            out.writeUTF("/register " + newLoginField.getText()
                    + " " + newPasswordField.getText()
                    + " " + newPasswordFieldAgain.getText()
                    + " " + newNickField.getText()
            );
            newPasswordField.clear();
            newPasswordFieldAgain.clear();
            newNickField.clear();
            showNewLoginPanel(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void selectClient(MouseEvent mouseEvent) throws IOException {
        if(mouseEvent.getClickCount() == 2) {
            fout = new DataOutputStream(fileSocket.getOutputStream());
            String selectedItem = clientsList.getSelectionModel().getSelectedItem();
            MiniStage ms = new MiniStage(selectedItem, out, fout, textAreas);
            ms.show();
        }
    }

    public void pressFileStorage(MouseEvent mouseEvent) throws IOException {
        FileViewerStage sfv = new FileViewerStage(files);
        sfv.show();
    }

}
