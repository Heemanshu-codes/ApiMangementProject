package apiMangement.example.Apimanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import apiMangement.example.Apimanagement.model.TrafficLogModel;
import apiMangement.example.Apimanagement.repository.TrafficLogRepository;

@Service
public class TrafficService {

    private int totalRequests = 0;

    private final TrafficLogRepository repository;

    public TrafficService(TrafficLogRepository repository) {
        this.repository = repository;
    }
    public double getAverageResponseTime() {
    List<TrafficLogModel> logs = repository.findAll();

    return logs.stream()
            .mapToLong(TrafficLogModel::getResponseTime)
            .average()
            .orElse(0.0);
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
    public TrafficLogModel getSlowestRequest() {
    return repository.findAll()
            .stream()
            .max((a, b) -> Long.compare(
                    a.getResponseTime(),
                    b.getResponseTime()))
            .orElse(null);
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
    public String getMostRequestedApi() {
    return repository.findAll()
            .stream()
            .collect(java.util.stream.Collectors.groupingBy(
                    TrafficLogModel::getApiPath,
                    java.util.stream.Collectors.counting()))
            .entrySet()
            .stream()
            .max(java.util.Map.Entry.comparingByValue())
            .map(entry -> entry.getKey() + " - " + entry.getValue() + " requests")
            .orElse("No requests found");
}

    public long getTotalRequestsFromDB() {
        return repository.count();
    }

    public long getFailedRequests() {
        return repository.countByStatusCodeGreaterThanEqual(400);
    }
    public long getRequestsByDate(
        java.time.LocalDate date) {

    return repository.findAll()
            .stream()
            .filter(log -> log.getRequestTime() != null)
            .filter(log -> log.getRequestTime()
                    .toLocalDate().equals(date))
            .count();
}

    public long getSuccessfulRequests() {
        return repository.countByStatusCodeBetween(200, 299);
    }
    public java.util.Map<String, Long> getRequestsByMethod() {
    return repository.findAll()
            .stream()
            .collect(java.util.stream.Collectors.groupingBy(
                    TrafficLogModel::getHttpMethod,
                    java.util.stream.Collectors.counting()
            ));
}
public java.util.Map<Integer, Long> getRequestsByStatusCode() {
    return repository.findAll()
            .stream()
            .collect(java.util.stream.Collectors.groupingBy(
                    TrafficLogModel::getStatusCode,
                    java.util.stream.Collectors.counting()
            ));
}
public List<TrafficLogModel> getTrafficByMethod(String method) {
    return repository.findByHttpMethod(method);
}
public List<TrafficLogModel> getTrafficByStatusCode(int statusCode) {
    return repository.findByStatusCode(statusCode);
}
public List<TrafficLogModel> getTrafficByDateRange(
        java.time.LocalDateTime start,
        java.time.LocalDateTime end) {

    return repository.findByRequestTimeBetween(start, end);
}
}