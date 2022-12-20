import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Scanner;

public class Client extends SocketTCP {
    static Packet[] buffer = new Packet[5];

    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        int count = 1;

        socket.setSoTimeout(1000);
        while (true) {
            //get user message
            System.out.println("Digite uma mensagem");
            Scanner sc = new Scanner(System.in);
            String msg = sc.nextLine();
            Packet pckt = new Packet(msg.length(), count, msg);
            //exit connection
            if (msg.equals("exit"))
                break;

            try {
                sendFile(socket, pckt);
                //receive packet (ack) from server
                count = rcvFile(socket, pckt, count);
            } catch (SocketTimeoutException socketTOE) {
                System.out.println("Deu timeout, acho que vou reenviar");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        socket.close();
    }


    //send data

}
