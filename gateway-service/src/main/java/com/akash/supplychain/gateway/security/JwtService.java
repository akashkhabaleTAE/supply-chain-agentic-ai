package com.akash.supplychain.gateway.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();

    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;

    public JwtService(JwtProperties jwtProperties, ObjectMapper objectMapper) {
        this.jwtProperties = jwtProperties;
        this.objectMapper = objectMapper;
    }

    public String createToken(String subject, List<String> roles) {
        Instant now = Instant.now();
        Map<String, Object> header = Map.of(
                "alg", "HS256",
                "typ", "JWT"
        );
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", subject);
        payload.put("iss", jwtProperties.getIssuer());
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", now.plus(jwtProperties.getTtl()).getEpochSecond());
        payload.put("roles", roles);

        String headerPart = encodeJson(header);
        String payloadPart = encodeJson(payload);
        String unsignedToken = headerPart + "." + payloadPart;
        return unsignedToken + "." + sign(unsignedToken);
    }

    public Optional<JwtClaims> validate(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return Optional.empty();
            }
            String unsignedToken = parts[0] + "." + parts[1];
            String expectedSignature = sign(unsignedToken);
            if (!MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) {
                return Optional.empty();
            }

            Map<String, Object> header = decodeJson(parts[0]);
            if (!"HS256".equals(header.get("alg"))) {
                return Optional.empty();
            }
            Map<String, Object> payload = decodeJson(parts[1]);
            String issuer = String.valueOf(payload.get("iss"));
            if (!jwtProperties.getIssuer().equals(issuer)) {
                return Optional.empty();
            }
            Instant expiresAt = Instant.ofEpochSecond(number(payload.get("exp")));
            if (expiresAt.isBefore(Instant.now())) {
                return Optional.empty();
            }
            Instant issuedAt = Instant.ofEpochSecond(number(payload.get("iat")));
            String subject = String.valueOf(payload.get("sub"));
            List<String> roles = objectMapper.convertValue(payload.get("roles"), new TypeReference<>() {
            });
            return Optional.of(new JwtClaims(subject, issuer, issuedAt, expiresAt, roles));
        } catch (RuntimeException exception) {
            return Optional.empty();
        }
    }

    private String encodeJson(Map<String, Object> value) {
        try {
            return URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to encode JWT", exception);
        }
    }

    private Map<String, Object> decodeJson(String value) {
        try {
            return objectMapper.readValue(URL_DECODER.decode(value), new TypeReference<>() {
            });
        } catch (Exception exception) {
            throw new IllegalArgumentException("Unable to decode JWT", exception);
        }
    }

    private String sign(String unsignedToken) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return URL_ENCODER.encodeToString(mac.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to sign JWT", exception);
        }
    }

    private long number(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }
}
