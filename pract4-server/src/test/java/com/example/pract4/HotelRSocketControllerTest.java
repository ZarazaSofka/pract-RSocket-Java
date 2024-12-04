package com.example.pract4;

import com.example.pract4.Hotel;
import com.example.pract4.HotelRepository;
import com.example.pract4.HotelRSocketController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@SpringBootTest
class HotelRSocketControllerTest {
    @Mock
    private HotelRepository hotelRepository;
    @InjectMocks
    private HotelRSocketController hotelRSocketController;
    private Hotel hotel1 = new Hotel(1L, "Hotel One", 5);
    private Hotel hotel2 = new Hotel(2L, "Hotel Two", 10);

    @Test
    void testGetHotel() {
        when(hotelRepository.findById(1L)).thenReturn(Mono.just(hotel1));

        Mono<Hotel> result = hotelRSocketController.getHotel(1L);

        StepVerifier.create(result)
                .expectNext(hotel1)
                .verifyComplete();

        verify(hotelRepository).findById(1L);
    }

    @Test
    void testListHotels() {
        when(hotelRepository.findAll()).thenReturn(Flux.just(hotel1, hotel2));

        Flux<Hotel> result = hotelRSocketController.listHotels();

        StepVerifier.create(result)
                .expectNext(hotel1)
                .expectNext(hotel2)
                .verifyComplete();

        verify(hotelRepository).findAll();
    }

    @Test
    void testAddHotel() {
        when(hotelRepository.save(hotel1)).thenReturn(Mono.just(hotel1));

        Mono<Void> result = hotelRSocketController.addHotel(hotel1);

        StepVerifier.create(result)
                .verifyComplete();

        verify(hotelRepository).save(hotel1);
    }

    @Test
    void testHotelChannel() {
        when(hotelRepository.save(hotel1)).thenReturn(Mono.just(hotel1));
        when(hotelRepository.save(hotel2)).thenReturn(Mono.just(hotel2));

        Flux<Hotel> result = hotelRSocketController.hotelChannel(Flux.just(hotel1, hotel2));

        StepVerifier.create(result)
                .expectNext(hotel1)
                .expectNext(hotel2)
                .verifyComplete();

        verify(hotelRepository).save(hotel1);
        verify(hotelRepository).save(hotel2);
    }

    @Test
    void testFilterHotels() {
        when(hotelRepository.findAll()).thenReturn(Flux.just(hotel1, hotel2));

        Flux<Hotel> result = hotelRSocketController.filterHotels(6);

        StepVerifier.create(result)
                .expectNext(hotel2)
                .verifyComplete();

        verify(hotelRepository).findAll();
    }
}

