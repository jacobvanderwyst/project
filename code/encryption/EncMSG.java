import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import javax.crypto.*;

public class EncMSG{
    private PrivateKey pikey;
    private PublicKey pukey;
    private String algo;
    private Cipher cipher;
    
    public EncMSG(PrivateKey pikey, PublicKey pukey, String algo) throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.pikey=pikey;
        this.pukey=pukey;
        this.algo=algo;
        this.cipher = Cipher.getInstance(algo);
    }

    public String encrypt(String str) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        cipher.init(Cipher.ENCRYPT_MODE, pukey);
        byte[] byteMSG=str.getBytes(StandardCharsets.UTF_8);

        byte[] encBytes=cipher.doFinal(byteMSG);
        String encStr=Base64.getEncoder().encodeToString(encBytes);
        return encStr;
    }

    public String decrypt(String encmsg) throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException{
        cipher.init(Cipher.DECRYPT_MODE,pikey);

        byte[] encMSGb64decoded=Base64.getDecoder().decode(encmsg.getBytes(StandardCharsets.UTF_8));
        byte[] decMSGbytes=cipher.doFinal(encMSGb64decoded);
        
        String dmsg=new String(decMSGbytes, StandardCharsets.UTF_8);
        return dmsg;
    }

}