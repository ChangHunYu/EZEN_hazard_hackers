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
    public Itinerary create(@RequestBody Itinerary itinerary){
        return itineraryService.createItineraty(itinerary);
    }

    //단일조회
    @GetMapping("/itinerary/{itineratyId}")
    ItineraryResponse findByItinerary(@PathVariable Long id){
        return itineraryService.findByOne(id);
    }

    //목록조회
    @GetMapping("/itineraries")
    public List<Itinerary> findAll(List<Itinerary> itineraries){
        return itineraryService.findAll();
    }

}
