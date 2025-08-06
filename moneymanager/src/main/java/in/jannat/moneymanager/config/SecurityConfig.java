package in.jannat.moneymanager.config;

import in.jannat.moneymanager.security.JwtRequestFilter;
import in.jannat.moneymanager.service.AppUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final AppUserDetailsService appUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers("/status", "/health", "/register", "/activate", "/login").permitAll().anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
//    Allows your React frontend to communicate with your backend.
//Prevents “CORS errors” in the browser.
//Supports secure APIs using JWT or other credentials.
//Ensures development flexibility while you build/test across ports.


//1. .cors(Customizer.withDefaults())
//Enables CORS (Cross-Origin Resource Sharing) using default settings.
//Important for frontend (React) to talk to backend (Spring Boot) when they run on different ports (localhost:3000 vs localhost:8080).

//2. .csrf(AbstractHttpConfigurer::disable)
//Disables CSRF protection.
//This is safe to do for stateless REST APIs, especially when you’re not using cookies or sessions.

//3. .authorizeHttpRequests(...)
//Controls access to different endpoints:
//auth.requestMatchers("/status","/health","/register","/activate","/login").permitAll()
//These endpoints don’t require login (they are public).
//.anyRequest().authenticated()
//All other endpoints require the user to be authenticated.

//4. .sessionManagement(...)
//.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//Tells Spring not to use sessions to keep track of users.
//Instead, authentication will likely be done using JWT tokens or similar, which are sent on every request.


    @Bean
    public AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(appUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }
}