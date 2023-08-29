import { useNavigate } from 'react-router-dom';
import XMLParser from "react-xml-parser/xmlParser";
import React, {useEffect, useState} from "react";
import "./AddArtist.css"
const AddArtist = ({jwt}) => {
    const navigate = useNavigate();

    const [name, setName] = useState('');
    const [active, setActive] = useState('');
    function uuidv4() {
        return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
            (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
        );
    }
    useEffect(() => {
        if(jwt === '')
            navigate("/login")

    }, []);
    const  handleSubmit = (e) =>{

        e.preventDefault();
        const requestOptions = {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' ,'Authorization': jwt },
            body: JSON.stringify({ name:name,is_active:active})
        };
        fetch('http://localhost:8080/api/songcollection/artists/'+uuidv4(), requestOptions)
            .then(response => response.json())
            .then(data => console.log(data));
        navigate('/');
    }

    return (
        <div className="add-artist-container">
            <form className="add-artist" onSubmit={handleSubmit} >
                <label>
                    Name:
                    <input type="text" value={name} onChange={(e) => setName(e.target.value)}></input>
                </label>

                <label>
                    Is artist active?
                    <input type="checkbox" value={active} onChange={(e) => setActive(e.target.checked)}></input>
                </label>
                <input type="submit" value="Add Artist" />
            </form>
        </div>

    );
};
export default AddArtist;