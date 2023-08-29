package com.mongodb_example.main.controller;

import com.mongodb_example.main.model.User;
import com.mongodb_example.main.model.repositories.SteamUserRepository;
import com.mongodb_example.main.utils.Exceptions.CustomExceptions.*;
import com.mongodb_example.main.utils.SOAP.SoapConfig;
import com.mongodb_example.main.view.DTOs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mongodb_example.main.view.DTOs.UserDTO;

import javax.xml.bind.JAXBException;
import java.util.*;
import java.util.regex.Pattern;

import static com.mongodb_example.main.utils.UserHATEOAS.addHateoas;
import static com.mongodb_example.main.view.converters.DTOConverter.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/songcollection")
public class PlaylistController {
    @Autowired
    SteamUserRepository userRepository;
    @Autowired
    SoapConfig soapConfig;

    public String[] Authorize(Optional<String> token) throws JWTException, SessionExpiredException {
        String auth_string;
        if (token.isPresent()) {
            try {
                auth_string = soapConfig.soapClient(soapConfig.marshaller()).AuthorizeUser(token.get());
                if (auth_string.startsWith("Error:Signature has expired"))
                    throw new SessionExpiredException("Session expired. Please log in again.");
                else if (auth_string.startsWith("Error"))
                    throw new JWTException("Something occured.");

            } catch (JAXBException e) {
                throw new JWTException("Something occured.");
            }
        } else throw new JWTException("You are not authorized");
        Pattern pattern = Pattern.compile("\\|\\|");
        return pattern.split(auth_string);
    }

    //ok
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") Optional<String> token) throws JWTException, SessionExpiredException, ResourceNotFoundException {
        List<User> userList = userRepository.findAll();
        List<UserDTO> userDTOList = userTouserDTOList(userList);
        return ResponseEntity.ok(CollectionModel.of(userDTOList, addHateoas(userDTOList)));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserbyId(@PathVariable("id") String id) throws JWTException, SessionExpiredException, ResourceNotFoundException {
        if (userRepository.findById(id).isPresent()) {
            User user = userRepository.findById(id).get();
            UserDTO userDTO = userTouserDTO(user);
            addHateoas(userDTO);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } else throw new ResourceNotFoundException("No user found with this id");
    }
    @GetMapping("/users/{id}/playlists")
    public ResponseEntity<?> getUserPlaylists(@PathVariable("id") String id) throws JWTException, SessionExpiredException, ResourceNotFoundException {
        if (userRepository.findById(id).isPresent()) {
            User user = userRepository.findById(id).get();
            List<INPlaylistDTO>  playlists = user.getPlaylists();
            List<OUTPlaylistDTO> playlistDTOS = inOutPlaylistList(playlists);
            return ResponseEntity.ok(CollectionModel.of(playlistDTOS, addHateoas(playlistDTOS, id)));
        } else throw new ResourceNotFoundException("No user found with this id");
    }
    @GetMapping("/users/{id}/playlists/{uuid}")
    public ResponseEntity<?> getUserPlaylist(@PathVariable("id") String id,@PathVariable("uuid") String uuid) throws JWTException, SessionExpiredException, ResourceNotFoundException {
        if (userRepository.findById(id).isPresent()) {
            OUTPlaylistDTO outPlaylistDTO = null;
            User user = userRepository.findById(id).get();
            List<INPlaylistDTO>  playlists = user.getPlaylists();
            for (INPlaylistDTO p:playlists) {
                if(Objects.equals(p.getUuid(), uuid)){
                    outPlaylistDTO = inOutPlaylist(p);
                    addHateoas(outPlaylistDTO,id);
                }
            }
            if(outPlaylistDTO == null){
                throw new ResourceNotFoundException("Acest playlist nu exista!");
            }

            return new ResponseEntity<>(outPlaylistDTO, HttpStatus.OK);
        } else throw new ResourceNotFoundException("No user found with this id");
    }


    @CrossOrigin
    @RequestMapping(value = "/users", method = RequestMethod.PUT)
    public ResponseEntity<?> createUser(@RequestHeader("Authorization") Optional<String> token, @RequestBody User user) throws JWTException, SessionExpiredException, ResourceNotFoundException, UserExistsException {
        String[] u = Authorize(token);
        boolean auth = false;
        for (String s : u) {
            if (Objects.equals(s, "client") && Objects.equals(u[0], user.getId())) {
                auth = true;
                break;
            }
        }
        if (auth) {
            List<User> allUsers = userRepository.findAll();
            boolean exists = false;
            for (User user1 : allUsers) {
                if (user1.getId() == user.getId())
                    exists = true;
            }
            if (exists)
                throw new UserExistsException("User exists with this id");
            else {
                userRepository.save(user);
                UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getFavorites(), inOutPlaylistList(user.getPlaylists()));
                addHateoas(userDTO);
                return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
            }
        } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @CrossOrigin
    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") Optional<String> token, @PathVariable("id") String id, @RequestBody User newuser) throws JWTException, SessionExpiredException, ResourceNotFoundException {
        if (userRepository.findById(id).isPresent()) {
            String[] u = Authorize(token);
            boolean auth = false;
            for (String s : u) {
                if (Objects.equals(s, "client") && Objects.equals(u[0], id)) {
                    auth = true;
                    break;
                }
            }
            if (auth) {
                User user = userRepository.findById(id).get();
                user.setUsername(newuser.getUsername());
                user.setPlaylists(newuser.getPlaylists());
                user.setFavorites(newuser.getFavorites());
                userRepository.save(user);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else throw new ResourceNotFoundException("No user found with this id");

    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") Optional<String> token, @PathVariable(value = "id") String id) throws ResourceNotFoundException, JWTException, SessionExpiredException {
        if (userRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("No user found with this id");
        }

        String[] u = Authorize(token);
        boolean auth = false;
        for (String s : u) {
            if (Objects.equals(s, "client") && Objects.equals(u[0], id)) {
                auth = true;
                break;
            }
        }
        if (auth) {
            User user = userRepository.findById(id).get();
            UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getFavorites(), inOutPlaylistList(user.getPlaylists()));

            userRepository.delete(user);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    //auth,not found,ok
    @CrossOrigin
    @PutMapping("/users/{id}/playlist/{uuid}/songs")
    public ResponseEntity<?> addSongToPlaylist(@RequestHeader("Authorization") Optional<String> token, @PathVariable(value = "id") String id, @PathVariable(value = "uuid") String uuid, @RequestBody MusicIdDTO musicIdDTO) throws SongAlreadyInPlaylistException, JWTException, SessionExpiredException, ResourceNotFoundException {
        if (userRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("No user found with this id");
        }

        String[] u = Authorize(token);
        boolean auth = false;
        for (String s : u) {
            if (Objects.equals(s, "client") && Objects.equals(u[0], id)) {
                auth = true;
                break;
            }
        }
        if (auth) {
            User user = userRepository.findById(id).get();
            List<INPlaylistDTO> allp = user.getPlaylists();
            INPlaylistDTO playlist = user.getPlaylists().stream()
                    .filter(p -> uuid.equals(p.getUuid()))
                    .findAny()
                    .orElse(null);
            allp.remove(playlist);
            if (playlist != null) {
                if (playlist.getMusicIdsList() != null) {
                    if (playlist.getMusicIdsList().contains(musicIdDTO.getId())) {
                        throw new SongAlreadyInPlaylistException("Song is already in this playlist");
                    }
                    playlist.getMusicIdsList().add(musicIdDTO.getId());
                    allp.add(playlist);
                    user.setPlaylists(allp);
                    User updated_user = userRepository.save(user);
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                    } else {
                    List<Integer> songs = new ArrayList<>();
                    songs.add(musicIdDTO.getId());
                    playlist.setMusicIdsList(songs);
                    allp.add(playlist);
                    user.setPlaylists(allp);
                    userRepository.save(user);
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
            }
            throw new ResourceNotFoundException("No playlist found for this user with this id");
        } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);

    }

    @CrossOrigin
    @PostMapping("/users/{id}/playlist")
    public ResponseEntity<?> addPlaylistToUser(@RequestHeader("Authorization") Optional<String> token, @PathVariable(value = "id") String id, @RequestBody INPlaylistDTO inPlaylistDTO) throws ResourceNotFoundException, JWTException, SessionExpiredException {
        if (userRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("No user found with this id");
        }
        String[] u = Authorize(token);
        boolean auth = false;
        for (String s : u) {
            if (Objects.equals(s, "client") && Objects.equals(u[0], id)) {
                auth = true;
                break;
            }
        }
        if (auth) {
            String uuid = UUID.randomUUID().toString();
            List<User> allUsers = userRepository.findAll();
            for (User user1: allUsers) {
                List<INPlaylistDTO> userPlaylist = user1.getPlaylists();
                for (INPlaylistDTO p:userPlaylist) {
                    if(Objects.equals(p.getUuid(), uuid))
                        return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
            }
            inPlaylistDTO.setUuid(uuid);
            User user = userRepository.findById(id).get();
            List<INPlaylistDTO> allp = user.getPlaylists();
            allp.add(inPlaylistDTO);
            user.setPlaylists(allp);
            User updated_user = userRepository.save(user);
            OUTPlaylistDTO outp = inOutPlaylist(inPlaylistDTO);
            addHateoas(outp,id);
            return new ResponseEntity<>(outp,HttpStatus.CREATED);
        } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
