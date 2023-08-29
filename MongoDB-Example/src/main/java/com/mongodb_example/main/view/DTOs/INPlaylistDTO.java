package com.mongodb_example.main.view.DTOs;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class INPlaylistDTO {
    private String uuid;
    private String title;
    private List<Integer> musicIdsList;
}