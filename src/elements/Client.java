package elements;

import myThreads.ClientBufferThread;
import packet.Packet;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Client extends Host {

    public static void main(String args[]) {
        try {
            DatagramSocket socket = new DatagramSocket(5002);
            int count = 1;
            boolean newMsg = true;
            List<Packet> pktSentList = new ArrayList<>();

            ClientBufferThread clientBufferThread = new ClientBufferThread(socket);
            Thread listen = new Thread(clientBufferThread);
            listen.start();

            Packet pkt = null;

            while (true) {

                //reenviar o pacote
                if (clientBufferThread.isResendingPtk()) {
                    for (Packet p : pktSentList) {
                        if (p.getSequenceNum() == clientBufferThread.getPktToResendSeqNum()) {
                            System.out.println("pacote para ser reenviado: " + p);
                            sendPkt(p, socket);

                            clientBufferThread.setResendingPtk(false);
                        }
                    }
                    newMsg = false;
                }

                int diff = clientBufferThread.getLastByteSent() - clientBufferThread.getLastByteAcked();

                if (newMsg) {
                    String msg = getMsg(count - 1);
                    pkt = new Packet(count, msg);
                    newMsg = false;
                }

                if (clientBufferThread.getCwnd() - diff >= pkt.getLength()) {

                    String msg = getMsg(count - 1);
                    pkt = new Packet(count, msg);
                    sendPkt(pkt, socket);
                    System.out.println("pacote enviado: " + pkt);
                    pktSentList.add(pkt);

                    clientBufferThread.increaseLastByteSent(pkt.getLength());
                    clientBufferThread.addSequenceNumToList(pkt.getSequenceNum());

                    newMsg = true;
                    count++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}