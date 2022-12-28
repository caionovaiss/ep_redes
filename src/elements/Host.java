package elements;

import insercoes.CreatePackets;
import packet.Packet;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public abstract class Host {

    public static String getMsg(int i) {
        String[] words = CreatePackets.getWords();
        String msg = words[i];
//        System.out.println("Digite uma mensagem");
//        Scanner sc = new Scanner(System.in);
//        String msg = sc.nextLine();
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
