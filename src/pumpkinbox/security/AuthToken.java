package pumpkinbox.security;

import java.security.SecureRandom;

/**
 * Created by ramiawar on 4/3/17.
 */
public class AuthToken
{

    public static String generateToken(){
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        String token = bytes.toString();

        return Hasher.sha256(token);
    }

}
