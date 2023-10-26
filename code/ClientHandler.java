import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private int groupNum;
    private String user;

    private BufferedInputStream reader;
    private BufferedOutputStream writer;
    private BufferedReader rdr;
    private BufferedWriter wtr;
    private NewMSG msg=new NewMSG();

    public ClientHandler(Socket soc, String username, int gnum) throws IOException{
        try{
            this.socket = soc;

            this.writer=new BufferedOutputStream(socket.getOutputStream()); //sends bytes
            this.reader=new BufferedInputStream(socket.getInputStream()); //reads bytes
            this.rdr=new BufferedReader(new InputStreamReader(socket.getInputStream())); //reads chars
            this.wtr=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //writes chars

            this.user = rdr.readLine();
            this.groupNum=Integer.parseInt(rdr.readLine());

            clientHandlers.add(this);
            
            /*
             * msg.setUser(user);
             * msg.setGroup(groupNum);
             * msg.setByteMSG(("Server: "+user + " has joined croup " + groupNum).getBytes("UTF-8"));
             * msg.serverBroadcast(writer);
            */
            
            
        }catch(IOException e){
            disconnect(socket, writer, reader, rdr, wtr);
        }
    }
    @Override
    public void run() {
        byte[] newMSG;
        String user;
        int gnum;

        while(socket.isConnected()){
            try{
                byte[] newByteMSG=reader.readAllBytes();
                user=rdr.readLine();
                gnum=Integer.parseInt(rdr.readLine());

                msg.setGroup(gnum);
                msg.setUser(user);

                if(newByteMSG != null || newStringMSG !=null){

                }
                msg.setByteMSG(newByteMSG);
                msg.forwardByteMSG(writer);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}