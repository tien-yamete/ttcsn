import React, { useEffect, useRef, useState } from "react";
import { resendVerification } from "../services/authenticationService";
import { Button, Snackbar, Alert, Box, Typography } from "@mui/material";

const COOLDOWN_SEC = 60;

export default function ResendOtpButton({ email }) {
  const [cooldown, setCooldown] = useState(0);
  const [snack, setSnack] = useState({ open: false, message: "", severity: "success" });
  const timerRef = useRef(null);
  const hasInitialized = useRef(false);

  // Bắt đầu countdown ngay khi component mount (vì đã gửi OTP lúc đăng ký)
  useEffect(() => {
    if (!hasInitialized.current) {
      hasInitialized.current = true;
      startCountdown();
    }
    return () => clearInterval(timerRef.current);
  }, []);

  const startCountdown = () => {
    setCooldown(COOLDOWN_SEC);
    timerRef.current = setInterval(() => {
      setCooldown((prev) => {
        if (prev <= 1) {
          clearInterval(timerRef.current);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
  };

  const onResend = async () => {
    try {
      await resendVerification(email);
      setSnack({ open: true, message: "OTP mới đã được gửi!", severity: "success" });
      startCountdown();
    } catch (err) {
      const msg = err?.response?.data?.message || "Không thể gửi lại OTP lúc này";
      setSnack({ open: true, message: msg, severity: "error" });
      if (msg.toLowerCase().includes("wait") || msg.toLowerCase().includes("frequent")) {
        startCountdown();
      }
    }
  };

  return (
    <>
      <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 1 }}>
        <Button
          onClick={onResend}
          disabled={cooldown > 0}
          sx={{
            px: 3,
            py: 1,
            borderRadius: 2,
            fontWeight: 600,
            fontSize: "0.9rem",
            textTransform: "none",
            color: "#00e5ff",
            background: "linear-gradient(90deg, rgba(0,229,255,0.08), rgba(41,121,255,0.08))",
            border: "1px solid rgba(0,229,255,0.3)",
            transition: "all 0.3s ease",
            "&:hover": {
              background: "linear-gradient(90deg, #00e5ff33, #2979ff33, #7b1fa233)",
              boxShadow: "0 0 12px rgba(0,229,255,0.4)",
              transform: "scale(1.02)",
            },
            "&:disabled": {
              color: "rgba(255,255,255,0.3)",
              border: "1px solid rgba(255,255,255,0.1)",
            }
          }}
        >
          {"Gửi lại mã OTP"}
        </Button>

        {cooldown > 0 && (
          <Typography
            variant="caption"
            sx={{
              color: "rgba(255,255,255,0.4)",
              fontSize: "0.75rem"
            }}
          >
            Vui lòng chờ {cooldown} giây
          </Typography>
        )}
      </Box>

      <Snackbar
        open={snack.open}
        autoHideDuration={2500}
        onClose={() => setSnack({ ...snack, open: false })}
        anchorOrigin={{ vertical: "top", horizontal: "center" }}
      >
        <Alert
          severity={snack.severity}
          variant="filled"
          onClose={() => setSnack({ ...snack, open: false })}
          sx={{ minWidth: 300 }}
        >
          {snack.message}
        </Alert>
      </Snackbar>
    </>
  );
}