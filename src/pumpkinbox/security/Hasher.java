package pumpkinbox.security;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ramiawar on 3/31/17.
 */
public class Hasher {

    public static String sha256(String s){
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(s.getBytes(StandardCharsets.UTF_8));
            String hashed = DatatypeConverter.printBase64Binary(hash);
            return hashed;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "fail";

    }

}
