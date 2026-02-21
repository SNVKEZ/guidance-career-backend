package org.ssu.belous.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.ssu.belous.security.helpers.JWTVerifier;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class JWTService {

    private final int timeLive;

    private static final String USERNAME_KEY = "username";
    private static final String ROLE_KEY = "role";
    private static final String SUBJECT = "Userdetails";
    private static final String ISSUE = "ssu_belous";

    private final JWTVerifier jwtVerifier;
    private final Algorithm algorithm;

    @Autowired
    public JWTService(JWTVerifier jwtVerifier,
                      @Value("${jwt.token.liveTime}") int timeLive,
                      @Value("${jwt.secret}") String secret,
                      @Value("${jwt.algorithm}") String algorithmName) {
        this.jwtVerifier = jwtVerifier;
        this.timeLive = timeLive;
        this.algorithm = initAlgorithm(Objects.requireNonNull(secret, "Secret не может быть null"), algorithmName);
    }

    private Algorithm initAlgorithm(String secret, String algorithmName) {
        try {
            Method method = Algorithm.class.getMethod(
                    Objects.requireNonNull(algorithmName, "Название алгоритма не может быть null"),
                    String.class);
            return (Algorithm) method.invoke(null, secret);
        } catch (Exception e) {
            throw new IllegalArgumentException("Алгоритм не существует: " + algorithmName, e);
        }
    }

    public String generateToken(String username, String roles) {
        return JWT.create()
                .withSubject(SUBJECT)
                .withClaim(USERNAME_KEY, username)
                .withClaim(ROLE_KEY, roles)
                .withIssuedAt(new Date())
                .withIssuer(ISSUE)
                .withExpiresAt(Instant.now().plus(timeLive, ChronoUnit.MINUTES))
                .sign(algorithm);
    }

    public Map<String, Claim> validTokenAndRetrieveSubject(String token) throws JWTVerificationException {
        return jwtVerifier.verifier(token, SUBJECT, ISSUE).getClaims();
    }
}
