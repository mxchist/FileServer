package ru.geekbrains.chat.client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FileViewerController  implements Initializable {

    @FXML
    VBox downloadPanel;

    @FXML
    Button uploadButton;

    @FXML
    Button downloadButton;

    @FXML
    Button viewfilesbutton;

    @FXML
    ListView files;

    ArrayList<String> fs;

    @FXML
    public void initialize (URL url, ResourceBundle r) {
//        fs = ((FileViewerStage)files.getScene().getWindow()).files;
//        for (int i = 1; i < fs.size(); i++) {
//            files.getItems().add(fs.get(i));
//        }
    }

    public void fillList(MouseEvent mouseEvent) {
        files.getItems().removeAll();
        fs = ((FileViewerStage)files.getScene().getWindow()).files;
        for (int i = 1; i < fs.size(); i++) {
            files.getItems().add(fs.get(i));
        }
    }

    public void sendFile() {
        fs = ((FileViewerStage)files.getScene().getWindow()).files;
        System.out.print("");
    }

    public void downloadFile() {
        ((FileDownloadStage)downloadButton.getScene().getWindow()).close();
    }
}
