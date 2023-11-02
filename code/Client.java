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
            this.writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.kb=new Scanner(System.in);
            this.userAgroup=userAgroup;
            }catch(IOException e){
                disconnect(socket, reader, writer);
            }
        }                 
       
        public void sendMessage() { 
            try{
                writer.write(userAgroup); // send username and groupnumber to server
                writer.newLine();
                writer.flush();

                while(socket.isConnected()){
                    System.out.print(userAgroup+": ");
                    String newMSG=kb.nextLine();

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
                            System.out.print("\n"+msgGroup+"\n"+userAgroup+": ");
                        }catch(IOException e){
                            disconnect(socket, reader, writer);
                            break;
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
    public static void main(String[] args) throws UnknownHostException, IOException {
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