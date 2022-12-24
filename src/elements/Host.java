package elements;

import packet.Packet;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public abstract class Host {

    public static void sendPkt(Packet pckt, DatagramSocket socket, int port) throws IOException {
        byte[] fileToSend;
        fileToSend = convertObjectToBytes(pckt);
        InetAddress ip = InetAddress.getByName("127.0.0.1");
        DatagramPacket dPacket = new DatagramPacket(fileToSend, fileToSend.length, ip, port);

        System.out.println("Tamanho do pacote de envio: " + fileToSend.length);
        socket.send(dPacket);
    }

    public static void rcvPkt(DatagramSocket socket) throws IOException {
        System.out.println("----------------------------RECEBENDO RESPOSTA----------------------------");
        byte[] fileToRecv = new byte[200];
        DatagramPacket packetToRecv = new DatagramPacket(fileToRecv, fileToRecv.length);

        socket.receive(packetToRecv);
        Packet pcktRcvd = (Packet) convertBytesToObject(packetToRecv.getData());

        System.out.println("Ack recebido: " + pcktRcvd.getAck());
    }

    public static String getMsg() {
        System.out.println("Digite uma mensagem");
        Scanner sc = new Scanner(System.in);
        String msg = sc.nextLine();
        return msg;
    }

    // Convert packet.Packet to byte[]
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
