package elements;

import packet.Packet;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server {
    public static void main(String args[]) {

        try {
            DatagramSocket serverSocket = new DatagramSocket(5001);
            System.out.println("Server started");

            while (true) {
                byte[] fileToRcv = new byte[200];
                DatagramPacket pktToRcv = new DatagramPacket(fileToRcv, fileToRcv.length);
                serverSocket.receive(pktToRcv);
                Packet pktRcvd = (Packet) convertBytesToObject(pktToRcv.getData());
                System.out.println("Cheogu no servidor: " + pktRcvd.getSequenceNum());

//                byte[] ackToSend;
//                Packet ackPckt = new Packet(pktRcvd.getSequenceNum(), 0);
//                ackToSend = convertObjectToBytes(ackPckt);
//                InetAddress clientIP = pktToRcv.getAddress();
//                int clientPort = pktToRcv.getPort();
//                DatagramPacket fileToSend = new DatagramPacket(ackToSend, ackToSend.length, clientIP, clientPort);
//                serverSocket.send(fileToSend);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Convert packet.Packet to byte[]
    public static byte[] convertObjectToBytes(Packet obj) {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        try (ObjectOutputStream ois = new ObjectOutputStream(boas)) {
            ois.writeObject(obj);
            return boas.toByteArray();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        throw new RuntimeException();
    }

    // Convert byte[] to object
    public static Object convertBytesToObject(byte[] bytes) {
        InputStream is = new ByteArrayInputStream(bytes);
        try (ObjectInputStream ois = new ObjectInputStream(is)) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException ioe) {
            ioe.printStackTrace();
        }
        throw new RuntimeException();
    }
}