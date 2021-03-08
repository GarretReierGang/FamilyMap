package server.dao.security;
import java.security.SecureRandom;
import java.util.UUID;

public final class Password
{
    SecureRandom random;
    int cost;
    /**
     * Authenticates a password, by hashing it and comparing it to
     * @return true if the password and the hashedPassword match
     *
     */
    public static boolean authenticate(String password, String hashedPassword, String salt)
    {
        return hash(password, salt).equals(hashedPassword);//no one can log in yet
    }

    /**
     * Hashes the the string password using _______ (probably md5 ) not implemented, but it will still function. it just doesn't hash.
     * @param password String to hash
     *
     * @param salt used to prevent table based hash attacks.
     * @return hashed password which is 128 chars long.
     */
    public static String hash(String password, String salt)
    {
        return password;//implement later
    }

    /**
     * creates a random salt string
     * @return
     */
    public static String generateSalt()
    {
        return UUID.randomUUID().toString();//implement later
    }
}