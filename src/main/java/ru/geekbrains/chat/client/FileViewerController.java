package ru.geekbrains.chat.client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Files;
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
        out = ((FileViewerStage)btnUpload.getScene().getWindow()).out;
        FileChooser fileChooser = new FileChooser();
        File fileToUpload = fileChooser.showOpenDialog(btnUpload.getScene().getWindow());
        byte b[];
        int bLen;
        int i=1;
        try (FileInputStream fis = new FileInputStream(fileToUpload)) {
            while ((bLen = fis.available()) > 0) {
                bLen = (bLen < 4096 ? bLen : 4096);
                b = new byte[bLen];
                System.out.printf("Was read %d bytes, chance: %d %n", fis.read(b), i++);
                fout.write(b);
            }
            fout.flush();
            out.writeUTF("/saveFile " + fileToUpload.getName());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
//        ((FileViewerStage)btnUpload.getScene().getWindow()).close();
    }

    public void downloadFile() {

        ((FileDownloadStage)downloadButton.getScene().getWindow()).close();
    }
}
