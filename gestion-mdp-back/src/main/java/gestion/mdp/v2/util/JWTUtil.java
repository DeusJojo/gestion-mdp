package gestion.mdp.v2.util;

public class JWTUtil {

    public static final String SECRET ="mySecret1234";
    public static final String AUTHORIZATION_HEADER ="Authorization";
    public static final String PREFIX ="Bearer ";
    public static final long EXPIRE_ACCESS_TOKEN =60L *60L * 1000L;
    public static final long EXPIRE_REFRESH_TOKEN =30L * 24L * 60L * 60L * 1000L;
}
