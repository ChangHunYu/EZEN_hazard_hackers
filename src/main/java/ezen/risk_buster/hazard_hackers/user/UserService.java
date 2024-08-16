package ezen.risk_buster.hazard_hackers.user;

import ezen.risk_buster.hazard_hackers.common.auth.JwtProvider;
import ezen.risk_buster.hazard_hackers.common.auth.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public UserService(UserRepository userRepository, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }


    @Transactional
    public void create(SignupRequest request) {
        userRepository.save(new User(
                request.username(),
                request.email(),
                SecurityUtils.sha256Encrypt(request.password())));
    }

//    public void authenticateAndGenerateToken(LoginRequest request) {
//       User user = authenticate(request);
//       String token = generateToken(user);
//        return new LoginRequest(token);
//    }

    public UserResponseDTO findById(Long id) {
        User user = userRepository.findByIdAndIsDeletedFalse(id);
        if (user == null) {
            throw new EntityNotFoundException("user Not Found");
        }
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Transactional
    public UserResponseDTO update(Long id, UserResponseDTO request, String userEmail) {
        User user = userRepository.findByIdAndIsDeletedFalse(id);
        if (user == null) {
            throw new EntityNotFoundException("Comment Not Found");
        }

        User savedUser = userRepository.save(user);

        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Transactional
    public void delete(String userEmail,Long id) {
        User loginUser = userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new EntityNotFoundException("사용자를 찾을 수 없습니다 email: " + userEmail));

        User deleteUser = userRepository.findByIdAndIsDeletedFalse(id);

        if (deleteUser == null) {
            throw new EntityNotFoundException("User Not Found");
        }

        if(!loginUser.getId().equals(deleteUser.getId())) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        deleteUser.softDelete();
        userRepository.save(deleteUser);
    }

    public LoginResponse login(LoginRequest request) {
        User user = authenticate(request);
        String token = generateToken(user);
        return new LoginResponse(token);
    }

    private User authenticate(LoginRequest request) {
        // email 검증
        User user = userRepository.findByEmail(request.userEmail())
                .orElseThrow(() -> new IllegalArgumentException("ID 또는 PW가 틀립니다"));

        // password 검증
        if (!user.authenticate(request.password())) {
            throw new IllegalArgumentException("ID 또는 PW가 틀립니다");
        }

        return user;
    }

    public String generateToken(User user) {
        // 주입받은 JwtProvider 오브젝트를 통해 토큰 발급
        return jwtProvider.createToken(user.getEmail());
    }

    public UserResponseDTO getCurrentUser(String userEmail) {
        User user = userRepository.findByEmailAndIsDeletedFalse(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("잘못된 접근");
        }
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    public void changePassword(String userEmail, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
        if (!user.authenticate(request.oldPassword())){
            throw new IllegalArgumentException("비밀번호 변경에 실패했습니다. 현재 비밀번호를 확인해주세요");
        }

        user.changePassword(request.newPassword());
    }
}