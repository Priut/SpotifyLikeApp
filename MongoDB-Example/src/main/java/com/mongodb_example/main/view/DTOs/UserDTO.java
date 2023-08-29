package com.mongodb_example.main.view.DTOs;

import com.mongodb_example.main.view.DTOs.INPlaylistDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@NoArgsConstructor
public class UserDTO extends RepresentationModel<UserDTO> {
    private String id;
    private String username;
    private List<Integer> favorites;
    private List<OUTPlaylistDTO> playlists;

    public UserDTO(String id,String username, List<Integer> favorites, List<OUTPlaylistDTO> playlists) {
        super();
        this.id = id;
        this.username = username;
        this.favorites = favorites;
        this.playlists = playlists;
    }
}