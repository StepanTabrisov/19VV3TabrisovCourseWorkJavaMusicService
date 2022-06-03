package com.example.alfa;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class Client {
    public final static int SERVER_PORT = 5000;
    DatagramSocket clientSocket;
    InetAddress IPAddress;
    byte[] receivingDataBuffer = new byte[128];
    byte[] sendingDataBuffer = new byte[128];

    public Client() throws SocketException, UnknownHostException {
        clientSocket = new DatagramSocket();
        IPAddress = InetAddress.getByName("localhost");
    }

    public void SendMessage(int param, String outMessage) throws IOException {
        String res = new String(outMessage!="" ? String.valueOf(param) + "/" + outMessage :String.valueOf(param) + "/" + "0");
        sendingDataBuffer = res.getBytes();
        DatagramPacket sendingPacket = new DatagramPacket(sendingDataBuffer,sendingDataBuffer.length, IPAddress, SERVER_PORT);
        clientSocket.send(sendingPacket);
        Arrays.fill(sendingDataBuffer, (byte) 0);

    }

    public void SendMessage(String outMessage) throws IOException {
        String res = outMessage;
        sendingDataBuffer = res.getBytes();
        DatagramPacket sendingPacket = new DatagramPacket(sendingDataBuffer,sendingDataBuffer.length, IPAddress, SERVER_PORT);
        clientSocket.send(sendingPacket);
        Arrays.fill(sendingDataBuffer, (byte) 0);
    }

    public DatagramPacket ReceiveMessage() throws IOException {
        DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
        clientSocket.receive(inputPacket);
        return inputPacket;
    }

    public byte[] ReceiveMes() throws IOException {
        DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
        clientSocket.receive(inputPacket);

        int len=0;
        for (int i = 0; i < 4; ++i) {
            len |= (receivingDataBuffer[3-i] & 0xff) << (i << 3);
        }

        Arrays.fill(receivingDataBuffer, (byte) 0);
        receivingDataBuffer = new byte [len];

        inputPacket = new DatagramPacket(receivingDataBuffer, len);
        clientSocket.receive(inputPacket);
        return receivingDataBuffer;
    }
}
