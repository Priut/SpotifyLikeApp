import React, {useEffect, useState} from 'react';
import Song from "../HomePage/Song";
const SongDetails = ({link}) => {
    const [song, setSong] = useState('');
    useEffect(() => {
        fetch(link)
            .then(response => {
                if (!response.ok) {
                    throw new Error(response.statusText);
                }
                return response.json();
            })
            .then(data => setSong(data))
            .catch(error => console.log(error));


    }, []);
    return (
        <div className="song">
            <Song name={song.name} genre={song.genre} release_year={song.release_year} m_type={song.m_type}/>
        </div>
    );
}

export default SongDetails;