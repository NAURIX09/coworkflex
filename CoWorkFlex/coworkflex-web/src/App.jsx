import { Route, Routes } from "react-router-dom";
import Navbar from "./components/Navbar.jsx";
import Dashboard from "./pages/Dashboard.jsx";
import SpaceDetail from "./pages/SpaceDetail.jsx";
import Profile from "./pages/Profile.jsx";
import Login from "./pages/Login.jsx";

export default function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/spaces/:id" element={<SpaceDetail />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/login" element={<Login />} />
      </Routes>
    </>
  );
}
