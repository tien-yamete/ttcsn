// src/pages/GroupDetail.jsx
import { useState, useRef } from "react";
import { useParams } from "react-router-dom";
import {
  Box,
  Card,
  Typography,
  Avatar,
  Button,
  Tabs,
  Tab,
  TextField,
  IconButton,
  Divider,
  Stack,
  Snackbar,
  Alert,
  AvatarGroup,
  Chip,
  Menu,
  MenuItem,
  ListItemIcon,
  ListItemText,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Grid,
} from "@mui/material";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import ImageIcon from "@mui/icons-material/Image";
import VideoLibraryIcon from "@mui/icons-material/VideoLibrary";
import CloseIcon from "@mui/icons-material/Close";
import ThumbUpIcon from "@mui/icons-material/ThumbUp";
import ChatBubbleOutlineIcon from "@mui/icons-material/ChatBubbleOutline";
import ShareIcon from "@mui/icons-material/Share";
import PublicIcon from "@mui/icons-material/Public";
import LockIcon from "@mui/icons-material/Lock";
import PeopleIcon from "@mui/icons-material/People";
import NotificationsIcon from "@mui/icons-material/Notifications";
import ExitToAppIcon from "@mui/icons-material/ExitToApp";
import SettingsIcon from "@mui/icons-material/Settings";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import ReportIcon from "@mui/icons-material/Report";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import Scene from "./Scene";

// Dữ liệu giả
const mockGroupData = {
  id: 1,
  name: "Lập trình viên Việt Nam",
  avatar: "https://i.pravatar.cc/300?img=20",
  cover: "https://picsum.photos/1200/400?random=1",
  description: "Cộng đồng lập trình viên Việt Nam - Nơi chia sẻ kiến thức, kinh nghiệm và kết nối các developer",
  privacy: "public",
  members: 12500,
  createdAt: "Tạo ngày 15/01/2023",
  category: "Công nghệ",
  isAdmin: true,
  isMember: true,
};

const mockMembers = [
  { id: 1, name: "Nguyễn Văn A", avatar: "https://i.pravatar.cc/150?img=1", role: "admin" },
  { id: 2, name: "Trần Thị B", avatar: "https://i.pravatar.cc/150?img=2", role: "moderator" },
  { id: 3, name: "Lê Minh C", avatar: "https://i.pravatar.cc/150?img=3", role: "member" },
  { id: 4, name: "Phạm Thu D", avatar: "https://i.pravatar.cc/150?img=4", role: "member" },
  { id: 5, name: "Hoàng Văn E", avatar: "https://i.pravatar.cc/150?img=5", role: "member" },
  { id: 6, name: "Đỗ Thị F", avatar: "https://i.pravatar.cc/150?img=6", role: "member" },
];

const mockPosts = [
  {
    id: 1,
    author: {
      id: 1,
      name: "Nguyễn Văn A",
      avatar: "https://i.pravatar.cc/150?img=1",
      role: "admin",
    },
    content: "Chào mừng các bạn đến với nhóm! Hãy chia sẻ những kiến thức và kinh nghiệm của mình nhé 🚀",
    media: [],
    likes: 125,
    comments: 23,
    shares: 5,
    timestamp: "2 giờ trước",
    isLiked: false,
  },
  {
    id: 2,
    author: {
      id: 2,
      name: "Trần Thị B",
      avatar: "https://i.pravatar.cc/150?img=2",
      role: "moderator",
    },
    content: "Mình vừa hoàn thành project React Native đầu tiên. Có ai muốn xem demo không? 📱",
    media: [
      { type: "image", url: "https://picsum.photos/600/400?random=2" },
    ],
    likes: 89,
    comments: 15,
    shares: 3,
    timestamp: "5 giờ trước",
    isLiked: true,
  },
  {
    id: 3,
    author: {
      id: 3,
      name: "Lê Minh C",
      avatar: "https://i.pravatar.cc/150?img=3",
      role: "member",
    },
    content: "Hỏi về best practices khi làm việc với React Hooks. Các bạn có thể chia sẻ kinh nghiệm không? 🤔",
    media: [],
    likes: 56,
    comments: 32,
    shares: 2,
    timestamp: "1 ngày trước",
    isLiked: false,
  },
];

export default function GroupDetail() {
  const { groupId } = useParams();
  const [tabValue, setTabValue] = useState(0);
  const [group] = useState(mockGroupData);
  const [posts, setPosts] = useState(mockPosts);
  const [members] = useState(mockMembers);
  const [newPostContent, setNewPostContent] = useState("");
  const [mediaFiles, setMediaFiles] = useState([]);
  const [mediaPreview, setMediaPreview] = useState([]);
  const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });
  const [anchorEl, setAnchorEl] = useState(null);
  const [selectedPost, setSelectedPost] = useState(null);
  const [inviteDialogOpen, setInviteDialogOpen] = useState(false);
  const fileInputRef = useRef(null);

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const handleMediaSelect = (e) => {
    const files = Array.from(e.target.files);
    if (files.length === 0) return;

    const validFiles = files.filter((file) => {
      const isImage = file.type.startsWith("image/");
      const isVideo = file.type.startsWith("video/");
      return isImage || isVideo;
    });

    const newPreviews = validFiles.map((file) => ({
      url: URL.createObjectURL(file),
      type: file.type.startsWith("image/") ? "image" : "video",
      name: file.name,
    }));

    setMediaFiles((prev) => [...prev, ...validFiles]);
    setMediaPreview((prev) => [...prev, ...newPreviews]);
  };

  const handleRemoveMedia = (index) => {
    URL.revokeObjectURL(mediaPreview[index].url);
    setMediaFiles((prev) => prev.filter((_, i) => i !== index));
    setMediaPreview((prev) => prev.filter((_, i) => i !== index));
  };

  const handleCreatePost = () => {
    if (!newPostContent.trim() && mediaFiles.length === 0) {
      setSnackbar({ open: true, message: "Vui lòng nhập nội dung hoặc thêm media!", severity: "error" });
      return;
    }

    const newPost = {
      id: Date.now(),
      author: {
        id: 100,
        name: "Bạn",
        avatar: "https://i.pravatar.cc/150?img=50",
        role: "member",
      },
      content: newPostContent,
      media: mediaPreview.map((m) => ({ type: m.type, url: m.url })),
      likes: 0,
      comments: 0,
      shares: 0,
      timestamp: "Vừa xong",
      isLiked: false,
    };

    setPosts((prev) => [newPost, ...prev]);
    setNewPostContent("");
    setMediaFiles([]);
    setMediaPreview([]);
    setSnackbar({ open: true, message: "Đã đăng bài viết!", severity: "success" });
  };

  const handleLikePost = (postId) => {
    setPosts((prev) =>
      prev.map((post) =>
        post.id === postId
          ? {
              ...post,
              isLiked: !post.isLiked,
              likes: post.isLiked ? post.likes - 1 : post.likes + 1,
            }
          : post
      )
    );
  };

  const handleMenuOpen = (event, post) => {
    setAnchorEl(event.currentTarget);
    setSelectedPost(post);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    setSelectedPost(null);
  };

  const handleDeletePost = () => {
    setPosts((prev) => prev.filter((post) => post.id !== selectedPost.id));
    setSnackbar({ open: true, message: "Đã xóa bài viết!", severity: "success" });
    handleMenuClose();
  };

  const handleLeaveGroup = () => {
    setSnackbar({ open: true, message: "Đã rời khỏi nhóm!", severity: "info" });
  };

  const handleCloseSnackbar = () => {
    setSnackbar({ ...snackbar, open: false });
  };

  const getRoleBadge = (role) => {
    if (role === "admin") return <Chip label="Admin" size="small" color="error" sx={{ height: 20, fontSize: 11 }} />;
    if (role === "moderator") return <Chip label="Mod" size="small" color="warning" sx={{ height: 20, fontSize: 11 }} />;
    return null;
  };

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
          {/* Cover & Header */}
          <Card
            elevation={0}
            sx={(t) => ({
              borderRadius: 4,
              mb: 3,
              boxShadow: t.shadows[1],
              border: "1px solid",
              borderColor: "divider",
              bgcolor: "background.paper",
              overflow: "hidden",
            })}
          >
            {/* Cover Image */}
            <Box
              sx={{
                width: "100%",
                height: 300,
                backgroundImage: `url(${group.cover})`,
                backgroundSize: "cover",
                backgroundPosition: "center",
                position: "relative",
              }}
            >
              <Box
                sx={{
                  position: "absolute",
                  bottom: 0,
                  left: 0,
                  right: 0,
                  background: "linear-gradient(transparent, rgba(0,0,0,0.7))",
                  p: 3,
                }}
              >
                <Box sx={{ display: "flex", alignItems: "flex-end" }}>
                  <Avatar
                    src={group.avatar}
                    sx={{
                      width: 120,
                      height: 120,
                      border: "4px solid white",
                      mr: 3,
                    }}
                  />
                  <Box sx={{ flex: 1, color: "white" }}>
                    <Typography variant="h4" fontWeight={700} mb={0.5}>
                      {group.name}
                    </Typography>
                    <Stack direction="row" spacing={2} alignItems="center">
                      <Chip
                        icon={group.privacy === "public" ? <PublicIcon /> : <LockIcon />}
                        label={group.privacy === "public" ? "Nhóm công khai" : "Nhóm riêng tư"}
                        size="small"
                        sx={{ bgcolor: "rgba(255,255,255,0.2)", color: "white" }}
                      />
                      <Typography variant="body2">
                        <PeopleIcon sx={{ fontSize: 16, mr: 0.5, verticalAlign: "middle" }} />
                        {group.members.toLocaleString()} thành viên
                      </Typography>
                    </Stack>
                  </Box>
                </Box>
              </Box>
            </Box>

            {/* Group Info & Actions */}
            <Box sx={{ p: 3 }}>
              <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 2 }}>
                <Box>
                  <Typography variant="body1" color="text.secondary" mb={0.5}>
                    {group.description}
                  </Typography>
                  <Stack direction="row" spacing={2} alignItems="center">
                    <Chip label={group.category} size="small" />
                    <Typography variant="caption" color="text.disabled">
                      {group.createdAt}
                    </Typography>
                  </Stack>
                </Box>

                <Stack direction="row" spacing={1}>
                  {group.isMember ? (
                    <>
                      <Button
                        variant="outlined"
                        startIcon={<PersonAddIcon />}
                        onClick={() => setInviteDialogOpen(true)}
                        sx={{
                          textTransform: "none",
                          fontWeight: 600,
                          borderRadius: 3,
                        }}
                      >
                        Mời
                      </Button>
                      <IconButton>
                        <NotificationsIcon />
                      </IconButton>
                      <IconButton onClick={handleLeaveGroup}>
                        <ExitToAppIcon />
                      </IconButton>
                      {group.isAdmin && (
                        <IconButton>
                          <SettingsIcon />
                        </IconButton>
                      )}
                    </>
                  ) : (
                    <Button
                      variant="contained"
                      sx={{
                        textTransform: "none",
                        fontWeight: 600,
                        borderRadius: 3,
                        px: 4,
                        background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                        "&:hover": {
                          background: "linear-gradient(135deg, #5568d3 0%, #63428a 100%)",
                        },
                      }}
                    >
                      Tham gia nhóm
                    </Button>
                  )}
                </Stack>
              </Box>

              <Divider sx={{ mb: 2 }} />

              {/* Members Preview */}
              <Box sx={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
                <Box sx={{ display: "flex", alignItems: "center" }}>
                  <AvatarGroup max={6} sx={{ mr: 2 }}>
                    {members.map((member) => (
                      <Avatar key={member.id} src={member.avatar} sx={{ width: 32, height: 32 }} />
                    ))}
                  </AvatarGroup>
                  <Typography variant="body2" color="text.secondary">
                    Các thành viên bạn biết: Nguyễn Văn A, Trần Thị B và {group.members - 2} người khác
                  </Typography>
                </Box>
              </Box>
            </Box>
          </Card>

          {/* Tabs */}
          <Card
            elevation={0}
            sx={(t) => ({
              borderRadius: 4,
              mb: 3,
              boxShadow: t.shadows[1],
              border: "1px solid",
              borderColor: "divider",
              bgcolor: "background.paper",
            })}
          >
            <Tabs
              value={tabValue}
              onChange={handleTabChange}
              sx={{
                px: 2,
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
              <Tab label="Thảo luận" />
              <Tab label={`Thành viên (${members.length})`} />
              <Tab label="Giới thiệu" />
            </Tabs>
          </Card>

          <Grid container spacing={3}>
            {/* Main Content */}
            <Grid item xs={12} md={8}>
              {/* Tab 0: Discussion */}
              {tabValue === 0 && (
                <Box>
                  {/* Create Post */}
                  {group.isMember && (
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
                      <Typography variant="h6" fontWeight={700} mb={2}>
                        Tạo bài viết
                      </Typography>

                      <TextField
                        fullWidth
                        multiline
                        rows={3}
                        placeholder="Bạn đang nghĩ gì?"
                        value={newPostContent}
                        onChange={(e) => setNewPostContent(e.target.value)}
                        sx={{
                          mb: 2,
                          "& .MuiOutlinedInput-root": {
                            borderRadius: 3,
                            bgcolor: (t) =>
                              t.palette.mode === "dark" ? "rgba(255,255,255,0.04)" : "background.default",
                          },
                        }}
                      />

                      {/* Media Preview */}
                      {mediaPreview.length > 0 && (
                        <Box
                          sx={{
                            mb: 2,
                            display: "grid",
                            gridTemplateColumns:
                              mediaPreview.length === 1 ? "1fr" : "repeat(auto-fill, minmax(180px, 1fr))",
                            gap: 1.5,
                            p: 2,
                            bgcolor: (t) =>
                              t.palette.mode === "dark" ? "rgba(255,255,255,0.02)" : "rgba(0,0,0,0.02)",
                            borderRadius: 3,
                            border: "1px solid",
                            borderColor: "divider",
                          }}
                        >
                          {mediaPreview.map((preview, index) => (
                            <Box
                              key={index}
                              sx={{
                                position: "relative",
                                paddingTop: mediaPreview.length === 1 ? "56.25%" : "100%",
                                borderRadius: 2,
                                overflow: "hidden",
                                bgcolor: "background.default",
                                border: "1px solid",
                                borderColor: "divider",
                              }}
                            >
                              {preview.type === "image" ? (
                                <img
                                  src={preview.url}
                                  alt={preview.name}
                                  style={{
                                    position: "absolute",
                                    top: 0,
                                    left: 0,
                                    width: "100%",
                                    height: "100%",
                                    objectFit: "cover",
                                  }}
                                />
                              ) : (
                                <video
                                  src={preview.url}
                                  style={{
                                    position: "absolute",
                                    top: 0,
                                    left: 0,
                                    width: "100%",
                                    height: "100%",
                                    objectFit: "cover",
                                  }}
                                  controls
                                />
                              )}

                              <IconButton
                                size="small"
                                onClick={() => handleRemoveMedia(index)}
                                sx={{
                                  position: "absolute",
                                  top: 6,
                                  right: 6,
                                  bgcolor: "rgba(0,0,0,0.6)",
                                  color: "white",
                                  "&:hover": {
                                    bgcolor: "rgba(0,0,0,0.8)",
                                  },
                                }}
                              >
                                <CloseIcon fontSize="small" />
                              </IconButton>
                            </Box>
                          ))}
                        </Box>
                      )}

                      <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                        <input
                          type="file"
                          ref={fileInputRef}
                          onChange={handleMediaSelect}
                          accept="image/*,video/*"
                          multiple
                          style={{ display: "none" }}
                        />
                        <Button
                          startIcon={<ImageIcon />}
                          onClick={() => fileInputRef.current?.click()}
                          sx={{
                            textTransform: "none",
                            fontWeight: 600,
                            borderRadius: 2,
                          }}
                        >
                          Ảnh/Video
                        </Button>

                        <Button
                          variant="contained"
                          onClick={handleCreatePost}
                          disabled={!newPostContent.trim() && mediaFiles.length === 0}
                          sx={{
                            textTransform: "none",
                            fontWeight: 600,
                            borderRadius: 3,
                            px: 4,
                            background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                            "&:hover": {
                              background: "linear-gradient(135deg, #5568d3 0%, #63428a 100%)",
                            },
                            "&:disabled": {
                              background: "action.disabledBackground",
                              color: "text.disabled",
                            },
                          }}
                        >
                          Đăng
                        </Button>
                      </Box>
                    </Card>
                  )}

                  {/* Posts List */}
                  {posts.map((post) => (
                    <Card
                      key={post.id}
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
                      {/* Post Header */}
                      <Box sx={{ display: "flex", justifyContent: "space-between", mb: 2 }}>
                        <Box sx={{ display: "flex", alignItems: "center" }}>
                          <Avatar src={post.author.avatar} sx={{ width: 48, height: 48, mr: 2 }} />
                          <Box>
                            <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                              <Typography variant="body1" fontWeight={700}>
                                {post.author.name}
                              </Typography>
                              {getRoleBadge(post.author.role)}
                            </Box>
                            <Typography variant="caption" color="text.secondary">
                              {post.timestamp}
                            </Typography>
                          </Box>
                        </Box>
                        <IconButton size="small" onClick={(e) => handleMenuOpen(e, post)}>
                          <MoreVertIcon />
                        </IconButton>
                      </Box>

                      {/* Post Content */}
                      <Typography variant="body1" mb={2}>
                        {post.content}
                      </Typography>

                      {/* Post Media */}
                      {post.media.length > 0 && (
                        <Box
                          sx={{
                            mb: 2,
                            borderRadius: 3,
                            overflow: "hidden",
                            border: "1px solid",
                            borderColor: "divider",
                          }}
                        >
                          {post.media.map((media, index) => (
                            <Box key={index}>
                              {media.type === "image" ? (
                                <img
                                  src={media.url}
                                  alt="post media"
                                  style={{ width: "100%", display: "block" }}
                                />
                              ) : (
                                <video src={media.url} controls style={{ width: "100%", display: "block" }} />
                              )}
                            </Box>
                          ))}
                        </Box>
                      )}

                      {/* Post Stats */}
                      <Box
                        sx={{
                          display: "flex",
                          justifyContent: "space-between",
                          alignItems: "center",
                          py: 1,
                          borderTop: "1px solid",
                          borderBottom: "1px solid",
                          borderColor: "divider",
                          mb: 1,
                        }}
                      >
                        <Typography variant="body2" color="text.secondary">
                          {post.likes} lượt thích
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          {post.comments} bình luận • {post.shares} chia sẻ
                        </Typography>
                      </Box>

                      {/* Post Actions */}
                      <Stack direction="row" spacing={1}>
                        <Button
                          fullWidth
                          startIcon={<ThumbUpIcon />}
                          onClick={() => handleLikePost(post.id)}
                          sx={{
                            textTransform: "none",
                            fontWeight: 600,
                            color: post.isLiked ? "primary.main" : "text.secondary",
                            "&:hover": { bgcolor: "action.hover" },
                          }}
                        >
                          Thích
                        </Button>
                        <Button
                          fullWidth
                          startIcon={<ChatBubbleOutlineIcon />}
                          sx={{
                            textTransform: "none",
                            fontWeight: 600,
                            color: "text.secondary",
                            "&:hover": { bgcolor: "action.hover" },
                          }}
                        >
                          Bình luận
                        </Button>
                        <Button
                          fullWidth
                          startIcon={<ShareIcon />}
                          sx={{
                            textTransform: "none",
                            fontWeight: 600,
                            color: "text.secondary",
                            "&:hover": { bgcolor: "action.hover" },
                          }}
                        >
                          Chia sẻ
                        </Button>
                      </Stack>
                    </Card>
                  ))}
                </Box>
              )}

              {/* Tab 1: Members */}
              {tabValue === 1 && (
                <Card
                  elevation={0}
                  sx={(t) => ({
                    borderRadius: 4,
                    p: 3,
                    boxShadow: t.shadows[1],
                    border: "1px solid",
                    borderColor: "divider",
                    bgcolor: "background.paper",
                  })}
                >
                  <Typography variant="h6" fontWeight={700} mb={3}>
                    Thành viên
                  </Typography>
                  <Grid container spacing={2}>
                    {members.map((member) => (
                      <Grid item xs={12} sm={6} key={member.id}>
                        <Box
                          sx={{
                            display: "flex",
                            alignItems: "center",
                            p: 2,
                            borderRadius: 3,
                            border: "1px solid",
                            borderColor: "divider",
                            transition: "all 0.2s ease",
                            "&:hover": {
                              bgcolor: "action.hover",
                              transform: "translateY(-2px)",
                            },
                          }}
                        >
                          <Avatar src={member.avatar} sx={{ width: 56, height: 56, mr: 2 }} />
                          <Box sx={{ flex: 1 }}>
                            <Box sx={{ display: "flex", alignItems: "center", gap: 1, mb: 0.5 }}>
                              <Typography variant="body1" fontWeight={700}>
                                {member.name}
                              </Typography>
                              {getRoleBadge(member.role)}
                            </Box>
                            <Typography variant="caption" color="text.secondary">
                              Thành viên
                            </Typography>
                          </Box>
                          <Button
                            variant="outlined"
                            size="small"
                            sx={{
                              textTransform: "none",
                              fontWeight: 600,
                              borderRadius: 2,
                            }}
                          >
                            Xem trang
                          </Button>
                        </Box>
                      </Grid>
                    ))}
                  </Grid>
                </Card>
              )}

              {/* Tab 2: About */}
              {tabValue === 2 && (
                <Card
                  elevation={0}
                  sx={(t) => ({
                    borderRadius: 4,
                    p: 3,
                    boxShadow: t.shadows[1],
                    border: "1px solid",
                    borderColor: "divider",
                    bgcolor: "background.paper",
                  })}
                >
                  <Typography variant="h6" fontWeight={700} mb={3}>
                    Giới thiệu về nhóm
                  </Typography>
                  <Stack spacing={3}>
                    <Box>
                      <Typography variant="subtitle2" fontWeight={700} mb={1}>
                        Mô tả
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {group.description}
                      </Typography>
                    </Box>
                    <Divider />
                    <Box>
                      <Typography variant="subtitle2" fontWeight={700} mb={1}>
                        Quyền riêng tư
                      </Typography>
                      <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                        {group.privacy === "public" ? <PublicIcon /> : <LockIcon />}
                        <Typography variant="body2" color="text.secondary">
                          {group.privacy === "public" ? "Nhóm công khai" : "Nhóm riêng tư"} •{" "}
                          {group.privacy === "public"
                            ? "Bất kỳ ai cũng có thể xem nội dung của nhóm"
                            : "Chỉ thành viên mới có thể xem nội dung"}
                        </Typography>
                      </Box>
                    </Box>
                    <Divider />
                    <Box>
                      <Typography variant="subtitle2" fontWeight={700} mb={1}>
                        Lịch sử
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {group.createdAt}
                      </Typography>
                    </Box>
                    <Divider />
                    <Box>
                      <Typography variant="subtitle2" fontWeight={700} mb={1}>
                        Danh mục
                      </Typography>
                      <Chip label={group.category} />
                    </Box>
                  </Stack>
                </Card>
              )}
            </Grid>

            {/* Sidebar */}
            <Grid item xs={12} md={4}>
              {/* Group Info Card */}
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
                <Typography variant="h6" fontWeight={700} mb={2}>
                  Thông tin
                </Typography>
                <Stack spacing={2}>
                  <Box>
                    <Typography variant="body2" color="text.secondary" mb={0.5}>
                      Thành viên
                    </Typography>
                    <Typography variant="h6" fontWeight={700}>
                      {group.members.toLocaleString()}
                    </Typography>
                  </Box>
                  <Divider />
                  <Box>
                    <Typography variant="body2" color="text.secondary" mb={0.5}>
                      Quyền riêng tư
                    </Typography>
                    <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                      {group.privacy === "public" ? <PublicIcon fontSize="small" /> : <LockIcon fontSize="small" />}
                      <Typography variant="body2" fontWeight={600}>
                        {group.privacy === "public" ? "Công khai" : "Riêng tư"}
                      </Typography>
                    </Box>
                  </Box>
                  <Divider />
                  <Box>
                    <Typography variant="body2" color="text.secondary" mb={0.5}>
                      Hoạt động
                    </Typography>
                    <Typography variant="body2" fontWeight={600}>
                      {posts.length} bài viết hôm nay
                    </Typography>
                  </Box>
                </Stack>
              </Card>

              {/* Recent Members */}
              <Card
                elevation={0}
                sx={(t) => ({
                  borderRadius: 4,
                  p: 3,
                  boxShadow: t.shadows[1],
                  border: "1px solid",
                  borderColor: "divider",
                  bgcolor: "background.paper",
                })}
              >
                <Typography variant="h6" fontWeight={700} mb={2}>
                  Thành viên gần đây
                </Typography>
                <Stack spacing={2}>
                  {members.slice(0, 5).map((member) => (
                    <Box key={member.id} sx={{ display: "flex", alignItems: "center" }}>
                      <Avatar src={member.avatar} sx={{ width: 40, height: 40, mr: 2 }} />
                      <Box sx={{ flex: 1 }}>
                        <Typography variant="body2" fontWeight={600}>
                          {member.name}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {member.role === "admin" ? "Quản trị viên" : member.role === "moderator" ? "Điều hành viên" : "Thành viên"}
                        </Typography>
                      </Box>
                    </Box>
                  ))}
                  <Button
                    fullWidth
                    variant="outlined"
                    sx={{
                      textTransform: "none",
                      fontWeight: 600,
                      borderRadius: 2,
                      mt: 1,
                    }}
                  >
                    Xem tất cả
                  </Button>
                </Stack>
              </Card>
            </Grid>
          </Grid>
        </Box>
      </Box>

      {/* Post Menu */}
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleMenuClose}
        PaperProps={{
          sx: { borderRadius: 3, minWidth: 200 },
        }}
      >
        <MenuItem onClick={handleMenuClose}>
          <ListItemIcon>
            <EditIcon fontSize="small" />
          </ListItemIcon>
          <ListItemText>Chỉnh sửa</ListItemText>
        </MenuItem>
        <MenuItem onClick={handleDeletePost}>
          <ListItemIcon>
            <DeleteIcon fontSize="small" />
          </ListItemIcon>
          <ListItemText>Xóa bài viết</ListItemText>
        </MenuItem>
        <Divider />
        <MenuItem onClick={handleMenuClose}>
          <ListItemIcon>
            <ReportIcon fontSize="small" />
          </ListItemIcon>
          <ListItemText>Báo cáo</ListItemText>
        </MenuItem>
      </Menu>

      {/* Invite Dialog */}
      <Dialog
        open={inviteDialogOpen}
        onClose={() => setInviteDialogOpen(false)}
        maxWidth="sm"
        fullWidth
        PaperProps={{
          sx: { borderRadius: 4 },
        }}
      >
        <DialogTitle sx={{ fontWeight: 700, pb: 1 }}>Mời bạn bè vào nhóm</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            placeholder="Tìm kiếm bạn bè..."
            sx={{
              mb: 3,
              "& .MuiOutlinedInput-root": { borderRadius: 3 },
            }}
          />
          <Stack spacing={2}>
            {[1, 2, 3, 4, 5].map((i) => (
              <Box
                key={i}
                sx={{
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "space-between",
                  p: 2,
                  borderRadius: 3,
                  border: "1px solid",
                  borderColor: "divider",
                }}
              >
                <Box sx={{ display: "flex", alignItems: "center" }}>
                  <Avatar src={`https://i.pravatar.cc/150?img=${i + 10}`} sx={{ width: 48, height: 48, mr: 2 }} />
                  <Box>
                    <Typography variant="body1" fontWeight={600}>
                      Người dùng {i}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      5 bạn chung
                    </Typography>
                  </Box>
                </Box>
                <Button
                  variant="outlined"
                  size="small"
                  sx={{
                    textTransform: "none",
                    fontWeight: 600,
                    borderRadius: 2,
                  }}
                >
                  Mời
                </Button>
              </Box>
            ))}
          </Stack>
        </DialogContent>
        <DialogActions sx={{ p: 3, pt: 0 }}>
          <Button
            onClick={() => setInviteDialogOpen(false)}
            sx={{
              textTransform: "none",
              fontWeight: 600,
              borderRadius: 3,
              px: 3,
            }}
          >
            Đóng
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