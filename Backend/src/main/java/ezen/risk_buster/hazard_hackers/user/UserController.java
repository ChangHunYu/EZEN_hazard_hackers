package ezen.risk_buster.hazard_hackers.user;

import ezen.risk_buster.hazard_hackers.common.auth.LoginUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup") //회원가입
    public void register(@RequestBody SignupRequest request) {
        userService.create(request);
    }

    @PostMapping("/login")//로그인
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}") //프로필 조회
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        UserResponseDTO responseDTO = userService.findById(id);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/me") //프로필 조회
    public ResponseEntity<UserResponseDTO> getCurrentUser(@LoginUser String userEmail) {
        UserResponseDTO responseDTO = userService.getCurrentUser(userEmail);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/{id}") //프로필 수정
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id,
                                                  @RequestBody UserUpdateRequestDTO request,
                                                  @LoginUser String userEmail) {
        UserResponseDTO responseDTO = userService.update(id, request, userEmail);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PatchMapping("/me") //비밀번호 수정
    public void changePassword(@LoginUser String userEmail,
                               @RequestBody ChangePasswordRequest request){
        userService.changePassword(userEmail, request);
    }

    @DeleteMapping("/{id}") //회원탈퇴
    public ResponseEntity<String> delete(@LoginUser String userEmail,
                                         @PathVariable Long id) {
        userService.delete(userEmail, id);

        return new ResponseEntity<>("Deleted Success",HttpStatus.OK);
    }
}