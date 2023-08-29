package MusicDBSpringService.Controller.Controllers;

import java.util.*;
import java.util.regex.Pattern;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;

import MusicDBSpringService.Controller.Services.ArtistService;
import MusicDBSpringService.Controller.Services.MusicService;
import MusicDBSpringService.Model.Entities.Artist;
import MusicDBSpringService.Model.Entities.Music;

import MusicDBSpringService.Model.Entities.Type;
import MusicDBSpringService.Utils.SOAP.SoapConfig;
import MusicDBSpringService.View.DTOs.MusicDTO;
import MusicDBSpringService.View.Exceptions.CustomExceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import static MusicDBSpringService.Utils.MusicConverter.*;
import static MusicDBSpringService.View.HATEOAS.MusicHATEOAS.addHateoas;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/api/songcollection")
public class MusicController {
    @Autowired
    private MusicService musicService;
    @Autowired
    private ArtistService artistService;

    @Autowired
    private SoapConfig soapConfig;

    public String[] Authorize(Optional<String> token) throws JWTException,SessionExpiredException {
        String auth_string;
        if(token.isPresent()){
            try{
                auth_string = soapConfig.soapClient(soapConfig.marshaller()).AuthorizeUser(token.get());
                if(auth_string.startsWith("Error:Signature has expired"))
                    throw new SessionExpiredException("Session expired. Please log in again.");
                else if (auth_string.startsWith("Error"))
                    throw new JWTException("Something occured.");

            }
            catch (JAXBException e) {
                throw new JWTException("Something occured.");
            }
        }
        else throw new JWTException("You are not authorized");
        Pattern pattern = Pattern.compile("\\|\\|");
        return pattern.split(auth_string);
    }


    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }
    @GetMapping(value =  "/songs")
    public ResponseEntity <?> getAllMusic(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Type mtype,
            @RequestParam() Optional<Integer> page,
            @RequestParam(defaultValue = "3") Optional<Integer>  size,
            @RequestParam() Optional<String[]> sort) throws ResourceNotFoundException, AlbumIDInvalidException, PageInvalidException {


        List<Music> allMusic = new ArrayList<Music>();
        if(page.isPresent() && sort.isPresent()) {
            List<Order> orders = new ArrayList<Order>();

            if (sort.get()[0].contains(",")) {
                for (String sortOrder : sort.get()) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
                }
            } else {
                orders.add(new Order(getSortDirection(sort.get()[1]), sort.get()[0]));
            }
            Pageable pagingSort = PageRequest.of(page.get(), size.get(), Sort.by(orders));

            Page<Music> pageMusic;
            if (name == null && mtype == null)
                pageMusic = musicService.getAllMusicListPage(pagingSort);
            else if(name != null)
                pageMusic = musicService.findByNameContaining(name, pagingSort);
            else pageMusic = musicService.findByMtype(mtype, pagingSort);

            allMusic = pageMusic.getContent();
            List<MusicDTO> allMusicDTO = musicListToMusicDTOList(allMusic);
            if(page.get()<0 || page.get()>pageMusic.getTotalPages())
                throw new PageInvalidException("Pagina nu exista!");

            for (MusicDTO musicDTO : allMusicDTO) {
                addHateoas(musicDTO);
            }
            if(page.get()==0){
                Link l1 = linkTo(methodOn(MusicController.class).getAllMusic(null,null, page,size,sort)).withSelfRel();
                Link l2 = linkTo(methodOn(MusicController.class).getAllMusic(null,null, Optional.of(page.get()+1),size,sort)).withRel("next");
                return ResponseEntity.ok(CollectionModel.of(allMusicDTO,l1,l2));
            }
            else if(page.get()==pageMusic.getTotalPages()-1){
                Link l1 = linkTo(methodOn(MusicController.class).getAllMusic(null,null, page,size,sort)).withSelfRel();
                Link l2 = linkTo(methodOn(MusicController.class).getAllMusic(null, null,Optional.of(page.get()-1),size,sort)).withRel("previous");
                return ResponseEntity.ok(CollectionModel.of(allMusicDTO,l1,l2));
            }
            else{
                Link l1 = linkTo(methodOn(MusicController.class).getAllMusic(null,null, page,size,sort)).withSelfRel();
                Link l2 = linkTo(methodOn(MusicController.class).getAllMusic(null, null,Optional.of(page.get()+1),size,sort)).withRel("next");
                Link l3 = linkTo(methodOn(MusicController.class).getAllMusic(null, null,Optional.of(page.get()-1),size,sort)).withRel("previous");
                return ResponseEntity.ok(CollectionModel.of(allMusicDTO,l1,l2,l3));
            }
        }
        else{
            if (name == null && mtype == null)
                allMusic = musicService.getAllMusicList();
            else if(name != null)
                allMusic = musicService.findByNameContaining(name);
            else allMusic = musicService.findByMtype(mtype);
            List<MusicDTO> allMusicDTO = musicListToMusicDTOList(allMusic);

            Link allMusicLink = addHateoas(allMusicDTO);
            return ResponseEntity.ok(CollectionModel.of(allMusicDTO,allMusicLink));
        }
    }



    @GetMapping("/songs/{id}")
    public HttpEntity<?> getMusic( @PathVariable("id") Integer id) throws ResourceNotFoundException, AlbumIDInvalidException, PageInvalidException {
        Music music = musicService.findById(id);
        MusicDTO musicDTO = musicToDto(music);
        addHateoas(musicDTO);
        return new ResponseEntity<>(musicDTO, HttpStatus.OK);
    }

    @GetMapping("/album/{id}")
    public HttpEntity<?> getSongsinAlbum(@PathVariable("id") Integer id) throws ResourceNotFoundException, AlbumIDInvalidException, PageInvalidException {
        List<Music> songs = musicService.getSongsinAlbum(musicService.findById(id));
        List<MusicDTO> musicDTO = musicListToMusicDTOList(songs);
        addHateoas(musicDTO);
        return new ResponseEntity<>(musicDTO, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/songs", method = RequestMethod.POST)
    public HttpEntity<?> createMusic(@RequestHeader("Authorization") Optional<String> token, @RequestBody MusicDTO musicDTO) throws JWTException, SessionExpiredException, ResourceNotFoundException, AlbumIDInvalidException, AlbuminAlbumException, PageInvalidException {

            String[] user = Authorize(token);
            boolean auth = false;
            for (String s : user) {
                if (Objects.equals(s, "content manager"))
                    auth = true;
                if (Objects.equals(s, "artist"))
                    auth = true;
            }
            if(auth){
                if (musicDTO.getId_album() != null) {
                    Music m = DtoToMusic(musicDTO, musicService);
                    // Adaugam melodia in lista de melodii a albumului
                    Music album = m.getAlbum();
                    Set<Music> songs = album.getMusic_in_album();
                    songs.add(m);
                    album.setMusic_in_album(songs);
                    //salvam melodia in bd
                    m = musicService.save(m);
                    //retransformam in DTO pentru raspuns
                    musicDTO = musicToDto(m);

                } else {
                    Music m = DtoToMusic(musicDTO, musicService);
                    //salvam melodia in bd
                    m = musicService.save(m);
                    //retransformam in DTO pentru raspuns
                    musicDTO = musicToDto(m);
                }
                addHateoas(musicDTO);
                return new ResponseEntity<>(musicDTO, HttpStatus.CREATED);
            }
            else return new ResponseEntity<>(HttpStatus.FORBIDDEN);

    }

    @PutMapping("/songs/{id}")
    public ResponseEntity < ? >updateMusic(@RequestHeader("Authorization") Optional<String> token, @PathVariable(value = "id") Integer id,
                                           @Valid @RequestBody MusicDTO musicDetails) throws JWTException, SessionExpiredException, ResourceNotFoundException, AlbuminAlbumException, AlbumIDInvalidException, PageInvalidException {

            String[] user = Authorize(token);
            boolean auth = false;
            for (String s : user) {
                if (Objects.equals(s, "content manager"))
                    auth = true;
                if (Objects.equals(s, "artist"))
                    auth = true;
            }
            if(auth) {
                Music music = musicService.findById(id);
                music.setGenre(musicDetails.getGenre());
                music.setName(musicDetails.getName());
                music.setType(musicDetails.getMtype());
                music.setReleaseYear(musicDetails.getRelease_year());

                if (!Objects.equals(music.getAlbum().getId(), musicDetails.getId_album()))//se schimba albumul
                {
                    //scot melodia din lista de cantece de la vechiul album
                    Music album = music.getAlbum();
                    Set<Music> songs = album.getMusic_in_album();
                    songs.remove(music);
                    album.setMusic_in_album(songs);
                    musicService.save(album);

                    //adaug in albumul nou
                    Music newAlbum = musicService.findById(musicDetails.getId_album());
                    songs = newAlbum.getMusic_in_album();
                    songs.add(music);
                    newAlbum.setMusic_in_album(songs);
                    musicService.save(newAlbum);

                    music.setAlbum(newAlbum);
                } else {
                    music.setAlbum(musicService.findById(musicDetails.getId_album()));
                }
                musicService.save(music);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            else return new ResponseEntity<>(HttpStatus.FORBIDDEN);

    }

    @DeleteMapping("/songs/{id}")
    public ResponseEntity<?> deleteMusic(@RequestHeader("Authorization") Optional<String> token,@PathVariable(value = "id") Integer id) throws JWTException, SessionExpiredException, ResourceNotFoundException, AlbuminAlbumException, AlbumIDInvalidException, PageInvalidException {
            String[] user = Authorize(token);
            boolean auth = false;
            for (String s : user) {
                if (Objects.equals(s, "content manager"))
                    auth = true;
                if (Objects.equals(s, "artist"))
                    auth = true;
            }
            if(auth) {
                Music music = musicService.findById(id);
                MusicDTO mDTO = musicToDto(music);

                List<Artist> allArtistList= artistService.getAllArtistList();
                for (Artist a:allArtistList) {
                    if(a.getMusic().contains(music)) {
                        Set<Music> songs = a.getMusic();
                        songs.remove(music);
                        a.setMusic(songs);
                        artistService.save(a);
                    }
                }

                musicService.delete(music);
                //raspunsul este ultima reprezentare valida a obiectului
                mDTO.add(linkTo(methodOn(MusicController.class).getAllMusic(null,null,Optional.empty(),Optional.empty(),null)).withRel("parent"));
                return new ResponseEntity<>(mDTO, HttpStatus.OK);
            }
            else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}