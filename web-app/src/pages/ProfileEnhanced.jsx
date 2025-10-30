import { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import {
  Box,
  Card,
  CircularProgress,
  Typography,
  Avatar,
  Button,
  Tabs,
  Tab,
  Grid,
  Paper,
  IconButton,
  Divider,
  Chip,
  Stack,
  TextField,
  Snackbar,
  Alert,
  alpha,
} from "@mui/material";
import PhotoCameraIcon from "@mui/icons-material/PhotoCamera";
import MessageIcon from "@mui/icons-material/Message";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import MoreHorizIcon from "@mui/icons-material/MoreHoriz";
import EditIcon from "@mui/icons-material/Edit";
import LocationOnIcon from "@mui/icons-material/LocationOn";
import WorkIcon from "@mui/icons-material/Work";
import SchoolIcon from "@mui/icons-material/School";
import CakeIcon from "@mui/icons-material/Cake";
import FavoriteIcon from "@mui/icons-material/Favorite";
import {
  getMyInfo,
  uploadAvatar,
} from "../services/userService";
import { isAuthenticated, logOut } from "../services/authenticationService";
import Scene from "./Scene";
import Post from "../components/Post";

const mockPosts = [
  {
    id: "p1",
    avatar: "https://i.pravatar.cc/150?img=1",
    username: "John Doe",
    created: "2 hours ago",
    content: "Just finished an amazing project! 🚀",
  },
  {
    id: "p2",
    avatar: "https://i.pravatar.cc/150?img=1",
    username: "John Doe",
    created: "1 day ago",
    content: "Beautiful sunset today 🌅",
  },
];

const mockFriends = [
  { id: 1, name: "Sarah Johnson", avatar: "https://i.pravatar.cc/150?img=2" },
  { id: 2, name: "Mike Chen", avatar: "https://i.pravatar.cc/150?img=3" },
  { id: 3, name: "Emma Wilson", avatar: "https://i.pravatar.cc/150?img=4" },
  { id: 4, name: "David Brown", avatar: "https://i.pravatar.cc/150?img=5" },
  { id: 5, name: "Lisa Anderson", avatar: "https://i.pravatar.cc/150?img=6" },
  { id: 6, name: "Tom Garcia", avatar: "https://i.pravatar.cc/150?img=7" },
];

const mockPhotos = [
  "https://picsum.photos/400/300?random=1",
  "https://picsum.photos/400/300?random=2",
  "https://picsum.photos/400/300?random=3",
  "https://picsum.photos/400/300?random=4",
  "https://picsum.photos/400/300?random=5",
  "https://picsum.photos/400/300?random=6",
  "https://picsum.photos/400/300?random=7",
  "https://picsum.photos/400/300?random=8",
  "https://picsum.photos/400/300?random=9",
];

export default function ProfileEnhanced() {
  const navigate = useNavigate();
  const [userDetails, setUserDetails] = useState(null);
  const [activeTab, setActiveTab] = useState(0);
  const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });
  const [uploading, setUploading] = useState(false);
  const [editingAbout, setEditingAbout] = useState(false);
  const avatarInputRef = useRef(null);
  const coverInputRef = useRef(null);

  const [coverImage, setCoverImage] = useState("https://picsum.photos/1200/400?random=10");

  const getUserDetails = async () => {
    try {
      const response = await getMyInfo();
      setUserDetails(response.data.result);
    } catch (error) {
      if (error.response?.status === 401) {
        logOut();
        navigate("/login");
      }
    }
  };

  useEffect(() => {
    if (!isAuthenticated()) {
      navigate("/login");
    } else {
      getUserDetails();
    }
  }, [navigate]);

  const handleAvatarClick = () => avatarInputRef.current?.click();
  const handleCoverClick = () => coverInputRef.current?.click();

  const handleAvatarUpload = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    if (!file.type.match("image.*")) {
      setSnackbar({ open: true, message: "Please select an image file", severity: "error" });
      return;
    }

    try {
      setUploading(true);
      const formData = new FormData();
      formData.append("file", file);
      const response = await uploadAvatar(formData);
      setUserDetails({ ...userDetails, avatar: response.data.result.avatar });
      setSnackbar({ open: true, message: "Avatar updated successfully!", severity: "success" });
    } catch (error) {
      setSnackbar({ open: true, message: "Failed to upload avatar", severity: "error" });
    } finally {
      setUploading(false);
    }
  };

  const handleCoverUpload = (event) => {
    const file = event.target.files[0];
    if (!file) return;

    if (!file.type.match("image.*")) {
      setSnackbar({ open: true, message: "Please select an image file", severity: "error" });
      return;
    }

    const reader = new FileReader();
    reader.onload = (e) => {
      setCoverImage(e.target.result);
      setSnackbar({ open: true, message: "Cover photo updated!", severity: "success" });
    };
    reader.readAsDataURL(file);
  };

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  if (!userDetails) {
    return (
      <Scene>
        <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "50vh" }}>
          <CircularProgress />
        </Box>
      </Scene>
    );
  }

  return (
    <Scene>
      <Box sx={{ width: "100%", maxWidth: 1200, mx: "auto" }}>
        {/* Cover Photo */}
        <Paper
          elevation={0}
          sx={{
            position: "relative",
            height: 400,
            borderRadius: 0,
            borderTopLeftRadius: 16,
            borderTopRightRadius: 16,
            overflow: "hidden",
            border: "1px solid",
            borderColor: "divider",
            borderBottom: "none",
          }}
        >
          <Box
            component="img"
            src={coverImage}
            alt="Cover"
            sx={{
              width: "100%",
              height: "100%",
              objectFit: "cover",
            }}
          />
          <IconButton
            onClick={handleCoverClick}
            sx={{
              position: "absolute",
              bottom: 16,
              right: 16,
              bgcolor: "background.paper",
              "&:hover": { bgcolor: "background.default" },
            }}
          >
            <PhotoCameraIcon />
          </IconButton>
          <input
            ref={coverInputRef}
            type="file"
            accept="image/*"
            style={{ display: "none" }}
            onChange={handleCoverUpload}
          />
        </Paper>

        {/* Profile Header */}
        <Paper
          elevation={0}
          sx={{
            borderRadius: 0,
            borderBottomLeftRadius: 16,
            borderBottomRightRadius: 16,
            border: "1px solid",
            borderColor: "divider",
            borderTop: "none",
            p: 3,
            mb: 3,
          }}
        >
          <Box sx={{ display: "flex", flexDirection: { xs: "column", md: "row" }, gap: 3 }}>
            {/* Avatar */}
            <Box sx={{ position: "relative", alignSelf: { xs: "center", md: "flex-start" }, mt: -8 }}>
              <Avatar
                src={userDetails.avatar}
                sx={{
                  width: 168,
                  height: 168,
                  border: "6px solid",
                  borderColor: "background.paper",
                  bgcolor: "primary.main",
                  fontSize: 64,
                  cursor: "pointer",
                  transition: "opacity 0.3s",
                  "&:hover": { opacity: 0.8 },
                }}
                onClick={handleAvatarClick}
              >
                {userDetails.firstName?.[0]}{userDetails.lastName?.[0]}
              </Avatar>
              <IconButton
                onClick={handleAvatarClick}
                sx={{
                  position: "absolute",
                  bottom: 8,
                  right: 8,
                  bgcolor: "background.paper",
                  border: "2px solid",
                  borderColor: "divider",
                  "&:hover": { bgcolor: "background.default" },
                }}
                size="small"
              >
                <PhotoCameraIcon fontSize="small" />
              </IconButton>
              {uploading && (
                <Box
                  sx={{
                    position: "absolute",
                    top: 0,
                    left: 0,
                    right: 0,
                    bottom: 0,
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    borderRadius: "50%",
                    bgcolor: "rgba(0, 0, 0, 0.5)",
                  }}
                >
                  <CircularProgress size={48} sx={{ color: "white" }} />
                </Box>
              )}
              <input
                ref={avatarInputRef}
                type="file"
                accept="image/*"
                style={{ display: "none" }}
                onChange={handleAvatarUpload}
              />
            </Box>

            {/* Info */}
            <Box sx={{ flex: 1, textAlign: { xs: "center", md: "left" } }}>
              <Typography variant="h4" sx={{ fontWeight: 700, mb: 1 }}>
                {userDetails.firstName} {userDetails.lastName}
              </Typography>
              <Typography variant="body1" color="text.secondary" sx={{ mb: 2 }}>
                @{userDetails.username}
              </Typography>
              <Stack
                direction="row"
                spacing={1}
                sx={{ mb: 2, justifyContent: { xs: "center", md: "flex-start" } }}
              >
                <Chip label={`${mockFriends.length} Friends`} size="small" />
                <Chip label={`${mockPhotos.length} Photos`} size="small" />
                <Chip label={`${mockPosts.length} Posts`} size="small" />
              </Stack>

              <Stack
                direction="row"
                spacing={1.5}
                sx={{ justifyContent: { xs: "center", md: "flex-start" } }}
              >
                <Button
                  variant="contained"
                  startIcon={<PersonAddIcon />}
                  sx={{
                    textTransform: "none",
                    fontWeight: 600,
                    borderRadius: 2.5,
                    px: 3,
                    background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                  }}
                >
                  Add Friend
                </Button>
                <Button
                  variant="outlined"
                  startIcon={<MessageIcon />}
                  sx={{
                    textTransform: "none",
                    fontWeight: 600,
                    borderRadius: 2.5,
                    px: 3,
                  }}
                >
                  Message
                </Button>
                <IconButton
                  sx={{
                    border: "1px solid",
                    borderColor: "divider",
                  }}
                >
                  <MoreHorizIcon />
                </IconButton>
              </Stack>
            </Box>
          </Box>

          {/* Tabs */}
          <Divider sx={{ my: 3 }} />
          <Tabs
            value={activeTab}
            onChange={handleTabChange}
            variant="scrollable"
            scrollButtons="auto"
            sx={{
              "& .MuiTab-root": {
                textTransform: "none",
                fontSize: 15,
                fontWeight: 600,
                minHeight: 48,
              },
            }}
          >
            <Tab label="Posts" />
            <Tab label="About" />
            <Tab label="Friends" />
            <Tab label="Photos" />
          </Tabs>
        </Paper>

        {/* Tab Content */}
        <Box>
          {/* Posts Tab */}
          {activeTab === 0 && (
            <Box sx={{ maxWidth: 680, mx: "auto" }}>
              {mockPosts.map((post) => (
                <Post key={post.id} post={post} />
              ))}
            </Box>
          )}

          {/* About Tab */}
          {activeTab === 1 && (
            <Paper
              elevation={0}
              sx={{
                borderRadius: 4,
                p: 3,
                border: "1px solid",
                borderColor: "divider",
                maxWidth: 800,
                mx: "auto",
              }}
            >
              <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 3 }}>
                <Typography variant="h6" sx={{ fontWeight: 700 }}>
                  About
                </Typography>
                <IconButton
                  size="small"
                  onClick={() => setEditingAbout(!editingAbout)}
                  sx={(t) => ({
                    bgcolor: editingAbout ? "primary.main" : "action.hover",
                    color: editingAbout ? "white" : "text.secondary",
                    "&:hover": {
                      bgcolor: editingAbout ? "primary.dark" : "action.selected",
                    },
                  })}
                >
                  <EditIcon fontSize="small" />
                </IconButton>
              </Box>

              <Stack spacing={3}>
                <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                  <WorkIcon color="action" />
                  <Box>
                    <Typography variant="body2" color="text.secondary">
                      Works at
                    </Typography>
                    <Typography variant="body1" fontWeight={600}>
                      Tech Company Inc.
                    </Typography>
                  </Box>
                </Box>

                <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                  <SchoolIcon color="action" />
                  <Box>
                    <Typography variant="body2" color="text.secondary">
                      Studied at
                    </Typography>
                    <Typography variant="body1" fontWeight={600}>
                      {userDetails.city || "University"}
                    </Typography>
                  </Box>
                </Box>

                <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                  <LocationOnIcon color="action" />
                  <Box>
                    <Typography variant="body2" color="text.secondary">
                      Lives in
                    </Typography>
                    <Typography variant="body1" fontWeight={600}>
                      {userDetails.city || "City"}
                    </Typography>
                  </Box>
                </Box>

                <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                  <CakeIcon color="action" />
                  <Box>
                    <Typography variant="body2" color="text.secondary">
                      Born on
                    </Typography>
                    <Typography variant="body1" fontWeight={600}>
                      {userDetails.dob || "Not specified"}
                    </Typography>
                  </Box>
                </Box>

                <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                  <FavoriteIcon color="action" />
                  <Box>
                    <Typography variant="body2" color="text.secondary">
                      Relationship
                    </Typography>
                    <Typography variant="body1" fontWeight={600}>
                      Single
                    </Typography>
                  </Box>
                </Box>
              </Stack>

              {editingAbout && (
                <Box sx={{ mt: 3, pt: 3, borderTop: "1px solid", borderColor: "divider" }}>
                  <TextField fullWidth label="Bio" multiline rows={4} sx={{ mb: 2 }} />
                  <Stack direction="row" spacing={1.5} justifyContent="flex-end">
                    <Button
                      variant="outlined"
                      onClick={() => setEditingAbout(false)}
                      sx={{ textTransform: "none", borderRadius: 2 }}
                    >
                      Cancel
                    </Button>
                    <Button
                      variant="contained"
                      onClick={() => {
                        setEditingAbout(false);
                        setSnackbar({ open: true, message: "Profile updated!", severity: "success" });
                      }}
                      sx={{
                        textTransform: "none",
                        borderRadius: 2,
                        background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                      }}
                    >
                      Save Changes
                    </Button>
                  </Stack>
                </Box>
              )}
            </Paper>
          )}

          {/* Friends Tab */}
          {activeTab === 2 && (
            <Paper
              elevation={0}
              sx={{
                borderRadius: 4,
                p: 3,
                border: "1px solid",
                borderColor: "divider",
              }}
            >
              <Typography variant="h6" sx={{ fontWeight: 700, mb: 3 }}>
                Friends ({mockFriends.length})
              </Typography>
              <Grid container spacing={2}>
                {mockFriends.map((friend) => (
                  <Grid item xs={6} sm={4} md={3} lg={2} key={friend.id}>
                    <Card
                      elevation={0}
                      sx={{
                        borderRadius: 3,
                        p: 2,
                        textAlign: "center",
                        border: "1px solid",
                        borderColor: "divider",
                        transition: "all 0.25s ease",
                        "&:hover": {
                          transform: "translateY(-4px)",
                          boxShadow: 2,
                        },
                      }}
                    >
                      <Avatar
                        src={friend.avatar}
                        sx={{
                          width: 80,
                          height: 80,
                          mx: "auto",
                          mb: 1,
                          border: "2px solid",
                          borderColor: "divider",
                        }}
                      />
                      <Typography
                        variant="body2"
                        sx={{ fontWeight: 600, fontSize: 13 }}
                        noWrap
                      >
                        {friend.name}
                      </Typography>
                    </Card>
                  </Grid>
                ))}
              </Grid>
            </Paper>
          )}

          {/* Photos Tab */}
          {activeTab === 3 && (
            <Paper
              elevation={0}
              sx={{
                borderRadius: 4,
                p: 3,
                border: "1px solid",
                borderColor: "divider",
              }}
            >
              <Typography variant="h6" sx={{ fontWeight: 700, mb: 3 }}>
                Photos ({mockPhotos.length})
              </Typography>
              <Grid container spacing={1.5}>
                {mockPhotos.map((photo, index) => (
                  <Grid item xs={6} sm={4} md={3} key={index}>
                    <Box
                      component="img"
                      src={photo}
                      alt={`Photo ${index + 1}`}
                      sx={{
                        width: "100%",
                        height: 200,
                        objectFit: "cover",
                        borderRadius: 2,
                        cursor: "pointer",
                        transition: "all 0.25s ease",
                        "&:hover": {
                          transform: "scale(1.05)",
                          boxShadow: 3,
                        },
                      }}
                    />
                  </Grid>
                ))}
              </Grid>
            </Paper>
          )}
        </Box>
      </Box>

      {/* Snackbar */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
        sx={{ mt: "64px" }}
      >
        <Alert
          onClose={() => setSnackbar({ ...snackbar, open: false })}
          severity={snackbar.severity}
          variant="filled"
          sx={{ width: "100%", borderRadius: 3 }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Scene>
  );
}
