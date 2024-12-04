package com.example.pract4_client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.bind.annotation.*;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;

import java.util.Scanner;

@Slf4j
@RequiredArgsConstructor
@ShellComponent
@ShellCommandGroup("Hotel")
public class HotelShellController {

    private final RSocketRequester rSocketRequester;

    @ShellMethod("Get hotel by id")
    public void getHotel(@ShellOption(defaultValue = "1", help = "id of hotel") Long id) {
        Hotel hotel = rSocketRequester
                .route("hotel.one")
                .data(id)
                .retrieveMono(Hotel.class)
                .block();
        if (hotel == null) log.info("No hotel found with id {}", id);
        else log.info("Got hotel {} with name {} and rooms {}", hotel.getId(), hotel.getName(), hotel.getRooms());
    }

    @ShellMethod("Get all hotels")
    public void getAllHotels() {
        log.info("Starting request-stream");
        rSocketRequester
                .route("hotel.all")
                .retrieveFlux(Hotel.class)
                .doOnNext(hotel -> log.info("Got hotel {} with name {} and rooms {}", hotel.getId(), hotel.getName(), hotel.getRooms()))
                .blockLast();

    }

    @ShellMethod("Filter hotels by min rooms")
    public void filterHotels(@ShellOption(defaultValue = "0", help = "Minimal count of rooms") int minRooms) {
        log.info("Starting request-stream with filter min rooms {}", minRooms);
        rSocketRequester
                .route("hotel.filter")
                .data(minRooms)
                .retrieveFlux(Hotel.class)
                .doOnNext(hotel -> log.info("Got hotel {} with name {} and rooms {}", hotel.getId(), hotel.getName(), hotel.getRooms()))
                .blockLast();
    }

    @ShellMethod("Add hotel")
    public void addHotel(
            @ShellOption(defaultValue = "Hotel", help = "Name of hotel") String name,
            @ShellOption(defaultValue = "0", help = "Count of rooms") int rooms) {
        log.info("Starting fire-and-forget for hotel with name {} and count of rooms {}", name, rooms);
        rSocketRequester
                .route("hotel.add")
                .data(new Hotel(name, rooms))
                .send()
                .block();
    }

    @ShellMethod("Open channel of hotels")
    public void hotelChannel() {
        log.info("Starting channel for hotels");
        Scanner scanner = new Scanner(System.in);
        Flux<Hotel> hotelFlux = Flux.create(sink -> {
            while (true) {
                System.out.print("Enter hotel name and number of rooms (or 'exit' to quit): ");
                String input = scanner.nextLine();
                if ("exit".equalsIgnoreCase(input)) {
                    sink.complete();
                    break;
                }

                String[] parts = input.split(" ");
                if (parts.length == 2) {
                    String hotelName = parts[0].trim();
                    int rooms;
                    try {
                        rooms = Integer.parseInt(parts[1].trim());
                        sink.next(new Hotel(hotelName, rooms));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number of rooms. Please try again.");
                    }
                } else {
                    System.out.println("Invalid input format. Please enter 'name number_of_rooms'.");
                }
            }
        });

        rSocketRequester.route("hotel.channel")
                .data(hotelFlux)
                .retrieveFlux(Hotel.class)
                .doOnNext(hotel -> log.info("Got hotel {} with name {} and rooms {}", hotel.getId(), hotel.getName(), hotel.getRooms()))
                .blockLast();
    }
}
