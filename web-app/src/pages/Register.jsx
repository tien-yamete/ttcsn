import {
  Box,
  Button,
  Card,
  CardContent,
  Divider,
  TextField,
  Typography,
  Snackbar,
  Alert,
  InputAdornment,
  Link as MuiLink,
} from "@mui/material";
import MailOutlineIcon from "@mui/icons-material/MailOutline";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import PersonOutlineIcon from "@mui/icons-material/PersonOutline";
import GoogleIcon from "@mui/icons-material/Google";
import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import IconButton from "@mui/material/IconButton";
import React, { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { registerAccount } from "../services/authenticationService";
import LoginLeftPanel from "../components/LoginLeftPanel";

export default function Register() {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirm, setConfirm] = useState("");
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);
  const [snack, setSnack] = useState({
    open: false,
    message: "",
    severity: "error",
  });
  const [showPassword, setShowPassword] = useState(false);

  // 🧹 Dọn cờ verify cũ nếu user quay lại trang đăng ký
  useEffect(() => {
    localStorage.removeItem("verifyEmail");
    localStorage.removeItem("verifyContext");
    localStorage.removeItem("verifyIssuedAt");
  }, []);

  const validate = () => {
    const e = {};
    if (!username?.trim()) e.username = "Required";
    if (!email?.trim()) {
      e.email = "Required";
    } else {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(email.trim())) {
        e.email = "Invalid email address";
      }
    }
    if (!password?.trim()) e.password = "Required";
    if (password !== confirm) e.confirm = "Passwords do not match";
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const onSubmit = async (evt) => {
    evt.preventDefault();
    if (!validate()) return;
    setSubmitting(true);
    try {
      const payload = {
        username: username.trim(),
        email: email.trim(),
        password,
      };
      const res = await registerAccount(payload);

      if (res?.status === 200 || res?.status === 201) {
        // ✅ Đặt cờ để Verify page cho phép vào & auto redirect sau khi verify
        localStorage.setItem("verifyEmail", payload.email);
        localStorage.setItem("verifyContext", "register");
        localStorage.setItem("verifyIssuedAt", Date.now().toString());

        setSnack({
          open: true,
          message: "Account created successfully! Check your email for verification.",
          severity: "success",
        });

        // Điều hướng tới trang verify bạn đang dùng trong routes
        setTimeout(() => navigate("/verify-user"), 800);
      } else {
        setSnack({
          open: true,
          message: "Unable to register. Please try again.",
          severity: "error",
        });
      }
    } catch (err) {
      const msg =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        err?.message ||
        "Registration failed";
      setSnack({ open: true, message: msg, severity: "error" });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: "100vh",
        display: "grid",
        gridTemplateColumns: { xs: "1fr", md: "1fr 1fr" },
      }}
    >
      <LoginLeftPanel variant="register" />

      {/* Right form panel */}
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          p: 3,
          backgroundColor: (t) =>
            t.palette.mode === "dark" ? "background.default" : "#f7f8fa",
        }}
      >
        <Card sx={{ width: 440, maxWidth: "100%", boxShadow: 6, borderRadius: 3 }}>
          <CardContent sx={{ p: 4 }}>
            <Typography variant="h5" sx={{ fontWeight: 700, mb: 1 }}>
              Create an account
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
              Sign up to start using Friendify
            </Typography>

            <Box component="form" onSubmit={onSubmit} noValidate>
              <TextField
                label="Username"
                fullWidth
                margin="normal"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                error={Boolean(errors.username)}
                helperText={errors.username || " "}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <PersonOutlineIcon fontSize="small" />
                    </InputAdornment>
                  ),
                }}
              />

              <TextField
                label="Email"
                fullWidth
                margin="normal"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                error={Boolean(errors.email)}
                helperText={errors.email || " "}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <MailOutlineIcon fontSize="small" />
                    </InputAdornment>
                  ),
                }}
              />

              <TextField
                label="Password"
                fullWidth
                margin="normal"
                type={showPassword ? "text" : "password"}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                error={Boolean(errors.password)}
                helperText={errors.password || " "}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <LockOutlinedIcon fontSize="small" />
                    </InputAdornment>
                  ),
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton tabIndex={-1} onClick={() => setShowPassword((prev) => !prev)} edge="end">
                        {showPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />

              <TextField
                label="Confirm Password"
                type={showPassword ? "text" : "password"}
                fullWidth
                margin="normal"
                value={confirm}
                onChange={(e) => setConfirm(e.target.value)}
                error={Boolean(errors.confirm)}
                helperText={errors.confirm || " "}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <LockOutlinedIcon fontSize="small" />
                    </InputAdornment>
                  ),
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton tabIndex={-1} onClick={() => setShowPassword((prev) => !prev)} edge="end">
                        {showPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />

              <Button
                type="submit"
                variant="contained"
                size="large"
                fullWidth
                sx={{ mt: 2 }}
                disabled={submitting}
              >
                {submitting ? "Signing up..." : "Sign Up"}
              </Button>

              <Divider sx={{ my: 3 }}>or</Divider>

              <Button
                variant="outlined"
                fullWidth
                startIcon={<GoogleIcon />}
                onClick={() =>
                  setSnack({
                    open: true,
                    message: "Google Sign-Up coming soon",
                    severity: "info",
                  })
                }
              >
                Continue with Google
              </Button>

              <Typography sx={{ mt: 3, textAlign: "center" }} variant="body2">
                Already have an account?{" "}
                <MuiLink component={Link} to="/login" underline="hover">
                  Sign in
                </MuiLink>
              </Typography>
            </Box>
          </CardContent>
        </Card>
      </Box>

      <Snackbar
        open={snack.open}
        autoHideDuration={4000}
        onClose={() => setSnack({ ...snack, open: false })}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
      >
        <Alert
          severity={snack.severity}
          variant="filled"
          onClose={() => setSnack({ ...snack, open: false })}
        >
          {snack.message}
        </Alert>
      </Snackbar>
    </Box>
  );
}
