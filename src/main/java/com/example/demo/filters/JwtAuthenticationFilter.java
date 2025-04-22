package com.example.demo.filters;

import com.example.demo.utils.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String authHeader = request.getHeader("Authorization");
//        System.out.println("Chekcing Token recieved from headers : " + authHeader);
//        if (authHeader != null) {
//            String token = authHeader.substring(0);
//            System.out.println(jwtUtil.validateToken(token));
//            if (jwtUtil.validateToken(token)) {
//                String email = jwtUtil.extractEmail(token);
//                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
////                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
////                        userDetails, null, userDetails.getAuthorities());
//                //SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//        filterChain.doFilter(request, response);
//    }

            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                String authHeader = request.getHeader("Authorization");
                System.out.println("Checking token received from headers: " + authHeader);

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7); // ✅ Remove "Bearer " prefix
                    System.out.println("Token after stripping prefix: " + token);

                    if (jwtUtil.validateToken(token)) {
                        String email = jwtUtil.extractEmail(token);
                        System.out.println("Extracted email: " + email);

                        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        System.out.println("Invalid token!");
                    }
                }

                filterChain.doFilter(request, response);
            }

}