import React from "react";
import {
  Box,
  Paper,
  Avatar,
  TextField,
  IconButton,
  Divider,
  alpha,
} from "@mui/material";
import ImageIcon from "@mui/icons-material/Image";
import VideocamIcon from "@mui/icons-material/Videocam";
import EmojiEmotionsIcon from "@mui/icons-material/EmojiEmotions";
import { isAuthenticated } from "../services/authenticationService";

export default function CreatePostComposer({ onClick, user }) {
  if (!isAuthenticated()) return null;

  return (
    <Paper
      elevation={0}
      sx={(t) => ({
        mb: 3,
        borderRadius: 4,
        bgcolor: "background.paper",
        border: "1px solid",
        borderColor: "divider",
        p: 2.5,
        transition: "all 0.25s ease",
        "&:hover": {
          boxShadow: t.shadows[2],
          borderColor: alpha(t.palette.primary.main, 0.25),
        },
      })}
    >
      <Box sx={{ display: "flex", alignItems: "center", gap: 1.5, mb: 2 }}>
        <Avatar
          src={user?.avatar || "/avatar.png"}
          sx={(t) => ({
            width: 48,
            height: 48,
            border: "2px solid",
            borderColor: "divider",
            background: `linear-gradient(135deg, ${t.palette.primary.main} 0%, ${alpha(
              t.palette.primary.main,
              0.6
            )} 100%)`,
          })}
        >
          {user?.name?.charAt(0) || "U"}
        </Avatar>
        <TextField
          fullWidth
          placeholder="What's on your mind?"
          onClick={onClick}
          variant="outlined"
          sx={{
            cursor: "pointer",
            "& .MuiOutlinedInput-root": {
              borderRadius: 6,
              fontSize: 15,
              bgcolor: (t) =>
                t.palette.mode === "dark"
                  ? alpha(t.palette.common.white, 0.04)
                  : alpha(t.palette.common.black, 0.02),
              "& fieldset": { borderColor: "divider" },
              "&:hover fieldset": { borderColor: "primary.main" },
              "&.Mui-focused fieldset": {
                borderColor: "primary.main",
                borderWidth: 2,
              },
            },
            "& .MuiInputBase-input": {
              cursor: "pointer",
            },
          }}
          InputProps={{
            readOnly: true,
          }}
        />
      </Box>

      <Divider sx={{ mb: 1.5 }} />

      <Box
        sx={{
          display: "flex",
          justifyContent: "space-around",
          alignItems: "center",
        }}
      >
        <IconButton
          onClick={onClick}
          sx={(t) => ({
            color: "#45bd62",
            borderRadius: 2.5,
            px: 2,
            py: 1,
            gap: 1,
            flex: 1,
            transition: "all 0.2s ease",
            "&:hover": {
              bgcolor: alpha("#45bd62", 0.08),
              transform: "scale(1.02)",
            },
          })}
        >
          <ImageIcon />
          <Box
            component="span"
            sx={{
              fontSize: 14,
              fontWeight: 600,
              display: { xs: "none", sm: "inline" },
            }}
          >
            Photo
          </Box>
        </IconButton>

        <IconButton
          onClick={onClick}
          sx={(t) => ({
            color: "#f3425f",
            borderRadius: 2.5,
            px: 2,
            py: 1,
            gap: 1,
            flex: 1,
            transition: "all 0.2s ease",
            "&:hover": {
              bgcolor: alpha("#f3425f", 0.08),
              transform: "scale(1.02)",
            },
          })}
        >
          <VideocamIcon />
          <Box
            component="span"
            sx={{
              fontSize: 14,
              fontWeight: 600,
              display: { xs: "none", sm: "inline" },
            }}
          >
            Video
          </Box>
        </IconButton>

        <IconButton
          onClick={onClick}
          sx={(t) => ({
            color: "#f7b928",
            borderRadius: 2.5,
            px: 2,
            py: 1,
            gap: 1,
            flex: 1,
            transition: "all 0.2s ease",
            "&:hover": {
              bgcolor: alpha("#f7b928", 0.08),
              transform: "scale(1.02)",
            },
          })}
        >
          <EmojiEmotionsIcon />
          <Box
            component="span"
            sx={{
              fontSize: 14,
              fontWeight: 600,
              display: { xs: "none", sm: "inline" },
            }}
          >
            Feeling
          </Box>
        </IconButton>
      </Box>
    </Paper>
  );
}
