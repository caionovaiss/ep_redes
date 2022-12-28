package elements;

import myThreads.ServerBufferThread;
import packet.Packet;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;
import java.util.Queue;

public class Server extends Host {
    private static int rwnd;

    public static void main(String[] args) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(5003);

            System.out.println("Server started");
            Queue<Packet> pktQueue = new LinkedList<>();

            ServerBufferThread bufferThread = new ServerBufferThread(10, pktQueue);
            Thread listen = new Thread(bufferThread);
            listen.start();

            while (true) {

                //receive pkt
                byte[] fileToRecv = new byte[1024];
                DatagramPacket datagram = new DatagramPacket(fileToRecv, fileToRecv.length);
                serverSocket.receive(datagram);
                Packet pktRcvd = (Packet) convertBytesToObject(datagram.getData());

                System.out.println(pktRcvd);

                if (pktRcvd.getLength() < bufferThread.getRwnd()) {
                    pktQueue.add(pktRcvd);
                    bufferThread.setQueue(pktQueue);

                    // calculo da janela
                    bufferThread.setLastByteRcvd(pktRcvd.getLength());
                    rwnd = bufferThread.getRwnd();
                    System.out.println("janela : " + rwnd);

                    //send ack
                    byte[] bytesToSend;
                    Packet ackPkt = new Packet(pktRcvd.getSequenceNum(), rwnd);
                    bytesToSend = convertObjectToBytes(ackPkt);
                    DatagramPacket fileToSend = new DatagramPacket(bytesToSend, bytesToSend.length, datagram.getAddress(), 5001);
                    serverSocket.send(fileToSend);
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}