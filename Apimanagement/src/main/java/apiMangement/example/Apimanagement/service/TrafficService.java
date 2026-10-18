package apiMangement.example.Apimanagement.service;

import java.util.List;

import apiMangement.example.Apimanagement.model.TrafficLogModel;
import apiMangement.example.Apimanagement.repository.TrafficLogRepository;

import org.springframework.stereotype.Service;

@Service
public class TrafficService {

    private int totalRequests = 0;

    private final TrafficLogRepository repository;

    public TrafficService(TrafficLogRepository repository) {
        this.repository = repository;
    }

    public void trackRequest(String method, String path,
                             int status, long time) {

        totalRequests++;

        TrafficLogModel log = new TrafficLogModel();
        log.setApiPath(path);
        log.setHttpMethod(method);
        log.setStatusCode(status);
        log.setResponseTime(time);
        log.setRequestTime(java.time.LocalDateTime.now());

        repository.save(log);
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public List<TrafficLogModel> getAllTraffic() {
        return repository.findAll();
    }

    public void saveTraffic(TrafficLogModel log) {
        repository.save(log);
    }
    public long getTotalRequestsFromDB() {
    return repository.count();
}

public long getFailedRequests() {
    return repository.findAll()
            .stream()
            .filter(log -> log.getStatusCode() >= 400)
            .count();
}

public long getSuccessfulRequests() {
    return repository.findAll()
            .stream()
            .filter(log -> log.getStatusCode() >= 200
                    && log.getStatusCode() < 300)
            .count();
}
}