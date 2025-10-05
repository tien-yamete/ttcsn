import {
  Box,
  Button,
  Card,
  CardContent,
  TextField,
  Typography,
  Snackbar,
  Alert,
  InputAdornment,
  IconButton,
} from "@mui/material";
import LockIcon from "@mui/icons-material/Lock";
import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { resetPassword } from "../services/authenticationService";

export default function ResetPassword() {
  const { token } = useParams();
  const navigate = useNavigate();

  const [password, setPassword] = useState("");
  const [confirm, setConfirm] = useState("");
  const [showPass, setShowPass] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);

  const [snack, setSnack] = useState({
    open: false,
    message: "",
    severity: "error",
  });
  const [submitting, setSubmitting] = useState(false);
  const [cursor, setCursor] = useState({ x: 50, y: 50 });

  const handleMouseMove = (e) => {
    const x = (e.clientX / window.innerWidth) * 100;
    const y = (e.clientY / window.innerHeight) * 100;
    setCursor({ x, y });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!password || !confirm) {
      setSnack({ open: true, message: "All fields are required", severity: "error" });
      return;
    }
    if (password !== confirm) {
      setSnack({ open: true, message: "Passwords do not match", severity: "error" });
      return;
    }

    setSubmitting(true);
    try {
      const res = await resetPassword(token, password);
      if (res?.status === 200) {
        setSnack({ open: true, message: "Password reset successful", severity: "success" });
        setTimeout(() => navigate("/login"), 2000);
      }
    } catch (err) {
      setSnack({ open: true, message: "Failed to reset password", severity: "error" });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Box
      onMouseMove={handleMouseMove}
      sx={{
        minHeight: "100vh",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        backgroundColor: "#050510",
        backgroundImage: `
          radial-gradient(circle at ${cursor.x}% ${cursor.y}%, rgba(0,200,255,0.25), transparent 35%),
          radial-gradient(circle at ${100 - cursor.x}% ${100 - cursor.y}%, rgba(180,0,255,0.25), transparent 40%),
          linear-gradient(135deg, #050510 0%, #0a0a1a 100%)
        `,
        transition: "background 0.3s ease",
      }}
    >
      <Card
        sx={{
          width: 420,
          borderRadius: 6,
          p: 2,
          background: "rgba(15,15,25,0.85)",
          backdropFilter: "blur(14px)",
          border: "1px solid rgba(255,255,255,0.1)",
          boxShadow:
            "0 0 20px rgba(0,200,255,0.15), 0 0 40px rgba(180,0,255,0.1)",
          color: "white",
        }}
      >
        <CardContent sx={{ p: 4 }}>
          <Box sx={{ textAlign: "center", mb: 3 }}>
            <Box
              sx={{
                display: "inline-flex",
                alignItems: "center",
                justifyContent: "center",
                width: 70,
                height: 70,
                borderRadius: "50%",
                mb: 2,
                background:
                  "linear-gradient(135deg, #00e5ff, #2979ff, #7b1fa2)",
                boxShadow: "0 0 20px rgba(0,200,255,0.6)",
              }}
            >
              <LockIcon sx={{ fontSize: 34, color: "#fff" }} />
            </Box>
            <Typography variant="h5" fontWeight={700}>
              Set a new password
            </Typography>
            <Typography
              variant="body2"
              sx={{ color: "rgba(255,255,255,0.7)", mt: 1 }}
            >
              Enter and confirm your new password below.
            </Typography>
          </Box>

          <Box component="form" onSubmit={handleSubmit}>
            <TextField
              label="New Password"
              type={showPass ? "text" : "password"}
              fullWidth
              margin="normal"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              variant="outlined"
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <LockIcon sx={{ color: "rgba(255,255,255,0.7)" }} />
                  </InputAdornment>
                ),
                endAdornment: (
                  <IconButton onClick={() => setShowPass(!showPass)} edge="end">
                    {showPass ? <VisibilityOff /> : <Visibility />}
                  </IconButton>
                ),
              }}
              sx={{
                "& .MuiOutlinedInput-root": {
                  borderRadius: 3,
                  color: "white",
                  background: "rgba(255,255,255,0.05)",
                  "& fieldset": { borderColor: "rgba(255,255,255,0.2)" },
                  "&:hover fieldset": { borderColor: "#00e5ff" },
                  "&.Mui-focused fieldset": { borderColor: "#2979ff" },
                },
                "& .MuiInputLabel-root": { color: "rgba(255,255,255,0.6)" },
                "& .MuiInputLabel-root.Mui-focused": { color: "#00e5ff" },
              }}
            />

            <TextField
              label="Confirm Password"
              type={showConfirm ? "text" : "password"}
              fullWidth
              margin="normal"
              value={confirm}
              onChange={(e) => setConfirm(e.target.value)}
              variant="outlined"
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <LockIcon sx={{ color: "rgba(255,255,255,0.7)" }} />
                  </InputAdornment>
                ),
                endAdornment: (
                  <IconButton onClick={() => setShowConfirm(!showConfirm)} edge="end">
                    {showConfirm ? <VisibilityOff /> : <Visibility />}
                  </IconButton>
                ),
              }}
              sx={{
                "& .MuiOutlinedInput-root": {
                  borderRadius: 3,
                  color: "white",
                  background: "rgba(255,255,255,0.05)",
                  "& fieldset": { borderColor: "rgba(255,255,255,0.2)" },
                  "&:hover fieldset": { borderColor: "#00e5ff" },
                  "&.Mui-focused fieldset": { borderColor: "#2979ff" },
                },
                "& .MuiInputLabel-root": { color: "rgba(255,255,255,0.6)" },
                "& .MuiInputLabel-root.Mui-focused": { color: "#00e5ff" },
              }}
            />

            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{
                mt: 3,
                py: 1.4,
                borderRadius: 3,
                fontWeight: 700,
                background: "linear-gradient(90deg,#00e5ff,#2979ff,#7b1fa2)",
                backgroundSize: "200% 100%",
                transition: "all 0.3s ease",
                "&:hover": {
                  backgroundPosition: "100% 0",
                  transform: "scale(1.03)",
                  boxShadow: "0 0 15px rgba(0,200,255,0.6)",
                },
              }}
              disabled={submitting}
            >
              {submitting ? "Resetting..." : "Reset Password"}
            </Button>
          </Box>

          <Typography
            variant="body2"
            textAlign="center"
            mt={3}
            sx={{ color: "rgba(255,255,255,0.7)" }}
          >
            Back to{" "}
            <a
              href="/login"
              style={{
                color: "#00e5ff",
                fontWeight: 600,
                textDecoration: "none",
              }}
            >
              Login
            </a>
          </Typography>
        </CardContent>
      </Card>

      <Snackbar
        open={snack.open}
        autoHideDuration={4000}
        onClose={() => setSnack({ ...snack, open: false })}
      >
        <Alert severity={snack.severity} variant="filled">
          {snack.message}
        </Alert>
      </Snackbar>
    </Box>
  );
}
