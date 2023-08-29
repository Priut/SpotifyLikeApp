package MusicDBSpringService.Model.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name="music")
public
class Music {

    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Integer id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Genre genre;

    private Integer release_year;

    @Enumerated(EnumType.STRING)
    private Type mtype;

    //private Integer id_album;

    @JsonBackReference
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="id_album")
    private Music album;

    @OneToMany(mappedBy="album")
    private Set<Music> music_in_album = new HashSet<Music>();

    public Music() {}

    public Music(String name, Genre genre, Integer release_year, Type mtype,Music album) {

        this.name = name;
        this.genre = genre;
        this.release_year = release_year;
        this.mtype = mtype;
        this.album = album;
    }



    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Genre getGenre() {
        return this.genre;
    }

    public Integer getReleaseYear() {
        return this.release_year;
    }

    public Type getType() {
        return this.mtype;
    }

    //public Integer getIdAlbum() {
        //return this.id_album;
   // }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGenre(Genre genre) {
        this.genre=genre;
    }

    public void setReleaseYear(Integer release_year) {
        this.release_year = release_year;
    }

    public void setType(Type mtype) {
        this.mtype = mtype;
    }

    public Music getAlbum() {
        return album;
    }

    public void setAlbum(Music album) {
        this.album = album;
    }
    public Set<Music> getMusic_in_album() {
        return music_in_album;
    }

    public void setMusic_in_album(Set<Music> music_in_album) {
        this.music_in_album = music_in_album;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof Music))
            return false;
        Music music = (Music) o;
        return Objects.equals(this.id, music.id) && Objects.equals(this.name, music.name)
                && Objects.equals(this.genre, music.genre) && Objects.equals(this.release_year, music.release_year)
                && Objects.equals(this.mtype, music.mtype) && Objects.equals(this.album, music.album);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, this.genre, this.release_year, this.mtype, this.album);
    }

    @Override
    public String toString() {
        return "Music{" + "id=" + this.id + ", name='" + this.name + '\'' + ", genre='" + this.genre + '\'' + ", release year='" + this.release_year + '\'' + ", type='" + this.mtype + '\'' + ", album='" + this.album+ '\'' +'}';
    }
}
