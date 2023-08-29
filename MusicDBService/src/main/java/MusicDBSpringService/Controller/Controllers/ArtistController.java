
package MusicDBSpringService.Controller.Controllers;

import java.util.*;
import java.util.regex.Pattern;


import MusicDBSpringService.Controller.Services.ArtistService;
import MusicDBSpringService.Controller.Services.MusicService;
import MusicDBSpringService.Model.Entities.Artist;
import MusicDBSpringService.Model.Entities.Music;
import MusicDBSpringService.Utils.SOAP.SoapConfig;
import MusicDBSpringService.View.DTOs.ArtistDTO;
import MusicDBSpringService.View.DTOs.MusicDTO;
import MusicDBSpringService.View.DTOs.MusicIdDTO;
import MusicDBSpringService.View.Exceptions.CustomExceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.JAXBException;

import static MusicDBSpringService.Utils.ArtistConverter.*;
import static MusicDBSpringService.Utils.MusicConverter.musicListToMusicDTOList;
import static MusicDBSpringService.View.HATEOAS.ArtistHATEOAS.addHateoas;
import static MusicDBSpringService.View.HATEOAS.MusicHATEOAS.addHateoas;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/api/songcollection")
@CrossOrigin
public class ArtistController {
    @Autowired
    private ArtistService artistService;
    @Autowired
    private MusicService musicService;
    @Autowired
    private SoapConfig soapConfig;

    public String[] Authorize(Optional<String> token) throws JWTException, SessionExpiredException {
        String auth_string;
        if(token.isPresent()){
            try{
                auth_string = soapConfig.soapClient(soapConfig.marshaller()).AuthorizeUser(token.get());
                if(auth_string.startsWith("Error! Expired"))
                    throw new SessionExpiredException("Session expired. Please log in again.");
                else if (auth_string.startsWith("Error!"))
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

    @GetMapping("/artists")
    public ResponseEntity<?>  getAllArtists() throws JWTException, SessionExpiredException, PageInvalidException, AlbumIDInvalidException, ResourceNotFoundException {
        List<ArtistDTO> allArtistsDTO = artistListToArtistDTOList(artistService.getAllArtistList());
        Link allArtistsLink = addHateoas(allArtistsDTO, artistService);

        return ResponseEntity.ok(CollectionModel.of(allArtistsDTO, allArtistsLink));
    }

    @GetMapping("/artists/{uuid}")
    public ResponseEntity<?> getArtistByUuid(@PathVariable(value = "uuid") String uuid) throws ResourceNotFoundException, JWTException, SessionExpiredException, PageInvalidException, AlbumIDInvalidException {
        try {
            ArtistDTO artistDTO = artistToDto(artistService.findById(uuid));
            addHateoas(artistDTO, artistService);
            return new ResponseEntity<>(artistDTO, HttpStatus.OK);
        }catch (ResourceNotFoundException e)
        {
            Link parent = WebMvcLinkBuilder.linkTo(methodOn(ArtistController.class).getAllArtists()).withRel("parent");
            ArrayList<Link> array = new ArrayList<>();
            array.add(parent);

            Map<String, ArrayList<Link>> links = new HashMap<>();
            links.put("_links", array);
            return new ResponseEntity<>(links, HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/artists/{uuid}/songs")
    public ResponseEntity<?> getArtistMusic(@PathVariable(value = "uuid") String uuid) throws ResourceNotFoundException, AlbumIDInvalidException, PageInvalidException, JWTException, SessionExpiredException {

        List<MusicDTO> allMusicDTO = musicListToMusicDTOList(new ArrayList<Music>(artistService.findById(uuid).getMusic()));
        for (MusicDTO musicDTO : allMusicDTO) {
            addHateoas(musicDTO);
        }
        Link link1 = linkTo(methodOn(ArtistController.class).getArtistMusic(uuid)).withSelfRel();
        Link link2 = linkTo(methodOn(ArtistController.class).getArtistByUuid(uuid)).withRel("parent");
        return ResponseEntity.ok(CollectionModel.of(allMusicDTO, link1,link2));
    }

    @RequestMapping(value = "/artists/{uuid}", method = RequestMethod.PUT)
    public ResponseEntity<?> createArtist(@RequestHeader("Authorization") Optional<String> token,@PathVariable(value = "uuid") String uuid,@RequestBody ArtistDTO artistDTO) throws JWTException, SessionExpiredException, ResourceNotFoundException, PageInvalidException, AlbumIDInvalidException {

        String[] user = Authorize(token);
        boolean auth = false;
        for (String s : user) {
            if (Objects.equals(s, "content manager"))
                auth = true;
            if (Objects.equals(s, "artist"))
                auth = true;
        }
        if(auth) {
            try{
                Artist a = artistService.findById(uuid);
                a.setActive(artistDTO.getIs_active());
                a.setName(artistDTO.getName());
                artistService.save(a);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            catch (ResourceNotFoundException e){
                artistDTO.setUuid(uuid);
                Artist a = DtoToAtrist(artistDTO, artistService);
                artistService.save(a);
                addHateoas(artistDTO, artistService);
                return new ResponseEntity<>(artistDTO, HttpStatus.CREATED);
            }
        }
        else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @CrossOrigin
    @RequestMapping(value = "/artists/{uuid}/songs/{id}", method = RequestMethod.PUT)
    //post pentru ca nu dai id-ul in cale, ci in request
    public ResponseEntity<?> addSong(@RequestHeader("Authorization") Optional<String> token, @PathVariable(value = "uuid") String uuid,@PathVariable(value = "id") Integer id) throws JWTException, SessionExpiredException, ResourceNotFoundException, PageInvalidException, AlbumIDInvalidException {

        String[] user = Authorize(token);
        boolean auth = false;
        for (String s : user) {
            if (Objects.equals(s, "content manager"))
                auth = true;
            if (Objects.equals(s, "artist"))
                auth = true;
        }
        if(auth) {
            Artist artist = artistService.addSong(artistService.findById(uuid),musicService.findById(id));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else return new ResponseEntity<>(HttpStatus.FORBIDDEN);

    }

    @DeleteMapping("/artists/{uuid}")
    public ResponseEntity<?> deleteArtist(@RequestHeader("Authorization") Optional<String> token, @PathVariable(value = "uuid") String uuid) throws JWTException, SessionExpiredException, ResourceNotFoundException, PageInvalidException, AlbumIDInvalidException {

        String[] user = Authorize(token);
        boolean auth = false;
        for (String s : user) {
            if (Objects.equals(s, "content manager"))
                auth = true;
            if (Objects.equals(s, "artist"))
                auth = true;
        }
        if(auth) {
            Artist artist = artistService.findById(uuid);
            artist.setMusic(null);
            artistService.save(artist);
            artistService.delete(artist);
            ArtistDTO artistDTO = artistToDto(artist);
            artistDTO.add(linkTo(methodOn(ArtistController.class).getAllArtists()).withRel("parent"));
            return new ResponseEntity<>(artistDTO, HttpStatus.OK);
        }
        else return new ResponseEntity<>(HttpStatus.FORBIDDEN);

    }


    @DeleteMapping("/artists/{uuid}/songs/{id}")
    public ResponseEntity<?> deleteSong(@RequestHeader("Authorization") Optional<String> token, @PathVariable(value = "uuid") String uuid, @PathVariable(value = "id") Integer id) throws JWTException, SessionExpiredException, ResourceNotFoundException, PageInvalidException, AlbumIDInvalidException, SongNotFoundForArtistException {
        String[] user = Authorize(token);
        boolean auth = false;
        for (String s : user) {
            if (Objects.equals(s, "content manager"))
                auth = true;
            if (Objects.equals(s, "artist"))
                auth = true;
        }
        if(auth) {
            Artist artist = artistService.deleteSong(artistService.findById(uuid),musicService.findById(id));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}