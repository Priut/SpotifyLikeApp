package MusicDBSpringService.View.HATEOAS;

import MusicDBSpringService.Controller.Controllers.ArtistController;
import MusicDBSpringService.Controller.Controllers.MusicController;
import MusicDBSpringService.Controller.Services.ArtistService;
import MusicDBSpringService.Model.Entities.Artist;
import MusicDBSpringService.View.DTOs.ArtistDTO;
import MusicDBSpringService.View.DTOs.MusicDTO;
import MusicDBSpringService.View.Exceptions.CustomExceptions.*;
import org.springframework.hateoas.Link;

import java.util.List;
import java.util.Optional;

import static MusicDBSpringService.Utils.ArtistConverter.DtoToAtrist;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ArtistHATEOAS {
    public static void addHateoas(ArtistDTO artist, ArtistService artistService) throws PageInvalidException, AlbumIDInvalidException, SessionExpiredException, JWTException, ResourceNotFoundException {

        artist.add(linkTo(methodOn(ArtistController.class).getArtistByUuid(artist.getUuid())).withSelfRel());
        artist.add(linkTo(methodOn(ArtistController.class).getAllArtists()).withRel("parent"));
        Artist a = DtoToAtrist(artist,artistService);
        if(a.getMusic()!=null && a.getMusic().size()!=0) {
            artist.add(linkTo(methodOn(ArtistController.class).getArtistMusic(artist.getUuid())).withRel("songs"));
        }
        else {
            artist.add(linkTo(methodOn(ArtistController.class).addSong(Optional.empty(),artist.getUuid(),null)).withRel("addSongs").withType("PUT"));
        }
    }
    public static Link addHateoas(List<ArtistDTO> allArtistDTO, ArtistService artistService) throws JWTException, SessionExpiredException, PageInvalidException, AlbumIDInvalidException, ResourceNotFoundException {
        for (ArtistDTO artist : allArtistDTO) {
            addHateoas(artist, artistService);
        }

        return linkTo(methodOn(ArtistController.class).getAllArtists()).withSelfRel();
    }
}
