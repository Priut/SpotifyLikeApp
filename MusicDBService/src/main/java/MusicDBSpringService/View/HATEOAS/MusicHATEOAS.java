package MusicDBSpringService.View.HATEOAS;

import MusicDBSpringService.Controller.Controllers.MusicController;
import MusicDBSpringService.Model.Entities.Type;
import MusicDBSpringService.View.DTOs.MusicDTO;
import MusicDBSpringService.View.Exceptions.CustomExceptions.AlbumIDInvalidException;
import MusicDBSpringService.View.Exceptions.CustomExceptions.PageInvalidException;
import MusicDBSpringService.View.Exceptions.CustomExceptions.ResourceNotFoundException;
import org.springframework.hateoas.Link;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class MusicHATEOAS {
    public static void addHateoas( MusicDTO music) throws ResourceNotFoundException, AlbumIDInvalidException, PageInvalidException {

        music.add(linkTo(methodOn(MusicController.class).getMusic(music.getId())).withSelfRel());
        music.add(linkTo(methodOn(MusicController.class).getAllMusic(null,null,Optional.empty(),Optional.empty(),null)).withRel("parent"));
        if(music.getMtype() == Type.album)
            music.add(linkTo(methodOn(MusicController.class).getSongsinAlbum(music.getId())).withRel("songs_in_album").withType("GET"));
    }

    public static Link addHateoas(List<MusicDTO> allMusicDTO) throws ResourceNotFoundException, AlbumIDInvalidException, PageInvalidException {
            for (MusicDTO musicDTO : allMusicDTO) {
                addHateoas(musicDTO);
            }
        return linkTo(methodOn(MusicController.class).getAllMusic(null,null,Optional.empty(),Optional.empty(),null)).withSelfRel();
    }
}
