package MusicDBSpringService.Utils;

import MusicDBSpringService.Controller.Services.ArtistService;
import MusicDBSpringService.Controller.Services.MusicService;
import MusicDBSpringService.Model.Entities.Artist;
import MusicDBSpringService.Model.Entities.Music;
import MusicDBSpringService.View.DTOs.ArtistDTO;
import MusicDBSpringService.View.DTOs.MusicDTO;
import MusicDBSpringService.View.Exceptions.CustomExceptions.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class ArtistConverter {
    public static ArtistDTO artistToDto(Artist artist) {
        ArtistDTO artistDTO = new ArtistDTO();
        artistDTO.setUuid(artist.getUuid());
        artistDTO.setName(artist.getName());
        artistDTO.setIs_active(artist.getActive());

        return artistDTO;
    }



    public static List<ArtistDTO> artistListToArtistDTOList(List<Artist> allArtists){
        List<ArtistDTO> allArtistsDTO = new ArrayList<ArtistDTO>();
        for(int i = 0; i < allArtists.size(); i++)
        {
            allArtistsDTO.add(artistToDto(allArtists.get(i)));
        }
        return allArtistsDTO;
    }
    public static Artist DtoToAtrist(ArtistDTO artistDTO, ArtistService artistService){

        Artist artist = new Artist();
        artist.setActive(artistDTO.getIs_active());
        artist.setUuid(artistDTO.getUuid());
        artist.setName(artistDTO.getName());
        try{
            artist.setMusic(artistService.findById(artistDTO.getUuid()).getMusic());
        }catch (ResourceNotFoundException e)
        {
        }
        return artist;
    }
}
