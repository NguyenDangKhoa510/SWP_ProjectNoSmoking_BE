package org.datcheems.swp_projectnosmoking.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import org.datcheems.swp_projectnosmoking.entity.User;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Component
public class JwtUtils {

    @Value("${jwt.signerKey}")
    private String SIGNING_KEY;

    // Access token expiration: 1 hour
    private static final long ACCESS_TOKEN_EXPIRATION = 1;

    // Refresh token expiration: 7 days
    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24;

    public String generateToken(User user) {
        return generateToken(user, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, REFRESH_TOKEN_EXPIRATION);
    }

    private String generateToken(User user, long expirationHours) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("datcheems")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(expirationHours, ChronoUnit.HOURS).toEpochMilli(
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

    public String extractUsername(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            JWTClaimsSet claimsSet = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
            return claimsSet.getSubject(); // đây chính là username
        } catch (Exception e) {
            throw new RuntimeException("Cannot parse JWT token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            JWTClaimsSet claimsSet = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());

            // Verify signature
            jwsObject.verify(new MACVerifier(SIGNING_KEY.getBytes()));

            // Check if token is expired
            Date expirationTime = claimsSet.getExpirationTime();
            return expirationTime != null && expirationTime.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public Long extractUserId(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            JWTClaimsSet claimsSet = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
            return claimsSet.getLongClaim("userId");
        } catch (Exception e) {
            throw new RuntimeException("Cannot extract userId from token", e);
        }
    }
}
