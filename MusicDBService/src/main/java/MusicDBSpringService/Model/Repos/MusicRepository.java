package MusicDBSpringService.Model.Repos;
import MusicDBSpringService.Model.Entities.Music;
import MusicDBSpringService.Model.Entities.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MusicRepository extends JpaRepository<Music, Integer> {
    Page<Music> findByName(String name, Pageable pageable);
    List<Music> findByName(String name);
    Page<Music> findByMtype(Type mtype, Pageable pageable);
    List<Music> findByMtype(Type mtype);
}