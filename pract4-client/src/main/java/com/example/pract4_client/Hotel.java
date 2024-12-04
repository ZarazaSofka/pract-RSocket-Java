package com.example.pract4_client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {
    private Long id;
    private String name;
    private int rooms;

    public Hotel(String name, int rooms) {
        this.name = name;
        this.rooms = rooms;
    }
}
