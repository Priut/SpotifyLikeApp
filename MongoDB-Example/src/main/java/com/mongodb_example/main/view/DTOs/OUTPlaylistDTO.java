package com.mongodb_example.main.view.DTOs;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OUTPlaylistDTO extends RepresentationModel<OUTPlaylistDTO> {
    private String uuid;
    private String title;
    private List<MusicDTO> musicList;
}
