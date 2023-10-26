import java.io.*; // IO control
import java.net.*; // Networking functions

public class Server{
    private ServerSocket serverSocket;
    BufferedReader iStream;

    public Server(ServerSocket socket){
        this.serverSocket = socket;
    }
    public void startServer(){
        try{
            while(!serverSocket.isClosed()){
                Socket soc=serverSocket.accept(); //accept a connection
                ClientHandler chandler=new ClientHandler(soc);
                
                Thread thread=new Thread(chandler);
        
            }
        }catch(Exception e){

        }
    }
    public void closeServer(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(1337);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}