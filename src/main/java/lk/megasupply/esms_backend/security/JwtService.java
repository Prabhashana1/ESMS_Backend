package lk.megasupply.esms_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // application.properties එකෙන් අර secret-key එක මෙතනට ගන්නවා
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    // application.properties එකෙන් expiration time එක ගන්නවා
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    // 1. ටෝකන් එකෙන් Username එක අරගන්නා Method එක
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 2. අලුත් ටෝකන් එකක් හදන Method එක
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) // Username එක ඇතුලත් කිරීම
                .setIssuedAt(new Date(System.currentTimeMillis())) // හැදූ වෙලාව
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Expire වෙන වෙලාව
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // අර Secret Key එකෙන් ලොක් කිරීම
                .compact();
    }

    // 3. එවන ටෝකන් එක නිවැරදිද සහ කල් ඉකුත් වෙලා නැද්ද කියලා බලන Method එක
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
