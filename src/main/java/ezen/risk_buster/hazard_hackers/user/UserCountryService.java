package ezen.risk_buster.hazard_hackers.user;

import ezen.risk_buster.hazard_hackers.country.Country;
import ezen.risk_buster.hazard_hackers.country.CountryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCountryService {

    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final UserCountryRepostiory userCountryRepostiory;

    public UserCountryService(UserRepository userRepository, CountryRepository countryRepository, UserCountryRepostiory userCountryRepostiory) {
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.userCountryRepostiory = userCountryRepostiory;
    }

    @Transactional
    public UserCountryResponseDto create(String userEmail, UserCountryRequestDto request) {

        User user = userRepository.findByEmailAndIsDeletedFalse(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("해당 유저가 존재하지 않습니다. " + userEmail);
        }
        if (!user.getEmail().equals(request.email())) {
            throw new IllegalArgumentException("잘못된 유저 email: " + request.email());
        }

        Country country = countryRepository.findByIdAndIsDeletedFalse(request.countryId());
        if (country == null) {
            throw new IllegalArgumentException("해당 국가가 존재하지 않습니다. " + request.countryId());
        }
        if (!country.getId().equals(request.countryId())) {
            throw new IllegalArgumentException("잘못된 국가 country: " + request.countryId());
        }

        UserCountry userCountry = UserCountry.builder()
                .user(user)
                .country(country)
                .build();

        UserCountry savedUserCountry = userCountryRepostiory.save(userCountry);

        return new UserCountryResponseDto(
                savedUserCountry.getId(),
                savedUserCountry.getUser().getEmail(),
                savedUserCountry.getCountry().getId(),
                savedUserCountry.getCountry().getCountryName()
        );
    }

    public List<UserCountryResponseDto> findAll(String userEmail) {

        User user = userRepository.findByEmailAndIsDeletedFalse(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("해당 유저가 존재하지 않습니다. " + userEmail);
        }

        List<UserCountry> userCountries = userCountryRepostiory.findByUser_EmailAndIsDeletedFalse(userEmail);

        return userCountries.stream().map(
                userCountry -> new UserCountryResponseDto(
                        userCountry.getId(),
                        userCountry.getUser().getEmail(),
                        userCountry.getCountry().getId(),
                        userCountry.getCountry().getCountryName()))
                .toList();
    }

    public UserCountryResponseDto findOne(String userEmail, Long id) {

        User user = userRepository.findByEmailAndIsDeletedFalse(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("해당 유저가 존재하지 않습니다. " + userEmail);
        }

        UserCountry userCountry = userCountryRepostiory.findByIdAndIsDeletedFalse(id);

        return new UserCountryResponseDto(
                userCountry.getId(),
                userCountry.getUser().getEmail(),
                userCountry.getCountry().getId(),
                userCountry.getCountry().getCountryName()
        );
    }

    @Transactional
    public UserCountryResponseDto update(String userEmail, Long id, UserCountryRequestDto request) {

        User user = userRepository.findByEmailAndIsDeletedFalse(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("해당 유저가 존재하지 않습니다. " + userEmail);
        }
        if (!user.getEmail().equals(request.email())) {
            throw new IllegalArgumentException("잘못된 유저 email: " + request.email());
        }

        Country country = countryRepository.findByIdAndIsDeletedFalse(request.countryId());
        if (country == null) {
            throw new IllegalArgumentException("해당 국가가 존재하지 않습니다. " + request.countryId());
        }
        if (!country.getId().equals(request.countryId())) {
            throw new IllegalArgumentException("잘못된 국가 country: " + request.countryId());
        }

        UserCountry userCountry = UserCountry.builder()
                .user(user)
                .country(country)
                .build();

        UserCountry updatedUserCountry = userCountryRepostiory.save(userCountry);

        return new UserCountryResponseDto(
                updatedUserCountry.getId(),
                updatedUserCountry.getUser().getEmail(),
                updatedUserCountry.getCountry().getId(),
                updatedUserCountry.getCountry().getCountryName()
        );
    }

    @Transactional
    public void delete(String userEmail, Long id) {

        User user = userRepository.findByEmailAndIsDeletedFalse(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("해당 유저가 존재하지 않습니다. " + userEmail);
        }

        UserCountry userCountry = userCountryRepostiory.findByIdAndIsDeletedFalse(id);

        userCountry.softDelete();
        userCountryRepostiory.delete(userCountry);
    }
}
