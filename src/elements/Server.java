package elements;

import attributes.Attributes;
import myThreads.ReceiveSocket;

import java.net.DatagramSocket;

public class Server extends Host {
    public static void main(String[] args) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(5003);

            System.out.println("Server started");

            ReceiveSocket rSocket = new ReceiveSocket(serverSocket, Attributes.ElementType.SERVER);
            Thread listen = new Thread(rSocket);
            listen.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}