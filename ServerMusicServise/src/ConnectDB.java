

import java.sql.*;
import java.util.ArrayList;

public class ConnectDB extends Config {
    Connection dbConnect;

    public Connection getDbConnect() throws ClassNotFoundException, SQLException {
        String ConnectionStr = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
        Class.forName("com.mysql.cj.jdbc.Driver");
        dbConnect = DriverManager.getConnection(ConnectionStr, dbUser, dbPassword);
        return dbConnect;
    }

    public void InsertUser(String userName, String userMail, String userPassword) {
        String InsertAt = "INSERT INTO " + ConstValues.USER_TABLE +
                "(" + ConstValues.USER_NAME + "," + ConstValues.USER_MAIl + "," + ConstValues.USER_PASSWORD + ")" + "VALUES(?,?,?)";
        try {
            PreparedStatement preparedStatement = getDbConnect().prepareStatement(InsertAt);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, userMail);
            preparedStatement.setString(3, userPassword);
            preparedStatement.execute();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ResultSet SelectUser(String userName) {
        ResultSet res = null;
        String select = "SELECT * FROM " + ConstValues.USER_TABLE + " WHERE " + ConstValues.USER_NAME + "=?";
        try {
            PreparedStatement preparedStatement = getDbConnect().prepareStatement(select);
            preparedStatement.setString(1, userName);
            res = preparedStatement.executeQuery();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }

    public ArrayList<String> searchMusicList(String s) {
        ArrayList<String> paths = new ArrayList<String>();
        if (s.equals("")) {
            return paths;
        }
        String select = "SELECT * FROM musicservicedatabase.song WHERE name LIKE ?";
        try {
            PreparedStatement preparedStatement = getDbConnect().prepareStatement(select);
            preparedStatement.setString(1, "%" + s + "%");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                paths.add(rs.getNString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return paths;
    }

    public void InsertMusicInPlaylistDB(String name, String musicName){
        String InsertAt = "INSERT INTO musicservicedatabase.playlist (song, username) VALUES(?,?)";
        try {
            PreparedStatement preparedStatement = getDbConnect().prepareStatement(InsertAt);
            preparedStatement.setString(1, musicName);
            preparedStatement.setString(2, name);
            preparedStatement.execute();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getUserPlayList(String name){
        ArrayList<String> musicList = new ArrayList<String>();
        String select = "SELECT * FROM musicservicedatabase.playlist WHERE username =?";
        try {
            PreparedStatement preparedStatement = getDbConnect().prepareStatement(select);
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                musicList.add(rs.getNString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return musicList;
    }
}

