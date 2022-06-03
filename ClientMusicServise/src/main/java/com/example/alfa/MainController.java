package com.example.alfa;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class MainController implements Initializable {
    public Label mainLabel;
    public AnchorPane searchMenu;
    public AnchorPane userCollection;
    public ListView<Music> userPlayList;
    public Slider volumeSlider;
    public Label nameTrackOnPane;
    public Slider timeSlider;
    public Label currTime;
    public Label timeTrack;
    public ImageView previousTrack;
    public ImageView playTrack;
    public ImageView nextTrack;
    public ImageView stopTrack;
    public ImageView volume1;
    public ImageView volume2;
    public TextField search;
    public ListView<Music> searchList;
    public ImageView addTrack;

    private Stage stage;
    private Scene scene;
    private Parent root;

    boolean play = false;
    public Media media;
    public MediaPlayer mediaPlayer;
    public Timer timer;
    public TimerTask timerTask;
    int currentTrackIndex=0;
    boolean b = false;
    ObservableList<Music> musicListObservableList = FXCollections.observableArrayList();
    ObservableList<Music> searchMusicListObservableList = FXCollections.observableArrayList();
    public ArrayList <Music> musicArrayList = new ArrayList<Music>();
    public ArrayList <Music> searchMusicArrayList = new ArrayList<Music>();
    public ArrayList <String> searchMusicArrayListS = new ArrayList<String>();
    public ArrayList <String> userPlayListS = new ArrayList<String>();
    public ArrayList <Music> musicPlaylist = new ArrayList<Music>();
    Music curr;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Client client = null;
        try {
            client = new Client();
            client.SendMessage(5, "playlist");
            ByteArrayInputStream baos = new ByteArrayInputStream(client.ReceiveMes());
            ObjectInputStream oos = new ObjectInputStream(baos);
            userPlayListS = (ArrayList<String>) oos.readObject();
            musicPlaylist = enterArray(userPlayListS);
            musicListObservableList.addAll(musicPlaylist);
            userPlayList.setItems(musicListObservableList);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        userPlayList.setCellFactory(param -> new ListCell<Music>() {
            @Override
            protected void updateItem(Music item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.name == null) {
                    setText(null);
                } else {
                    setText(item.name);
                }
            }
        });

        userPlayList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Music>(){
            public void changed(ObservableValue<? extends Music> changed, Music oldValue, Music newValue){
                addTrack.setVisible(false);
                currentTrackIndex = userPlayList.getSelectionModel().getSelectedIndex();
                playSong(userPlayList.getSelectionModel().getSelectedItem());
            }
        });

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if(volumeSlider.getValue() == 0){
                    volume2.setVisible(true);
                }else{
                    volume2.setVisible(false);
                }
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            }
        });

        Client finalClient = client;
        search.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldV, String newV) {
                searchMusicListObservableList.clear();
                try {
                    finalClient.SendMessage(3, newV);
                    System.out.println(newV);
                    ByteArrayInputStream baos = new ByteArrayInputStream(finalClient.ReceiveMes());
                    ObjectInputStream oos = new ObjectInputStream(baos);
                    searchMusicArrayListS = (ArrayList<String>) oos.readObject();
                    searchMusicArrayList = enterArray(searchMusicArrayListS);
                    searchMusicListObservableList.addAll(searchMusicArrayList);
                    searchList.setItems(searchMusicListObservableList);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        searchList.setCellFactory(param -> new ListCell<Music>() {
            @Override
            protected void updateItem(Music item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.name == null) {
                    setText(null);
                } else {
                    setText(item.name);
                }
            }
        });

        searchList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Music>(){
            public void changed(ObservableValue<? extends Music> changed, Music oldValue, Music newValue){
             addTrack.setVisible(true);
             currentTrackIndex = searchList.getSelectionModel().getSelectedIndex();
             if(currentTrackIndex!=-1){
                 playSong(searchList.getSelectionModel().getSelectedItem());
             }
            }
        });

        addTrack.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(!musicArrayList.contains(searchList.getSelectionModel().getSelectedItem())){
                    musicArrayList.add(searchList.getSelectionModel().getSelectedItem());
                    musicListObservableList.add(searchList.getSelectionModel().getSelectedItem());
                    userPlayList.setItems(musicListObservableList);
                    try {
                        finalClient.SendMessage(4, searchList.getSelectionModel().getSelectedItem().name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void playSong(Music music){
        if(play){
            mediaPlayer.stop();
            cancelTimer();
        }
        stopTrack.setVisible(true);
        media = new Media(music.file.toURI().toString());
        nameTrackOnPane.setText(music.name);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        play = true;
        beginTimer();
        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                timeSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
                timeTrack.setText(getTimeString(mediaPlayer.getTotalDuration().toMillis()));
                double curr = mediaPlayer.getCurrentTime().toSeconds();
                timeSlider.setValue(curr);
            }
        });

        timeSlider.setOnMouseReleased(mouseEvent -> mediaPlayer.seek(Duration.seconds(timeSlider.getValue())));

        timeSlider.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mediaPlayer.pause();
                mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
                mediaPlayer.play();
            }
        });

        timeSlider.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mediaPlayer.pause();
                mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
                mediaPlayer.play();
            }
        });

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration duration, Duration t1) {
                currTime.setText(getTimeString(t1.toMillis()));
            }
        });
    }

    /*public void playSong(int index){
        if(play){
            mediaPlayer.stop();
        }
        stopTrack.setVisible(true);
        media = new Media(userPlayList.getItems().get(index).file.toURI().toString());
        nameTrackOnPane.setText(userPlayList.getItems().get(index).name);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        play = true;

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                timeSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
                timeTrack.setText(getTimeString(mediaPlayer.getTotalDuration().toMillis()));
                timeSlider.setValue(mediaPlayer.getCurrentTime().toSeconds());
            }
        });

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration duration, Duration t1) {
                currTime.setText(getTimeString(t1.toMillis()));
            }
        });

        timeSlider.setOnMouseReleased(mouseEvent -> mediaPlayer.seek(Duration.seconds(timeSlider.getValue())));

        timeSlider.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
            }
        });
    }*/

    public static String getTimeString(double millis) {
        String s = formatTime((millis / 1000) % 60);
        String m = formatTime((millis / 1000) / 60);
        return m + ":" + s;
    }

    public static String formatTime(double time) {
        int t = (int)time;
        if (t > 9) { return String.valueOf(t); }
        return "0" + t;
    }

    public void beginTimer(){
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                double curr = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                timeSlider.setValue(curr);
                if(curr / end == 1) cancelTimer();;
            }
        };
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }

    public void cancelTimer(){
        timer.cancel();
    }

    public void playMusic(MouseEvent actionEvent) {
        if(play){
            stopTrack.setVisible(false);
            mediaPlayer.pause();
            play = false;
        }
        else{
            stopTrack.setVisible(true);
            mediaPlayer.play();
            play = true;
        }
    }

    public void prevMusic(MouseEvent actionEvent) {
        cancelTimer();
        if(currentTrackIndex > 0){
            currentTrackIndex--;
        }else{

            currentTrackIndex = userPlayList.getItems().size() - 1;
        }
        userPlayList.getSelectionModel().select(currentTrackIndex);
        playSong(userPlayList.getItems().get(currentTrackIndex));
    }

    public void nextMusic(MouseEvent actionEvent) {
        cancelTimer();
        if(currentTrackIndex < userPlayList.getItems().size() - 1){
            currentTrackIndex++;
        }else{
            currentTrackIndex = 0;
        }
        userPlayList.getSelectionModel().select(currentTrackIndex);
        playSong(userPlayList.getItems().get(currentTrackIndex));
    }

    public void setMain(ActionEvent actionEvent){
        mainLabel.setText("Главная");
        searchMenu.setVisible(false);
        userCollection.setVisible(false);
    }

    public void setSearch(ActionEvent actionEvent) {
        mainLabel.setText("Поиск");
        searchMenu.setVisible(true);
        userCollection.setVisible(false);
    }

    public void setCollect(ActionEvent actionEvent) {
        mainLabel.setText("Ваша коллекция");
        searchMenu.setVisible(false);
        userCollection.setVisible(true);
    }

    public void SetVolume(MouseEvent mouseEvent) {
        volumeSlider.setValue(0);
        volume2.setVisible(true);
    }

    public void AddVolume(MouseEvent mouseEvent) {
        volumeSlider.setValue(50);
        volume2.setVisible(false);
    }

    /*public void addTrackToCollection(MouseEvent mouseEvent) {
        musicListObservableList.add(searchList.getSelectionModel().getSelectedItem());
    }*/

    public ArrayList<Music> enterArray(ArrayList<String> b){
        ArrayList<Music> temp = new ArrayList<Music>();
        for(int i = 0; i < b.size(); i++){
            temp.add(i, new Music(b.get(i)));
        }
        return temp;
    }

    public void goToLogin(ActionEvent actionEvent) throws IOException {
        mediaPlayer.stop();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}