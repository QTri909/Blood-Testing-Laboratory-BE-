package sum25.group03.monitoringservice.controller;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import sum25.group03.monitoringservice.model.EventLog;
import sum25.group03.monitoringservice.service.MonitoringService;

import java.util.List;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {
    private final MonitoringService monitoringService;

    public MonitoringController(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @GetMapping("/send")
    public ResponseEntity<String> sendMessage(){
        monitoringService.sendMessage();
        return ResponseEntity.ok().body("success");
    }

    @GetMapping
    public ResponseEntity<List<EventLog>> getEventLogs(){
        List<EventLog> eventLogs = monitoringService.getAllEventLogs();
        return ResponseEntity.ok().body(eventLogs);
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventLog>> searchEventLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String operator
    ) {
        List<EventLog> results = monitoringService.searchEventLogs(action, message, operator);
        return ResponseEntity.ok().body(results);
    }
    @PostMapping
    ResponseEntity<EventLog> saveEventLog(@RequestBody EventLog eventLog){
     EventLog newEvent =   monitoringService.addEventLog(eventLog);
     return ResponseEntity.ok().body(newEvent);
    }

}
