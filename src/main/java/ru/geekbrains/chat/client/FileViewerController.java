package ru.geekbrains.chat.client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FileViewerController  implements Initializable {

    @FXML
    VBox downloadPanel;

    @FXML
    Button btnUpload;

    @FXML
    Button downloadButton;

    @FXML
    Button viewfilesbutton;

    @FXML
    ListView files;

    ArrayList<String> fs;
    DataInputStream in;
    DataOutputStream out;
    DataInputStream fin;
    DataOutputStream fout;

    @FXML
    public void initialize (URL url, ResourceBundle r) {
//        fs = ((FileViewerStage)files.getScene().getWindow()).files;
//        for (int i = 1; i < fs.size(); i++) {
//            files.getItems().add(fs.get(i));
//        }
    }

    public void fillList(MouseEvent mouseEvent) {
        files.getItems().clear();
        fs = ((FileViewerStage)files.getScene().getWindow()).files;
        for (int i = 1; i < fs.size(); i++) {
            files.getItems().add(fs.get(i));
        }
    }

    public void sendFile() {
        fout = ((FileViewerStage)btnUpload.getScene().getWindow()).fout;
        FileChooser fileChooser = new FileChooser();
        File fileToUpload = fileChooser.showOpenDialog(btnUpload.getScene().getWindow());
        try (FileInputStream fis = new FileInputStream(fileToUpload)) {
            byte b[];
            b = new byte[fis.available()];
            fis.read(b);
            fout.write(b);
            fout.flush();
            out.writeUTF("/saveFile " + fileToUpload.getName());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        ((FileViewerStage)btnUpload.getScene().getWindow()).close();
    }

    public void downloadFile() {
        ((FileDownloadStage)downloadButton.getScene().getWindow()).close();
    }
}
