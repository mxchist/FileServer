package ru.geekbrains.chat.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.*;

public class PersonalController {
    @FXML
    Button btnSent;

    @FXML
    Button btnUpload;

    @FXML
    TextArea textArea;

    @FXML
    TextField msgField;


//    public PersonalController() {
//        super();
//        textArea = new TextArea();
////        for (TextArea t: ((MiniStage)textArea.getScene().getWindow()).parentList ) {
////            textArea.appendText(t.getText());
////        }
//    }

    public void btnClick() {
        DataOutputStream out = ((MiniStage)btnSent.getScene().getWindow()).out;
        String nickTo = ((MiniStage)btnSent.getScene().getWindow()).nickTo;

        TextArea newTextAera =  new TextArea(msgField.getText());
        if ( !( (MiniStage)btnSent.getScene().getWindow() ).parentList.contains(newTextAera)) {
            ((MiniStage)btnSent.getScene().getWindow()).parentList.add(newTextAera);
            this.textArea.appendText( msgField.getText());
        }
        try {
            out.writeUTF("/w " + nickTo + " " + msgField.getText());
            msgField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void buttonClickUpload() {
        DataOutputStream out = ((MiniStage)btnUpload.getScene().getWindow()).out;
        String nickTo = ((MiniStage)btnUpload.getScene().getWindow()).nickTo;

//        try {
//            out.writeUTF("/putfile " + nickTo);
//            textArea.clear();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        DataOutputStream fout = ((MiniStage)btnUpload.getScene().getWindow()).fout;

        FileChooser fileChooser = new FileChooser();
        File fileToUpload = fileChooser.showOpenDialog(btnUpload.getScene().getWindow());
        try (FileInputStream fis = new FileInputStream(fileToUpload)) {
            byte b[];
                b = new byte[fis.available()];
                fis.read(b);
                fout.write(b);
                fout.flush();
                out.writeUTF("/putFile " + nickTo);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                fout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
