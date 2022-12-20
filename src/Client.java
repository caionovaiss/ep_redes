import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        int count = 1;

        socket.setSoTimeout(3000);
        while (true) {
            //get user message
            System.out.println("Digite uma mensagem");
            Scanner sc = new Scanner(System.in);
            String msg = sc.nextLine();
            Packet pckt = new Packet(msg.length(), count, msg);
            //exit connection
            if (msg.equals("exit"))
                break;

            try {
                sendFile(socket, pckt);
                //receive packet (ack) from server
                count = rcvFile(socket, pckt, count);
            } catch (SocketTimeoutException socketTOE) {
                System.out.println("Deu timeout, acho que vou reenviar");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        socket.close();
    }


    //send data
    public static void sendFile(DatagramSocket socket, Packet pckt) throws IOException, InterruptedException {
        byte[] fileToSend = new byte[200];
        fileToSend = convertObjectToBytes(pckt);
        InetAddress ip = InetAddress.getByName("127.0.0.1");
        DatagramPacket dPacket = new DatagramPacket(fileToSend, fileToSend.length, ip, 5000);
        System.out.println(dPacket.getLength());
        System.out.println(fileToSend.length);
        socket.send(dPacket);
    }

    //receive data
    public static int rcvFile(DatagramSocket socket, Packet pckt, int count) throws IOException {
        byte[] fileToRecv = new byte[100];
        DatagramPacket packetToRecv = new DatagramPacket(fileToRecv, fileToRecv.length);
        socket.receive(packetToRecv);
        String msgRecvd = new String(packetToRecv.getData());
        msgRecvd = cleanString(msgRecvd);
        if (count == Integer.valueOf(msgRecvd)) {
            System.out.println("Ack recebido: " + msgRecvd);
            count++;
        }
        return count;
    }

    // Convert Packet to byte[]
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

    public static String cleanString(String dirtyStr) {
        String strClean = "";
        for (int i = 0; i < dirtyStr.length(); i++) {
            if (Character.isDigit(dirtyStr.charAt(i)))
                strClean += dirtyStr.charAt(i);
        }

        return strClean;
    }

}
