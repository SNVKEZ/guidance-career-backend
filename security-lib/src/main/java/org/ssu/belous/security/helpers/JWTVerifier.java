package org.ssu.belous.security.helpers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

@Component
@Slf4j
public class JWTVerifier {

    private final Algorithm algorithm;

    public JWTVerifier(@Value("${jwt.secret}") String secret, @Value("${jwt.algorithm}") String algorithmName) {
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

    public DecodedJWT verifier(String token, String subject, String issuer) {
        return JWT.require(algorithm)
                .withSubject(Objects.requireNonNull(subject, "Ключ SUBJECT не может быть null"))
                .withIssuer(Objects.requireNonNull(issuer, "Ключ ISSUE не может быть null"))
                .build()
                .verify(Objects.requireNonNull(token, "Токен не может быть null"));
    }
}
