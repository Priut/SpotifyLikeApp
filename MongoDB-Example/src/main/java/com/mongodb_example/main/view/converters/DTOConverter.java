package com.mongodb_example.main.view.converters;

import com.mongodb_example.main.model.User;
import com.mongodb_example.main.view.DTOs.INPlaylistDTO;
import com.mongodb_example.main.view.DTOs.MusicDTO;
import com.mongodb_example.main.view.DTOs.OUTPlaylistDTO;
import com.mongodb_example.main.view.DTOs.UserDTO;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class DTOConverter {
    public static OUTPlaylistDTO inOutPlaylist (INPlaylistDTO inPlaylistDTO){
        OUTPlaylistDTO outPlaylistDTO = new OUTPlaylistDTO();
        outPlaylistDTO.setUuid(inPlaylistDTO.getUuid());
        outPlaylistDTO.setTitle(inPlaylistDTO.getTitle());
        outPlaylistDTO.setMusicList(new ArrayList<>());


        RestTemplate restTemplate = new RestTemplate();
        if(inPlaylistDTO.getMusicIdsList()!=null) {
            for (Integer id : inPlaylistDTO.getMusicIdsList()) {
                String uri = "http://localhost:8080/api/songcollection/songs/" + id;
                ResponseEntity<MusicDTO> response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), MusicDTO.class);
                MusicDTO musicDTO = response.getBody();
                List<MusicDTO> list = outPlaylistDTO.getMusicList();
                list.add(musicDTO);
                outPlaylistDTO.setMusicList(list);
            }
        }

       return outPlaylistDTO;
    }

    public static List<OUTPlaylistDTO> inOutPlaylistList (List<INPlaylistDTO> inPlaylistDTOList){
        List<OUTPlaylistDTO> outPlaylistDTOList = new ArrayList<OUTPlaylistDTO>();
        for (INPlaylistDTO inPlaylistDTO : inPlaylistDTOList){
            outPlaylistDTOList.add(inOutPlaylist(inPlaylistDTO));
        }
        return outPlaylistDTOList;
    }

    public static List<UserDTO> userTouserDTOList (List<User> userList){
        List<UserDTO> userDTOList = new ArrayList<>();
        for (User user: userList) {
            userDTOList.add(new UserDTO(user.getId(),user.getUsername(),user.getFavorites(),inOutPlaylistList(user.getPlaylists())));
        }
        return userDTOList;
    }

    public static UserDTO userTouserDTO (User user){
        return new UserDTO(user.getId(),user.getUsername(),user.getFavorites(),inOutPlaylistList(user.getPlaylists()));
    }
}
