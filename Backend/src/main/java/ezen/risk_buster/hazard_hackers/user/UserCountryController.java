package ezen.risk_buster.hazard_hackers.user;

import ezen.risk_buster.hazard_hackers.common.auth.LoginUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/UserCountries")
public class UserCountryController {

    private final UserService userService;
    private final UserCountryService userCountryService;

    public UserCountryController(UserService userService, UserCountryService userCountryService) {
        this.userService = userService;
        this.userCountryService = userCountryService;
    }

    @PostMapping
    public ResponseEntity<UserCountryResponseDto> create(
            @LoginUser String userEmail,
            @RequestBody UserCountryRequestDto request) {
        UserCountryResponseDto responseDto = userCountryService.create(userEmail, request);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserCountryResponseDto>> findAll(@LoginUser String userEmail, @RequestParam(required = false) String userCountryEngName) {
        List<UserCountryResponseDto> responseDtos = userCountryService.findAll(userEmail, userCountryEngName);

        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserCountryResponseDto> findOne(
            @LoginUser String userEmail,
            @PathVariable Long id) {
        UserCountryResponseDto responseDto = userCountryService.findOne(userEmail, id);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserCountryResponseDto> update(
            @LoginUser String userEmail,
            @PathVariable Long id,
            @RequestBody UserCountryRequestDto request) {
        UserCountryResponseDto responseDto = userCountryService.update(userEmail ,id, request);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@LoginUser String userEmail, @PathVariable Long id) {
        userCountryService.delete(userEmail, id);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
