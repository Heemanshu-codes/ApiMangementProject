package apiMangement.example.Apimanagement.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import apiMangement.example.Apimanagement.service.TrafficService;
import apiMangement.example.Apimanagement.model.TrafficLogModel;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;

@Component
public class TrafficInterceptor implements HandlerInterceptor {

    private final TrafficService trafficService;

    public TrafficInterceptor(TrafficService trafficService) {
        this.trafficService = trafficService;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {

        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {

        long start = (long) request.getAttribute("startTime");
        long time = System.currentTimeMillis() - start;

        TrafficLogModel log = new TrafficLogModel();

        log.setApiPath(request.getRequestURI());
        log.setHttpMethod(request.getMethod());
        log.setStatusCode(response.getStatus());
        log.setResponseTime(time);
        log.setRequestTime(LocalDateTime.now());

        trafficService.saveTraffic(log);
    }
}