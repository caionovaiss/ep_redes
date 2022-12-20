import java.io.Serializable;

public class Packet implements Serializable {
    private int length;
    private int sequenceNum;
    private String text;

    @Override
    public String toString() {
        return "Packet{" +
                "length=" + length +
                ", sequenceNum=" + sequenceNum +
                ", text='" + text + '\'' +
                '}';
    }

    public Packet(int length, int sequenceNum, String text) {
        this.length = length;
        this.sequenceNum = sequenceNum;
        this.text = text;
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


}
