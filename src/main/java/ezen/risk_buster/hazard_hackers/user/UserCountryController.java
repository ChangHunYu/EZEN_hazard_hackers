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
            @RequestBody UserCountryRequestDto request
            ) {
        UserCountryResponseDto responseDto = userCountryService.create(userEmail, request);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }


}
