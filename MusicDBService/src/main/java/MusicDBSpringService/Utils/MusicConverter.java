package MusicDBSpringService.Utils;

import MusicDBSpringService.Controller.Services.MusicService;
import MusicDBSpringService.Model.Entities.Music;
import MusicDBSpringService.View.DTOs.MusicDTO;
import MusicDBSpringService.View.Exceptions.CustomExceptions.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class MusicConverter {
    public static MusicDTO musicToDto(Music music) {
        MusicDTO musicDTO = new MusicDTO();
        musicDTO.setId(music.getId());
        musicDTO.setName(music.getName());
        musicDTO.setGenre(music.getGenre());
        if(music.getAlbum() != null)
            musicDTO.setId_album(music.getAlbum().getId());
        musicDTO.setMtype(music.getType());
        musicDTO.setRelease_year(music.getReleaseYear());

        return musicDTO;
    }



    public static List<MusicDTO> musicListToMusicDTOList(List<Music> allMusic){
        List<MusicDTO> allMusicDTO = new ArrayList<MusicDTO>();
        for(int i = 0; i < allMusic.size(); i++)
        {
            allMusicDTO.add(musicToDto(allMusic.get(i)));
        }
        return allMusicDTO;
    }
    public static Music DtoToMusic(MusicDTO musicDTO, MusicService musicService) throws ResourceNotFoundException {
        Music music = new Music();
        music.setGenre(musicDTO.getGenre());
        music.setId(musicDTO.getId());
        music.setName(musicDTO.getName());
        music.setType(musicDTO.getMtype());
        music.setReleaseYear(musicDTO.getRelease_year());
        if (musicDTO.getId_album() != null) {
            music.setAlbum(musicService.findById(musicDTO.getId_album()));
        }
        return music;
    }
}
