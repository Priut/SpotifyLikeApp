import React, { useState } from 'react';
import './AddPlaylist.css';
import {useNavigate} from "react-router-dom";

const AddPlaylist = ({id, jwt}) => {
    const [title, setTitle] = useState('');
    const navigate = useNavigate();
    const handleSubmit = (e) => {
        e.preventDefault();
        console.log(title);
        //,'Authorization': jwt
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json','Authorization': jwt},
            body: JSON.stringify({ title:title})
        };
        fetch('http://localhost:8081/api/songcollection/users/'+id+"/playlist", requestOptions)
            .then(response => response.json())
            .then(data => console.log(data));
        navigate('/');
    }

    return (
        <div className="add-playlist-container">
            <form className="add-playlist" onSubmit={handleSubmit}>
                <label>
                    Title:
                    <input type="text" value={title} onChange={(e) => setTitle(e.target.value)} />
                </label>
                <input type="submit" value="Add Playlist" />
            </form>
        </div>
    );
};

export default AddPlaylist;
