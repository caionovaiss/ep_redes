package elements;

import insercoes.MyData;
import myThreads.ClientBufferThread;
import packet.Packet;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public abstract class Host {

    public static String getMsg(int i) {
        String[] words = MyData.getComidas();
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

    public static void sendPkt(Packet pkt, DatagramSocket socket) throws IOException {
        byte[] fileToSend;
        fileToSend = convertObjectToBytes(pkt);
        InetAddress ip = InetAddress.getByName("127.0.0.1");
        DatagramPacket dPacket = new DatagramPacket(fileToSend, fileToSend.length, ip, 5000);

        socket.send(dPacket);
    }

    public static boolean resendPkt(ClientBufferThread clientBufferThread, List<Packet> pktSentList, DatagramSocket socket) throws IOException {
        //reenviar o pacote
        if (clientBufferThread.isResendingPtk()) {
            for (Packet p : pktSentList) {
                if (p.getSequenceNum() == clientBufferThread.getPktToResendSeqNum()) {
                    System.out.println("pacote para ser reenviado: " + p);
                    sendPkt(p, socket);

                    clientBufferThread.setResendingPtk(false);
                }
            }
            return false;
        }
        return true;
    }

}
