import { useNavigate } from 'react-router-dom';
import React, {useEffect, useState} from "react";
const AddSongToPlaylist = ({id,jwt,uuid}) => {
    const navigate = useNavigate();

    const [songs, setSongs] = useState([]);
    const [songId, setSongId] = useState('');
    useEffect(() => {
        if(jwt === '')
            navigate("/login")
        fetch("http://localhost:8080/api/songcollection/songs")
            .then(response => {
                if (!response.ok) {
                    throw new Error(response.statusText);
                }
                return response.json();
            })
            .then(data => setSongs(data._embedded.musicDTOList))
            .catch(error => console.log(error));

    }, []);
    const  handleSubmit = (e) =>{

        e.preventDefault();
        console.log(songId);
        const requestOptions = {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json','Authorization': jwt},
            body: JSON.stringify({ id:songId})
        };
        fetch('http://localhost:8081/api/songcollection/users/'+id+"/playlist/"+uuid+"/songs", requestOptions)
            .then(response => response.json())
            .then(data => console.log(data));
        navigate('/');
    }

    return (
        <div className="add-song-container">
            <form className="add-song" onSubmit={handleSubmit} >
                <label>
                    Song:
                    <select value={songId} onChange={(e) => setSongId(e.target.value)}>
                        {songs.map(song => (

                            <option key={song.id} value={song.id}>{song.name}</option>
                        ))}
                    </select>
                </label>
                <input type="submit" value="Add Artist" />
            </form>
        </div>

    );
};
export default AddSongToPlaylist;