package com.haiph.kongtutorial.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class KongUserInfoFilter extends OncePerRequestFilter {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        var getUserInfo = getXUserHeaders(request);
        request.setAttribute("userInfo", getUserInfo);
        filterChain.doFilter(request, response);
    }

    public Map<String, String> getXUserHeaders(HttpServletRequest request) {
        Map<String, String> xUserHeaders = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (headerName.toLowerCase().startsWith("x-user-")) {
                String headerValue = request.getHeader(headerName);
                xUserHeaders.put(headerName, headerValue);
            }
        }

        return xUserHeaders;
    }
}
