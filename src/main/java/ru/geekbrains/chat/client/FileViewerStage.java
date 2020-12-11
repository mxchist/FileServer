package ru.geekbrains.chat.client;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class FileViewerStage extends Stage {


    Parent root = null;
    public ArrayList<String> files;

    public FileViewerStage(ArrayList<String> files) throws IOException{

        try {
            this.files = files;
            root = FXMLLoader.load(getClass().getResource("/fxml/fileViewer.fxml"));
            setTitle("You files on the server");
            setScene(new Scene(root, 600, 400));
            show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
