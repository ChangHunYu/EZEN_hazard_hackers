package ezen.risk_buster.hazard_hackers.itinerary;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ItineraryController {
    private final ItineraryService itineraryService;

    public ItineraryController(ItineraryService itineraryService) {
        this.itineraryService = itineraryService;
    }

    //일정생성
    @PostMapping
    public ResponseEntity<String> create(@RequestBody ItineraryRequest itinerary){
        ItineraryResponse itineraryResponse = itineraryService.create(itinerary);
        return new ResponseEntity<>(itineraryResponse.title()+"일정생성에 성공했습니다.", HttpStatus.OK);
    }

    //단일조회
    @GetMapping("/itinerary/{id}")
    public ResponseEntity<ItineraryResponse> findByItinerary(@PathVariable Long id){
        ItineraryResponse itineraryResponse = itineraryService.findById(id);
        return new ResponseEntity<>(itineraryResponse, HttpStatus.OK) ;
    }

    //목록조회
    @GetMapping("/itineraries")
    public ResponseEntity<List<ItineraryResponse>> findAll(){
        List<ItineraryResponse> itineraryResponses = itineraryService.findAll();

        return new ResponseEntity<>(itineraryResponses, HttpStatus.OK);
    }

    //일정수정
    @PutMapping("/itineray/{id}")
    public ResponseEntity<ItineraryResponse> update(@PathVariable Long id,
                                                    @RequestBody ItineraryResponse request) {
        ItineraryResponse responseDTO = itineraryService.update(id, request);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    //일정삭제
    @DeleteMapping("/itineray/{id}")
    public ResponseEntity<String> delete (@PathVariable Long id){
        itineraryService.deleteItinerary(id);

        return new ResponseEntity<>("Deleted Success",HttpStatus.OK);
    }

}
