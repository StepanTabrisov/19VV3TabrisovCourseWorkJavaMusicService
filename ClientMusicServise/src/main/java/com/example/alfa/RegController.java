package com.example.alfa;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegController {

    public TextField RegUserName;
    public TextField RegMail;
    public PasswordField RegPassword;
    public Label RegErrorMsg;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void CompleteRegistration(ActionEvent actionEvent) throws IOException {
        User user = new User(RegUserName.getText().trim(), RegPassword.getText().trim(), RegMail.getText().trim());
        if(user.name.equals("") || user.password.equals("") || user.mail.equals("")) {
            RegErrorMsg.setText("Заполните поля регистрации");
            RegErrorMsg.setVisible(true);
        }
        if(CheckNameUser(user).equals("true")){
            Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
            stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }else{
            RegErrorMsg.setVisible(true);
        }
    }

    public String CheckNameUser(User user) throws IOException {
        Client client = new Client();
        client.SendMessage(2, user.name + "/" + user.password + "/" + user.mail);
        String answer = new String(client.ReceiveMessage().getData());
        return answer.trim();
    }
}
