import java.io.BufferedOutputStream;
import java.io.BufferedWriter;

public class NewMSG{
    String msg;
    byte[] newMSG;
    String user;
    int gnum;
    public void setStringMSG(String message){
        msg = message;
    }
    public void setByteMSG(byte[] message){
        newMSG=message;
    }
    public void setUser(String u){
        user = u;
    }
    public void setGroup(int g){
        gnum = g;
    }
    public void serverBroadcast(BufferedOutputStream writer){

    }
    public void forwardByteMSG(BufferedOutputStream writer){

    }
    public void forwardStringMSG(BufferedWriter writer){

    }
}