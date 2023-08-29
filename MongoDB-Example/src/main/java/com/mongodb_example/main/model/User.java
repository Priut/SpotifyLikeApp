package com.mongodb_example.main.model;

import com.mongodb_example.main.view.DTOs.INPlaylistDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "Playlists")
public class User {
    @Id
    private String id;
    private String username;
    private List<Integer> favorites;
    private List<INPlaylistDTO> playlists;

    public User(String username, List<Integer> favorites, List<INPlaylistDTO> playlists) {
        super();
        this.username = username;
        this.favorites = favorites;
        this.playlists = playlists;
    }
}
