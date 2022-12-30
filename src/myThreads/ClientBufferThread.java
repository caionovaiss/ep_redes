package myThreads;

import attributes.Enums;
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
    private volatile int cwnd; //janela de congestionamento
    volatile Enums.CongestionControl typeOfGrowth;
    volatile Queue<Integer> seqNumSentList;
    volatile int ackReceived = 0;
    volatile List<Integer> lastAcksReceived;
    volatile int lastByteSent = 0;
    volatile int lastByteAcked = 0;
    volatile int MSS = 10;
    volatile int sstresh;
    volatile int ackExpected;
    volatile boolean resendingPtk;
    volatile int pktToResendSeqNum = 0;
    volatile Queue<Integer> pktsResended;
    volatile Enums.CongestionControl lastTypeOfGrowth;
    volatile long startRwndEmptyTime;
    volatile long startTime;
    volatile long rtt;

    public ClientBufferThread(DatagramSocket socket) {
        this.seqNumSentList = new LinkedList<>();
        this.lastAcksReceived = new ArrayList<>();
        this.pktsResended = new LinkedList<>();
        this.bytesToRcv = null;
        this.socket = socket;
        this.typeOfGrowth = Enums.CongestionControl.SLOW_START;
        this.cwnd = MSS;
        this.sstresh = MSS * 8;
        this.resendingPtk = false;
        this.lastTypeOfGrowth = this.typeOfGrowth;
    }

    @Override
    public void run() {
        try {
            while (true) {
                //evitar que acks ja recebidos sejam considerados.
                //Isso significa que NÃO estamos usando GBN, já que não
                //reenviamos os pacotes com acks ja recebidos
                if (lastAcksReceived.contains(seqNumSentList.peek()))
                    seqNumSentList.poll();

                // socket.setSoTimeout(10000);
                byte[] fileToRecv = new byte[1024];
                DatagramPacket datagram = new DatagramPacket(fileToRecv, fileToRecv.length);
                socket.receive(datagram);
                setRtt(System.currentTimeMillis() - getStartTime());
                printRate();


                Packet pktReceived = (Packet) convertBytesToObject(datagram.getData());

                checkRwnd(pktReceived);

                ackReceived = pktReceived.getAck();
                addAckReceived(ackReceived);

                if (!seqNumSentList.isEmpty()) {
                    System.out.println("lista de num seq: " + seqNumSentList);
                    ackExpected = seqNumSentList.peek();

                    System.out.println("ack expected: " + ackExpected);
                    System.out.println("ack received: " + ackReceived);
                    System.out.println("lista de ACKS receiveds: " + lastAcksReceived);
                }

                checkIfAckReceivedIsEqualExpected(pktReceived);
                checkTripleAck();
                checkIfHitSstresh();

                System.out.println("--------------------------------");
            }
        } catch (SocketException e) {
            System.out.println("Timeout");
            setTypeOfGrowth(Enums.CongestionControl.SLOW_START);
            this.cwnd = MSS;
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printRate() {
        double rate = (double) getCwnd() / ((double) getRtt());
        System.out.println("rate: " + rate * 1000 + "bytes/sec");
    }

    private void checkRwnd(Packet pktReceived) {
        if (pktReceived.getRwnd() == 0 && getTypeOfGrowth() != Enums.CongestionControl.STOP) {
            startRwndEmptyTime = System.currentTimeMillis();
            setLastTypeOfGrowth(getTypeOfGrowth());
            setTypeOfGrowth(Enums.CongestionControl.STOP);
        }
    }

    private void checkIfHitSstresh() {
        if (getCwnd() == getSstresh())
            setTypeOfGrowth(Enums.CongestionControl.CONGESTION_AVOIDANCE);
    }

    private void checkTripleAck() {
        //checar 3 acks duplicados
        if (getLastAcksReceived().size() >= 3
                && ackReceived == getLastAcksReceived().get(getLastAcksReceived().size() - 1)
                && ackReceived == getLastAcksReceived().get(getLastAcksReceived().size() - 2)) {
            setSstresh(this.cwnd);
            this.cwnd /= 2;
            setTypeOfGrowth(Enums.CongestionControl.CONGESTION_AVOIDANCE);
        }
    }

    private void checkIfAckReceivedIsEqualExpected(Packet pktReceived) {
        //verificar se não recebemos o ack desejado para fazer reenvio
        if (ackReceived != ackExpected && getTypeOfGrowth() != Enums.CongestionControl.WAITING_PKT) {
            resendPkt(ackExpected);
        } else if (ackReceived != ackExpected
                && seqNumSentList.contains(ackReceived)
                && getTypeOfGrowth() == Enums.CongestionControl.WAITING_PKT) {
            seqNumSentList.remove(ackReceived);
        } else {
            seqNumSentList.poll();
            setTypeOfGrowth(this.lastTypeOfGrowth);
            //atualizando lastByteAcked
            increaseLastByteAcked(pktReceived.getLength());
            increaseCwnd();
        }
    }

    public void increaseCwnd() {
        if (this.typeOfGrowth == Enums.CongestionControl.SLOW_START) {
            this.cwnd += this.MSS;
            //System.out.println("tamanho da janela: " + this.cwnd);
        } else if (this.typeOfGrowth == Enums.CongestionControl.CONGESTION_AVOIDANCE) {
            this.cwnd += (this.MSS * (this.MSS / this.cwnd));
            //System.out.println("tamanho da janela: " + this.cwnd);
        }
    }

    public void increaseLastByteAcked(int length) {
        setLastByteAcked(getLastByteAcked() + length);
    }

    public void increaseLastByteSent(int length) {
        setLastByteSent(getLastByteSent() + length);
    }

    public void resendPkt(int num) {
        this.pktToResendSeqNum = num;
        this.resendingPtk = true;
        this.lastTypeOfGrowth = this.typeOfGrowth;
        this.setTypeOfGrowth(Enums.CongestionControl.WAITING_PKT);
        this.pktsResended.add(num);
    }

    public void decreaseCwnd() {
        this.cwnd -= 1;
    }

    public void addSequenceNumToList(int num) {
        this.seqNumSentList.add(num);
    }

    public void setTypeOfGrowth(Enums.CongestionControl typeOfGrowth) {
        this.typeOfGrowth = typeOfGrowth;
    }

    public void addAckReceived(int ack) {
        this.lastAcksReceived.add(ack);
    }

    public int getLastByteSent() {
        return lastByteSent;
    }

    public void setLastByteSent(int lastByteSent) {
        this.lastByteSent = lastByteSent;
    }

    public int getLastByteAcked() {
        return lastByteAcked;
    }

    public void setLastByteAcked(int lastByteAcked) {
        this.lastByteAcked = lastByteAcked;
    }

    public int getSstresh() {
        return sstresh;
    }

    public void setSstresh(int sstresh) {
        this.sstresh = sstresh;
    }

    public List<Integer> getLastAcksReceived() {
        return lastAcksReceived;
    }

    public boolean isResendingPtk() {
        return resendingPtk;
    }

    public int getPktToResendSeqNum() {
        return pktToResendSeqNum;
    }

    public int getCwnd() {
        return cwnd;
    }

    public void setResendingPtk(boolean resendingPtk) {
        this.resendingPtk = resendingPtk;
    }

    public long getStartRwndEmptyTime() {
        return startRwndEmptyTime;
    }

    public Enums.CongestionControl getLastTypeOfGrowth() {
        return lastTypeOfGrowth;
    }

    public void setLastTypeOfGrowth(Enums.CongestionControl lastTypeOfGrowth) {
        this.lastTypeOfGrowth = lastTypeOfGrowth;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getRtt() {
        return rtt;
    }

    public void setRtt(long rtt) {
        this.rtt = rtt;
    }

    public Enums.CongestionControl getTypeOfGrowth() {
        return typeOfGrowth;
    }
}