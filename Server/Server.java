import java.io.*;
import java.net.*;
import java.nio.*;

class Server
{
    public static void main(String args[]) throws IOException
    {
        int PORT=6789;
        DatagramSocket SERVER_SOCKET = new DatagramSocket(PORT);
        byte[] DATA = new byte[1024];
        DatagramPacket PACKET = new DatagramPacket(DATA, DATA.length);
        
        long AVERAGE_TIME=0;
        int CORRECT_FILES=0;
        String FILE_PATH = System.getProperty("user.dir") + "\\OUTPUT";
        File DIRECTORY = new File(FILE_PATH); if(!DIRECTORY.exists()) DIRECTORY.mkdirs();
        
        System.out.println("I am ready for any client side request.");
        
        SERVER_SOCKET.receive(PACKET);
        String FILE_NAME = new String(PACKET.getData(), 0, PACKET.getLength());
        
        SERVER_SOCKET.receive(PACKET);
        int NO_OF_PACKETS = ByteBuffer.wrap(PACKET.getData()).order(ByteOrder.BIG_ENDIAN).getInt();
        
        for(int i = 0; i < 100; i++)
        {
            String OUTPUT_FILE_NAME = "OUTPUT\\" + FILE_NAME + " (" + (i + 1) + ").txt";
            System.out.println("I am starting receiving file \"" + FILE_NAME + "\" for instance " + (i + 1) + ".");
            
            FileOutputStream FILE_OUTPUT_STREAM = new FileOutputStream(OUTPUT_FILE_NAME);
            
            long TIME = System.currentTimeMillis();
            
            for(int l = 0; l < NO_OF_PACKETS; l++)
            {
                SERVER_SOCKET.receive(PACKET);
                FILE_OUTPUT_STREAM.write(PACKET.getData(), 0, PACKET.getLength());
                FILE_OUTPUT_STREAM.flush(); 
                if(l==(NO_OF_PACKETS-1)) FILE_OUTPUT_STREAM.close();
            }
            
            TIME -= System.currentTimeMillis();
            
            System.out.println("I am finishing receiving file \"" + FILE_NAME + "\" for instance " + (i + 1));
            System.out.println("The time used in millisecond to receive \"" + FILE_NAME + "\" for instance "+ (i + 1) + " is: " + Math.abs(TIME) + "ms.");
            
            AVERAGE_TIME -= TIME;
        }
        
        AVERAGE_TIME/=100;
        System.out.println("The average time to receive file \"" + FILE_NAME + "\" in millisecond is: " + AVERAGE_TIME + "ms.");
        
        for(int i = 0; i < 100; i++)
        {
            BufferedInputStream FILE_1 = new BufferedInputStream(new FileInputStream("Test1 - large.txt"));
            BufferedInputStream FILE_2 = new BufferedInputStream(new FileInputStream("OUTPUT\\" + FILE_NAME + " (" + (i + 1) + ").txt"));
            
            int BYTE_1 = 0;
            int BYTE_2 = 0;
            
            while (BYTE_1 != -1 && BYTE_2 != -1)
            {
                if (BYTE_1 != BYTE_2)
                {
                    BYTE_1 = 0;
                    BYTE_2 = 1;
                    break;
                }
                BYTE_1 = FILE_1.read();
                BYTE_2 = FILE_2.read();
            }
            if (BYTE_1 != BYTE_2) continue;
            else CORRECT_FILES++;
            
            FILE_1.close();
            FILE_2.close();
        }
        
        System.out.println(CORRECT_FILES + " out of 100 files are identical to the original file.");
        
        System.out.println("I am done.");
    }
}