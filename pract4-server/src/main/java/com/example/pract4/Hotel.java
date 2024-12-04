package com.example.pract4;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("hotels")
public class Hotel {

    @Id
    private Long id;

    private String name;
    private int rooms;
}
