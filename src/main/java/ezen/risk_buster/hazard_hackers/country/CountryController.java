package ezen.risk_buster.hazard_hackers.country;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/country")
@RestController
public class CountryController {
    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @PostMapping
    public ResponseEntity<String> Create(@RequestBody CountryRequest request) {
        CountryResponse countryResponse = countryService.create(request);
        return new ResponseEntity<>(countryResponse.countryName()+" 생성에 성공했습니다.", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CountryResponse> findById(@PathVariable Long id) {
        CountryResponse countryResponse = countryService.findById(id);
        return new ResponseEntity<>(countryResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CountryResponse>> findAll() {
        List<CountryResponse> countryResponses = countryService.findAll();
        return new ResponseEntity<>(countryResponses, HttpStatus.OK);
    }
}
