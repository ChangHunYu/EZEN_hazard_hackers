package ezen.risk_buster.hazard_hackers.country;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/continent")
public class ContinentController {
    private final ContinentService continentService;

    public ContinentController(ContinentService continentService) {
        this.continentService = continentService;
    }

    @PostMapping
    public ResponseEntity<ContinentResponse> create(@RequestBody ContinentRequest request) {
        ContinentResponse response = continentService.create(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ContinentResponse>> findAll() {
        List<ContinentResponse> response = continentService.findAll();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
