import java.security.*;

public class KeyExchange{
        private PrivateKey pikey;
        private PublicKey puKey;
        private KeyPair pair;
        
        public KeyExchange(String algo) throws NoSuchAlgorithmException{
            KeyPairGenerator generator = KeyPairGenerator.getInstance(algo);
            generator.initialize(2048);
            
            this.pair = generator.generateKeyPair();
            this.pikey=pair.getPrivate();
            this.puKey=pair.getPublic();
        }

        public PrivateKey getPrivateKey(){
            return pikey;
        }

        public PublicKey getPublicKey(){
            return puKey;
        }
}