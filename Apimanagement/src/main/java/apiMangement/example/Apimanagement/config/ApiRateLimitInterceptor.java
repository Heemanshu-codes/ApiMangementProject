package apiMangement.example.Apimanagement.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import apiMangement.example.Apimanagement.model.ApiKeyModel;
import apiMangement.example.Apimanagement.repository.ApiKeyRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiRateLimitInterceptor implements HandlerInterceptor {

    private final ApiKeyRepository repository;

    private final Map<String, Integer> requestCounts = new HashMap<>();
    private final Map<String, Long> startTimes = new HashMap<>();

    private final long window = 60_000;

    public ApiRateLimitInterceptor(ApiKeyRepository repository) {
        this.repository = repository;
    }

    @Override
    public synchronized boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        String key = request.getHeader("X-API-KEY");

        ApiKeyModel apiKey = repository.findByApiKey(key).orElse(null);

        if (apiKey == null || !apiKey.isActive()) {
            response.setStatus(401);
            response.getWriter().write("Invalid API Key");
            return false;
        }

        long now = System.currentTimeMillis();

        long start = startTimes.getOrDefault(key, now);

        if (now - start >= window) {
            startTimes.put(key, now);
            requestCounts.put(key, 0);
        }

        int count = requestCounts.getOrDefault(key, 0);

        if (count >= apiKey.getRequestLimit()) {
            response.setStatus(429);
            response.getWriter().write("Rate limit exceeded");
            return false;
        }

        requestCounts.put(key, count + 1);

        return true;
    }
}