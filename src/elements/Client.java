package elements;

import attributes.Enums;
import myThreads.ClientBufferThread;
import packet.Packet;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Client extends Host {

    public static void main(String args[]) {
        try {
            DatagramSocket socket = new DatagramSocket(5002);
            int count = 1;
            boolean newMsg;
            List<Packet> pktSentList = new ArrayList<>();

            ClientBufferThread clientBufferThread = new ClientBufferThread(socket);
            Thread listen = new Thread(clientBufferThread);
            listen.start();

            Packet pkt = null;

            while (true) {
                if (clientBufferThread.getTypeOfGrowth() != Enums.CongestionControl.STOP) {
                    newMsg = resendPkt(clientBufferThread, pktSentList, socket);
                    int diff = clientBufferThread.getLastByteSent() - clientBufferThread.getLastByteAcked();

                    if (clientBufferThread.getTypeOfGrowth() != Enums.CongestionControl.WAITING_PKT) {
                        if (newMsg) {
                            String msg = getMsg(count - 1);
                            pkt = new Packet(count, msg);
                        }

                        if (clientBufferThread.getCwnd() - diff >= pkt.getLength()) {
                            String msg = getMsg(count - 1);
                            pkt = new Packet(count, msg);
                            sendPkt(pkt, socket);
                            clientBufferThread.setStartTime(System.currentTimeMillis());
                            System.out.println("pacote enviado: " + pkt);
                            pktSentList.add(pkt);

                            clientBufferThread.increaseLastByteSent(pkt.getLength());
                            clientBufferThread.addSequenceNumToList(pkt.getSequenceNum());

                            count++;
                        }
                    }
                } else {
                    long timeElapsed = System.currentTimeMillis();
                    if (timeElapsed - clientBufferThread.getStartRwndEmptyTime() >= 3000) {
                        clientBufferThread.setTypeOfGrowth(clientBufferThread.getLastTypeOfGrowth());
                        Packet pktToCheckRwnd = new Packet(0, "check rwnd");
                        sendPkt(pktToCheckRwnd, socket);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}