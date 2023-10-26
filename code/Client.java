import java.io.*; // IO control
import java.net.*; // Networking functions
import java.nio.charset.StandardCharsets; // Char Conversion
import java.security.*; // Security
import java.util.Scanner;

import javax.crypto.*; //crypto

public class Client{
        PublicKey clientPublicKeys;
        PrivateKey clientPrivateKeys;
        PublicKey targetPublicKeys;
        static BufferedReader in;
        static Socket socket;
        int groupNum;

        static Scanner kb = new Scanner(System.in);
        static String host="localhost";
        static int port=1337;
        BufferedReader messages;
    
        public void setTarget(String h, int p){
            host = h;
            port = p;
        }
        
        public void setKeys(int[] keys) throws NoSuchAlgorithmException{
            KeyPairGenerator kpGen=KeyPairGenerator.getInstance("RSA");
            kpGen.initialize(2048);
            KeyPair pair=kpGen.generateKeyPair();

            clientPrivateKeys=pair.getPrivate();
            clientPublicKeys=pair.getPublic();
        }
        public int getGroupNum(){
            return groupNum;
        }

        public PublicKey getPublicKeys(){
            return clientPublicKeys;
        }

        public void setTargetKeys(PublicKey tkeys){
            targetPublicKeys=tkeys;
        }

        public static void connect() throws UnknownHostException, IOException{                //Connect to the server on the serving port
            PrintWriter w = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            kb = new Scanner(System.in);

            socket = new Socket(host, port);
            System.out.println("Groupnumber: ");
            int getGroupNum = kb.nextInt();

            w.println(getGroupNum); // send group number to server for assignment
        }                   
       
        public void sendMessage(String msg) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException{               //Should be checked for in a loop. Message should be encrypted using the targets public keys and sent to server with the clients group number 
            PrintWriter w = new PrintWriter(socket.getOutputStream(), true);
            Cipher encCipher = Cipher.getInstance("RSA");
            encCipher.init(Cipher.ENCRYPT_MODE, targetPublicKeys);
            
            byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
            byte[] encMSGbytes =encCipher.doFinal(msgBytes);
            w.println(encMSGbytes); //sends message
            w.println(groupNum); //sends groupNumber
            w.close();
        }                 
        public void recvMessage(byte[] msg) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException{
            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, clientPrivateKeys);

            byte[] decryptedMessageBytes = decryptCipher.doFinal(msg);
            String decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);

            System.out.println(decryptedMessage);
        }
        public int exitSession(){                  //Used to exit out of the server by each Client.  
            int exitCode;
            try{
                socket.close();
                kb.close();
            }catch(Exception e){
                System.out.println(e);
                exitCode = -1;
            }finally{
                exitCode=0;
            }
            return exitCode;
        }
    public static void main(String[] args) throws UnknownHostException, IOException{
        connect();

        while(true) {
            String newMsg=in.readLine();
            recvMessage(newMsg);
            if(newMsg)
            if(newMsg == "exit");
        }
    }
}