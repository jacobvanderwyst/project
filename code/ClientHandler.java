import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String[] userAgroup;

    public ClientHandler(Socket socket) throws IOException{
        try{
            this.socket = socket;
            this.writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader=new BufferedReader(new InputStreamReader(socket.getInputStream())); // reading and writing chars
            String user=reader.readLine();
            this.userAgroup=user.split(" ");

            clientHandlers.add(this);
            broadcast("Server: "+userAgroup[0]+" has joined group "+userAgroup[1]);
        }catch(IOException e){
            disconnect(socket, reader, writer);
        }
    }

    @Override
    public void run() { // Listen for messages from client
        String msg;

        while(socket.isConnected()){
            try{
                msg=reader.readLine();
                broadcast(msg);
            }catch(IOException e){
                disconnect(socket, reader, writer);
                break;
            }
        }
    }

    public void broadcast(String msg){
        for(ClientHandler clientHandler:clientHandlers){
            try{
                if(!clientHandler.userAgroup[0].equals(userAgroup[0])){ // Broadcast message to all users accept the client the message originated from
                    if(clientHandler.userAgroup[1].equals(userAgroup[1])){ // broadcast to all users in the same group as the client the message originated from
                        clientHandler.writer.write(msg);
                        clientHandler.writer.newLine();
                        clientHandler.writer.flush();
                    }
                    
                }
            }catch(IOException e){
                disconnect(socket, reader, writer);
            }
        }
    }

    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcast("Server: "+ userAgroup[0]+" has left the chat");
    }

    public void disconnect(Socket socket, BufferedReader reader, BufferedWriter writer){
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
        }
    }
}