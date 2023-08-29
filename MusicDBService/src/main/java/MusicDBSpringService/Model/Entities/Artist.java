package MusicDBSpringService.Model.Entities;

import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;
import java.util.Set;
import javax.persistence.*;


@Entity
@Table(name="artists")
public
class Artist{

    private @Id String uuid;
    private String name;
    private Boolean is_active;
    @ManyToMany()
    @JoinTable(
            name = "joinMA",
            joinColumns = @JoinColumn(name = "a_id"),
            inverseJoinColumns = @JoinColumn(name = "m_id")
    )
    private Set<Music> music;

    public Set<Music> getMusic() {
        return music;
    }
    public void setMusic(Set<Music> music) {
        this.music=music;
    }

    public Artist() {}

    public Artist(String uuid, String name, Boolean is_active,Set<Music> music) {

        this.uuid = uuid;
        this.name = name;
        this.is_active = is_active;
        this.music = music;
    }



    public String getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public Boolean getActive() {
        return this.is_active;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(Boolean active) {
        this.is_active=active;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof Artist))
            return false;
        Artist artist = (Artist) o;
        return Objects.equals(this.uuid, artist.uuid) && Objects.equals(this.name, artist.name)
                && Objects.equals(this.is_active, artist.is_active) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid, this.name, this.is_active);
    }

    @Override
    public String toString() {
        return "Artist{" + "uuid=" + this.uuid + ", name='" + this.name + '\'' + ", is_active='" + this.is_active + '\'' +'}';
    }
}

