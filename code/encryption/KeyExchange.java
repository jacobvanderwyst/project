
import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.*;


public class KeyExchange{
        private PrivateKey pikey;
        private PublicKey puKey;
        private KeyPair pair;
        private String privStr;
        private String pubStr;
        
        public KeyExchange(String algo, String operator) throws NoSuchAlgorithmException{
            KeyPairGenerator generator = KeyPairGenerator.getInstance(algo);
            generator.initialize(1024);
            
            this.pair = generator.generateKeyPair();
            this.pikey=pair.getPrivate();
            this.puKey=pair.getPublic();
            this.privStr=operator+"private.key";
            this.pubStr=operator+"public.key";

        }

        public void setPublic() throws FileNotFoundException, IOException{
            try(FileOutputStream fos = new FileOutputStream(pubStr, false)){
                fos.write(puKey.getEncoded());
                fos.close();
            }
        }

        public void setPrivate() throws FileNotFoundException, IOException{
            try(FileOutputStream fos = new FileOutputStream(privStr, false)){
                fos.write(pikey.getEncoded());
                fos.close();
            }
            
        }

        public PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
            File privateKeyFile = new File(privStr);
            byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
            
            KeyFactory keyFactory = KeyFactory.getInstance("DSA");
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            PrivateKey key =keyFactory.generatePrivate(privateKeySpec);
            return key;
        }

        public PublicKey getPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
            File publicKeyFile = new File(pubStr);
            byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
            
            KeyFactory keyFactory = KeyFactory.getInstance("DSA");
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey key =keyFactory.generatePublic(publicKeySpec);
            return key;
        }

        
}