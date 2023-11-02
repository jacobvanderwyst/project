import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private static String[] userAgroup;
    private Crypto crypto;
    private IvParameterSpec ivspec;

    public ClientHandler(Socket socket) {
        try{
            this.socket = socket;
            this.writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader=new BufferedReader(new InputStreamReader(socket.getInputStream())); // reading and writing chars
            String tmp=reader.readLine(); // why is this recieving null from client buffer?
            this.crypto=new Crypto(256, "networking", "salty");
            
            
            System.out.println("input from client "+tmp);
            crypto.setInput(tmp);
            this.ivspec=crypto.getIvspecFile();
            
            String user=crypto.decrypt("AES/CBC/PKCS5Padding", ivspec);
            System.out.println(crypto.decrypt("AES/CBC/PKCS5Padding", ivspec)+" user "+user+"\nuser.split(\" \""+user.split(" "));
            ClientHandler.userAgroup=user.split(" "); // split the username and groupnumber

            clientHandlers.add(this);
            
            broadcast("Server: "+userAgroup[0]+" has joined group "+userAgroup[1]+"\n"+userAgroup[0]+": ");
        }catch(IOException e){
            try {
                disconnect(socket, reader, writer);
            } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException
                    | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() { // Listen for messages from client
        String msg;
        
        while(socket.isConnected()){
            try{
                crypto.setInput(reader.readLine());
                msg=crypto.decrypt("AES/CBC/PKCS5Padding", ivspec);
                broadcast(msg);
            }catch(IOException e){
                try {
                    System.out.println("disconnecting due to IO error");
                    e.printStackTrace();
                    disconnect(socket, reader, writer);
                } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException
                        | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e1) {
                    // TODO Auto-generated catch block
                    System.out.println("clienthandler");
                    e1.printStackTrace();
                }
                break;
            } catch (InvalidKeyException e) {
                // TODO Auto-generated catch block
                System.out.println("clienthandler");
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                // TODO Auto-generated catch block
                System.out.println("clienthandler");
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                System.out.println("clienthandler");
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                // TODO Auto-generated catch block
                System.out.println("clienthandler");
                e.printStackTrace();
            } catch (BadPaddingException e) {
                // TODO Auto-generated catch block
                System.out.println("clienthandler");
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                // TODO Auto-generated catch block
                System.out.println("clienthandler");
                e.printStackTrace();
            }catch(NullPointerException e){

            }
        }
    }

    public void broadcast(String msg) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException{
        for(ClientHandler clientHandler:clientHandlers){
            try{
                if(!clientHandler.userAgroup[0].equals(this.userAgroup[0])){ // Broadcast message to all users accept the client the message originated from
                    if(clientHandler.userAgroup[1].equals(this.userAgroup[1])){ // broadcast to all users in the same group as the client the message originated from
                        crypto.setInput(msg);
                        clientHandler.writer.write(crypto.encrypt("AES/CBC/PKCS5Padding"));
                        clientHandler.writer.newLine();
                        clientHandler.writer.flush();
                    }
                    
                }
            }catch(IOException e){
                disconnect(socket, reader, writer);
            }
        }
    }

    public void removeClientHandler() throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException{
        clientHandlers.remove(this);
        broadcast("Server: "+ this.userAgroup[0]+" has left the chat\n"+this.userAgroup[0]+": ");
    }

    public void disconnect(Socket socket, BufferedReader reader2, BufferedWriter writer2) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException{
        removeClientHandler();
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
            System.out.println("clienthandler");
            e.printStackTrace();
        }finally{
            System.out.println("Client removed and disconnected Successfully");
        }
    }
}