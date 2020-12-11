package ru.geekbrains.chat.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FileViewerStage extends Stage {


    Parent root = null;
    public ArrayList<String> files;
    DataInputStream in;
    DataOutputStream out;
    DataInputStream fin;
    DataOutputStream fout;

    public FileViewerStage(ArrayList<String> files
    , DataInputStream in, DataOutputStream out, DataInputStream fin,DataOutputStream fout) throws IOException{

        try {
            this.files = files;
            this.in = in;
            this.out = out;
            this.fin = fin;
            this.fout = fout;
            root = FXMLLoader.load(getClass().getResource("/fxml/fileViewer.fxml"));
            setTitle("Your files on the server");
            setScene(new Scene(root, 600, 400));
            show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
