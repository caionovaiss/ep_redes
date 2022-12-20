import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Server extends SocketTCP {
    static int port = 5000;
    static int ack = 0;

    public static Timer timer;

    public static void main(String[] args) throws IOException {
        DatagramSocket dSocket = new DatagramSocket(port);
        System.out.println("Servidor em execução na porta " + port);

        timer = new Timer(2000);

        while (true) {

            //recv client msg
            byte[] fileToRecv = new byte[200];
            DatagramPacket dPacket = new DatagramPacket(fileToRecv, fileToRecv.length);
            dSocket.receive(dPacket); //acho que isso trava o codigo abaixo até receber um pacote

            Packet pckt = (Packet) convertBytesToObject(dPacket.getData());
            System.out.println(pckt);
            boolean put = fillBuffer(pckt);

            if (put)
                ack = pckt.getSequenceNum();

            //send ack to client
            byte[] ackToSend = new byte[100];
            ackToSend = String.valueOf(ack).getBytes();
            InetAddress clientIP = dPacket.getAddress();
            int clientPort = dPacket.getPort();
            DatagramPacket fileToSend = new DatagramPacket(ackToSend, ackToSend.length, clientIP, clientPort);
            dSocket.send(fileToSend);

            emptyBuffer(pckt);

        }
        //dSocket.close();

    }
}


