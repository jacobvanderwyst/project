import java.io.*; // IO control
import java.net.*; // Networking functions
import java.security.*;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.*;

public class Server{
    private ServerSocket serverSocket;
    private PublicKey serverPublicKey;
    private PrivateKey serverPrivateKey;

    public Server(ServerSocket socket) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException{
        KeyExchange keyExchange = new KeyExchange("DSA", "server");
        keyExchange.setPrivate();
        keyExchange.setPublic();

        this.serverSocket = socket;
        this.serverPublicKey = keyExchange.getPublicKey();
        this.serverPrivateKey = keyExchange.getPrivateKey();
    }

    public PublicKey getServerPublicKey(){
        return serverPublicKey;
    }

    public void startServer() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        try{
            while(!serverSocket.isClosed()){
                Socket soc=serverSocket.accept(); //accept a connection
                ClientHandler chandler=new ClientHandler(soc, serverPublicKey, serverPrivateKey);
                System.out.println("Client connection established");

                Thread thread=new Thread(chandler);
                thread.start();
            }
        }catch(IOException e){
            //e.printStackTrace();
        }catch(NullPointerException e){
            System.out.println("Client connection error in ClientHandler: "+e.getMessage());
            e.printStackTrace();
        }
    }
    public void closeServer(){
        try{
            if(serverSocket != null){
                serverSocket.close();
                System.out.println("Server socket closed");
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException{
        ServerSocket serverSocket = new ServerSocket(8008);
        
        Server server = new Server(serverSocket);
        System.out.println("Server started");
        server.startServer();
    }
}