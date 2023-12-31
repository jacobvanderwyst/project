import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;


public class Client{
        private Socket socket;
        private BufferedReader reader;
        private BufferedWriter writer;
        private Scanner kb;
        private String userAgroup;
        private Crypto crypto;
        public IvParameterSpec ivspec;

        public Client(Socket socket, String userAgroup) throws NoSuchAlgorithmException, InvalidKeySpecException{
            try{
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.kb=new Scanner(System.in);
            this.userAgroup=userAgroup;

            //System.out.println(this.userAgroup);
            this.crypto=new Crypto(256, "networking", "salty");
            this.ivspec=crypto.getIvspecFile();
            }catch(IOException e){
                disconnect(socket, reader, writer);
            }
        }                 
        
        public void sendMessage(){ 
            try{
                writer.write(userAgroup); // send username and groupnumber to server
                writer.newLine();
                writer.flush();

                System.out.println(userAgroup);
                while(socket.isConnected()){
                    System.out.print(this.userAgroup+": ");
                    String newMSG=kb.nextLine();
                    crypto.setInput(this.userAgroup+": "+newMSG);
                    writer.write(crypto.encrypt("AES/CBC/PKCS5Padding"));
                    writer.newLine();
                    writer.flush();
                }
            }catch(IOException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e){
                disconnect(socket, reader, writer);
            } 
        }                 
        
        public void listenMSG(){
            new Thread(new Runnable() {
                @Override
                public void run(){
                    String msgGroup;

                    while(socket.isConnected()){
                        try{
                            crypto.setInput(reader.readLine());
                            msgGroup=crypto.decrypt("AES/CBC/PKCS5Padding", ivspec);
                            System.out.print("\n"+msgGroup+"\n"+userAgroup+": ");
                        }catch(IOException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e){
                            disconnect(socket, reader, writer);
                            break;
                        }
                    }
                }
            }).start();
        }

        public void disconnect(Socket socket,BufferedReader reader2,BufferedWriter writer2){ // Used to exit out of the server by each Client.  
            try{
                if(reader2!=null){
                    reader2.close();
                }
                if(writer2!=null){
                    writer2.close();
                }
                if(socket!=null){
                    socket.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    public static void main(String[] args) throws UnknownHostException, IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        Scanner kb=new Scanner(System.in);
        System.out.print("Enter username and group number to connect to server\n\"User groupNum\": "); // username and group number will be split
        String userAgroup=kb.nextLine();
        System.out.println();
        
        Socket socket=new Socket("localhost", 8008);
        Client client=new Client(socket, userAgroup);
        client.listenMSG(); // start the listener thread
        client.sendMessage(); // listen for messages to send
        

        
    }
}