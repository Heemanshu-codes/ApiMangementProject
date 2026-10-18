package apiMangement.example.Apimanagement.controller;

import apiMangement.example.Apimanagement.model.TrafficLogModel;
import apiMangement.example.Apimanagement.service.TrafficService;

import org.springframework.web.bind.annotation.*;
import java.util.List;

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
}