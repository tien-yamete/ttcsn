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
import GoogleIcon from "@mui/icons-material/Google";
import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { logIn, isAuthenticated } from "../services/authenticationService";
import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import IconButton from "@mui/material/IconButton";
import LoginLeftPanel from "../components/LoginLeftPanel";

export default function Login() {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);
  const [snack, setSnack] = useState({ open: false, message: "", severity: "error" });
  const [showPassword, setShowPassword] = useState(false);

  useEffect(() => {
    if (isAuthenticated()) {
      navigate("/");
    }
  }, [navigate]);

  const validate = () => {
    const e = {};
    if (!username?.trim()) e.username = "Required";
    if (!password?.trim()) e.password = "Required";
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const onSubmit = async (evt) => {
    evt.preventDefault();
    if (!validate()) return;
    setSubmitting(true);

    try {
      const res = await logIn(username.trim(), password);

      if (res?.status === 200) {
        navigate("/");
      } else {
        setSnack({
          open: true,
          message: "Đăng nhập thất bại, vui lòng thử lại.",
          severity: "error",
        });
      }
    } catch (err) {
      const msg =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        err?.message ||
        "Đăng nhập thất bại";
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
      <LoginLeftPanel variant="login" />
      {/* Right form panel */}
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          p: 3,
          backgroundColor: (t) => (t.palette.mode === "dark" ? "background.default" : "#f7f8fa"),
        }}
      >
        <Card sx={{ width: 440, maxWidth: "100%", boxShadow: 6, borderRadius: 3 }}>
          <CardContent sx={{ p: 4 }}>
            <Typography variant="h5" sx={{ fontWeight: 700, mb: 1 }}>
              Welcome back
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
              Sign in to your account
            </Typography>

            <Box component="form" onSubmit={onSubmit} noValidate>
              <TextField
                label="Username or Email"
                fullWidth
                margin="normal"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                error={Boolean(errors.username)}
                helperText={errors.username || " "}
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
                      <IconButton
                        tabIndex={-1}
                        onClick={() => setShowPassword((prev) => !prev)}
                        edge="end"
                      >
                        {showPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />


              <Box sx={{ display: "flex", justifyContent: "space-between", mt: 1 }}>
                <MuiLink tabIndex={-1} component={Link} to="#" underline="hover" sx={{ fontSize: 14 }}>
                  Forgot password?
                </MuiLink>
              </Box>

              <Button
                type="submit"
                variant="contained" 
                size="large"
                fullWidth
                sx={{ mt: 2 }}
                disabled={submitting}
              >
                {submitting ? "Signing in..." : "Continue"}
              </Button>

              <Divider sx={{ my: 3 }}>or</Divider>

              <Button
                variant="outlined"
                fullWidth
                startIcon={<GoogleIcon />}
                onClick={() => setSnack({ open: true, message: "Google Sign-In coming soon", severity: "info" })}
              >
                Continue with Google
              </Button>

              <Typography sx={{ mt: 3, textAlign: "center" }} variant="body2">
                New to Friendify?{" "}
                <MuiLink component={Link} to="/register" underline="hover">
                  Create an account
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
        <Alert severity={snack.severity} variant="filled" onClose={() => setSnack({ ...snack, open: false })}>
          {snack.message}
        </Alert>
      </Snackbar>
    </Box>
  );
}
