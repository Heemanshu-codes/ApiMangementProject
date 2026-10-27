package apiMangement.example.Apimanagement.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import apiMangement.example.Apimanagement.model.TrafficLogModel;
import apiMangement.example.Apimanagement.service.TrafficService;
@RestController
@RequestMapping("/traffic")
public class TrafficController {

    private final TrafficService trafficService;

    public TrafficController(TrafficService trafficService) {
        this.trafficService = trafficService;
    }

    @GetMapping("/logs")
    public List<TrafficLogModel> getAllTraffic() {
        return trafficService.getAllTraffic();
    }
    @GetMapping("/stats/total")
public long getTotalRequests() {
    return trafficService.getTotalRequestsFromDB();
}

@GetMapping("/stats/failed")
public long getFailedRequests() {
    return trafficService.getFailedRequests();
}

@GetMapping("/stats/successful")
public long getSuccessfulRequests() {
    return trafficService.getSuccessfulRequests();
}
@GetMapping("/stats/slowest")
public TrafficLogModel getSlowestRequest() {
    return trafficService.getSlowestRequest();
}
@GetMapping("/stats/average-response-time")
public double getAverageResponseTime() {
    return trafficService.getAverageResponseTime();
}
@GetMapping("/stats/most-requested")
public String getMostRequestedApi() {
    return trafficService.getMostRequestedApi();
}
@GetMapping("/stats/date")
public long getRequestsByDate(
        @RequestParam
        @org.springframework.format.annotation.DateTimeFormat(
                iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
        java.time.LocalDate date) {

    return trafficService.getRequestsByDate(date);
}
@GetMapping("/stats/methods")
public java.util.Map<String, Long> getRequestsByMethod() {
    return trafficService.getRequestsByMethod();
}
@GetMapping("/stats/status-codes")
public java.util.Map<Integer, Long> getRequestsByStatusCode() {
    return trafficService.getRequestsByStatusCode();
}
@GetMapping("/logs/method")
public List<TrafficLogModel> getTrafficByMethod(
        @RequestParam String method) {
    return trafficService.getTrafficByMethod(method);
}
@GetMapping("/logs/status")
public List<TrafficLogModel> getTrafficByStatusCode(
        @RequestParam int statusCode) {
    return trafficService.getTrafficByStatusCode(statusCode);
}
@GetMapping("/logs/date-range")
public List<TrafficLogModel> getTrafficByDateRange(
        @RequestParam String start,
        @RequestParam String end) {

    return trafficService.getTrafficByDateRange(
            java.time.LocalDateTime.parse(start),
            java.time.LocalDateTime.parse(end)
    );
}
}