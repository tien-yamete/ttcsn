// src/components/NewChatPopover.jsx
import React, { useState, useEffect, useCallback } from "react";
import PropTypes from "prop-types";
import {
  Box,
  Popover,
  TextField,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  Avatar,
  Typography,
  CircularProgress,
  InputAdornment,
  IconButton,
  Alert,
  useTheme,
} from "@mui/material";
import { alpha } from "@mui/material/styles";
import SearchIcon from "@mui/icons-material/Search";
import ClearIcon from "@mui/icons-material/Clear";

// MOCK USERS (tùy bạn thay / mở rộng)
const MOCK_USERS = [
  { id: "u-1", username: "mai.le", firstName: "Mai", lastName: "Lê", avatar: "https://i.pravatar.cc/150?u=mai.le" },
  { id: "u-2", username: "kien.tran", firstName: "Kiên", lastName: "Trần", avatar: "https://i.pravatar.cc/150?u=kien.tran" },
  { id: "u-3", username: "thuy.nguyen", firstName: "Thuý", lastName: "Nguyễn", avatar: "https://i.pravatar.cc/150?u=thuy.nguyen" },
  { id: "u-4", username: "nhom-kientruc", firstName: "Nhóm", lastName: "Kiến Trúc", avatar: "https://i.pravatar.cc/150?u=team1" },
];

// simulate network delay
const delay = (ms) => new Promise((res) => setTimeout(res, ms));

const NewChatPopover = ({ anchorEl, open, onClose, onSelectUser }) => {
  const theme = useTheme();
  const isDark = theme.palette.mode === "dark";

  // color tokens tuned for dark/light
  const paperBg = isDark ? alpha(theme.palette.background.paper, 0.95) : theme.palette.background.paper;
  const inputBg = isDark ? alpha("#ffffff", 0.03) : alpha(theme.palette.primary.light, 0.06);
  const listHoverBg = isDark ? alpha("#ffffff", 0.03) : "rgba(0,0,0,0.04)";
  const placeholderColor = isDark ? alpha("#ffffff", 0.6) : theme.palette.text.secondary;

  const [searchQuery, setSearchQuery] = useState("");
  const [loading, setLoading] = useState(false);
  const [searchResults, setSearchResults] = useState([]);
  const [hasSearched, setHasSearched] = useState(false);
  const [error, setError] = useState(null);

  // mock search function (filter by username / firstName / lastName)
  const handleSearch = useCallback(
    async (query) => {
      if (!query?.trim()) {
        setSearchResults([]);
        setHasSearched(false);
        setError(null);
        return;
      }

      setLoading(true);
      setHasSearched(true);
      setError(null);

      try {
        // simulate network latency
        await delay(300);

        const q = query.trim().toLowerCase();
        const filtered = MOCK_USERS.filter(
          (u) =>
            (u.username && u.username.toLowerCase().includes(q)) ||
            (u.firstName && u.firstName.toLowerCase().includes(q)) ||
            (u.lastName && u.lastName.toLowerCase().includes(q)) ||
            `${u.firstName} ${u.lastName}`.toLowerCase().includes(q)
        );

        setSearchResults(filtered);
      } catch (err) {
        console.error("Mock search error:", err);
        setError("Đã xảy ra lỗi khi tìm kiếm (mock).");
        setSearchResults([]);
      } finally {
        setLoading(false);
      }
    },
    []
  );

  // Debounced search effect (500ms)
  useEffect(() => {
    const timeoutId = setTimeout(() => {
      if (searchQuery) handleSearch(searchQuery);
      else {
        setSearchResults([]);
        setHasSearched(false);
        setError(null);
      }
    }, 500);

    return () => clearTimeout(timeoutId);
  }, [searchQuery, handleSearch]);

  // Clear when popover closes
  useEffect(() => {
    if (!open) {
      setSearchQuery("");
      setSearchResults([]);
      setHasSearched(false);
      setError(null);
      setLoading(false);
    }
  }, [open]);

  const handleClearSearch = () => {
    setSearchQuery("");
    setSearchResults([]);
    setHasSearched(false);
    setError(null);
  };

  const handleUserSelect = (user) => {
    // normalize shape so parent gets: { userId, displayName, avatar }
    const normalized = {
      userId: user.id,
      displayName: user.username || `${user.firstName} ${user.lastName}`.trim(),
      avatar: user.avatar || "",
    };
    onSelectUser(normalized);
    // reset local state
    setSearchQuery("");
    setSearchResults([]);
    setHasSearched(false);
    onClose();
  };

  return (
    <Popover
      open={open}
      anchorEl={anchorEl}
      onClose={onClose}
      anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
      transformOrigin={{ vertical: "top", horizontal: "right" }}
      slotProps={{
        paper: {
          sx: {
            width: 320,
            p: 2,
            mt: 1,
            bgcolor: paperBg,
            boxShadow: isDark
              ? "0 6px 18px rgba(2,6,23,0.8)"
              : "0 6px 18px rgba(15,15,15,0.08)",
            border: `1px solid ${isDark ? alpha("#ffffff", 0.03) : alpha("#000000", 0.06)}`,
          },
        },
      }}
    >
      <Typography variant="subtitle1" sx={{ mb: 2, fontWeight: "bold", color: "text.primary" }}>
        Start a new conversation
      </Typography>

      <TextField
        fullWidth
        placeholder="Start typing to search users..."
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.target.value)}
        InputProps={{
          startAdornment: (
            <InputAdornment position="start">
              <SearchIcon sx={{ color: placeholderColor }} />
            </InputAdornment>
          ),
          endAdornment: searchQuery && (
            <InputAdornment position="end">
              <IconButton size="small" onClick={handleClearSearch} aria-label="clear search">
                <ClearIcon fontSize="small" sx={{ color: placeholderColor }} />
              </IconButton>
            </InputAdornment>
          ),
          sx: {
            bgcolor: inputBg,
            borderRadius: 1.5,
            "& .MuiOutlinedInput-notchedOutline": {
              borderColor: isDark ? alpha("#ffffff", 0.04) : alpha("#000", 0.06),
            },
            "&:hover .MuiOutlinedInput-notchedOutline": {
              borderColor: isDark ? alpha("#ffffff", 0.08) : alpha("#000", 0.12),
            },
            "&.Mui-focused .MuiOutlinedInput-notchedOutline": {
              borderColor: theme.palette.primary.main,
              boxShadow: `0 0 0 2px ${alpha(theme.palette.primary.main, 0.12)}`,
            },
            input: { color: "text.primary" },
          },
        }}
        sx={{ mb: 2 }}
        autoFocus
      />

      <Box sx={{ height: 300, overflow: "auto" }}>
        {loading && (
          <Box sx={{ display: "flex", justifyContent: "center", p: 3 }}>
            <CircularProgress size={28} />
          </Box>
        )}

        {!loading && error && (
          <Box sx={{ p: 2 }}>
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          </Box>
        )}

        {!loading && !error && searchResults.length > 0 && (
          <List>
            {searchResults.map((user) => (
              <ListItem
                key={user.id}
                onClick={() => handleUserSelect(user)}
                sx={{
                  borderRadius: 1,
                  cursor: "pointer",
                  px: 0.5,
                  "&:hover": {
                    bgcolor: listHoverBg,
                    transform: "translateX(4px)",
                  },
                }}
              >
                <ListItemAvatar>
                  <Avatar
                    src={user.avatar || ""}
                    alt={user.username}
                    sx={{
                      width: 40,
                      height: 40,
                      border: `2px solid ${isDark ? alpha("#ffffff", 0.06) : alpha("#000", 0.06)}`,
                    }}
                  />
                </ListItemAvatar>
                <ListItemText
                  primary={
                    <Typography sx={{ color: "text.primary", fontWeight: 600 }}>{user.username}</Typography>
                  }
                  secondary={
                    <Typography sx={{ color: "text.secondary", fontSize: 13 }}>
                      {`${user.firstName || ""} ${user.lastName || ""}`.trim()}
                    </Typography>
                  }
                  primaryTypographyProps={{ variant: "body1" }}
                />
              </ListItem>
            ))}
          </List>
        )}

        {!loading && !error && searchResults.length === 0 && hasSearched && (
          <Box sx={{ p: 2, textAlign: "center" }}>
            <Typography color="text.secondary">No users found matching "{searchQuery}"</Typography>
          </Box>
        )}

        {!loading && !error && !hasSearched && (
          <Box sx={{ p: 2, textAlign: "center" }}>
            <Typography color="text.secondary">Search for a user to start a conversation</Typography>
          </Box>
        )}
      </Box>
    </Popover>
  );
};

NewChatPopover.propTypes = {
  anchorEl: PropTypes.object,
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  onSelectUser: PropTypes.func.isRequired,
};

export default NewChatPopover;
