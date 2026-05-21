package ru.itmo.common.network;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;

public class JwtToken {
    private static final Algorithm ALGORITHM = Algorithm.HMAC256("SECRET_KEY");

    public static String generateToken(String username) {
        return JWT.create().withSubject(username).withExpiresAt(new Date(System.currentTimeMillis() + 5 * 60 * 1000)).sign(ALGORITHM);
    }

    public static String validateToken(String token) {
        try {
            return JWT.require(ALGORITHM).build().verify(token).getSubject();
        } catch (Exception e) {
            return null; 
        }
    }
}