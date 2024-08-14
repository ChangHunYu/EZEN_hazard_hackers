package ezen.risk_buster.hazard_hackers.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Transactional
    public void create(SignupRequest request) {
        userRepository.save(new User(
                request.username(),
                request.email(),
                request.password()));
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
                .password(user.getPassword())
                .build();
    }

    @Transactional
    public UserResponseDTO update(Long id, UserResponseDTO request) {
        User user = userRepository.findByIdAndIsDeletedFalse(id);
        if (user == null) {
            throw new EntityNotFoundException("Comment Not Found");
        }

        User savedUser = userRepository.save(user);

        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }

    @Transactional
    public void delete(Long id) {
        User deleteUser = userRepository.findByIdAndIsDeletedFalse(id);
        if (deleteUser == null) {
            throw new EntityNotFoundException("User Not Found");
        }

        deleteUser.softDelete();
    }

    public void login(LoginRequest request) {
        User user = userRepository.findByEmailAndIsDeletedFalse(request.userEmail());
        if (user == null) {
            throw new IllegalArgumentException("이메일 또는 패스워드가 유효하지 않습니다.");
        }
        if (!user.getPassword().equals(request.password())) {
            throw new IllegalArgumentException("이메일 또는 패스워드가 유효하지 않습니다.");
        }
    }
}