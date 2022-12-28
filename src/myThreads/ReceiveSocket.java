package myThreads;

import attributes.Attributes;
import elements.Host;
import packet.Packet;

import java.io.IOException;
import java.net.*;

public class ReceiveSocket extends Host implements Runnable {
    volatile DatagramSocket socket;
    volatile Packet pkt;
    volatile DatagramPacket datagram;
    volatile byte[] bytesToRcv;
    volatile Attributes.ElementType elementType;

    public ReceiveSocket(DatagramSocket socket, Attributes.ElementType elementType) {
        this.bytesToRcv = null;
        this.socket = socket;
        this.elementType = elementType;
    }

    @Override
    public void run() {
        try {
            int i = 0;
            while (true) {

                //recebimento de pacotes
                byte[] fileToRecv = new byte[1024];
                DatagramPacket datagram = new DatagramPacket(fileToRecv, fileToRecv.length);

                socket.receive(datagram);
                Packet pcktRcvd = (Packet) convertBytesToObject(datagram.getData());
                setPkt(pcktRcvd);
                setDatagram(datagram);
                setBytesToRcv(fileToRecv);

                //pacote indo de CLIENT para SERVER
                if (elementType == Attributes.ElementType.ROUTER_G) {
                    Thread.sleep(3000);
                    if (!getPkt().getText().equals("arrozzzzzz"))
                        routerAns(getPkt(), getBytesToRcv(), 5003);
                    else if (i == 0) {
                        i++;
                    } else {
                        routerAns(getPkt(), getBytesToRcv(), 5003);
                    }
                    //pacote indo de SERVER para CLIENT
                } else if (elementType == Attributes.ElementType.ROUTER_B) {
                    routerAns(getPkt(), getBytesToRcv(), 5002);
                } else if (elementType == Attributes.ElementType.SERVER) {
                    System.out.print("Recebido esse pacote no servidor: ");
                    System.out.println(pcktRcvd);
                    serverAns(socket, getPkt().getSequenceNum(), datagram.getAddress(), 5001);
                } else {
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void serverAns(DatagramSocket socket, int sequenceNum, InetAddress address, int port) throws IOException {
        byte[] bytesToSend;
        Packet ackPkt = new Packet(sequenceNum, 0);
        bytesToSend = convertObjectToBytes(ackPkt);
        DatagramPacket fileToSend = new DatagramPacket(bytesToSend, bytesToSend.length, address, port);
        socket.send(fileToSend);
    }

    public static void routerAns(Packet pkt, byte[] bytesToRcv, int port) throws IOException {
        System.out.println("Enviando para o client/servidor: " + pkt);
        DatagramSocket sendSocket = new DatagramSocket();
        InetAddress ip = InetAddress.getByName("127.0.0.1"); //localhost
        DatagramPacket sendPacket = new DatagramPacket(bytesToRcv, bytesToRcv.length, ip, port);
        sendSocket.send(sendPacket);
    }

    public void setDatagram(DatagramPacket datagram) {
        this.datagram = datagram;
    }

    public Packet getPkt() {
        return pkt;
    }

    public byte[] getBytesToRcv() {
        return bytesToRcv;
    }

    public void setBytesToRcv(byte[] bytesToRcv) {
        this.bytesToRcv = bytesToRcv;
    }

    public void setPkt(Packet pkt) {
        this.pkt = pkt;
    }


}
