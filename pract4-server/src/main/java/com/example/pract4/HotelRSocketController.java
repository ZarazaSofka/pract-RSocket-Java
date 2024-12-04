package com.example.pract4;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@MessageMapping("hotel")
@RequiredArgsConstructor
@Controller
public class HotelRSocketController {

    private final HotelRepository hotelRepository;

    // Request-Response
    @MessageMapping("one")
    public Mono<Hotel> getHotel(Long id) {
        System.out.println("Finding hotel with id " + id);
        return hotelRepository.findById(id);
    }

    // Request-Stream
    @MessageMapping("all")
    public Flux<Hotel> listHotels() {
        System.out.println("Finding all hotels");
        return hotelRepository.findAll();
    }

    // Fire-and-Forget
    @MessageMapping("add")
    public Mono<Void> addHotel(Hotel hotel) {
        System.out.println("Saving hotel " + hotel);
        return hotelRepository.save(hotel)
                .doOnNext(savedHotel -> System.out.println("Saved hotel " + savedHotel))
                .then();
    }

    // Channel
    @MessageMapping("channel")
    public Flux<Hotel> hotelChannel(Flux<Hotel> hotelFlux) {
        System.out.println("Creating channel");
        return hotelFlux.doOnNext(hotel -> System.out.println("Saving hotel " + hotel))
                .flatMap(hotelRepository::save);
    }

    // Request-Stream с фильтрацией
    @MessageMapping("filter")
    public Flux<Hotel> filterHotels(int minRooms) {
        System.out.println("Finding hotels with min rooms " + minRooms);
        return hotelRepository.findAll()
                .filter(hotel -> hotel.getRooms() >= minRooms);

    }
}
