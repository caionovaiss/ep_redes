package elements;

import attributes.Enums;
import myThreads.ReceiveSocket;

import java.net.DatagramSocket;
import java.net.SocketException;

public class Router extends Host {

    public static void main(String[] args) {
        try {
            //ida
            DatagramSocket routerSocket = new DatagramSocket(5000);
            ReceiveSocket receiveSocket = new ReceiveSocket(routerSocket, Enums.ElementType.ROUTER_G);
            Thread listen = new Thread(receiveSocket);
            listen.start();

            //volta
            DatagramSocket routerSocket2 = new DatagramSocket(5001);
            ReceiveSocket receiveSocket2 = new ReceiveSocket(routerSocket2, Enums.ElementType.ROUTER_B);
            Thread listen2 = new Thread(receiveSocket2);
            listen2.start();

        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

}
