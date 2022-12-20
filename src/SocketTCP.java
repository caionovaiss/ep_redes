import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

abstract class SocketTCP {
    private static int MAX_BUFFER_SIZE = 5;
    private static Packet[] buffer = new Packet[MAX_BUFFER_SIZE];
    protected boolean isCount;

    public static void sendFile(DatagramSocket socket, Packet pckt) throws IOException, InterruptedException {
        byte[] fileToSend = new byte[200];
        fileToSend = convertObjectToBytes(pckt);
        InetAddress ip = InetAddress.getByName("127.0.0.1");
        DatagramPacket dPacket = new DatagramPacket(fileToSend, fileToSend.length, ip, 5000);
        socket.send(dPacket);
    }

    //receive data
    public static int rcvFile(DatagramSocket socket, Packet pckt, int count) throws IOException {
        byte[] fileToRecv = new byte[100];
        DatagramPacket packetToRecv = new DatagramPacket(fileToRecv, fileToRecv.length);
        socket.receive(packetToRecv);
        String msgRecvd = new String(packetToRecv.getData());
        msgRecvd = cleanString(msgRecvd);
        if (count == Integer.parseInt(msgRecvd)) {
            System.out.println("Ack recebido: " + msgRecvd);
            count++;
        }
        return count;
    }

    // Convert Packet to byte[]
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

    public static String cleanString(String dirtyStr) {
        String strClean = "";
        for (int i = 0; i < dirtyStr.length(); i++) {
            if (Character.isDigit(dirtyStr.charAt(i)))
                strClean += dirtyStr.charAt(i);
        }

        return strClean;
    }

    public static boolean fillBuffer(Packet pckt) {
        boolean put = false;
        for (int i = 0; i < MAX_BUFFER_SIZE; i++) {
            if (buffer[i] == null) {
                buffer[i] = pckt;
                put = true;
            }
        }
        return put;
    }

    public static void emptyBuffer(Packet pckt, Timer timer) {
        printBuffer(buffer);

        if(!timer.isRunning())
        {
            timer.start();
            return;
        }

        if(!timer.isFinished())
        {
            return;
        }

        for (int i = 0; i < MAX_BUFFER_SIZE; i++) {
            if (buffer[i] != null) {
                buffer[i] = null;
            }
        }
        //buffer[i % MAX_BUFFER_SIZE - 1] = null;
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

    public static void printBuffer(Packet[] buffer) {
        System.out.println("----------");
        for (Packet packet : buffer) System.out.print(packet + " ");
        System.out.println("----------");
    }

    public static int getMaxBufferSize() {
        return MAX_BUFFER_SIZE;
    }

    public static void setMaxBufferSize(int maxBufferSize) {
        MAX_BUFFER_SIZE = maxBufferSize;
    }

    public static Packet[] getBuffer() {
        return buffer;
    }

    public static void setBuffer(Packet[] buffer) {
        SocketTCP.buffer = buffer;
    }

}
