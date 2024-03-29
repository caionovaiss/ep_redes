package packet;

import java.io.Serializable;

public class Packet implements Serializable {
    private int length;
    private int sequenceNum;
    private String text;
    private int rwnd;
    private int ack;

    @Override
    public String toString() {
        return "Packet{" +
                "length=" + length +
                ", sequenceNum=" + sequenceNum +
                ", text='" + text + '\'' +
                ", rwnd=" + rwnd +
                ", ack=" + ack +
                '}';
    }

    public Packet(int sequenceNum, String text) {
        this.sequenceNum = sequenceNum;
        this.text = text;
        this.rwnd = 0;
        this.length = text.length();
    }

    public Packet(int ack, int rwnd) {
        this.rwnd = rwnd;
        this.ack = ack;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(int sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRwnd() {
        return rwnd;
    }

    public void setRwnd(int rwnd) {
        this.rwnd = rwnd;
    }

    public int getAck() {
        return ack;
    }

    public void setAck(int ack) {
        this.ack = ack;
    }
}
