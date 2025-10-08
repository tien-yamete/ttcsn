import React, { useEffect, useMemo, useState } from "react";
import {
  Box,
  Card,
  CardContent,
  Typography,
  TextField,
  Button,
  Snackbar,
  Alert,
  CircularProgress,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import { verifyUser } from "../services/authenticationService";
import SendOtpButton from "../components/SendOtpButton";

const VERIFY_TTL_MS = 15 * 60 * 1000; // 15 phút

export default function VerifyOtpPage() {
  const navigate = useNavigate();

  const email = useMemo(
    () => (typeof window !== "undefined" ? localStorage.getItem("verifyEmail") || "" : ""),
    []
  );
  const context = useMemo(
    () => (typeof window !== "undefined" ? localStorage.getItem("verifyContext") || "" : ""),
    []
  );
  const issuedAt = useMemo(() => {
    if (typeof window === "undefined") return 0;
    const raw = localStorage.getItem("verifyIssuedAt");
    return raw ? parseInt(raw, 10) : 0;
  }, []);

  const [otp, setOtp] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [snack, setSnack] = useState({
    open: false,
    message: "",
    severity: "success",
  });
  const [cursor, setCursor] = useState({ x: 50, y: 50 });

  const cleanupVerifyFlags = () => {
    localStorage.removeItem("verifyEmail");
    localStorage.removeItem("verifyContext");
    localStorage.removeItem("verifyIssuedAt");
  };

  useEffect(() => {
    const now = Date.now();
    const expired = !issuedAt || now - issuedAt > VERIFY_TTL_MS;

    if (!email || context !== "register" || expired) {
      setSnack({
        open: true,
        message: !email
          ? "Không có email cần xác minh. Hãy đăng ký trước."
          : expired
            ? "Phiên xác minh đã hết hạn. Vui lòng đăng ký lại."
            : "Bạn không ở trong luồng đăng ký. Vui lòng đăng ký trước.",
        severity: "warning",
      });
      cleanupVerifyFlags();
      const t = setTimeout(() => navigate("/register"), 1500);
      return () => clearTimeout(t);
    }
  }, [email, context, issuedAt, navigate]);

  const handleMouseMove = (e) => {
    const x = (e.clientX / window.innerWidth) * 100;
    const y = (e.clientY / window.innerHeight) * 100;
    setCursor({ x, y });
  };

  const maskedEmail = useMemo(() => {
    if (!email) return "";
    const [local, domain] = email.split("@");
    if (!domain) return email;
    const head = local.slice(0, 2);
    return `${head}${local.length > 2 ? "***" : ""}@${domain}`;
  }, [email]);

  const handleVerify = async (e) => {
    e.preventDefault();
    if (!email) return;

    const code = otp.replace(/\D/g, "");
    if (code.length !== 6) {
      setSnack({ open: true, message: "Vui lòng nhập đủ 6 số OTP.", severity: "warning" });
      return;
    }

    setSubmitting(true);
    try {
      await verifyUser({ email, otpCode: code });
      setSnack({
        open: true,
        message: "Xác minh thành công! Đang chuyển về trang đăng nhập...",
        severity: "success",
      });

      cleanupVerifyFlags();
      setTimeout(() => navigate("/login"), 1200);
    } catch (err) {
      const msg = err?.response?.data?.message || "OTP không hợp lệ hoặc đã hết hạn";
      setSnack({ open: true, message: msg, severity: "error" });
    } finally {
      setSubmitting(false);
    }
  };

  const handleOtpChange = (e) => {
    const value = e.target.value.replace(/\D/g, "").slice(0, 6);
    setOtp(value);
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
          radial-gradient(circle at ${cursor.x}% ${cursor.y}%, rgba(0,200,255,0.2), transparent 40%),
          radial-gradient(circle at ${100 - cursor.x}% ${100 - cursor.y}%, rgba(180,0,255,0.2), transparent 45%),
          linear-gradient(135deg, #050510 0%, #0a0a1a 100%)
        `,
        transition: "background 0.3s ease",
        px: 2,
      }}
    >
      <Card
        sx={{
          width: 480,
          maxWidth: "100%",
          borderRadius: 4,
          background: "rgba(20,20,35,0.9)",
          backdropFilter: "blur(20px)",
          border: "1px solid rgba(255,255,255,0.08)",
          boxShadow: "0 8px 32px rgba(0,0,0,0.4)",
        }}
      >
        <CardContent sx={{ p: { xs: 3, sm: 5 } }}>
          {/* Header */}
          <Box sx={{ textAlign: "center", mb: 4 }}>
            <Typography
              variant="h4"
              fontWeight={800}
              sx={{
                background: "linear-gradient(90deg, #00e5ff, #2979ff)",
                backgroundClip: "text",
                WebkitBackgroundClip: "text",
                WebkitTextFillColor: "transparent",
                mb: 1
              }}
            >
              Friendify
            </Typography>
            <Typography
              variant="h5"
              fontWeight={700}
              sx={{ color: "white", mb: 1.5 }}
            >
              Xác minh email
            </Typography>
            <Typography
              variant="body1"
              sx={{
                color: "rgba(255,255,255,0.6)",
                lineHeight: 1.6,
                px: 2
              }}
            >
              Nhập mã OTP gồm 6 chữ số đã được gửi đến
            </Typography>
            <Typography
              variant="body1"
              sx={{
                color: "#00e5ff",
                fontWeight: 600,
                mt: 0.5
              }}
            >
              {maskedEmail || "email của bạn"}
            </Typography>
          </Box>

          {/* Form */}
          <Box component="form" onSubmit={handleVerify}>
            <TextField
              fullWidth
              value={otp}
              onChange={handleOtpChange}
              placeholder="• • • • • •"
              inputProps={{
                maxLength: 6,
                inputMode: "numeric",
                pattern: "[0-9]*",
                style: {
                  textAlign: "center",
                  fontSize: "2rem",
                  letterSpacing: "1rem",
                  fontWeight: 700
                }
              }}
              autoFocus
              sx={{
                mb: 3,
                "& .MuiOutlinedInput-root": {
                  borderRadius: 2,
                  color: "white",
                  background: "rgba(255,255,255,0.03)",
                  border: "2px solid rgba(255,255,255,0.1)",
                  py: 2,
                  "& fieldset": { border: "none" },
                  "&:hover": {
                    background: "rgba(255,255,255,0.05)",
                    borderColor: "rgba(0,229,255,0.3)"
                  },
                  "&.Mui-focused": {
                    background: "rgba(255,255,255,0.05)",
                    borderColor: "#00e5ff",
                    boxShadow: "0 0 0 3px rgba(0,229,255,0.1)"
                  },
                },
              }}
            />

            <Button
              type="submit"
              fullWidth
              variant="contained"
              disabled={submitting || !email || otp.length !== 6}
              sx={{
                py: 1.8,
                borderRadius: 2,
                fontWeight: 700,
                fontSize: "1rem",
                textTransform: "none",
                background: otp.length === 6
                  ? "linear-gradient(135deg, #00e5ff 0%, #2979ff 100%)"
                  : "rgba(255,255,255,0.08)",
                color: otp.length === 6 ? "white" : "rgba(255,255,255,0.3)",
                boxShadow: otp.length === 6 ? "0 4px 20px rgba(0,229,255,0.3)" : "none",
                transition: "all 0.3s ease",
                "&:hover": {
                  background: otp.length === 6
                    ? "linear-gradient(135deg, #00d4e6 0%, #2168e6 100%)"
                    : "rgba(255,255,255,0.08)",
                  transform: otp.length === 6 ? "translateY(-2px)" : "none",
                  boxShadow: otp.length === 6 ? "0 6px 25px rgba(0,229,255,0.4)" : "none",
                },
                "&:disabled": {
                  background: "rgba(255,255,255,0.08)",
                  color: "rgba(255,255,255,0.3)",
                }
              }}
            >
              {submitting ? (
                <CircularProgress size={24} sx={{ color: "#fff" }} />
              ) : (
                "Xác minh"
              )}
            </Button>

            {/* Resend Section */}
            <Box sx={{
              mt: 4,
              pt: 3,
              borderTop: "1px solid rgba(255,255,255,0.06)",
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              gap: 1.5
            }}>
              <Typography
                variant="body2"
                sx={{ color: "rgba(255,255,255,0.5)" }}
              >
                Chưa nhận được mã?
              </Typography>
              <SendOtpButton email={email} />
            </Box>

            {/* Back Button */}
            <Button
              fullWidth
              onClick={() => {
                cleanupVerifyFlags();
                navigate("/register");
              }}
              sx={{ 
                mt: 2.5,
                py: 1.2,
                borderRadius: 2,
                color: "rgba(255,255,255,0.5)", 
                fontWeight: 600,
                fontSize: "0.9rem",
                textTransform: "none",
                background: "rgba(255,255,255,0.03)",
                transition: "all 0.3s ease",
                "&:hover": {
                  color: "rgba(255,255,255,0.8)",
                  background: "rgba(255,255,255,0.08)"
                }
              }}
            >
              ← Quay lại đăng ký
            </Button>

          </Box>
        </CardContent>
      </Card>

      <Snackbar
        open={snack.open}
        autoHideDuration={3500}
        onClose={() => setSnack((s) => ({ ...s, open: false }))}
        anchorOrigin={{ vertical: "top", horizontal: "center" }}
      >
        <Alert
          severity={snack.severity}
          variant="filled"
          onClose={() => setSnack((s) => ({ ...s, open: false }))}
          sx={{ minWidth: 300 }}
        >
          {snack.message}
        </Alert>
      </Snackbar>
    </Box>
  );
}