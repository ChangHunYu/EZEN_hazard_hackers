package ezen.risk_buster.hazard_hackers.alert;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping
    public ResponseEntity<AlertResponseDto> create(@RequestBody AlertRequestDto request) {
        AlertResponseDto responseDto = alertService.create(request);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AlertResponseDto>> findAll() {
        List<AlertResponseDto> responseDtos = alertService.findAll();

        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertResponseDto> findById(@PathVariable Long id) {
        AlertResponseDto responseDto = alertService.findById(id);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
