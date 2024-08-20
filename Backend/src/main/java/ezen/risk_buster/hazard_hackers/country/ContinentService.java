package ezen.risk_buster.hazard_hackers.country;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContinentService {
    private final ContinentRepository continentRepository;

    public ContinentService(ContinentRepository continentRepository) {
        this.continentRepository = continentRepository;
    }

    public ContinentResponse create(ContinentRequest request) {
        Continent continent = continentRepository.save(Continent.builder()
                .continentEngNm(request.continentEngNm())
                .continentNm(request.continentNm())
                .build());
        return new ContinentResponse(
                continent.getId(),
                continent.getContinentEngNm(),
                continent.getContinentNm());
    }

    public List<ContinentResponse> findAll() {
        List<Continent> continents = continentRepository.findAll();
        return continents.stream().map(
                c -> new ContinentResponse(
                c.getId(),
                c.getContinentEngNm(),
                c.getContinentNm())
        ).toList();
    }
}
