import React from 'react';
import {Link, useNavigate} from 'react-router-dom';
import './Navbar.css';
import XMLParser from "react-xml-parser/xmlParser";

const Navbar = ({ roles ,jwt, setJWT,setId, setRoles, setLink, setUuid}) => {
    const navigate = useNavigate();

    const handleClick = () => {

        const soapRequest = '<soap11env:Envelope xmlns:soap11env="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sample="services.dbManager.soap">' +
            '<soap11env:Body>' +
            '<sample:logout>'+
            '<sample:jwt>'+jwt+'</sample:jwt>' +
            '</sample:logout>' +
            '</soap11env:Body>' +
            '</soap11env:Envelope>';
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
                setJWT("");
                setId("");
                setRoles([]);
                setLink("");
                setUuid("");
                navigate('/login');
            } else if (xmlhttp.readyState === 4) {
                console.error(xmlhttp.response);
            }
        };
        xmlhttp.open('POST',"http://127.0.0.1:8000",true);
        xmlhttp.send(soapRequest);
    };
    return (
        <nav className="navbar">
            <Link to="/" className="navbar__link">Home</Link>
            {roles.includes('content manager') && <Link to="/addArtist" className="navbar__link">Adaugare artist</Link>}
            {roles.includes('artist') && <Link to="/addSong" className="navbar__link">Adauga o melodie</Link>}
            {roles.includes('client') && <Link to="/playlists" className="navbar__link">Vizualizare playlist-uri</Link>}
            {jwt!=='' && <Link onClick={handleClick} className="navbar__link">Logout</Link>}

        </nav>
    );
}

export default Navbar;