import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Login from "../pages/Login";
import Home from "../pages/Home";
import Profile from "../pages/Profile";
import ProfileEnhanced from "../pages/ProfileEnhanced";
import Register from "../pages/Register";
import ForgotPassword from "../pages/ForgotPassword";
import ResetPassword from "../pages/ResetPassword";
import VerifyOtpPage from "../pages/VerifyOtpPage";
import ChatPage from "../pages/ChatPage";
import FriendsPage from "../pages/FriendsPage";
import Settings from "../pages/Settings";
import SearchPage from "../pages/SearchPage";
import GroupPage from "../pages/GroupPage";
import GroupDetail from "../pages/GroupDetail";
import Marketplace from "../pages/Marketplace";
import Pages from "../pages/Pages";
import Saved from "../pages/Saved";

const AppRoutes = () => {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/" element={<Home />} />
        <Route path="/profile" element={<ProfileEnhanced />} />
        <Route path="/profile-simple" element={<Profile />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/reset-password/:token" element={<ResetPassword />} />
        <Route path="/verify-user" element={<VerifyOtpPage />} />
        <Route path="/chat" element={<ChatPage />} />
        <Route path="/friends" element={<FriendsPage />} />
        <Route path="/settings" element={<Settings />} />
        <Route path="/search" element={<SearchPage />} />
        <Route path="/groups" element={<GroupPage />} />
        <Route path="/group-detail" element={<GroupDetail />} />
        <Route path="/marketplace" element={<Marketplace />} />
        <Route path="/pages" element={<Pages />} />
        <Route path="/saved" element={<Saved />} />
      </Routes>
    </Router>
  );
};

export default AppRoutes;
