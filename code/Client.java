import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Client{
        private Socket socket;
        private BufferedReader reader;
        private BufferedWriter writer;
        private Scanner kb;
        private String userAgroup;

        public Client(Socket socket, String userAgroup){
            try{
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.kb=new Scanner(System.in);
            this.userAgroup=userAgroup;
            }catch(IOException e){
                disconnect(socket, reader, writer);
            }
        }                 
       
        public void sendMessage() {               //Should be checked for in a loop. Message should be encrypted using the targets public keys and sent to server with the clients group number 
            try{
                writer.write(userAgroup);
                writer.newLine();
                writer.flush();

                while(socket.isConnected()){
                    System.out.print(userAgroup+": ");
                    String newMSG=kb.nextLine();
                    System.out.println();

                    writer.write(userAgroup+": "+newMSG);
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
                            System.out.println(msgGroup);
                        }catch(IOException e){
                            disconnect(socket, reader, writer);
                            break;
                        }
                    }
                }
            }).start();
        }

        public void disconnect(Socket socket,BufferedReader reader,BufferedWriter writer){                  //Used to exit out of the server by each Client.  
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
    public static void main(String[] args) throws UnknownHostException, IOException {
        Scanner kb=new Scanner(System.in);
        System.out.print("Enter username and group number to connect to server\n\"User groupNum\": ");
        String userAgroup=kb.nextLine();
        System.out.println();
        
        Socket socket=new Socket("localhost", 1337);
        Client client=new Client(socket, userAgroup);
        client.listenMSG();
        client.sendMessage();

        
    }
}