package com.mongodb_example.main.utils;

import com.mongodb_example.main.controller.PlaylistController;
import com.mongodb_example.main.utils.Exceptions.CustomExceptions.JWTException;
import com.mongodb_example.main.utils.Exceptions.CustomExceptions.ResourceNotFoundException;
import com.mongodb_example.main.utils.Exceptions.CustomExceptions.SessionExpiredException;
import com.mongodb_example.main.view.DTOs.OUTPlaylistDTO;
import com.mongodb_example.main.view.DTOs.UserDTO;
import org.springframework.hateoas.Link;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class UserHATEOAS {
    public static void addHateoas( UserDTO userDTO) throws JWTException, SessionExpiredException, ResourceNotFoundException {

        userDTO.add(linkTo(methodOn(PlaylistController.class).getUserbyId(userDTO.getId())).withSelfRel());
        userDTO.add(linkTo(methodOn(PlaylistController.class).getAllUsers(Optional.empty())).withRel("parent"));
        addHateoas(userDTO.getPlaylists(),userDTO.getId());
    }

    public static Link addHateoas(List<UserDTO> allUsers) throws JWTException, SessionExpiredException, ResourceNotFoundException {
        for (UserDTO u : allUsers) {
            addHateoas(u);
        }
        return linkTo(methodOn(PlaylistController.class).getAllUsers(Optional.empty())).withSelfRel();
    }
    public static void addHateoas( OUTPlaylistDTO playlistDTO, String u_id) throws JWTException, SessionExpiredException, ResourceNotFoundException {

        playlistDTO.add(linkTo(methodOn(PlaylistController.class).getUserPlaylist(u_id,playlistDTO.getUuid())).withSelfRel());
        playlistDTO.add(linkTo(methodOn(PlaylistController.class).getUserPlaylists(u_id)).withRel("parent"));
    }
    public static Link addHateoas( List<OUTPlaylistDTO> playlistDTOs, String u_id) throws JWTException, SessionExpiredException, ResourceNotFoundException {
        for (OUTPlaylistDTO p : playlistDTOs) {
            addHateoas(p,u_id);
        }
        return linkTo(methodOn(PlaylistController.class).getUserPlaylists(u_id)).withSelfRel();
    }
}


