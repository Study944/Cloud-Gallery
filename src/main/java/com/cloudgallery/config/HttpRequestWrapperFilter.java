package com.cloudgallery.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * 请求包装过滤器，用于支持多次读取请求体
 */
@Order(1)
@Component
public class HttpRequestWrapperFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    jakarta.servlet.http.HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            // 包装请求以支持多次读取 body
            filterChain.doFilter(new RequestWrapper(request), response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
