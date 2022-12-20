import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server {
    static int port = 5000;

    public static void main(String[] args) throws IOException, InterruptedException {
        DatagramSocket dSocket = new DatagramSocket(port);
        System.out.println("Servidor em execução na porta " + port);
        int ackRcvd = 0;

        while (true) {
            //recv client msg
            byte[] fileToRecv = new byte[200];
            DatagramPacket dPacket = new DatagramPacket(fileToRecv, fileToRecv.length);
            dSocket.receive(dPacket);

            Packet pckt = (Packet) convertBytesToObject(dPacket.getData());
            System.out.println(pckt);

            //send ack to client
            byte[] ackToSend = new byte[100];
            String ack = "" + pckt.getSequenceNum();
            ackToSend = ack.getBytes();
            InetAddress clientIP = dPacket.getAddress();
            int clientPort = dPacket.getPort();
            DatagramPacket fileToSend = new DatagramPacket(ackToSend, ackToSend.length, clientIP, clientPort);
            dSocket.send(fileToSend);
        }
        //dSocket.close();


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
