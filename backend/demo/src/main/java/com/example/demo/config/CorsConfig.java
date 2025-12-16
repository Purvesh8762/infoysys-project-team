
package com.example.demo.config; // Recommended package for config files

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply this CORS configuration to all API endpoints ("/**")
            .allowedOrigins("http://localhost:5173") // ONLY allow requests from your React frontend
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow common HTTP methods used by your forms
            .allowedHeaders("*"); // Allow all request headers
    }
}