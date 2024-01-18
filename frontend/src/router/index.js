import { createBrowserRouter, createRoutesFromElements, Route } from "react-router-dom";

import Start from "../pages/Start";
import Room from "../pages/Room";
import Lobby from "../pages/Lobby";
import Login from "../pages/Auth/Login";
import Signup from "../pages/Auth/Signup";
import Layout from "../layout";
import VideoRoomComponent from "../components/OpenVidu/VideoRoomComponent";

const route = (
    <>
    <Route path="/" element={<Start />}></Route>
    <Route element={<Layout/>}>
        <Route path="room" element={<Room />}></Route>
        <Route path="room2" element={<VideoRoomComponent />}></Route>
        <Route path="lobby" element={<Lobby />}></Route>
        <Route path="auth">
            <Route path="login" element={<Login />}></Route>
            <Route path="signup" element={<Signup />}></Route>
        </Route>
    </Route>
    </>
);

const rootRouter = createBrowserRouter(createRoutesFromElements(route));
export default rootRouter;
