import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.*;

public class Crypto {
    private String inputstr;
    private IvParameterSpec ivParameterSpec;
    private SecretKey key;
    
    public Crypto(int keysize, String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException, FileNotFoundException, IOException{
        this.key=getKeyFromPassword(password, salt);
        this.ivParameterSpec=generateIv();
        
    }

    public void setInput(String input){
        inputstr = input;
    }

    public IvParameterSpec getIvspecFile() throws IOException{
        byte[] ivspecbytes = Files.readAllBytes(Paths.get("ivspec.key"));
        IvParameterSpec ivspec=new IvParameterSpec(ivspecbytes);
        return ivspec;
    }

    public static SecretKey getKeyFromPassword(String password, String salt)throws NoSuchAlgorithmException, InvalidKeySpecException {
    
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    return secret;
}

    public static IvParameterSpec generateIv() throws FileNotFoundException, IOException {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivspec= new IvParameterSpec(iv);

        try (FileOutputStream out = new FileOutputStream("ivpsec.key",false)) {
            out.write(iv);
        }
        return ivspec;
    }

    public String encrypt(String algorithm) throws NoSuchPaddingException, NoSuchAlgorithmException,InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        System.out.println("e: "+inputstr);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        byte[] cipherText = cipher.doFinal(inputstr.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public String decrypt(String algorithm, IvParameterSpec ivspec) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,BadPaddingException, IllegalBlockSizeException {
        System.out.println("d: "+inputstr);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, ivspec);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(inputstr));
        return new String(plainText);
    }

}
