package ru.geekbrains.chat.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MiniStage extends Stage {
    String nickTo;
    DataOutputStream out;
    DataOutputStream fout;
    ArrayList<TextArea> parentList;

    public MiniStage(String nickTo, DataOutputStream out, DataOutputStream fout, ArrayList<TextArea> parentList) {
        this.nickTo = nickTo;
        this.out = out;
        this.fout = fout;
        this.parentList = parentList;
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/personal.fxml"));
            setTitle("chatting with " + nickTo);
            Scene scene = new Scene(root, 300, 400);
            setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
