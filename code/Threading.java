import java.io.DataInputStream;
import java.net.Socket;

import javax.xml.crypto.Data;

public class Threading extends Thread{
    int groupNum;
    String threadID;
    DataInputStream inputStream;
    Socket threadSocket;

    public void setGroup(int gNum){
        groupNum = gNum;
    }
    public void setThreadID(String thrdID){
        threadID = thrdID;
    }
    public void setDataInputStream(DataInputStream inpStream){
        inputStream=inpStream;
    }
    public void setSocket(Socket soc){
        threadSocket=soc;
    }

    public int getGroupNum(){
        return groupNum;
    }
    public String getThreadID(){
        return threadID;
    }
    public DataInputStream getInputStream(){
        return inputStream;
    }
    public Socket getThreadSocket(){
        return threadSocket;
    }

    public void run(int gNum, String threadID, DataInputStream inpStream, Socket threadSocket) {
        try{
            setGroup(gNum);
            setThreadID(threadID);
            setDataInputStream(inpStream);
            setSocket(threadSocket);

            System.out.println("Thread " + threadID + " has been started");
        }catch(Exception e){

        }
    }
}