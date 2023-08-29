import React, {useState} from 'react';
import {Route, Routes} from 'react-router-dom';
import Login from './Components/LoginRegister/Login';
import Home from './Components/HomePage/Home';
import Navbar from "./Components/Navbar";
import AddSong from "./Components/AddSong";
import AddArtist from "./Components/AddArtist";
import ViewPlaylists from "./Components/Playlist/ViewPlaylists";
import SongDetails from "./Components/Playlist/SongDetails";
import { createGlobalStyle } from 'styled-components';
import AddPlaylist from "./Components/Playlist/AddPlaylist";
import AddSongToPlaylist from "./Components/Playlist/AddSongToPlaylist";

const GlobalStyle = createGlobalStyle`
  body {
    background-color: #333;
  }
`;

function App() {
    const [jwt,setJWT] = useState('');
    const [roles, setRoles] = useState([]);
    const [id, setId] = useState('');
    const [link, setLink] = useState('');
    const [uuid, setUuid] = useState('');
  return (
      <>
          <GlobalStyle />
          <Navbar roles={roles} jwt={jwt} setJWT={setJWT} setId={setId} setRoles={setRoles} setLink={setLink} setUuid={setUuid}/>
          <Routes>
              <Route path="/login" element={<Login setJWT = {setJWT}  jwt={jwt}/>} />
              <Route path="/" element={<Home jwt = {jwt} setRoles={setRoles} roles = {roles} setId={setId} id={id}/>} />
              <Route path="/addSong" element={<AddSong jwt = {jwt}/>} />
              <Route path="/addArtist" element={<AddArtist jwt = {jwt}/>} />
              <Route path="/playlists" element={<ViewPlaylists id={id} setLink={setLink} uuid ={uuid} setUuid={setUuid}/>} />
              <Route path="/songinfo" element={<SongDetails link={link}/>} />
              <Route path="/addPlaylist" element={<AddPlaylist id = {id} jwt={jwt}/>} />
              <Route path="/addSongToPlaylist" element={<AddSongToPlaylist id = {id} jwt={jwt} uuid={uuid}/>} />
          </Routes>
      </>
  );
}

export default App;
