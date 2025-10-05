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
} from "@mui/material";
import MailOutlineIcon from "@mui/icons-material/MailOutline";
import LockResetIcon from "@mui/icons-material/LockReset";
import { useState } from "react";
import { requestPasswordReset } from "../services/authenticationService";

export default function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [snack, setSnack] = useState({
    open: false,
    message: "",
    severity: "error",
  });
  const [submitting, setSubmitting] = useState(false);
  const [cursor, setCursor] = useState({ x: 50, y: 50 });

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!email.trim()) {
      setSnack({ open: true, message: "Email is required", severity: "error" });
      return;
    }
    setSubmitting(true);
    try {
      const res = await requestPasswordReset(email);
      if (res?.status === 200) {
        setSnack({
          open: true,
          message: "Check your inbox for reset link",
          severity: "success",
        });
      }
    } catch (err) {
      setSnack({
        open: true,
        message: "Failed to send email",
        severity: "error",
      });
    } finally {
      setSubmitting(false);
    }
  };

  const handleMouseMove = (e) => {
    const x = (e.clientX / window.innerWidth) * 100;
    const y = (e.clientY / window.innerHeight) * 100;
    setCursor({ x, y });
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
          radial-gradient(circle at 20% 80%, rgba(0,150,255,0.15), transparent 50%),
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
              <LockResetIcon sx={{ fontSize: 34, color: "#fff" }} />
            </Box>
            <Typography variant="h5" fontWeight={700}>
              Reset your password
            </Typography>
            <Typography
              variant="body2"
              sx={{ color: "rgba(255,255,255,0.7)", mt: 1 }}
            >
              Enter your email and we’ll send you a reset link.
            </Typography>
          </Box>

          <Box component="form" onSubmit={handleSubmit}>
            <TextField
              label="Email address"
              type="email"
              fullWidth
              margin="normal"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              variant="outlined"
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <MailOutlineIcon sx={{ color: "rgba(255,255,255,0.7)" }} />
                  </InputAdornment>
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
              {submitting ? "Sending..." : "Send Reset Link"}
            </Button>
          </Box>

          <Typography
            variant="body2"
            textAlign="center"
            mt={3}
            sx={{ color: "rgba(255,255,255,0.7)" }}
          >
            Remember your password?{" "}
            <a
              href="/login"
              style={{
                color: "#00e5ff",
                fontWeight: 600,
                textDecoration: "none",
              }}
            >
              Back to login
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
