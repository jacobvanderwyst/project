import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;
import javax.crypto.*;


public class Client{
        private Socket socket;
        private BufferedReader reader;
        private BufferedWriter writer;
        private Scanner kb;
        private String userAgroup;

        private PrivateKey pikey;
        private PublicKey pukey;
        private EncMSG encMan;
        private PublicKey serverPublicKey;

        public Client(Socket socket, String userAgroup) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException{
            try{
                this.socket = socket;
                this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                this.kb=new Scanner(System.in);
                this.userAgroup=userAgroup;

                KeyExchange cliExc = new KeyExchange("DSA", "client");
                KeyExchange servExchange=new KeyExchange("DSA", "server");
                cliExc.setPrivate();
                cliExc.setPublic();
                
                this.pikey = cliExc.getPrivateKey();
                this.pukey = cliExc.getPublicKey();
                this.serverPublicKey=servExchange.getPublicKey();

                this.encMan=new EncMSG(pikey, serverPublicKey, "DSA");
            }catch(IOException e){
                disconnect(socket, reader, writer);
            }
        }                 
        
        public PublicKey getPublicKey(){
            return pukey;
        }

        public void sendMessage() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException { 
            try{
                String eMSG=encMan.encrypt(userAgroup);
                writer.write(eMSG); // send username and groupnumber to server
                writer.newLine();
                writer.flush();

                while(socket.isConnected()){
                    System.out.print(userAgroup+": ");
                    String newMSG=kb.nextLine();

                    String encStr=userAgroup+": "+newMSG;
                    String encMSG=encMan.encrypt(encStr);
                    
                    writer.write(encMSG);
                    writer.newLine();
                    writer.flush();
                }
            }catch(IOException e){
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
                            msgGroup=reader.readLine();
                            String dmsg=encMan.decrypt(msgGroup);
                            System.out.print("\n"+dmsg+"\n"+userAgroup+": ");
                        }catch(IOException e){
                            disconnect(socket, reader, writer);
                            break;
                        } catch (InvalidKeyException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IllegalBlockSizeException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (BadPaddingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        public void disconnect(Socket socket,BufferedReader reader,BufferedWriter writer){ // Used to exit out of the server by each Client.  
            try{
                if(reader!=null){
                    reader.close();
                }
                if(writer!=null){
                    writer.close();
                }
                if(socket!=null){
                    socket.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    public static void main(String[] args) throws UnknownHostException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
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