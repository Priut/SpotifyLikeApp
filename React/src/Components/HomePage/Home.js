import React, { useState, useEffect } from 'react';
import './Home.css';
import Song from "./Song";
import XMLParser from "react-xml-parser/xmlParser";
import {useNavigate} from "react-router-dom";
const Home = ({ jwt ,roles,setRoles,id,setId}) => {
    const navigate = useNavigate();

    const [songs, setSongs] = useState([]);

    useEffect(() => {
        if(jwt === '')
            navigate("/login")
        else{
            fetch("http://localhost:8080/api/songcollection/songs")
                .then(response => {
                    if (!response.ok) {
                        throw new Error(response.statusText);
                    }
                    return response.json();
                })
                .then(data => setSongs(data._embedded.musicDTOList))
                .catch(error => console.log(error));
        }

    }, []);
    useEffect(() => {
        if(jwt === '')
            navigate("/login")
        else {
            var soapRequest = '<soap11env:Envelope xmlns:soap11env="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sample="services.dbManager.soap">' +
                '<soap11env:Body>' +
                '<sample:authorize>' +
                '<sample:token>' + jwt + '</sample:token>' +
                '</sample:authorize>' +
                '</soap11env:Body>' +
                '</soap11env:Envelope>';
            // Create a new XMLHttpRequest object
            var xmlhttp = new XMLHttpRequest();

            // Define the onreadystatechange function
            xmlhttp.onreadystatechange = function() {
                if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
                    var xml = new XMLParser().parseFromString(xmlhttp.responseText);
                    var response = xml.getElementsByTagName('tns:authorizeResult')[0].value.split("||")
                    setId(response[0]);
                    var roles1 = []
                    for (let i = 1; i < response.length; i++) {
                        roles1.push(response[i]);
                    }
                    setRoles(roles1)
                } else if (xmlhttp.readyState === 4) {
                    console.error(xmlhttp.response);
                }
            };

            xmlhttp.open('POST',"http://127.0.0.1:8000",true);
            xmlhttp.send(soapRequest);
        }

    }, []);

    return (
        <div className="home">
            {console.log(songs)}
            <div className="home__header">
                <h1 className="home__title">SPOTIFY</h1>
            </div>
            <div className="home__content">
                    {songs.map(song => (
                        <div key={song.id}>
                            <Song name={song.name} genre={song.genre} release_year={song.release_yeas} m_type={song.m_type}/>
                        </div>
                    ))}

            </div>
        </div>
    );
}

export default Home;
