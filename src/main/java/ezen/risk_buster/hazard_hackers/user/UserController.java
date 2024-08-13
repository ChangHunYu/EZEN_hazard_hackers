package ezen.risk_buster.hazard_hackers.user;

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
    public void register(@RequestBody UserResponseDTO request) {
        userService.create(request);
    }

    @PostMapping("/login") //로그인
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        userService.login(request);
        return new ResponseEntity<>("로그인에 성공했습니다.", HttpStatus.OK);
    }

    @GetMapping("/{id}") //프로필 조회
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        UserResponseDTO responseDTO = userService.findById(id);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/{id}") //프로필 수정
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id,
                                                  @RequestBody UserResponseDTO request) {
        UserResponseDTO responseDTO = userService.update(id, request);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}") //회원탈퇴
    public ResponseEntity<String> delete(@PathVariable String email) {
        userService.delete(email);

        return new ResponseEntity<>("Deleted Success",HttpStatus.OK);
    }
}