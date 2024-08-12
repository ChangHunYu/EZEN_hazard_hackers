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
    public void create(UserResponseDTO request) {
        userRepository.save(new User(
                request.id(),
                request.name(),
                request.email(),
                request.password()));
    }

//    public LoginRequest authenticateAndGenerateToken(LoginRequest request) {
////        User user = authenticate(request);
////        String token = generateToken(user);
////        return new LoginRequest(token);
//    }

    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id).orElse(null);
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
        User user = userRepository.findById(id).orElse(null);
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
        public void delete (String email){
            User deleteUser = userRepository.deleteByEmail(email);
            if (deleteUser == null) {
                throw new EntityNotFoundException("User Not Found");
            }

            deleteUser.softDelete();
            userRepository.save(deleteUser);
        }
    }