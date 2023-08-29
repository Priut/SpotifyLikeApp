package MusicDBSpringService.View.DTOs;

import org.springframework.hateoas.RepresentationModel;

public class ArtistDTO extends RepresentationModel<ArtistDTO> {
    private String uuid;
    private String name;
    private Boolean is_active;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIs_active() {
        return is_active;
    }

    public void setIs_active(Boolean is_active) {
        this.is_active = is_active;
    }

    //private Set<Music> music;
}
