package com.haiph.kongtutorial.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
public class KongUserInfoFilter extends OncePerRequestFilter {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String xUserInfo = request.getHeader("x-user-info");
        if (xUserInfo != null) {
            Map<String, Object> userInfo = mapper.readValue(xUserInfo, Map.class);
            request.setAttribute("userInfo", userInfo);
        }

        filterChain.doFilter(request, response);
    }
}
