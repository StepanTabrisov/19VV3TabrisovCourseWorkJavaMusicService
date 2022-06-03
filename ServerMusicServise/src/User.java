import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class User {
    public String name;
    public String mail;
    public String password;

    public User(){
        name = "";
        mail = "";
        password = "";
    }

    public User(String pName, String pPass){
        name = pName;
        mail = "";
        password = pPass;
    }

    public User(String pName, String pPass, String pMail){
        name = pName;
        mail = pMail;
        password = pPass;
    }

    public boolean CompareUser(){
        String passwordFromDB;
        ConnectDB cnDB = new ConnectDB();
        ResultSet resultSet = cnDB.SelectUser(name);
        try{
            while(resultSet.next()){
                passwordFromDB = resultSet.getNString(4);
                if(password.equals(passwordFromDB)){
                    return true;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean RegistrationUser(){
        String nameFromDB;
        ConnectDB cnDB = new ConnectDB();
        ResultSet resultSet = cnDB.SelectUser(name);
        try{
            while(resultSet.next()){
                nameFromDB = resultSet.getNString(2);
                if(name.equals(nameFromDB)){
                    return false;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        cnDB.InsertUser(name, mail, password);
        return true;
    }
}