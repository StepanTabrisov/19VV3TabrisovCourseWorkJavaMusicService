package com.example.alfa;

import java.io.*;

public class Music {

    public String name;
    public File file;

    public Music(String n, InputStream f){
        name = n;
        file = new File(name + ".mp3");
        try{
            FileOutputStream fileOut = new FileOutputStream(file);
            byte[] buffer = new byte[32 * 1024];
            while(true){
                int byteCount = f.read(buffer);
                if(byteCount < 0){
                    break;
                }
                fileOut.write(buffer, 0, byteCount);
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Music(String n){
        name = n;
        file = new File(name + ".mp3");
    }
}

