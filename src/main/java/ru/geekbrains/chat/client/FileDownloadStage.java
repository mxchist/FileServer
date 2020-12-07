package ru.geekbrains.chat.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.IOException;

public class FileDownloadStage extends Stage {
    String nickFrom;
    DataInputStream fin;

    public FileDownloadStage(String nickFrom, DataInputStream fin) {
        this.nickFrom = nickFrom;
        this.fin = fin;
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/fileDownload.fxml"));
            setTitle("File from " + nickFrom);
            Scene scene = new Scene(root, 200, 100);
            setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
