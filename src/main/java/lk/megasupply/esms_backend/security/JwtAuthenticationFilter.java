package lk.megasupply.esms_backend.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Spring එකට මේක අඳුරගන්න Component එකක් විදිහට දානවා
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private final JwtService jwtService;
    // Database එකෙන් User ගේ විස්තර ගන්න මේක පාවිච්චි කරනවා (මේක අපි ඊළඟ පියවරේදී Config කරනවා)
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Request එකේ "Authorization" කියන Header එක ගන්නවා
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 2. Header එකක් නැත්නම් හෝ ඒක "Bearer " වලින් පටන් ගන්නේ නැත්නම්, මේක ටෝකන් එකක් නෙවෙයි.
        // ඒ නිසා ඊළඟ ෆිල්ටර් එකට යන්න දෙනවා (මෙතනින් එහාට මුකුත් කරන්නේ නෑ)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. "Bearer " කියන කෑල්ල අයින් කරලා, ටෝකන් එක විතරක් කපාගන්නවා (අකුරු 7කට පස්සේ තියෙන ටික)
        jwt = authHeader.substring(7);

        // 4. අපේ JwtService එක හරහා ටෝකන් එකෙන් Username එක අරගන්නවා
        try {
            username = jwtService.extractUsername(jwt);
        } catch (ExpiredJwtException e) {
            // ඊළඟ ෆිල්ටර් එකට යැව්වාම Spring Security මගින් මෙය 401 Unauthorized ලෙස යවයි
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Username එකක් තියෙනවා නම් සහ, මේ කෙනා තාම පද්ධතියට ලොග් වෙලා නැත්නම් විතරක් ඉස්සරහට යනවා
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Database එකෙන් User ගේ විස්තර ටික ගන්නවා
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 6. ටෝකන් එක හරියටම වලංගුද කියලා බලනවා
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // ටෝකන් එක වලංගු නම්, Spring Security එකට කියනවා "මේ කෙනා හරි, මෙයාව ඇතුලට ගන්න" කියලා
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Request එකේ අමතර විස්තර (IP Address, Web Browser එක වගේ දේවල්) Token එකට එකතු කරනවා
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Spring Security Context එකට මේ අලුත් Token එක දානවා (දැන් එයා ලොග් වෙලා ඉන්නේ)
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 7. ඔක්කොම හරි නම් (හෝ වැරදි නම්) අනිවාර්යයෙන්ම Request එක ඊළඟ පියවරට යවනවා
        filterChain.doFilter(request, response);
    }
}