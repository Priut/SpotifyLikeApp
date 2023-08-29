package MusicDBSpringService.View.DTOs;

import MusicDBSpringService.Model.Entities.Genre;
import MusicDBSpringService.Model.Entities.Type;
import org.springframework.hateoas.RepresentationModel;


public class MusicDTO extends RepresentationModel<MusicDTO> {

    private Integer id;

    private String name;

    private Genre genre;

    private Integer release_year;

    private Type mtype;

    private Integer id_album;

    public MusicDTO() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Integer getRelease_year() {
        return release_year;
    }

    public void setRelease_year(Integer release_year) {
        this.release_year = release_year;
    }

    public Type getMtype() {
        return mtype;
    }

    public void setMtype(Type mtype){
            this.mtype = mtype;

    }

    public Integer getId_album() {
        return id_album;
    }

    public void setId_album(Integer id_album) {
        this.id_album = id_album;
    }
}