// src/pages/Groups.jsx
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
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Badge,
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import AddIcon from "@mui/icons-material/Add";
import GroupsIcon from "@mui/icons-material/Groups";
import PublicIcon from "@mui/icons-material/Public";
import LockIcon from "@mui/icons-material/Lock";
import PeopleIcon from "@mui/icons-material/People";
import NotificationsActiveIcon from "@mui/icons-material/NotificationsActive";
import ExitToAppIcon from "@mui/icons-material/ExitToApp";
import SettingsIcon from "@mui/icons-material/Settings";
import TrendingUpIcon from "@mui/icons-material/TrendingUp";
import Scene from "./Scene";

// Dữ liệu giả
const mockMyGroups = [
  {
    id: 1,
    name: "Lập trình viên Việt Nam",
    avatar: "https://i.pravatar.cc/150?img=20",
    members: 12500,
    privacy: "public",
    unreadPosts: 5,
    lastActivity: "2 phút trước",
    description: "Cộng đồng lập trình viên Việt Nam",
  },
  {
    id: 2,
    name: "Du lịch Hà Nội",
    avatar: "https://i.pravatar.cc/150?img=21",
    members: 8200,
    privacy: "public",
    unreadPosts: 0,
    lastActivity: "1 giờ trước",
    description: "Chia sẻ địa điểm du lịch tại Hà Nội",
  },
  {
    id: 3,
    name: "Ẩm thực Việt",
    avatar: "https://i.pravatar.cc/150?img=22",
    members: 15000,
    privacy: "private",
    unreadPosts: 12,
    lastActivity: "30 phút trước",
    description: "Khám phá ẩm thực Việt Nam",
  },
  {
    id: 4,
    name: "Nhiếp ảnh chuyên nghiệp",
    avatar: "https://i.pravatar.cc/150?img=23",
    members: 5600,
    privacy: "private",
    unreadPosts: 3,
    lastActivity: "5 giờ trước",
    description: "Cộng đồng nhiếp ảnh gia",
  },
];

const mockSuggestedGroups = [
  {
    id: 5,
    name: "React Developers Vietnam",
    avatar: "https://i.pravatar.cc/150?img=24",
    members: 8900,
    privacy: "public",
    mutualMembers: 45,
    description: "Cộng đồng React developers tại Việt Nam",
    category: "Công nghệ",
  },
  {
    id: 6,
    name: "Khởi nghiệp Startup",
    avatar: "https://i.pravatar.cc/150?img=25",
    members: 20000,
    privacy: "public",
    mutualMembers: 28,
    description: "Hỗ trợ các startup Việt Nam",
    category: "Kinh doanh",
  },
  {
    id: 7,
    name: "Yoga & Fitness",
    avatar: "https://i.pravatar.cc/150?img=26",
    members: 12300,
    privacy: "public",
    mutualMembers: 15,
    description: "Cộng đồng yêu thích yoga và thể dục",
    category: "Sức khỏe",
  },
  {
    id: 8,
    name: "Đầu tư chứng khoán",
    avatar: "https://i.pravatar.cc/150?img=27",
    members: 18500,
    privacy: "private",
    mutualMembers: 32,
    description: "Chia sẻ kinh nghiệm đầu tư",
    category: "Tài chính",
  },
  {
    id: 9,
    name: "Game thủ Việt Nam",
    avatar: "https://i.pravatar.cc/150?img=28",
    members: 25000,
    privacy: "public",
    mutualMembers: 50,
    description: "Cộng đồng game thủ lớn nhất VN",
    category: "Giải trí",
  },
  {
    id: 10,
    name: "Học tiếng Anh giao tiếp",
    avatar: "https://i.pravatar.cc/150?img=29",
    members: 16700,
    privacy: "public",
    mutualMembers: 22,
    description: "Luyện tập tiếng Anh mỗi ngày",
    category: "Giáo dục",
  },
];

const mockDiscoverGroups = [
  {
    id: 11,
    name: "Marketing Digital",
    avatar: "https://i.pravatar.cc/150?img=30",
    members: 14200,
    privacy: "public",
    trending: true,
    growth: "+1.2k tuần này",
    description: "Kiến thức marketing số",
    category: "Marketing",
  },
  {
    id: 12,
    name: "Thiết kế UI/UX",
    avatar: "https://i.pravatar.cc/150?img=31",
    members: 9800,
    privacy: "public",
    trending: true,
    growth: "+850 tuần này",
    description: "Cộng đồng designer chuyên nghiệp",
    category: "Thiết kế",
  },
  {
    id: 13,
    name: "Nuôi dạy con",
    avatar: "https://i.pravatar.cc/150?img=32",
    members: 22000,
    privacy: "private",
    trending: false,
    growth: "+500 tuần này",
    description: "Chia sẻ kinh nghiệm làm cha mẹ",
    category: "Gia đình",
  },
  {
    id: 14,
    name: "Xe hơi Việt Nam",
    avatar: "https://i.pravatar.cc/150?img=33",
    members: 11500,
    privacy: "public",
    trending: true,
    growth: "+720 tuần này",
    description: "Cộng đồng yêu xe",
    category: "Xe cộ",
  },
];

export default function Groups() {
  const [tabValue, setTabValue] = useState(0);
  const [searchQuery, setSearchQuery] = useState("");
  const [myGroups, setMyGroups] = useState(mockMyGroups);
  const [suggestedGroups, setSuggestedGroups] = useState(mockSuggestedGroups);
  const [discoverGroups] = useState(mockDiscoverGroups);
  const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [newGroup, setNewGroup] = useState({
    name: "",
    description: "",
    privacy: "public",
    category: "",
  });

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
    setSearchQuery("");
  };

  const handleJoinGroup = (id) => {
    const group = [...suggestedGroups, ...discoverGroups].find((g) => g.id === id);
    if (group) {
      setMyGroups((prev) => [...prev, { ...group, unreadPosts: 0, lastActivity: "Vừa xong" }]);
      setSuggestedGroups((prev) => prev.filter((g) => g.id !== id));
      setSnackbar({ open: true, message: `Đã tham gia nhóm "${group.name}"!`, severity: "success" });
    }
  };

  const handleLeaveGroup = (id) => {
    const group = myGroups.find((g) => g.id === id);
    if (group) {
      setMyGroups((prev) => prev.filter((g) => g.id !== id));
      setSnackbar({ open: true, message: `Đã rời khỏi nhóm "${group.name}"!`, severity: "info" });
    }
  };

  const handleRemoveSuggestion = (id) => {
    setSuggestedGroups((prev) => prev.filter((g) => g.id !== id));
    setSnackbar({ open: true, message: "Đã xóa gợi ý này!", severity: "info" });
  };

  const handleCreateGroup = () => {
    if (!newGroup.name.trim()) {
      setSnackbar({ open: true, message: "Vui lòng nhập tên nhóm!", severity: "error" });
      return;
    }
    
    const group = {
      id: Date.now(),
      name: newGroup.name,
      avatar: "https://i.pravatar.cc/150?img=" + Math.floor(Math.random() * 50),
      members: 1,
      privacy: newGroup.privacy,
      unreadPosts: 0,
      lastActivity: "Vừa tạo",
      description: newGroup.description,
    };

    setMyGroups((prev) => [group, ...prev]);
    setCreateDialogOpen(false);
    setNewGroup({ name: "", description: "", privacy: "public", category: "" });
    setSnackbar({ open: true, message: "Đã tạo nhóm mới thành công!", severity: "success" });
  };

  const handleCloseSnackbar = () => {
    setSnackbar({ ...snackbar, open: false });
  };

  const filteredMyGroups = myGroups.filter((group) =>
    group.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <Scene>
      <Box
        sx={{
          width: "100%",
          height: "100%",
          overflow: "auto",
          display: "flex",
          justifyContent: "center",
        }}
      >
        <Box sx={{ width: "100%", maxWidth: 1200, py: 2 }}>
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
              <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                <Typography
                  sx={{
                    fontSize: 26,
                    fontWeight: 700,
                    background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                    WebkitBackgroundClip: "text",
                    WebkitTextFillColor: "transparent",
                  }}
                >
                  Nhóm
                </Typography>
                <Chip
                  icon={<GroupsIcon />}
                  label={`${myGroups.length} nhóm`}
                  sx={{
                    background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                    color: "white",
                    fontWeight: 600,
                  }}
                />
              </Box>

              <Button
                variant="contained"
                startIcon={<AddIcon />}
                onClick={() => setCreateDialogOpen(true)}
                sx={{
                  textTransform: "none",
                  fontWeight: 600,
                  borderRadius: 3,
                  px: 3,
                  background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                  "&:hover": {
                    background: "linear-gradient(135deg, #5568d3 0%, #63428a 100%)",
                  },
                }}
              >
                Tạo nhóm mới
              </Button>
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
              <Tab label="Nhóm của bạn" />
              <Tab label="Gợi ý" />
              <Tab label="Khám phá" />
            </Tabs>
          </Card>

          {/* Tab 0: My Groups */}
          {tabValue === 0 && (
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
                  placeholder="Tìm kiếm nhóm của bạn..."
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

              {filteredMyGroups.length === 0 ? (
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
                  <GroupsIcon sx={{ fontSize: 64, color: "text.disabled", mb: 2 }} />
                  <Typography variant="h6" color="text.secondary" mb={2}>
                    Không tìm thấy nhóm nào
                  </Typography>
                  <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={() => setCreateDialogOpen(true)}
                    sx={{
                      textTransform: "none",
                      fontWeight: 600,
                      borderRadius: 3,
                      background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                      "&:hover": {
                        background: "linear-gradient(135deg, #5568d3 0%, #63428a 100%)",
                      },
                    }}
                  >
                    Tạo nhóm mới
                  </Button>
                </Card>
              ) : (
                <Grid container spacing={2.5}>
                  {filteredMyGroups.map((group) => (
                    <Grid item xs={12} md={6} key={group.id}>
                      <Card
                        elevation={0}
                        sx={(t) => ({
                          borderRadius: 4,
                          p: 3,
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
                        <Box sx={{ display: "flex", alignItems: "flex-start", mb: 2 }}>
                          <Badge
                            badgeContent={group.unreadPosts}
                            color="error"
                            max={99}
                            sx={{
                              "& .MuiBadge-badge": {
                                top: 8,
                                right: 8,
                              },
                            }}
                          >
                            <Avatar
                              src={group.avatar}
                              sx={{
                                width: 72,
                                height: 72,
                                mr: 2,
                                border: "3px solid",
                                borderColor: "divider",
                              }}
                            />
                          </Badge>
                          <Box sx={{ flex: 1 }}>
                            <Typography variant="h6" fontWeight={700} mb={0.5}>
                              {group.name}
                            </Typography>
                            <Stack direction="row" spacing={1} alignItems="center" mb={1}>
                              <Chip
                                icon={group.privacy === "public" ? <PublicIcon /> : <LockIcon />}
                                label={group.privacy === "public" ? "Công khai" : "Riêng tư"}
                                size="small"
                                sx={{ height: 24, fontSize: 12 }}
                              />
                              <Chip
                                icon={<PeopleIcon />}
                                label={`${group.members.toLocaleString()} thành viên`}
                                size="small"
                                sx={{ height: 24, fontSize: 12 }}
                              />
                            </Stack>
                            <Typography variant="caption" color="text.secondary">
                              Hoạt động: {group.lastActivity}
                            </Typography>
                          </Box>
                          <IconButton size="small">
                            <SettingsIcon />
                          </IconButton>
                        </Box>

                        <Typography variant="body2" color="text.secondary" mb={2}>
                          {group.description}
                        </Typography>

                        <Divider sx={{ my: 2 }} />

                        <Stack direction="row" spacing={1}>
                          <Button
                            fullWidth
                            variant="outlined"
                            startIcon={<NotificationsActiveIcon />}
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
                            Xem nhóm
                          </Button>
                          <Button
                            fullWidth
                            variant="outlined"
                            startIcon={<ExitToAppIcon />}
                            onClick={() => handleLeaveGroup(group.id)}
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
                            Rời nhóm
                          </Button>
                        </Stack>
                      </Card>
                    </Grid>
                  ))}
                </Grid>
              )}
            </Box>
          )}

          {/* Tab 1: Suggested Groups */}
          {tabValue === 1 && (
            <Box>
              <Grid container spacing={2.5}>
                {suggestedGroups.map((group) => (
                  <Grid item xs={12} sm={6} md={4} key={group.id}>
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
                          src={group.avatar}
                          sx={{
                            width: 96,
                            height: 96,
                            mb: 2,
                            border: "3px solid",
                            borderColor: "divider",
                          }}
                        />
                        <Typography variant="h6" fontWeight={700} mb={0.5} textAlign="center">
                          {group.name}
                        </Typography>
                        <Chip label={group.category} size="small" sx={{ mb: 1 }} />
                        <Stack direction="row" spacing={1} mb={1}>
                          <Chip
                            icon={group.privacy === "public" ? <PublicIcon /> : <LockIcon />}
                            label={group.privacy === "public" ? "Công khai" : "Riêng tư"}
                            size="small"
                            sx={{ height: 22, fontSize: 11 }}
                          />
                          <Chip
                            icon={<PeopleIcon />}
                            label={group.members.toLocaleString()}
                            size="small"
                            sx={{ height: 22, fontSize: 11 }}
                          />
                        </Stack>
                        <Typography variant="caption" color="text.secondary" mb={1}>
                          {group.mutualMembers} bạn chung
                        </Typography>
                        <Typography
                          variant="body2"
                          color="text.secondary"
                          textAlign="center"
                          mb={2}
                          sx={{ minHeight: 40 }}
                        >
                          {group.description}
                        </Typography>

                        <Stack direction="row" spacing={1} width="100%">
                          <Button
                            fullWidth
                            variant="contained"
                            onClick={() => handleJoinGroup(group.id)}
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
                            Tham gia
                          </Button>
                          <IconButton
                            onClick={() => handleRemoveSuggestion(group.id)}
                            sx={{
                              borderRadius: 2.5,
                              border: "1px solid",
                              borderColor: "divider",
                              "&:hover": { bgcolor: "action.hover" },
                            }}
                          >
                            <ExitToAppIcon />
                          </IconButton>
                        </Stack>
                      </Box>
                    </Card>
                  </Grid>
                ))}
              </Grid>
            </Box>
          )}

          {/* Tab 2: Discover Groups */}
          {tabValue === 2 && (
            <Box>
              <Grid container spacing={2.5}>
                {discoverGroups.map((group) => (
                  <Grid item xs={12} sm={6} md={4} key={group.id}>
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
                      {group.trending && (
                        <Chip
                          icon={<TrendingUpIcon />}
                          label="Đang thịnh hành"
                          size="small"
                          color="error"
                          sx={{
                            position: "absolute",
                            top: 12,
                            right: 12,
                            fontWeight: 600,
                            fontSize: 11,
                          }}
                        />
                      )}

                      <Box sx={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
                        <Avatar
                          src={group.avatar}
                          sx={{
                            width: 96,
                            height: 96,
                            mb: 2,
                            border: "3px solid",
                            borderColor: "divider",
                          }}
                        />
                        <Typography variant="h6" fontWeight={700} mb={0.5} textAlign="center">
                          {group.name}
                        </Typography>
                        <Chip label={group.category} size="small" sx={{ mb: 1 }} />
                        <Stack direction="row" spacing={1} mb={1}>
                          <Chip
                            icon={group.privacy === "public" ? <PublicIcon /> : <LockIcon />}
                            label={group.privacy === "public" ? "Công khai" : "Riêng tư"}
                            size="small"
                            sx={{ height: 22, fontSize: 11 }}
                          />
                          <Chip
                            icon={<PeopleIcon />}
                            label={group.members.toLocaleString()}
                            size="small"
                            sx={{ height: 22, fontSize: 11 }}
                          />
                        </Stack>
                        <Chip
                          icon={<TrendingUpIcon />}
                          label={group.growth}
                          size="small"
                          color="success"
                          sx={{ mb: 1, fontSize: 11 }}
                        />
                        <Typography
                          variant="body2"
                          color="text.secondary"
                          textAlign="center"
                          mb={2}
                          sx={{ minHeight: 40 }}
                        >
                          {group.description}
                        </Typography>

                        <Button
                          fullWidth
                          variant="contained"
                          onClick={() => handleJoinGroup(group.id)}
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
                          Tham gia nhóm
                        </Button>
                      </Box>
                    </Card>
                  </Grid>
                ))}
              </Grid>
            </Box>
          )}
        </Box>
      </Box>

      {/* Create Group Dialog */}
      <Dialog
        open={createDialogOpen}
        onClose={() => setCreateDialogOpen(false)}
        maxWidth="sm"
        fullWidth
        PaperProps={{
          sx: { borderRadius: 4 },
        }}
      >
        <DialogTitle sx={{ fontWeight: 700, pb: 1 }}>Tạo nhóm mới</DialogTitle>
        <DialogContent>
          <Stack spacing={3} sx={{ mt: 2 }}>
            <TextField
              fullWidth
              label="Tên nhóm"
              placeholder="Nhập tên nhóm..."
              value={newGroup.name}
              onChange={(e) => setNewGroup({ ...newGroup, name: e.target.value })}
              sx={{
                "& .MuiOutlinedInput-root": { borderRadius: 3 },
              }}
            />
            <TextField
              fullWidth
              label="Mô tả"
              placeholder="Mô tả về nhóm..."
              multiline
              rows={3}
              value={newGroup.description}
              onChange={(e) => setNewGroup({ ...newGroup, description: e.target.value })}
              sx={{
                "& .MuiOutlinedInput-root": { borderRadius: 3 },
              }}
            />
            <FormControl fullWidth>
              <InputLabel>Quyền riêng tư</InputLabel>
              <Select
                value={newGroup.privacy}
                onChange={(e) => setNewGroup({ ...newGroup, privacy: e.target.value })}
                label="Quyền riêng tư"
                sx={{ borderRadius: 3 }}
              >
                <MenuItem value="public">
                  <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                    <PublicIcon fontSize="small" />
                    <Box>
                      <Typography variant="body2" fontWeight={600}>
                        Công khai
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        Bất kỳ ai cũng có thể tham gia
                      </Typography>
                    </Box>
                  </Box>
                </MenuItem>
                <MenuItem value="private">
                  <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                    <LockIcon fontSize="small" />
                    <Box>
                      <Typography variant="body2" fontWeight={600}>
                        Riêng tư
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        Cần phê duyệt để tham gia
                      </Typography>
                    </Box>
                  </Box>
                </MenuItem>
              </Select>
            </FormControl>
            <FormControl fullWidth>
              <InputLabel>Danh mục</InputLabel>
              <Select
                value={newGroup.category}
                onChange={(e) => setNewGroup({ ...newGroup, category: e.target.value })}
                label="Danh mục"
                sx={{ borderRadius: 3 }}
              >
                <MenuItem value="Công nghệ">Công nghệ</MenuItem>
                <MenuItem value="Kinh doanh">Kinh doanh</MenuItem>
                <MenuItem value="Giáo dục">Giáo dục</MenuItem>
                <MenuItem value="Giải trí">Giải trí</MenuItem>
                <MenuItem value="Thể thao">Thể thao</MenuItem>
                <MenuItem value="Nghệ thuật">Nghệ thuật</MenuItem>
                <MenuItem value="Du lịch">Du lịch</MenuItem>
                <MenuItem value="Ẩm thực">Ẩm thực</MenuItem>
                <MenuItem value="Khác">Khác</MenuItem>
              </Select>
            </FormControl>
          </Stack>
        </DialogContent>
        <DialogActions sx={{ p: 3, pt: 0 }}>
          <Button
            onClick={() => setCreateDialogOpen(false)}
            sx={{
              textTransform: "none",
              fontWeight: 600,
              borderRadius: 3,
              px: 3,
            }}
          >
            Hủy
          </Button>
          <Button
            onClick={handleCreateGroup}
            variant="contained"
            sx={{
              textTransform: "none",
              fontWeight: 600,
              borderRadius: 3,
              px: 3,
              background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
              "&:hover": {
                background: "linear-gradient(135deg, #5568d3 0%, #63428a 100%)",
              },
            }}
          >
            Tạo nhóm
          </Button>
        </DialogActions>
      </Dialog>

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