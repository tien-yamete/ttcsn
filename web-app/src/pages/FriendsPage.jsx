// src/pages/Friends.jsx
import { useState } from "react";
import {
  Box,
  Card,
  Typography,
  Avatar,
  Button,
  Tabs,
  Tab,
  TextField,
  InputAdornment,
  Grid,
  Chip,
  IconButton,
  Divider,
  Stack,
  Snackbar,
  Alert,
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import CheckIcon from "@mui/icons-material/Check";
import CloseIcon from "@mui/icons-material/Close";
import PeopleIcon from "@mui/icons-material/People";
import PersonIcon from "@mui/icons-material/Person";
import Scene from "./Scene";

// Dữ liệu giả
const mockFriendRequests = [
  {
    id: 1,
    name: "Nguyễn Văn A",
    avatar: "https://i.pravatar.cc/150?img=1",
    mutualFriends: 12,
    time: "2 giờ trước",
  },
  {
    id: 2,
    name: "Trần Thị B",
    avatar: "https://i.pravatar.cc/150?img=2",
    mutualFriends: 8,
    time: "5 giờ trước",
  },
  {
    id: 3,
    name: "Lê Minh C",
    avatar: "https://i.pravatar.cc/150?img=3",
    mutualFriends: 15,
    time: "1 ngày trước",
  },
  {
    id: 4,
    name: "Phạm Thu D",
    avatar: "https://i.pravatar.cc/150?img=4",
    mutualFriends: 5,
    time: "2 ngày trước",
  },
];

const mockSuggestions = [
  {
    id: 5,
    name: "Hoàng Văn E",
    avatar: "https://i.pravatar.cc/150?img=5",
    mutualFriends: 20,
    reason: "Bạn chung: Mai Linh, Tuấn Anh",
  },
  {
    id: 6,
    name: "Đỗ Thị F",
    avatar: "https://i.pravatar.cc/150?img=6",
    mutualFriends: 18,
    reason: "Học tại Đại học Bách Khoa",
  },
  {
    id: 7,
    name: "Vũ Minh G",
    avatar: "https://i.pravatar.cc/150?img=7",
    mutualFriends: 10,
    reason: "Làm việc tại FPT Software",
  },
  {
    id: 8,
    name: "Bùi Thu H",
    avatar: "https://i.pravatar.cc/150?img=8",
    mutualFriends: 7,
    reason: "Sống tại Hà Nội",
  },
  {
    id: 9,
    name: "Đinh Văn I",
    avatar: "https://i.pravatar.cc/150?img=9",
    mutualFriends: 14,
    reason: "Bạn chung: Phương Anh",
  },
  {
    id: 10,
    name: "Ngô Thị K",
    avatar: "https://i.pravatar.cc/150?img=10",
    mutualFriends: 9,
    reason: "Học tại Đại học Quốc Gia",
  },
];

const mockAllFriends = [
  {
    id: 11,
    name: "Mai Linh",
    avatar: "https://i.pravatar.cc/150?img=11",
    mutualFriends: 25,
    since: "Bạn bè từ 2020",
  },
  {
    id: 12,
    name: "Tuấn Anh",
    avatar: "https://i.pravatar.cc/150?img=12",
    mutualFriends: 30,
    since: "Bạn bè từ 2019",
  },
  {
    id: 13,
    name: "Phương Anh",
    avatar: "https://i.pravatar.cc/150?img=13",
    mutualFriends: 22,
    since: "Bạn bè từ 2021",
  },
  {
    id: 14,
    name: "Đức Thắng",
    avatar: "https://i.pravatar.cc/150?img=14",
    mutualFriends: 18,
    since: "Bạn bè từ 2022",
  },
  {
    id: 15,
    name: "Hương Giang",
    avatar: "https://i.pravatar.cc/150?img=15",
    mutualFriends: 16,
    since: "Bạn bè từ 2021",
  },
  {
    id: 16,
    name: "Minh Tuấn",
    avatar: "https://i.pravatar.cc/150?img=16",
    mutualFriends: 12,
    since: "Bạn bè từ 2023",
  },
];

export default function Friends() {
  const [tabValue, setTabValue] = useState(0);
  const [searchQuery, setSearchQuery] = useState("");
  const [friendRequests, setFriendRequests] = useState(mockFriendRequests);
  const [suggestions, setSuggestions] = useState(mockSuggestions);
  const [allFriends, setAllFriends] = useState(mockAllFriends);
  const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
    setSearchQuery("");
  };

  const handleAcceptRequest = (id) => {
    setFriendRequests((prev) => prev.filter((req) => req.id !== id));
    setSnackbar({ open: true, message: "Đã chấp nhận lời mời kết bạn!", severity: "success" });
  };

  const handleDeclineRequest = (id) => {
    setFriendRequests((prev) => prev.filter((req) => req.id !== id));
    setSnackbar({ open: true, message: "Đã từ chối lời mời kết bạn!", severity: "info" });
  };

  const handleAddFriend = (id) => {
    setSuggestions((prev) => prev.filter((sug) => sug.id !== id));
    setSnackbar({ open: true, message: "Đã gửi lời mời kết bạn!", severity: "success" });
  };

  const handleRemoveSuggestion = (id) => {
    setSuggestions((prev) => prev.filter((sug) => sug.id !== id));
    setSnackbar({ open: true, message: "Đã xóa gợi ý này!", severity: "info" });
  };

  const handleUnfriend = (id) => {
    setAllFriends((prev) => prev.filter((friend) => friend.id !== id));
    setSnackbar({ open: true, message: "Đã hủy kết bạn!", severity: "warning" });
  };

  const handleCloseSnackbar = () => {
    setSnackbar({ ...snackbar, open: false });
  };

  const filteredFriends = allFriends.filter((friend) =>
    friend.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <Scene>
      <Box sx={{ display: "flex", justifyContent: "center", width: "100%", mt: 4, px: 2 }}>
        <Box sx={{ width: "100%", maxWidth: 1200 }}>
          {/* Header */}
          <Card
            elevation={0}
            sx={(t) => ({
              borderRadius: 4,
              p: 3,
              mb: 3,
              boxShadow: t.shadows[1],
              border: "1px solid",
              borderColor: "divider",
              bgcolor: "background.paper",
            })}
          >
            <Box sx={{ display: "flex", alignItems: "center", justifyContent: "space-between", mb: 2 }}>
              <Typography
                sx={{
                  fontSize: 26,
                  fontWeight: 700,
                  background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                  WebkitBackgroundClip: "text",
                  WebkitTextFillColor: "transparent",
                }}
              >
                Bạn bè
              </Typography>
              <Chip
                icon={<PeopleIcon />}
                label={`${allFriends.length} bạn bè`}
                sx={{
                  background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                  color: "white",
                  fontWeight: 600,
                }}
              />
            </Box>

            <Tabs
              value={tabValue}
              onChange={handleTabChange}
              sx={{
                "& .MuiTab-root": {
                  textTransform: "none",
                  fontSize: 15,
                  fontWeight: 600,
                  minHeight: 48,
                },
                "& .Mui-selected": {
                  color: "primary.main",
                },
              }}
            >
              <Tab label={`Lời mời (${friendRequests.length})`} />
              <Tab label="Gợi ý kết bạn" />
              <Tab label="Tất cả bạn bè" />
            </Tabs>
          </Card>

          {/* Tab 0: Friend Requests */}
          {tabValue === 0 && (
            <Box>
              {friendRequests.length === 0 ? (
                <Card
                  elevation={0}
                  sx={(t) => ({
                    borderRadius: 4,
                    p: 6,
                    textAlign: "center",
                    boxShadow: t.shadows[1],
                    border: "1px solid",
                    borderColor: "divider",
                    bgcolor: "background.paper",
                  })}
                >
                  <PersonIcon sx={{ fontSize: 64, color: "text.disabled", mb: 2 }} />
                  <Typography variant="h6" color="text.secondary">
                    Không có lời mời kết bạn nào
                  </Typography>
                </Card>
              ) : (
                <Grid container spacing={2.5}>
                  {friendRequests.map((request) => (
                    <Grid item xs={12} sm={6} md={4} key={request.id}>
                      <Card
                        elevation={0}
                        sx={(t) => ({
                          borderRadius: 4,
                          p: 2.5,
                          boxShadow: t.shadows[1],
                          border: "1px solid",
                          borderColor: "divider",
                          bgcolor: "background.paper",
                          transition: "all 0.3s ease",
                          "&:hover": {
                            boxShadow: t.shadows[4],
                            transform: "translateY(-4px)",
                          },
                        })}
                      >
                        <Box sx={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
                          <Avatar
                            src={request.avatar}
                            sx={{
                              width: 96,
                              height: 96,
                              mb: 2,
                              border: "3px solid",
                              borderColor: "divider",
                            }}
                          />
                          <Typography variant="h6" fontWeight={700} mb={0.5} textAlign="center">
                            {request.name}
                          </Typography>
                          <Typography variant="body2" color="text.secondary" mb={1}>
                            {request.mutualFriends} bạn chung
                          </Typography>
                          <Typography variant="caption" color="text.disabled" mb={2}>
                            {request.time}
                          </Typography>

                          <Stack direction="row" spacing={1} width="100%">
                            <Button
                              fullWidth
                              variant="contained"
                              startIcon={<CheckIcon />}
                              onClick={() => handleAcceptRequest(request.id)}
                              sx={{
                                textTransform: "none",
                                fontWeight: 600,
                                borderRadius: 2.5,
                                background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                                "&:hover": {
                                  background: "linear-gradient(135deg, #5568d3 0%, #63428a 100%)",
                                },
                              }}
                            >
                              Xác nhận
                            </Button>
                            <Button
                              fullWidth
                              variant="outlined"
                              onClick={() => handleDeclineRequest(request.id)}
                              sx={{
                                textTransform: "none",
                                fontWeight: 600,
                                borderRadius: 2.5,
                                borderColor: "divider",
                                color: "text.secondary",
                                "&:hover": {
                                  borderColor: "error.main",
                                  color: "error.main",
                                  bgcolor: "rgba(211, 47, 47, 0.04)",
                                },
                              }}
                            >
                              Xóa
                            </Button>
                          </Stack>
                        </Box>
                      </Card>
                    </Grid>
                  ))}
                </Grid>
              )}
            </Box>
          )}

          {/* Tab 1: Friend Suggestions */}
          {tabValue === 1 && (
            <Box>
              <Grid container spacing={2.5}>
                {suggestions.map((suggestion) => (
                  <Grid item xs={12} sm={6} md={4} key={suggestion.id}>
                    <Card
                      elevation={0}
                      sx={(t) => ({
                        borderRadius: 4,
                        p: 2.5,
                        boxShadow: t.shadows[1],
                        border: "1px solid",
                        borderColor: "divider",
                        bgcolor: "background.paper",
                        position: "relative",
                        transition: "all 0.3s ease",
                        "&:hover": {
                          boxShadow: t.shadows[4],
                          transform: "translateY(-4px)",
                        },
                      })}
                    >
                      <IconButton
                        size="small"
                        onClick={() => handleRemoveSuggestion(suggestion.id)}
                        sx={{
                          position: "absolute",
                          top: 12,
                          right: 12,
                          bgcolor: "background.default",
                          "&:hover": { bgcolor: "action.hover" },
                        }}
                      >
                        <CloseIcon fontSize="small" />
                      </IconButton>

                      <Box sx={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
                        <Avatar
                          src={suggestion.avatar}
                          sx={{
                            width: 96,
                            height: 96,
                            mb: 2,
                            border: "3px solid",
                            borderColor: "divider",
                          }}
                        />
                        <Typography variant="h6" fontWeight={700} mb={0.5} textAlign="center">
                          {suggestion.name}
                        </Typography>
                        <Typography variant="body2" color="text.secondary" mb={1}>
                          {suggestion.mutualFriends} bạn chung
                        </Typography>
                        <Typography
                          variant="caption"
                          color="text.disabled"
                          mb={2}
                          textAlign="center"
                          sx={{ minHeight: 32 }}
                        >
                          {suggestion.reason}
                        </Typography>

                        <Button
                          fullWidth
                          variant="contained"
                          startIcon={<PersonAddIcon />}
                          onClick={() => handleAddFriend(suggestion.id)}
                          sx={{
                            textTransform: "none",
                            fontWeight: 600,
                            borderRadius: 2.5,
                            background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                            "&:hover": {
                              background: "linear-gradient(135deg, #5568d3 0%, #63428a 100%)",
                            },
                          }}
                        >
                          Thêm bạn bè
                        </Button>
                      </Box>
                    </Card>
                  </Grid>
                ))}
              </Grid>
            </Box>
          )}

          {/* Tab 2: All Friends */}
          {tabValue === 2 && (
            <Box>
              <Card
                elevation={0}
                sx={(t) => ({
                  borderRadius: 4,
                  p: 2.5,
                  mb: 3,
                  boxShadow: t.shadows[1],
                  border: "1px solid",
                  borderColor: "divider",
                  bgcolor: "background.paper",
                })}
              >
                <TextField
                  fullWidth
                  placeholder="Tìm kiếm bạn bè..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <SearchIcon color="action" />
                      </InputAdornment>
                    ),
                  }}
                  sx={{
                    "& .MuiOutlinedInput-root": {
                      borderRadius: 3,
                      bgcolor: (t) =>
                        t.palette.mode === "dark" ? "rgba(255,255,255,0.04)" : "background.default",
                      "& fieldset": { borderColor: "divider" },
                      "&:hover fieldset": { borderColor: "primary.main" },
                      "&.Mui-focused fieldset": { borderColor: "primary.main", borderWidth: 2 },
                    },
                  }}
                />
              </Card>

              <Grid container spacing={2.5}>
                {filteredFriends.map((friend) => (
                  <Grid item xs={12} sm={6} md={4} key={friend.id}>
                    <Card
                      elevation={0}
                      sx={(t) => ({
                        borderRadius: 4,
                        p: 2.5,
                        boxShadow: t.shadows[1],
                        border: "1px solid",
                        borderColor: "divider",
                        bgcolor: "background.paper",
                        transition: "all 0.3s ease",
                        "&:hover": {
                          boxShadow: t.shadows[4],
                          transform: "translateY(-4px)",
                        },
                      })}
                    >
                      <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
                        <Avatar
                          src={friend.avatar}
                          sx={{
                            width: 64,
                            height: 64,
                            mr: 2,
                            border: "2px solid",
                            borderColor: "divider",
                          }}
                        />
                        <Box sx={{ flex: 1 }}>
                          <Typography variant="h6" fontWeight={700} mb={0.5}>
                            {friend.name}
                          </Typography>
                          <Typography variant="body2" color="text.secondary" mb={0.3}>
                            {friend.mutualFriends} bạn chung
                          </Typography>
                          <Typography variant="caption" color="text.disabled">
                            {friend.since}
                          </Typography>
                        </Box>
                      </Box>

                      <Divider sx={{ my: 2 }} />

                      <Stack direction="row" spacing={1}>
                        <Button
                          fullWidth
                          variant="outlined"
                          sx={{
                            textTransform: "none",
                            fontWeight: 600,
                            borderRadius: 2.5,
                            borderColor: "divider",
                            color: "text.secondary",
                            "&:hover": {
                              borderColor: "primary.main",
                              color: "primary.main",
                              bgcolor: "rgba(102, 126, 234, 0.04)",
                            },
                          }}
                        >
                          Nhắn tin
                        </Button>
                        <Button
                          fullWidth
                          variant="outlined"
                          onClick={() => handleUnfriend(friend.id)}
                          sx={{
                            textTransform: "none",
                            fontWeight: 600,
                            borderRadius: 2.5,
                            borderColor: "divider",
                            color: "text.secondary",
                            "&:hover": {
                              borderColor: "error.main",
                              color: "error.main",
                              bgcolor: "rgba(211, 47, 47, 0.04)",
                            },
                          }}
                        >
                          Hủy kết bạn
                        </Button>
                      </Stack>
                    </Card>
                  </Grid>
                ))}
              </Grid>

              {filteredFriends.length === 0 && (
                <Card
                  elevation={0}
                  sx={(t) => ({
                    borderRadius: 4,
                    p: 6,
                    textAlign: "center",
                    boxShadow: t.shadows[1],
                    border: "1px solid",
                    borderColor: "divider",
                    bgcolor: "background.paper",
                  })}
                >
                  <SearchIcon sx={{ fontSize: 64, color: "text.disabled", mb: 2 }} />
                  <Typography variant="h6" color="text.secondary">
                    Không tìm thấy bạn bè nào
                  </Typography>
                </Card>
              )}
            </Box>
          )}
        </Box>
      </Box>

      {/* Snackbar */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={handleCloseSnackbar}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
        sx={{ mt: "64px" }}
      >
        <Alert
          onClose={handleCloseSnackbar}
          severity={snackbar.severity}
          variant="filled"
          sx={{ width: "100%", borderRadius: 3, boxShadow: 3, fontWeight: 500 }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Scene>
  );
}