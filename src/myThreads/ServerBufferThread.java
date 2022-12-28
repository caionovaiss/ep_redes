package myThreads;

import packet.Packet;

import java.util.Queue;

public class ServerBufferThread implements Runnable {
    private volatile int buffer;
    private volatile Queue<Packet> queue;
    private volatile int lastByteRead = 0;
    private volatile int lastByteRcvd = 0;
    private volatile int rwnd;
    private volatile int seqNumExpected = 1;

    public ServerBufferThread(int buffer, Queue<Packet> queue) {
        this.buffer = buffer;
        this.queue = queue;
        this.rwnd = buffer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                getRwnd();
                if (!queue.isEmpty()) {
                    Thread.sleep(5000);
                    Packet pktRead = queue.remove();
                    setLastByteRead(pktRead.getLength());
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int getBuffer() {
        return buffer;
    }

    public Queue<Packet> getQueue() {
        return queue;
    }

    public void setQueue(Queue<Packet> queue) {
        this.queue = queue;
    }

    public int getLastByteRead() {
        return lastByteRead;
    }

    public void setLastByteRead(int lastByteRead) {
        this.lastByteRead += lastByteRead;
    }

    public int getLastByteRcvd() {
        return lastByteRcvd;
    }

    public void setLastByteRcvd(int lastByteRcvd) {
        this.lastByteRcvd += lastByteRcvd;
    }

    public int getRwnd() {
        this.rwnd = getBuffer() - (getLastByteRcvd() - getLastByteRead());
        return this.rwnd;
    }

    public int getSeqNumExpected() {
        return seqNumExpected;
    }

    public void setSeqNumExpected(int seqNumExpected) {
        this.seqNumExpected = seqNumExpected;
    }

    public void setRwnd(int rwnd) {
        this.rwnd = rwnd;
    }

    public void updateSeqNumExpected() {
        this.seqNumExpected += 1;
    }

}