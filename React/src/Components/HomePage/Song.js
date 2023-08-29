import React from 'react';
import "./Song.css"
const Song = ({ name,genre,release_year,m_type}) => {
    return (
        <div className="song">
            <h2 className="song__name">{name}</h2>
            <p className="song__genre">{genre}</p>
            <p className="song__releaseyear">{release_year}</p>
            <p className="song__type">{m_type}</p>
        </div>
    );
}

export default Song;