import React, {useEffect, useState} from 'react';
import './AddSong.css';
import {useNavigate} from "react-router-dom";
import Song from "./HomePage/Song";

const AddSong = ({jwt}) => {
    const navigate = useNavigate();
    const [name, setName] = useState('');
    const [genre, setGenre] = useState('');
    const [year, setYear] = useState('');
    const [type, setType] = useState('');
    const [idAlbum, setIdAlbum] = useState('');
    const [albums, setAlbums] = useState([]);

    useEffect(() => {
        if(jwt === '')
            navigate("/login")

    }, []);
    useEffect(() => {
        fetch("http://localhost:8080/api/songcollection/songs?mtype=album")
            .then(response => {
                if (!response.ok) {
                    throw new Error(response.statusText);
                }
                return response.json();
            })
            .then(data => setAlbums(data._embedded.musicDTOList))
            .catch(error => console.log(error));

    }, []);
    const handleSubmit = (event) => {
        event.preventDefault();
        console.log(name+" "+genre+" "+year+" "+type+" "+idAlbum)
        if(idAlbum === "")
        {
            const requestOptions = {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' ,'Authorization': jwt},
                body: JSON.stringify({ name:name,genre:genre,release_year:year,mtype:type})
            };
            fetch('http://localhost:8080/api/songcollection/songs', requestOptions)
                .then(response => response.json())
                .then(data => console.log(data));
            navigate('/');
        }
        else{
            const requestOptions = {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' ,'Authorization': jwt},
                body: JSON.stringify({ name:name,genre:genre,release_year:year,mtype:type,id_album:idAlbum})
            };
            fetch('http://localhost:8080/api/songcollection/songs', requestOptions)
                .then(response => response.json())
                .then(data => console.log(data));
            navigate('/');
        }
        setName('');
        setGenre('');
        setYear('');
        setType('');
        setIdAlbum('');

    };

    const handleTypeChange = (e) => {
        setType(e.target.value);
        if (e.target.value === 'album') {
            setIdAlbum('');
        }
    }

    return (
        <div className="add-song-container">
            {console.log(albums)}
            <form className="add-song" onSubmit={handleSubmit}>
                <label>
                    Name:
                    <input type="text" value={name} onChange={(e) => setName(e.target.value)} />
                </label>
                <label>
                    Genre:
                    <select value={genre} onChange={(e) => setGenre(e.target.value)} >
                        <option value="blues">Blues</option>
                        <option value="rock">Rock</option>
                        <option value="hiphop">Hiphop</option>
                        <option value="pop">Pop</option>
                        <option value="electronic">Electronic</option>
                        <option value="jazz">Jazz</option>
                    </select>
                </label>
                <label>
                    Year:
                    <input type="number" value={year} onChange={(e) => setYear(e.target.value)} />
                </label>
                <label>
                    Type:
                    <select value={type} onChange={handleTypeChange}>
                        <option value="song">Song</option>
                        <option value="album">Album</option>
                        <option value="single">Single</option>
                    </select>
                </label>
                {type !== 'album' && type !== 'single' &&
                    <label>
                        Album:
                        <select value={idAlbum} onChange={(e) => setIdAlbum(e.target.value)}>
                        {albums.map(album => (

                            <option key={album.id} value={album.id}>{album.name}</option>
                        ))}
                        </select>
                    </label>

                }

                <input type="submit" value="Add Song" />
            </form>
        </div>
    );
};

export default AddSong;