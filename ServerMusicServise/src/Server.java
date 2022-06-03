import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Server {

    public final static int SERVER_PORT = 5000;

    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        ConnectDB v = new ConnectDB();
        ArrayList <String> searchMusicArrayList = new ArrayList<String>();
        ArrayList <String> userPlayList = new ArrayList<String>();
        DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT);
        System.out.println("Waiting for a client to connect...");
        byte[] receivingDataBuffer = new byte[256];
        byte[] sendingDataBuffer = new byte[256];
        String newMsg = "";
        User user = new User();
        int param = 0;
        String[] parseMsg;
        boolean bAnswer;

        DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
        serverSocket.receive(inputPacket);

        String receivedData = new String(inputPacket.getData());
        System.out.println("Sent from the client: " + receivedData.trim());

        Arrays.fill(receivingDataBuffer, (byte) 0);

        InetAddress senderAddress;
        int senderPort;
        DatagramPacket outputPacket;

        while(true){
            serverSocket.receive(inputPacket);
            newMsg = new String(inputPacket.getData()).trim();
            Arrays.fill(receivingDataBuffer, (byte) 0);
            parseMsg = (newMsg.split("/"));
            param = Integer.parseInt(parseMsg[0]);
            System.out.println(parseMsg[0] + parseMsg[1]);
            switch(param){
                case 1:
                    user = new User(parseMsg[1], parseMsg[2]);
                    bAnswer = user.CompareUser();
                    sendingDataBuffer = String.valueOf(bAnswer).getBytes();
                    senderAddress = inputPacket.getAddress();
                    senderPort = inputPacket.getPort();
                    outputPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, senderAddress,senderPort);
                    serverSocket.send(outputPacket);
                    System.out.println(bAnswer);
                    break;
                case 2:
                    user = new User(parseMsg[1], parseMsg[2], parseMsg[3]);
                    bAnswer = user.RegistrationUser();
                    sendingDataBuffer = String.valueOf(bAnswer).getBytes();
                    senderAddress = inputPacket.getAddress();
                    senderPort = inputPacket.getPort();
                    outputPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, senderAddress,senderPort);
                    serverSocket.send(outputPacket);
                    System.out.println(bAnswer);
                    break;
                case 3:
                    System.out.println(parseMsg[1]);
                    searchMusicArrayList = v.searchMusicList(parseMsg[1]);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(searchMusicArrayList);
                    oos.flush();
                    sendingDataBuffer = baos.toByteArray();

                    int len = sendingDataBuffer.length;
                    byte[] data = new byte[4];

                    for (int i = 0; i < 4; ++i) {
                        int shift = i << 3; // i * 8
                        data[3-i] = (byte)((len & (0xff << shift)) >>> shift);
                    }

                    senderAddress = inputPacket.getAddress();
                    senderPort = inputPacket.getPort();

                    outputPacket = new DatagramPacket(data, data.length, senderAddress, senderPort);
                    serverSocket.send(outputPacket);

                    senderAddress = inputPacket.getAddress();
                    senderPort = inputPacket.getPort();

                    outputPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, senderAddress, senderPort);
                    serverSocket.send(outputPacket);
                    break;
                case 4:
                    v.InsertMusicInPlaylistDB(user.name, parseMsg[1]);
                    break;
                case 5:
                    userPlayList = v.getUserPlayList(user.name);

                    baos = new ByteArrayOutputStream();
                    oos = new ObjectOutputStream(baos);
                    oos.writeObject(userPlayList);
                    oos.flush();
                    sendingDataBuffer = baos.toByteArray();

                    len = sendingDataBuffer.length;
                    data = new byte[4];

                    for (int i = 0; i < 4; ++i) {
                        int shift = i << 3; // i * 8
                        data[3-i] = (byte)((len & (0xff << shift)) >>> shift);
                    }

                    senderAddress = inputPacket.getAddress();
                    senderPort = inputPacket.getPort();

                    outputPacket = new DatagramPacket(data, data.length, senderAddress, senderPort);
                    serverSocket.send(outputPacket);

                    senderAddress = inputPacket.getAddress();
                    senderPort = inputPacket.getPort();

                    outputPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, senderAddress, senderPort);
                    serverSocket.send(outputPacket);
                default:
            }
        }
    }
}
