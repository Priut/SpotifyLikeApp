package MusicDBSpringService.Controller.Services;
import MusicDBSpringService.Model.Entities.Music;
import MusicDBSpringService.Model.Entities.Type;
import MusicDBSpringService.Model.Repos.MusicRepository;
import MusicDBSpringService.View.DTOs.MusicDTO;
import MusicDBSpringService.View.Exceptions.CustomExceptions.AlbumIDInvalidException;
import MusicDBSpringService.View.Exceptions.CustomExceptions.AlbuminAlbumException;
import MusicDBSpringService.View.Exceptions.CustomExceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static MusicDBSpringService.Utils.MusicConverter.musicToDto;

@Service
public class MusicService {
    @Autowired
    private MusicRepository musicRepo;

    public List <Music> getAllMusicList(){
        return musicRepo.findAll();
    }

    public Music save(Music music) throws AlbuminAlbumException , AlbumIDInvalidException{
        if(music.getType() != Type.song && music.getAlbum()!=null)
            throw new AlbuminAlbumException("An album cannot contain another album, only songs");
        if(music.getAlbum()!= null && music.getAlbum().getType() != Type.album)
            throw new AlbumIDInvalidException("The album id given is not of an album type.");
        return musicRepo.save(music);
    }
    public Music findById(Integer id) throws ResourceNotFoundException {
        if(musicRepo.findById(id).isPresent())
            return musicRepo.findById(id).get();
        else
            throw new ResourceNotFoundException("Music not found with this id : " + id);
    }

    public void delete(Music music) throws AlbuminAlbumException, AlbumIDInvalidException {
        if(music.getAlbum() != null) {
            //scoatem melodia din lista de melodii din album
            Music album = music.getAlbum();
            Set<Music> songs = album.getMusic_in_album();
            songs.remove(music);
            album.setMusic_in_album(songs);
            save(album);
            //stergem
            music.setAlbum(null);

        }
        musicRepo.delete(music);
    }
    public List<Music> getSongsinAlbum(Music album) throws AlbumIDInvalidException {
        if(album.getType()!=Type.album)
            throw new AlbumIDInvalidException("Id does not belong to an album");
        return new ArrayList<>(album.getMusic_in_album());
    }
    public Page<Music> getAllMusicListPage(Pageable page){
        return musicRepo.findAll(page);
    }
    public Page<Music> getAllMusicList(Pageable page){
        return (Page<Music>) musicRepo.findAll();
    }

    public Page<Music> findByNameContaining(String name, Pageable pagingSort) {
        return musicRepo.findByName(name, pagingSort);
    }
    public List<Music> findByNameContaining(String name) {
        return musicRepo.findByName(name);
    }
    public Page<Music> findByMtype(Type mtype, Pageable pagingSort) {
        return musicRepo.findByMtype(mtype, pagingSort);
    }
    public List<Music> findByMtype(Type mtype) {
        return musicRepo.findByMtype(mtype);
    }
}
