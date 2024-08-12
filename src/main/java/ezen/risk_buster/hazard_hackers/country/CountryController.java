package ezen.risk_buster.hazard_hackers.country;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
