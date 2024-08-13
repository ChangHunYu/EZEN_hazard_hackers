package ezen.risk_buster.hazard_hackers.itinerary;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ItineraryController {
    private final ItineraryService itineraryService;

    public ItineraryController(ItineraryService itineraryService) {
        this.itineraryService = itineraryService;
    }

    @PostMapping("/itineraryCreate")
    public ItineraryResponse<R> create(@RequestBody ItineraryRequest itinerary){
        return itineraryService.createItineraty(itinerary);
    }

    //단일조회
    @GetMapping("/itinerary/{id}")
    public ItineraryResponse<R> findByItinerary(@PathVariable Long id){
        return itineraryService.findByOne(id);
    }

    //목록조회
    @GetMapping("/itineraries")
    public List<ItineraryResponse<R>> findAll(){
        return itineraryService.findAll();
    }

    //일정삭제
    @DeleteMapping("/itineray/{id}")
    public void DeleteItinerary (@PathVariable Long id){
         itineraryService.DeleteItinerary(id);


    }

}
