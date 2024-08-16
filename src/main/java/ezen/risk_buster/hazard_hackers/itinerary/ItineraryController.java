package ezen.risk_buster.hazard_hackers.itinerary;

import ezen.risk_buster.hazard_hackers.common.auth.LoginUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/itinerary")
@RestController
public class ItineraryController {
    private final ItineraryService itineraryService;

    public ItineraryController(ItineraryService itineraryService) {
        this.itineraryService = itineraryService;
    }

    //일정생성
    @PostMapping
    public ResponseEntity<String> create(@RequestBody ItineraryRequest itinerary, @LoginUser String userEmail){
        ItineraryResponse itineraryResponse = itineraryService.create(itinerary, userEmail);
        return new ResponseEntity<>(itineraryResponse.title()+"일정생성에 성공했습니다.", HttpStatus.OK);
    }

    //단일조회
    @GetMapping("/{id}")
    public ResponseEntity<ItineraryResponse> findByItinerary(@PathVariable Long id, @LoginUser String userEmail){
        ItineraryResponse itineraryResponse = itineraryService.findById(userEmail, id);
        return new ResponseEntity<>(itineraryResponse, HttpStatus.OK) ;
    }

    //목록조회
    @GetMapping
    public ResponseEntity<List<ItineraryResponse>> findAll(@LoginUser String userEmail){
        List<ItineraryResponse> itineraryResponses = itineraryService.findAll(userEmail);

        return new ResponseEntity<>(itineraryResponses, HttpStatus.OK);
    }

    //일정수정
    @PutMapping("/{id}")
    public ResponseEntity<ItineraryResponse> update(@PathVariable Long id,
                                                    @RequestBody ItineraryRequest request,
                                                    @LoginUser String userEmail) {
        ItineraryResponse responseDTO = itineraryService.update(id, request, userEmail);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    //일정삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete (@PathVariable Long id, @LoginUser String userEmail ){
        itineraryService.deleteItinerary(id, userEmail);

        return new ResponseEntity<>("Deleted Success", HttpStatus.OK);
    }

}
