package org.datcheems.swp_projectnosmoking.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import org.datcheems.swp_projectnosmoking.entity.User;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Component
public class JwtUtils {

    @Value("${jwt.signerKey}")
    private String SIGNING_KEY;

    public String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("datcheems")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli(
                        )))
                .claim("scope", buildScope(user))
                .claim("userId", user.getId())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNING_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
            return "";
        }

        StringJoiner stringJoiner = new StringJoiner(" ");
        user.getRoles().forEach(role -> {
            if (role != null && role.getName() != null) {
                stringJoiner.add(role.getName().name());
            }
        });

        return stringJoiner.toString();
    }

    public static Long extractUserIdFromAuthentication(Authentication authentication) {
        // Nếu dùng JWT (Spring Security 6)
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaim("userId");
        }

        // Nếu dùng UserDetails (Spring Security cổ điển)
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            User user = (User) userDetails;
            return user.getId();
        }

        throw new IllegalArgumentException("Cannot extract userId from authentication");
    }
}



