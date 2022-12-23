package elements;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Router {
    public static void main(String[] args) {
        try {
            DatagramSocket rcvSocket = new DatagramSocket(5000);

            while (true) {
                byte[] fileToRcv = new byte[200];
                DatagramPacket pktRcvd = new DatagramPacket(fileToRcv, fileToRcv.length);
                rcvSocket.receive(pktRcvd);

                System.out.println("Passou pelo roteador");

                DatagramSocket sendSocket = new DatagramSocket();
                InetAddress ip = InetAddress.getByName("127.0.0.1");
                DatagramPacket sendPacket = new DatagramPacket(fileToRcv, fileToRcv.length, ip, 5001);
                sendSocket.send(sendPacket);

            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
