package MusicDBSpringService.Controller.Services;

import MusicDBSpringService.Model.Entities.Artist;
import MusicDBSpringService.Model.Entities.Music;
import MusicDBSpringService.Model.Repos.ArtistRepository;
import MusicDBSpringService.View.Exceptions.CustomExceptions.ResourceNotFoundException;
import MusicDBSpringService.View.Exceptions.CustomExceptions.SongNotFoundForArtistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ArtistService {
    @Autowired
    private ArtistRepository artistRepository;

    public List<Artist> getAllArtistList(){
        return artistRepository.findAll();
    }

    public Artist save(Artist artist){
        return artistRepository.save(artist);
    }
    public Artist findById(String id) throws ResourceNotFoundException {
        if(artistRepository.findById(id).isPresent())
            return artistRepository.findById(id).get();
        else
            throw new ResourceNotFoundException("Artist not found with this id : " + id);

    }

    public void delete(Artist artist){
        artist.setMusic(new HashSet<Music>());
        artistRepository.delete(artist);
    }
    public Artist addSong(Artist artist, Music music){
        Set<Music> allMusic = artist.getMusic();
        allMusic.add(music);
        artist.setMusic(allMusic);
        return save(artist);
    }
    public Artist deleteSong(Artist artist, Music music) throws SongNotFoundForArtistException {
        if (!artist.getMusic().contains(music))
            throw new SongNotFoundForArtistException("Song not found for this singer");
        artist.getMusic().remove(music);
        return save(artist);
    }
}