package elements;

import myThreads.ClientBufferThread;
import packet.Packet;

import java.io.IOException;
import java.net.*;

public class Client extends Host {

    public static void main(String args[]) {
        try {
            DatagramSocket socket = new DatagramSocket(5002);
            int count = 1;

            ClientBufferThread clientBufferThread = new ClientBufferThread(socket);
            Thread listen = new Thread(clientBufferThread);
            listen.start();

            while (true) {
                while (clientBufferThread.getCwnd() != 0) {
                    //criando pacote e enviando
                    String msg = getMsg(count);
                    Packet pkt = new Packet(count, msg);

                    byte[] fileToSend;
                    fileToSend = convertObjectToBytes(pkt);
                    InetAddress ip = InetAddress.getByName("127.0.0.1");
                    DatagramPacket dPacket = new DatagramPacket(fileToSend, fileToSend.length, ip, 5000);

                    socket.send(dPacket);
                    System.out.println("Pacote enviado: " + pkt);

                    clientBufferThread.addSequenceNumToList(pkt.getSequenceNum());

                    clientBufferThread.decreaseCwnd();
                    count++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendPkt(int count, DatagramSocket socket) throws IOException {
        //criando pacote e enviando
        String msg = getMsg(count);
        Packet pkt = new Packet(count, msg);

        byte[] fileToSend;
        fileToSend = convertObjectToBytes(pkt);
        InetAddress ip = InetAddress.getByName("127.0.0.1");
        DatagramPacket dPacket = new DatagramPacket(fileToSend, fileToSend.length, ip, 5000);

        socket.send(dPacket);
    }

}