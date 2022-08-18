package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JokeDto {
    private int id;
    private boolean error;
    private String category;
    private String setup;
    private String delivery;
    private String lang;
}
