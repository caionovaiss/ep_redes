package elements;

import attributes.Attributes;
import myThreads.ReceiveSocket;

import java.net.DatagramSocket;
import java.net.SocketException;

public class Router extends Host {

    public static void main(String[] args) {
        try {
            //ida
            DatagramSocket routerSocket = new DatagramSocket(5000);
            ReceiveSocket receiveSocket = new ReceiveSocket(routerSocket, Attributes.ElementType.ROUTER_G);
            Thread listen = new Thread(receiveSocket);
            listen.start();

            //volta
            DatagramSocket routerSocket2 = new DatagramSocket(5001);
            ReceiveSocket receiveSocket2 = new ReceiveSocket(routerSocket2, Attributes.ElementType.ROUTER_B);
            Thread listen2 = new Thread(receiveSocket2);
            listen2.start();

        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

}
