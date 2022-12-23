package elements;

import packet.Packet;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    public static void main(String args[]) {
        try {
            DatagramSocket socket = new DatagramSocket();
            int count = 1;

            while (true) {
                String msg = getMsg();
                Packet pckt = new Packet(msg.length(), count, msg);

                byte[] fileToSend;
                fileToSend = convertObjectToBytes(pckt);
                InetAddress ip = InetAddress.getByName("127.0.0.1");
                DatagramPacket dPacket = new DatagramPacket(fileToSend, fileToSend.length, ip, 5000);

                System.out.println("Tamanho do pacote de envio: " + fileToSend.length);
                socket.send(dPacket);

//                System.out.println("----------------------------RECEBENDO RESPOSTA----------------------------");
//                byte[] fileToRecv = new byte[200];
//                DatagramPacket packetToRecv = new DatagramPacket(fileToRecv, fileToRecv.length);
//
//                socket.receive(packetToRecv);
//                Packet pcktRcvd = (Packet) convertBytesToObject(packetToRecv.getData());
//
//                System.out.println("Tamanho da JANELA: " + pcktRcvd.getRwnd());
//                System.out.println("Ack recebido: " + pcktRcvd.getAck());
                count++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
