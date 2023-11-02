import java.io.*; // IO control
import java.net.*; // Networking functions
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Server{
    private ServerSocket serverSocket;
    
    public Server(ServerSocket socket){
        this.serverSocket = socket;
    }
    public void startServer() {
        try{
            while(!serverSocket.isClosed()){
                Socket soc=serverSocket.accept(); //accept a connection
                ClientHandler chandler=new ClientHandler(soc);
                System.out.println("Client connection established");

                Thread thread=new Thread(chandler);
                thread.start();
            }
        }catch(IOException e){
            System.out.println("server");
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
    public static void main(String[] args) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException{
        ServerSocket serverSocket = new ServerSocket(8008);
        Server server = new Server(serverSocket);
        System.out.println("Server started");
        server.startServer();
    }
}