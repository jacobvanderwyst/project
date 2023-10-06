import java.io.*; // IO control
import java.net.*; // Networking functions

public class Server{
    
    public static void main(String[] args){
        try{
            ServerSocket socket=new ServerSocket(1337); // bind the port to 1337 on localhost(127.0.0.1)
            Master master=new Master();
            Client client;
            
            while(true){ // look for client connection indefinitely

                Socket soc=socket.accept(); //accept a connection
                DataInputStream dataInputStream=new DataInputStream(soc.getInputStream()); // read data from the socket connection (not thread safe, be aware)
                
                int groupNum=dataInputStream.readInt(); // group number should be the first thing client sends into the socket connection after iniating the socket connection
                client=new Client(groupNum, soc, dataInputStream); // this will throw an error until client has been updated to take groupNum as a parameter
                
                // client then needs to be created as a thread
            }
        }catch(Exception e){

        }
        

    }
}