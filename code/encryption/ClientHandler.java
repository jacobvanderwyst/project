import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.*;
import java.util.ArrayList;
import javax.crypto.*;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private static EncMSG msg;

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String[] userAgroup;

    private PublicKey serverPublicKey;
    private PrivateKey serverPrivateKey;

    private PublicKey clientPublicKey;
    
    public ClientHandler(Socket socket, PublicKey serverPublicKey, PrivateKey serverPrivateKey) throws IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException{
        try{
            this.socket = socket;
            this.writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader=new BufferedReader(new InputStreamReader(socket.getInputStream())); // reading and writing chars
            
            this.serverPublicKey = serverPublicKey;
            this.serverPrivateKey = serverPrivateKey; //crypto for server
            msg=new EncMSG(serverPrivateKey, clientPublicKey, "RSA");

            KeyExchange cliKeyExchange=new KeyExchange("RSA", "client");
            this.clientPublicKey=cliKeyExchange.getPublicKey();//get and store client public keys

            String duser=reader.readLine();
            String user=msg.decrypt(duser);
            System.out.println(user);
            this.userAgroup=user.split(" "); // split the username and groupnumber

            clientHandlers.add(this);

            broadcast("Server: "+userAgroup[0]+" has joined group "+userAgroup[1]+"\n"+userAgroup[0]+": ");
        }catch(IOException e){
            disconnect(socket, reader, writer);
        }
    }

    @Override
    public void run() { // Listen for messages from client
        String mesg;
        try {
            msg=new EncMSG(serverPrivateKey, serverPublicKey, "DSA");
            while(socket.isConnected()){
            try{  
                mesg=reader.readLine();
                String emsg=msg.encrypt(msg.decrypt(mesg));
                broadcast(emsg);
            }catch(IOException e){
                try {
                    disconnect(socket, reader, writer);
                } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
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
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    public void broadcast(String mesg) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{ // add ability to encrypt client messages
        for(ClientHandler clientHandler:clientHandlers){
            
            try{
                if(!clientHandler.userAgroup[0].equals(userAgroup[0])){ // Broadcast message to all users accept the client the message originated from
                    if(clientHandler.userAgroup[1].equals(userAgroup[1])){ // broadcast to all users in the same group as the client the message originated from
                        
                        msg=new EncMSG(serverPrivateKey, clientPublicKey, "DSA");
                        String emsg=msg.encrypt(mesg);
                        clientHandler.writer.write(emsg);
                        clientHandler.writer.newLine();
                        clientHandler.writer.flush();
                    }
                    
                }
            }catch(IOException e){
                disconnect(socket, reader, writer);
            }
        }
    }

    public void removeClientHandler() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
        clientHandlers.remove(this);
        broadcast("Server: "+ userAgroup[0]+" has left the chat\n"+userAgroup[0]+": ");
    }

    public void disconnect(Socket socket, BufferedReader reader, BufferedWriter writer) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
        removeClientHandler();
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
        }finally{
            System.out.println("Client removed and disconnected Successfully");
        }
    }
}