package myThreads;

import attributes.Attributes;
import elements.Host;
import packet.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ClientBufferThread extends Host implements Runnable {
    volatile DatagramSocket socket;
    volatile byte[] bytesToRcv;
    volatile Packet pkt;
    volatile DatagramPacket datagram;
    private volatile int cwnd; //janela de congestionamento
    volatile Attributes.CongestionControl typeOfGrowth;
    volatile int i = 1;
    volatile Queue<Integer> pktSentList;
    volatile int ackReceived = 0;
    volatile List<Integer> lastAcksReceived;
    volatile int sstresh = 8;
    volatile int currentWindow = 1;

    public ClientBufferThread(DatagramSocket socket) {
        this.pktSentList = new LinkedList<>();
        this.lastAcksReceived = new ArrayList<>();
        this.bytesToRcv = null;
        this.socket = socket;
        this.typeOfGrowth = Attributes.CongestionControl.PARTIDA_LENTA;
        this.cwnd = 1;
    }

    @Override
    public void run() {
        try {
            socket.setSoTimeout(100);
            while (true) {

                byte[] fileToRecv = new byte[1024];
                DatagramPacket datagram = new DatagramPacket(fileToRecv, fileToRecv.length);

                socket.receive(datagram);
                Packet pktReceived = (Packet) convertBytesToObject(datagram.getData());
                ackReceived = pktReceived.getAck();
                addAckReceived(ackReceived);

                //verificando existencia de 3 acks repetidos recebidos
                int listSize = getLastAcksReceived().size();
                if (listSize >= 3 && getLastAcksReceived().get(listSize - 1) == getLastAcksReceived().get(listSize - 2) && getLastAcksReceived().get(listSize - 2) == getLastAcksReceived().get(listSize - 3)) {
                    this.cwnd = this.cwnd / 2;
                }


                if (!getPktSentList().isEmpty()) {
                    if (ackReceived == getPktSentList().peek()) {
                        setPkt(pktReceived);
                        setDatagram(datagram);
                        setBytesToRcv(fileToRecv);

                        getPktSentList().poll();

                    } else if (ackReceived != getPktSentList().peek()) {
                        resendPtk(getPktSentList().peek());
                    }
                }

                //aumentar janela pois recebeu um ack
                if (getPktSentList().isEmpty())
                    increaseCwnd();

                if (getCwnd() >= sstresh) {
                    setTypeOfGrowth(Attributes.CongestionControl.CRESCIMENTO_LINEAR);
                    this.currentWindow = getCwnd();
                }

            }
        } catch (SocketException e) {
            System.out.println("Timeout");
            setTypeOfGrowth(Attributes.CongestionControl.PARTIDA_LENTA);
            this.cwnd = 1;
            this.i = 1;
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void increaseCwnd() {
        if (this.typeOfGrowth == Attributes.CongestionControl.PARTIDA_LENTA) {
            this.cwnd = (int) Math.pow(2, this.i);
            System.out.println(this.cwnd);
            this.i++;
        } else {
            this.cwnd = this.currentWindow + 1;
            System.out.println(this.cwnd);
        }
    }

    public void decreaseCwnd() {
        this.cwnd -= 1;
    }

    public void addSequenceNumToList(int num) {
        this.pktSentList.add(num);
    }

    public void resendPtk(int num) {

    }

    public void setBytesToRcv(byte[] bytesToRcv) {
        this.bytesToRcv = bytesToRcv;
    }

    public void setPkt(Packet pkt) {
        this.pkt = pkt;
    }

    public void setDatagram(DatagramPacket datagram) {
        this.datagram = datagram;
    }

    public int getCwnd() {
        return cwnd;
    }

    public Queue<Integer> getPktSentList() {
        return pktSentList;
    }

    public void setTypeOfGrowth(Attributes.CongestionControl typeOfGrowth) {
        this.typeOfGrowth = typeOfGrowth;
    }

    public List<Integer> getLastAcksReceived() {
        return lastAcksReceived;
    }

    public void addAckReceived(int ack) {
        this.lastAcksReceived.add(ack);
    }

}

