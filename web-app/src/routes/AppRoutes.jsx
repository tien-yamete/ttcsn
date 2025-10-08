import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Login from "../pages/Login";
import Home from "../pages/Home";
import Profile from "../pages/Profile";
import Register from "../pages/Register";
import ForgotPassword from "../pages/ForgotPassword";  
import ResetPassword from "../pages/ResetPassword";     
import VerifyOtpPage from "../pages/VerifyOtpPage";

const AppRoutes = () => {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/" element={<Home />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/reset-password/:token" element={<ResetPassword />} />
        <Route path="/verify-user" element={<VerifyOtpPage />} />
      </Routes>
    </Router>
  );
};

export default AppRoutes;
