package com.example.alfa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene scene = new Scene(root);
        stage.setResizable(false);
        stage.setTitle("PIPE");
        stage.setScene(scene);
        stage.show();

        Client client = new Client();
        client.SendMessage("connected");
    }

    public static void main(String[] args) {
        launch(args);
    }
}