import { Box, Typography } from "@mui/material";
import { useEffect, useState } from "react";

export default function AuthLeftPanel({ variant = "login" }) {
  const images = [
    "logo/bg1.jpg",
    "logo/bg2.jpg",
    "logo/bg3.jpg",
  ];

  const [bgIndex, setBgIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setBgIndex((prev) => (prev + 1) % images.length);
    }, 3000);
    return () => clearInterval(interval);
  }, [images.length]);

  return (
    <Box
      sx={{
        display: { xs: "none", md: "flex" },
        position: "relative",
        alignItems: "center",
        justifyContent: "center",
        overflow: "hidden",
        color: "white",
      }}
    >
      {/* Slideshow */}
      {images.map((img, i) => (
        <Box
          key={i}
          sx={{
            position: "absolute",
            inset: 0,
            backgroundImage: `url(${img})`,
            backgroundSize: "cover",
            backgroundPosition: "center",
            opacity: i === bgIndex ? 1 : 0,
            transition: "opacity 1.5s ease-in-out",
          }}
        />
      ))}

      {/* overlay gradient */}
      <Box
        sx={{
          position: "absolute",
          inset: 0,
          background: "linear-gradient(to bottom, rgba(0,0,0,0.3), rgba(0,0,0,0.6))",
          zIndex: 0,
        }}
      />

      {/* overlay màu xanh tím */}
      <Box
        sx={{
          position: "absolute",
          inset: 0,
          background: "rgba(60, 20, 120, 0.5)",
          zIndex: 1,
        }}
      />

      {/* Nội dung chữ */}
      <Box
        sx={{
          textAlign: "center",
          px: 6,
          position: "relative",
          zIndex: 2,
        }}
      >
        <Typography
          variant="h1"
          sx={{
            fontWeight: 800,
            letterSpacing: 0.5,
            textShadow: "0px 4px 20px rgba(0,0,0,0.7)",
          }}
        >
          Friendify
        </Typography>

        <Typography sx={{ opacity: 0.9, mt: 2 }} variant="h6">
          {variant === "login"
            ? "Sign in to connect with friends and share moments."
            : "Create your account and join the community."}
        </Typography>
      </Box>
    </Box>
  );
}
