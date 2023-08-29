import React, { useState, useEffect } from 'react';
import './ViewPlaylists.css';
import {useNavigate} from "react-router-dom";

const ViewPlaylists = ({id, setLink, uuid, setUuid}) => {
    const navigate = useNavigate();
    const [playlists, setPlaylists] = useState([]);

    useEffect(() => {
        console.log(id)
        fetch("http://localhost:8081/api/songcollection/users/"+id+"/playlists")
            .then(response => {
                if (!response.ok) {
                    throw new Error(response.statusText);
                }
                return response.json();
            })
            .then(data => setPlaylists(data._embedded.oUTPlaylistDTOes))
            .catch(error => console.log(error));
    }, []);

    function handleClickSong(link) {
        setLink(link.href);
        navigate("/songinfo")
    }
    function handleClick() {
        navigate("/addPlaylist")
    }

    function handleClickAddSong(uuid) {
        console.log(uuid)
        setUuid(uuid)
        navigate("/addSongToPlaylist")
    }

    return (

        <div className="view-playlist">
            <button onClick = {() => handleClick()}>Create new playlist</button>
            <h2 className="view-playlist__title">Playlists</h2>
            <ul className="view-playlist__list">
                {playlists.map(playlist => (
                    <li key={playlist.title}>
                        <p>{playlist.title}</p>
                        <ul className="view-songs__list">
                            {playlist.musicList.map(song => (
                                <div key={song.id}>
                                    <li >{song.name}</li>
                                    <button onClick = {() => handleClickSong(song._links.self)}>View details</button>
                                </div>
                            ))}
                        </ul>
                        <button onClick = {() => handleClickAddSong(playlist.uuid)}>Add song to this playlist</button>

                    </li>
                ))}
            </ul>
        </div>
    );
}

export default ViewPlaylists;
