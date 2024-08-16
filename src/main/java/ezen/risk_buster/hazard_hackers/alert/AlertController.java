package ezen.risk_buster.hazard_hackers.alert;

import jakarta.validation.Valid;
import org.springframework.http.HttpEntity;
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
    public ResponseEntity<AlertResponseDto> create(@Valid @RequestBody AlertRequestDto request) {
        AlertResponseDto responseDto = alertService.create(request);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AlertResponseDto>> findAll() {
        List<AlertResponseDto> alertResponseDtos = alertService.findAll();

        return new ResponseEntity<>(alertResponseDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertResponseDto> findById(@PathVariable Long id) {
        AlertResponseDto alertResponseDto = alertService.findById(id);

        return new ResponseEntity<>(alertResponseDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody AlertRequestDto request
    ) {
        AlertResponseDto alertResponseDto = alertService.update(id, request);

        return new ResponseEntity<>(alertResponseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        alertService.delete(id);

        return new ResponseEntity(HttpStatus.OK);

    }
}
