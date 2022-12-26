package elements;

import attributes.Attributes;
import myThreads.ReceiveSocket;
import packet.Packet;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client extends Host {

    public static void main(String args[]) {
        try {
            DatagramSocket socket = new DatagramSocket(5002);
            int count = 1;

            Thread listen = new Thread(new ReceiveSocket(socket, Attributes.ElementType.CLIENT));
            listen.start();

            while (true) {
                //criando pacote e enviando
                String msg = getMsg();
                Packet pckt = new Packet(msg.length(), count, msg);

                byte[] fileToSend;
                fileToSend = convertObjectToBytes(pckt);
                InetAddress ip = InetAddress.getByName("127.0.0.1");
                DatagramPacket dPacket = new DatagramPacket(fileToSend, fileToSend.length, ip, 5000);

                System.out.println("Tamanho do pacote de envio: " + fileToSend.length);
                socket.send(dPacket);

                count++;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}