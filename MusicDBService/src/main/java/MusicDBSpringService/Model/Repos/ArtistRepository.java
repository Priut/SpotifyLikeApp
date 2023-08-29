
package MusicDBSpringService.Model.Repos;
import MusicDBSpringService.Model.Entities.Artist;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ArtistRepository extends JpaRepository<Artist, String> {

}