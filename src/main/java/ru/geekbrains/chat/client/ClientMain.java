package ru.geekbrains.chat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.Scanner;

public class ClientMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

//        Parent root = FXMLLoader.load(getClass().getResource("src\\main\\java\\ru\\geekbrains\\chat\\client\\client.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/client.fxml"));
//        Parent root = FXMLLoader.load(getClass().getResource("client.fxml"));
//        Parent root = FXMLLoader.load(getClass().getResource("src\\main\\java\\ru\\geekbrains\\chat\\client\\client.fxml"));
        primaryStage.setTitle("2k18SummerChat");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
//        File file = new File("src\\main\\java\\ru\\geekbrains\\chat\\client");
//        for (String f : file.list()) {
//            System.out.println(f);
//        }
//        File f1 = new File("src\\main\\java\\ru\\geekbrains\\chat\\client\\client.fxml");
//        Scanner sc;
//        try {
//            sc = new Scanner(f1);
//            for (int i = 0; i < 5; i++) {
//                System.out.println(sc.next());
//            }
//        }
//        catch (FileNotFoundException exc) {
//            System.out.println(exc.getMessage());
//        }

        launch(args);
    }
}
