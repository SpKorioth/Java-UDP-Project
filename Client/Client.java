import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;

class Client
{
    public static void main(String args[]) throws Exception
    {
        int PORT = 6789;
        String FILE_NAME = "Test1 - large";
        String FILE_PATH = System.getProperty("user.dir") + "\\"+ FILE_NAME + ".txt";
        final int MAX_SIZE = 1024;
    
        DatagramSocket CLIENT_SOCKET = new DatagramSocket();
        InetAddress ADDRESS = InetAddress.getLocalHost();
        byte[] SEND_DATA = new byte[MAX_SIZE]; //Array to hold data.
        long AVERAGE_TIME=0;
        //File setup.
        File FILE = new File(FILE_PATH);
        int TOTAL_LENGTH = (int) FILE.length(); //Determining length to calculate how many packets needed for file
        int NO_OF_PACKETS = TOTAL_LENGTH / MAX_SIZE; //Calculating number of packets needed for file of full size.
        int OFFSET = NO_OF_PACKETS * MAX_SIZE; //Calculate offset for last packet.
        int FINAL_PACKET_LENGTH = TOTAL_LENGTH - OFFSET;
        byte[] FINAL_PACKET = new byte[FINAL_PACKET_LENGTH - 1]; //Creating array to hold last packet.
            
        byte[] SIZE_MESSAGE = ByteBuffer.allocate(4).putInt(NO_OF_PACKETS+1).array();
        
        System.out.println("I am sending to server side: " + ADDRESS);
        
        byte[] FILE_DATA = FILE_NAME.getBytes();
        CLIENT_SOCKET.send(new DatagramPacket(FILE_DATA, FILE_DATA.length, ADDRESS, PORT));
        
        CLIENT_SOCKET.send(new DatagramPacket(SIZE_MESSAGE, SIZE_MESSAGE.length, ADDRESS, PORT));
        
        for(int a = 0; a < 100; a++)
        {
            int COUNT = NO_OF_PACKETS;
            FileInputStream FILE_INPUT_STREAM = new FileInputStream(FILE);
            
            System.out.println("I am sending file \"" + FILE_NAME + "\" for attempt " + (a + 1) + ".");
            long TIME = System.currentTimeMillis();
            
            while((FILE_INPUT_STREAM.read(SEND_DATA)) != -1 )
            { 
                if(COUNT <= 0) break;
                
                DatagramPacket SEND_PACKET = new DatagramPacket(SEND_DATA, SEND_DATA.length, ADDRESS, PORT);
                CLIENT_SOCKET.send(SEND_PACKET);
                
                COUNT--;
                
                Thread.sleep(0,1); //One nanosecond pause added in for time for writing data to file.
            }
            //Send final packet.
    
            FINAL_PACKET = Arrays.copyOf(SEND_DATA, FINAL_PACKET_LENGTH);
            
            DatagramPacket SEND_PACKET1 = new DatagramPacket(FINAL_PACKET, FINAL_PACKET.length, ADDRESS, PORT);
            CLIENT_SOCKET.send(SEND_PACKET1);
            
            TIME-=System.currentTimeMillis();
            
            System.out.println("I am finished sending file \"" + FILE_NAME + "\" for attempt " + (a + 1) + ".");
            System.out.println("The time used in millisecond to recieve file \"" + FILE_NAME + "\" is: " + Math.abs(TIME) + "ms.");
            
            AVERAGE_TIME -= TIME;
            FILE_INPUT_STREAM.close();
            
            Thread.sleep(0,1);
        }
        
        AVERAGE_TIME /= 100;
        
        System.out.println("The average time to send file \"" + FILE_NAME + "\" is: " + AVERAGE_TIME + "ms.");
        System.out.println("I am done.");
    }
}