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

public class LoginController {
    public Label ErrorMsg;
    public TextField LoginField;
    public PasswordField PasswordField;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void SignUpBtn(ActionEvent e) throws IOException {
        String userName = LoginField.getText().trim();
        String userPassword = PasswordField.getText().trim();
        if(userName.equals("") || userPassword.equals("")){
            ErrorMsg.setText("Введите логин или пароль!");
            ErrorMsg.setVisible(true);
        }else if(LoginUser(userName, userPassword).equals("true")){
            ErrorMsg.setVisible(false);
            Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }else{
            ErrorMsg.setText("Неверный логин или пароль!");
            ErrorMsg.setVisible(true);
        }
    }

    public void GoToRegistration(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Registration.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public String LoginUser(String name, String password) throws IOException {
        User user = new User(name, password);

        Client client = new Client();
        client.SendMessage(1, user.name + "/" + user.password);

        String answer = new String(client.ReceiveMessage().getData());

        return answer.trim();
    }
}
