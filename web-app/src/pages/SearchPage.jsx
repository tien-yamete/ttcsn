// src/pages/SearchPage.jsx
import React, { useMemo, useState } from "react";
import {
  Box,
  Grid,
  Card,
  CardContent,
  Avatar,
  Typography,
  Button,
  Tabs,
  Tab,
  TextField,
  IconButton,
  Chip,
  Stack,
  Snackbar,
  Alert,
  Divider,
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import CheckIcon from "@mui/icons-material/Check";
import PeopleIcon from "@mui/icons-material/People";
import ArticleIcon from "@mui/icons-material/Article";
import GroupIcon from "@mui/icons-material/Group";
import ThumbUpIcon from "@mui/icons-material/ThumbUp";
import ChatBubbleOutlineIcon from "@mui/icons-material/ChatBubbleOutline";
import ShareIcon from "@mui/icons-material/Share";
import PlaceIcon from "@mui/icons-material/Place";
import CloseIcon from "@mui/icons-material/Close";

/**
 * SearchPage.jsx
 * - React + MUI implementation of the search UI
 * - Contains mockUsers, mockPosts, mockGroups (from your data)
 * - Tabs: Users / Posts / Groups
 * - Actions: Add Friend, Join Group (mock) with Snackbar
 */

const mockUsers = [
  { id: 1, name: "Nguyễn Văn An", avatar: "https://i.pravatar.cc/150?img=21", bio: "Software Engineer tại FPT Software", mutualFriends: 15, location: "Hà Nội", isFriend: false },
  { id: 2, name: "Trần Thị Bình", avatar: "https://i.pravatar.cc/150?img=22", bio: "Designer tại VNG Corporation", mutualFriends: 8, location: "TP. Hồ Chí Minh", isFriend: false },
  { id: 3, name: "Lê Minh Cường", avatar: "https://i.pravatar.cc/150?img=23", bio: "Marketing Manager", mutualFriends: 22, location: "Đà Nẵng", isFriend: true },
  { id: 4, name: "Phạm Thu Duyên", avatar: "https://i.pravatar.cc/150?img=24", bio: "Content Creator & Blogger", mutualFriends: 12, location: "Hà Nội", isFriend: false },
  { id: 5, name: "Hoàng Văn Em", avatar: "https://i.pravatar.cc/150?img=25", bio: "Data Analyst tại Viettel", mutualFriends: 18, location: "Hà Nội", isFriend: false },
  { id: 6, name: "Đỗ Thị Phượng", avatar: "https://i.pravatar.cc/150?img=26", bio: "Teacher tại THPT Chu Văn An", mutualFriends: 5, location: "Hải Phòng", isFriend: false },
];

const mockPosts = [
  { id: 1, author: "Nguyễn Minh Tuấn", avatar: "https://i.pravatar.cc/150?img=31", time: "2 giờ trước", content: "Hôm nay thời tiết đẹp quá! Ai rảnh đi cafe không? ☕️", image: "https://picsum.photos/600/400?random=1", likes: 124, comments: 28, shares: 5 },
  { id: 2, author: "Trần Hương Giang", avatar: "https://i.pravatar.cc/150?img=32", time: "5 giờ trước", content: "Chia sẻ một vài tips học lập trình hiệu quả cho những bạn mới bắt đầu 💻", likes: 256, comments: 45, shares: 12 },
  { id: 3, author: "Lê Đức Anh", avatar: "https://i.pravatar.cc/150?img=33", time: "1 ngày trước", content: "Sunset tại Đà Lạt đẹp không thể tin được! 🌅", image: "https://picsum.photos/600/400?random=2", likes: 489, comments: 67, shares: 23 },
  { id: 4, author: "Phạm Thảo Linh", avatar: "https://i.pravatar.cc/150?img=34", time: "2 ngày trước", content: "Công thức làm bánh bông lan siêu ngon mà đơn giản! 🍰", likes: 178, comments: 34, shares: 8 },
];

const mockGroups = [
  { id: 1, name: "JavaScript Developers Vietnam", avatar: "https://picsum.photos/150?random=11", members: 15420, postsPerDay: 25, description: "Cộng đồng lập trình JavaScript tại Việt Nam", isJoined: false },
  { id: 2, name: "Du lịch Việt Nam", avatar: "https://picsum.photos/150?random=12", members: 8932, postsPerDay: 45, description: "Chia sẻ kinh nghiệm du lịch khắp Việt Nam", isJoined: true },
  { id: 3, name: "Startup Việt Nam", avatar: "https://picsum.photos/150?random=13", members: 12567, postsPerDay: 18, description: "Kết nối và chia sẻ kinh nghiệm khởi nghiệp", isJoined: false },
  { id: 4, name: "Ẩm thực Hà Nội", avatar: "https://picsum.photos/150?random=14", members: 23456, postsPerDay: 67, description: "Review các quán ăn ngon tại Hà Nội", isJoined: false },
  { id: 5, name: "Designer Vietnam Community", avatar: "https://picsum.photos/150?random=15", members: 9876, postsPerDay: 22, description: "Cộng đồng thiết kế đồ họa Việt Nam", isJoined: false },
  { id: 6, name: "Mua bán đồ công nghệ", avatar: "https://picsum.photos/150?random=16", members: 34567, postsPerDay: 89, description: "Nhóm mua bán trao đổi đồ công nghệ", isJoined: true },
];

export default function SearchPage() {
  const [tabIndex, setTabIndex] = useState(0);
  const [query, setQuery] = useState("");
  const [users, setUsers] = useState(mockUsers);
  const [groups, setGroups] = useState(mockGroups);
  const [snackbar, setSnackbar] = useState({ open: false, severity: "success", message: "" });

  // Derived / filtered lists
  const filteredUsers = useMemo(() => {
    const q = query.trim().toLowerCase();
    if (!q) return users;
    return users.filter((u) => u.name.toLowerCase().includes(q) || u.bio.toLowerCase().includes(q) || u.location.toLowerCase().includes(q));
  }, [users, query]);

  const filteredPosts = useMemo(() => {
    const q = query.trim().toLowerCase();
    if (!q) return mockPosts;
    return mockPosts.filter((p) => p.content.toLowerCase().includes(q) || p.author.toLowerCase().includes(q));
  }, [query]);

  const filteredGroups = useMemo(() => {
    const q = query.trim().toLowerCase();
    if (!q) return groups;
    return groups.filter((g) => g.name.toLowerCase().includes(q) || g.description.toLowerCase().includes(q));
  }, [groups, query]);

  const totalResults = filteredUsers.length + filteredPosts.length + filteredGroups.length;

  const handleAddFriend = (id) => {
    setUsers((prev) => prev.map((u) => (u.id === id ? { ...u, isFriend: true } : u)));
    setSnackbar({ open: true, severity: "success", message: "Đã gửi lời mời kết bạn!" });
  };

  const handleJoinGroup = (id) => {
    setGroups((prev) => prev.map((g) => (g.id === id ? { ...g, isJoined: true } : g)));
    setSnackbar({ open: true, severity: "success", message: "Đã tham gia nhóm!" });
  };

  const handleCloseSnackbar = (event, reason) => {
    if (reason === "clickaway") return;
    setSnackbar((s) => ({ ...s, open: false }));
  };

  return (
    <Box sx={{ minHeight: "100vh", bgcolor: "background.default", py: 6 }}>
      <Box sx={{ maxWidth: 1200, mx: "auto", px: 2 }}>
        {/* Header */}
        <Card variant="outlined" sx={{ borderRadius: 3, mb: 4 }}>
          <CardContent>
            <Box sx={{ display: "flex", alignItems: "center", justifyContent: "space-between", mb: 2 }}>
              <Typography variant="h4" sx={{ fontWeight: 700, background: "linear-gradient(90deg,#7c3aed,#4f46e5)", WebkitBackgroundClip: "text", WebkitTextFillColor: "transparent" }}>
                Kết quả tìm kiếm
              </Typography>

              <Box sx={{ display: "flex", alignItems: "center", gap: 1, bgcolor: "primary.main", color: "primary.contrastText", px: 2, py: 0.5, borderRadius: 4 }}>
                <SearchIcon fontSize="small" />
                <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>{totalResults} kết quả</Typography>
              </Box>
            </Box>

            <Box sx={{ display: "flex", gap: 2, alignItems: "center", mt: 1 }}>
              <TextField
                fullWidth
                size="medium"
                placeholder="Tìm kiếm người dùng, bài viết, nhóm..."
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                InputProps={{
                  startAdornment: (
                    <Box sx={{ display: "flex", alignItems: "center", pl: 1 }}>
                      <SearchIcon color="action" />
                    </Box>
                  ),
                }}
                sx={{ bgcolor: "background.paper", borderRadius: 2 }}
                aria-label="search"
              />
            </Box>
          </CardContent>
        </Card>

        {/* Tabs */}
        <Card variant="outlined" sx={{ borderRadius: 3, p: 2 }}>
          <Tabs value={tabIndex} onChange={(e, v) => setTabIndex(v)} aria-label="search tabs" sx={{ mb: 2 }}>
            <Tab label={`Mọi người (${filteredUsers.length})`} icon={<PeopleIcon />} iconPosition="start" />
            <Tab label={`Bài viết (${filteredPosts.length})`} icon={<ArticleIcon />} iconPosition="start" />
            <Tab label={`Nhóm (${filteredGroups.length})`} icon={<GroupIcon />} iconPosition="start" />
          </Tabs>

          <Divider sx={{ mb: 2 }} />

          {/* Tab panels */}
          {tabIndex === 0 && (
            <Box>
              {filteredUsers.length === 0 ? (
                <Box sx={{ textAlign: "center", py: 6 }}>
                  <PeopleIcon sx={{ fontSize: 64, color: "text.secondary", mb: 1 }} />
                  <Typography variant="h6" color="text.secondary">Không tìm thấy người dùng nào</Typography>
                </Box>
              ) : (
                <Grid container spacing={3}>
                  {filteredUsers.map((u) => (
                    <Grid item xs={12} sm={6} md={4} key={u.id}>
                      <Card variant="outlined" sx={{ borderRadius: 3, p: 2, height: "100%" }}>
                        <Box sx={{ display: "flex", flexDirection: "column", alignItems: "center", textAlign: "center" }}>
                          <Avatar src={u.avatar} alt={u.name} sx={{ width: 96, height: 96, mb: 1 }} />
                          <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>{u.name}</Typography>
                          <Typography variant="body2" color="text.secondary" sx={{ mb: 1, minHeight: 40 }}>{u.bio}</Typography>
                          <Box sx={{ display: "flex", alignItems: "center", gap: 1, bgcolor: "grey.100", px: 1.5, py: 0.5, borderRadius: 2, mb: 1 }}>
                            <PlaceIcon fontSize="small" color="action" />
                            <Typography variant="body2" sx={{ fontWeight: 600 }}>{u.location}</Typography>
                          </Box>
                          <Typography variant="caption" color="text.secondary" sx={{ mb: 2 }}>{u.mutualFriends} bạn chung</Typography>

                          <Button
                            variant={u.isFriend ? "outlined" : "contained"}
                            color={u.isFriend ? "inherit" : "primary"}
                            startIcon={u.isFriend ? <CheckIcon /> : <PersonAddIcon />}
                            onClick={() => !u.isFriend && handleAddFriend(u.id)}
                            disabled={u.isFriend}
                            fullWidth
                            sx={{ textTransform: "none", borderRadius: 2 }}
                          >
                            {u.isFriend ? "Đã kết bạn" : "Kết bạn"}
                          </Button>
                        </Box>
                      </Card>
                    </Grid>
                  ))}
                </Grid>
              )}
            </Box>
          )}

          {tabIndex === 1 && (
            <Box>
              {filteredPosts.length === 0 ? (
                <Box sx={{ textAlign: "center", py: 6 }}>
                  <ArticleIcon sx={{ fontSize: 64, color: "text.secondary", mb: 1 }} />
                  <Typography variant="h6" color="text.secondary">Không tìm thấy bài viết nào</Typography>
                </Box>
              ) : (
                <Stack spacing={2}>
                  {filteredPosts.map((p) => (
                    <Card key={p.id} variant="outlined" sx={{ borderRadius: 3 }}>
                      <CardContent>
                        <Box sx={{ display: "flex", gap: 2, alignItems: "center", mb: 1 }}>
                          <Avatar src={p.avatar} alt={p.author} />
                          <Box>
                            <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>{p.author}</Typography>
                            <Typography variant="caption" color="text.secondary">{p.time}</Typography>
                          </Box>
                        </Box>

                        <Typography variant="body1" sx={{ mb: 1 }}>{p.content}</Typography>

                        {p.image && (
                          <Box sx={{ my: 1 }}>
                            <img src={p.image} alt="post" style={{ width: "100%", borderRadius: 12, maxHeight: 420, objectFit: "cover" }} />
                          </Box>
                        )}

                        <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mt: 2 }}>
                          <Typography variant="body2" color="text.secondary">{p.likes.toLocaleString()} lượt thích</Typography>
                          <Typography variant="body2" color="text.secondary">{p.comments} bình luận · {p.shares} chia sẻ</Typography>
                        </Box>

                        <Divider sx={{ my: 2 }} />

                        <Box sx={{ display: "flex", gap: 1 }}>
                          <Button startIcon={<ThumbUpIcon />} fullWidth variant="text" sx={{ textTransform: "none" }}>Thích</Button>
                          <Button startIcon={<ChatBubbleOutlineIcon />} fullWidth variant="text" sx={{ textTransform: "none" }}>Bình luận</Button>
                          <Button startIcon={<ShareIcon />} fullWidth variant="text" sx={{ textTransform: "none" }}>Chia sẻ</Button>
                        </Box>
                      </CardContent>
                    </Card>
                  ))}
                </Stack>
              )}
            </Box>
          )}

          {tabIndex === 2 && (
            <Box>
              {filteredGroups.length === 0 ? (
                <Box sx={{ textAlign: "center", py: 6 }}>
                  <GroupIcon sx={{ fontSize: 64, color: "text.secondary", mb: 1 }} />
                  <Typography variant="h6" color="text.secondary">Không tìm thấy nhóm nào</Typography>
                </Box>
              ) : (
                <Grid container spacing={3}>
                  {filteredGroups.map((g) => (
                    <Grid item xs={12} sm={6} key={g.id}>
                      <Card variant="outlined" sx={{ borderRadius: 3, p: 2 }}>
                        <Box sx={{ display: "flex", gap: 2 }}>
                          <Avatar src={g.avatar} variant="rounded" sx={{ width: 80, height: 80 }} />
                          <Box sx={{ flex: 1 }}>
                            <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>{g.name}</Typography>
                            <Stack direction="row" spacing={1} alignItems="center" sx={{ mt: 0.5 }}>
                              <Chip icon={<PeopleIcon />} label={`${g.members.toLocaleString()} thành viên`} size="small" />
                              <Chip label={`${g.postsPerDay} bài/ngày`} size="small" />
                            </Stack>
                            <Typography variant="body2" color="text.secondary" sx={{ mt: 1, minHeight: 40 }}>{g.description}</Typography>
                          </Box>
                        </Box>

                        <Box sx={{ mt: 2 }}>
                          <Button
                            variant={g.isJoined ? "outlined" : "contained"}
                            startIcon={g.isJoined ? <CheckIcon /> : <PeopleIcon />}
                            onClick={() => !g.isJoined && handleJoinGroup(g.id)}
                            disabled={g.isJoined}
                            sx={{ textTransform: "none", borderRadius: 2 }}
                            fullWidth
                          >
                            {g.isJoined ? "Đã tham gia" : "Tham gia nhóm"}
                          </Button>
                        </Box>
                      </Card>
                    </Grid>
                  ))}
                </Grid>
              )}
            </Box>
          )}
        </Card>
      </Box>

      {/* Snackbar */}
      <Snackbar open={snackbar.open} autoHideDuration={3500} onClose={handleCloseSnackbar} anchorOrigin={{ vertical: "top", horizontal: "right" }}>
        <Alert onClose={handleCloseSnackbar} severity={snackbar.severity} sx={{ width: "100%" }} iconMapping={{ success: <CheckIcon fontSize="inherit" /> }}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
}
