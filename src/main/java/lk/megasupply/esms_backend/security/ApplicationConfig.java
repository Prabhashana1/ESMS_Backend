package lk.megasupply.esms_backend.security;


import lk.megasupply.esms_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration // මේක Configuration class එකක් කියලා Spring එකට කියනවා
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    // 1. Database එකෙන් User ව හොයාගන්න විදිහ Spring Security එකට කියලා දෙනවා
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found in the system"));
    }

    // 2. දත්ත ගන්නේ කොහෙන්ද සහ Password එක බලන්නේ කොහොමද කියලා Provider කෙනෙක් හදනවා
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder()); // Password එක Hash කරන විදිහ පහලින් දීලා තියෙනවා
        return authProvider;
    }

    // 3. Login වෙනකොට User ගේ විස්තර හරියටම පරීක්ෂා කරන මැනේජර්ව හදනවා
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 4. Passwords Database එකේ Save කරද්දී Encrypt කරලා (BCrypt) Save කරන්න මේක පාවිච්චි කරනවා
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
