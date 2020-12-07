package ru.geekbrains.chat.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;

public class DownloadController {

    @FXML
    VBox downloadPanel;

    @FXML
    Button yes;

    @FXML
    Button no;

    @FXML
    TextArea messageText;

    public void acceptDownloading() {
        DataInputStream fin = ((FileDownloadStage)yes.getScene().getWindow()).fin;

        FileChooser fileChooser = new FileChooser();
        File fileToDownload = fileChooser.showSaveDialog(yes.getScene().getWindow());
        try (FileOutputStream fos = new FileOutputStream(fileToDownload)) {
                byte[] b;
                int bLen;
                ArrayList<Byte> bFull = new ArrayList<Byte>();
                try {
                    while ((bLen = fin.available()) > 0) {
                        b = new byte[bLen];
                        fin.read(b);
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
                    fos.write(b);
                } catch (IOException e) {
                    e.printStackTrace();
                }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ((FileDownloadStage) yes.getScene().getWindow()).close();
    }

    public void rejectDownloading() {
        ((FileDownloadStage)no.getScene().getWindow()).close();
    }
}
