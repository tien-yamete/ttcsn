// src/pages/Settings.jsx
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
  Switch,
  Divider,
  Stack,
  IconButton,
  Select,
  MenuItem,
  FormControl,
  Alert,
  Snackbar,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  Chip,
} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import PhotoCameraIcon from "@mui/icons-material/PhotoCamera";
import LockIcon from "@mui/icons-material/Lock";
import NotificationsIcon from "@mui/icons-material/Notifications";
import SecurityIcon from "@mui/icons-material/Security";
import DeleteIcon from "@mui/icons-material/Delete";
import CloseIcon from "@mui/icons-material/Close";
import Scene from "./Scene";

export default function Settings() {
  const [tabValue, setTabValue] = useState(0);
  const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

  // Profile Settings
  const [profile, setProfile] = useState({
    name: "Nguyễn Văn A",
    email: "nguyenvana@example.com",
    phone: "0123456789",
    bio: "Yêu thích công nghệ và du lịch",
    location: "Hà Nội, Việt Nam",
    website: "https://example.com",
    avatar: "https://i.pravatar.cc/150?img=1",
  });

  // Privacy Settings
  const [privacy, setPrivacy] = useState({
    profileVisibility: "public",
    showEmail: false,
    showPhone: false,
    allowFriendRequests: true,
    allowMessages: true,
    allowTagging: true,
    searchable: true,
  });

  // Notification Settings
  const [notifications, setNotifications] = useState({
    emailNotifications: true,
    pushNotifications: true,
    friendRequests: true,
    comments: true,
    likes: true,
    messages: true,
    groupInvites: true,
  });

  // Security Settings
  const [security, setSecurity] = useState({
    twoFactorAuth: false,
    loginAlerts: true,
    showActiveStatus: true,
  });

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const handleProfileChange = (field, value) => {
    setProfile({ ...profile, [field]: value });
  };

  const handlePrivacyChange = (field, value) => {
    setPrivacy({ ...privacy, [field]: value });
  };

  const handleNotificationChange = (field, value) => {
    setNotifications({ ...notifications, [field]: value });
  };

  const handleSecurityChange = (field, value) => {
    setSecurity({ ...security, [field]: value });
  };

  const handleSaveProfile = () => {
    // API call to save profile
    setSnackbar({ open: true, message: "Đã lưu thông tin cá nhân!", severity: "success" });
  };

  const handleSavePrivacy = () => {
    // API call to save privacy settings
    setSnackbar({ open: true, message: "Đã cập nhật cài đặt quyền riêng tư!", severity: "success" });
  };

  const handleSaveNotifications = () => {
    // API call to save notification settings
    setSnackbar({ open: true, message: "Đã cập nhật cài đặt thông báo!", severity: "success" });
  };

  const handleSaveSecurity = () => {
    // API call to save security settings
    setSnackbar({ open: true, message: "Đã cập nhật cài đặt bảo mật!", severity: "success" });
  };

  const handleChangePassword = () => {
    setSnackbar({ open: true, message: "Đã gửi email đặt lại mật khẩu!", severity: "info" });
  };

  const handleDeleteAccount = () => {
    setDeleteDialogOpen(false);
    // API call to delete account
    setSnackbar({ open: true, message: "Yêu cầu xóa tài khoản đã được gửi!", severity: "warning" });
  };

  const handleAvatarChange = () => {
    setSnackbar({ open: true, message: "Chức năng đổi ảnh đại diện đang được phát triển!", severity: "info" });
  };

  const handleCloseSnackbar = (event, reason) => {
    if (reason === "clickaway") return;
    setSnackbar({ ...snackbar, open: false });
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
        <Box sx={{ width: "100%", maxWidth: 1000, py: 2 }}>
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
            <Typography
              sx={{
                fontSize: 26,
                fontWeight: 700,
                mb: 2,
                background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                WebkitBackgroundClip: "text",
                WebkitTextFillColor: "transparent",
              }}
            >
              Cài đặt
            </Typography>

            <Tabs
              value={tabValue}
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
                "& .Mui-selected": {
                  color: "primary.main",
                },
              }}
            >
              <Tab icon={<EditIcon />} iconPosition="start" label="Thông tin cá nhân" />
              <Tab icon={<LockIcon />} iconPosition="start" label="Quyền riêng tư" />
              <Tab icon={<NotificationsIcon />} iconPosition="start" label="Thông báo" />
              <Tab icon={<SecurityIcon />} iconPosition="start" label="Bảo mật" />
            </Tabs>
          </Card>

          {/* Tab 0: Profile Settings */}
          {tabValue === 0 && (
            <Card
              elevation={0}
              sx={(t) => ({
                borderRadius: 4,
                p: 4,
                boxShadow: t.shadows[1],
                border: "1px solid",
                borderColor: "divider",
                bgcolor: "background.paper",
              })}
            >
              <Typography variant="h6" fontWeight={700} mb={3}>
                Thông tin cá nhân
              </Typography>

              {/* Avatar Section */}
              <Box sx={{ display: "flex", alignItems: "center", mb: 4 }}>
                <Box sx={{ position: "relative" }}>
                  <Avatar
                    src={profile.avatar}
                    sx={{
                      width: 120,
                      height: 120,
                      border: "4px solid",
                      borderColor: "divider",
                    }}
                  />
                  <IconButton
                    onClick={handleAvatarChange}
                    sx={{
                      position: "absolute",
                      bottom: 0,
                      right: 0,
                      bgcolor: "primary.main",
                      color: "white",
                      "&:hover": { bgcolor: "primary.dark" },
                    }}
                    aria-label="change-avatar"
                  >
                    <PhotoCameraIcon />
                  </IconButton>
                </Box>
                <Box sx={{ ml: 3 }}>
                  <Typography variant="h6" fontWeight={700}>
                    {profile.name}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" mb={1}>
                    {profile.email}
                  </Typography>
                  <Button
                    variant="outlined"
                    size="small"
                    onClick={handleAvatarChange}
                    sx={{
                      textTransform: "none",
                      borderRadius: 2,
                      fontWeight: 600,
                    }}
                  >
                    Thay đổi ảnh đại diện
                  </Button>
                </Box>
              </Box>

              <Divider sx={{ mb: 3 }} />

              {/* Profile Form */}
              <Stack spacing={3}>
                <TextField
                  fullWidth
                  label="Họ và tên"
                  value={profile.name}
                  onChange={(e) => handleProfileChange("name", e.target.value)}
                  sx={{
                    "& .MuiOutlinedInput-root": { borderRadius: 3 },
                  }}
                />
                <TextField
                  fullWidth
                  label="Email"
                  type="email"
                  value={profile.email}
                  onChange={(e) => handleProfileChange("email", e.target.value)}
                  sx={{
                    "& .MuiOutlinedInput-root": { borderRadius: 3 },
                  }}
                />
                <TextField
                  fullWidth
                  label="Số điện thoại"
                  value={profile.phone}
                  onChange={(e) => handleProfileChange("phone", e.target.value)}
                  sx={{
                    "& .MuiOutlinedInput-root": { borderRadius: 3 },
                  }}
                />
                <TextField
                  fullWidth
                  label="Giới thiệu"
                  multiline
                  rows={3}
                  value={profile.bio}
                  onChange={(e) => handleProfileChange("bio", e.target.value)}
                  sx={{
                    "& .MuiOutlinedInput-root": { borderRadius: 3 },
                  }}
                />
                <TextField
                  fullWidth
                  label="Địa chỉ"
                  value={profile.location}
                  onChange={(e) => handleProfileChange("location", e.target.value)}
                  sx={{
                    "& .MuiOutlinedInput-root": { borderRadius: 3 },
                  }}
                />
                <TextField
                  fullWidth
                  label="Website"
                  value={profile.website}
                  onChange={(e) => handleProfileChange("website", e.target.value)}
                  sx={{
                    "& .MuiOutlinedInput-root": { borderRadius: 3 },
                  }}
                />
              </Stack>

              <Box sx={{ display: "flex", justifyContent: "flex-end", mt: 4, gap: 2 }}>
                <Button
                  variant="outlined"
                  sx={{
                    textTransform: "none",
                    fontWeight: 600,
                    borderRadius: 3,
                    px: 4,
                  }}
                >
                  Hủy
                </Button>
                <Button
                  variant="contained"
                  onClick={handleSaveProfile}
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
                  Lưu thay đổi
                </Button>
              </Box>
            </Card>
          )}

          {/* Tab 1: Privacy Settings */}
          {tabValue === 1 && (
            <Card
              elevation={0}
              sx={(t) => ({
                borderRadius: 4,
                p: 4,
                boxShadow: t.shadows[1],
                border: "1px solid",
                borderColor: "divider",
                bgcolor: "background.paper",
              })}
            >
              <Typography variant="h6" fontWeight={700} mb={3}>
                Quyền riêng tư
              </Typography>

              <List>
                <ListItem>
                  <ListItemText
                    primary="Hiển thị trang cá nhân"
                    secondary="Ai có thể xem trang cá nhân của bạn"
                  />
                  <ListItemSecondaryAction>
                    <FormControl sx={{ minWidth: 150 }}>
                      <Select
                        value={privacy.profileVisibility}
                        onChange={(e) => handlePrivacyChange("profileVisibility", e.target.value)}
                        size="small"
                        sx={{ borderRadius: 2 }}
                      >
                        <MenuItem value="public">Công khai</MenuItem>
                        <MenuItem value="friends">Bạn bè</MenuItem>
                        <MenuItem value="private">Riêng tư</MenuItem>
                      </Select>
                    </FormControl>
                  </ListItemSecondaryAction>
                </ListItem>
                <Divider />

                <ListItem>
                  <ListItemText
                    primary="Hiển thị email"
                    secondary="Cho phép người khác xem email của bạn"
                  />
                  <ListItemSecondaryAction>
                    <Switch
                      checked={privacy.showEmail}
                      onChange={(e) => handlePrivacyChange("showEmail", e.target.checked)}
                    />
                  </ListItemSecondaryAction>
                </ListItem>
                <Divider />

                <ListItem>
                  <ListItemText
                    primary="Hiển thị số điện thoại"
                    secondary="Cho phép người khác xem số điện thoại"
                  />
                  <ListItemSecondaryAction>
                    <Switch
                      checked={privacy.showPhone}
                      onChange={(e) => handlePrivacyChange("showPhone", e.target.checked)}
                    />
                  </ListItemSecondaryAction>
                </ListItem>
                <Divider />

                <ListItem>
                  <ListItemText
                    primary="Cho phép lời mời kết bạn"
                    secondary="Người khác có thể gửi lời mời kết bạn"
                  />
                  <ListItemSecondaryAction>
                    <Switch
                      checked={privacy.allowFriendRequests}
                      onChange={(e) => handlePrivacyChange("allowFriendRequests", e.target.checked)}
                    />
                  </ListItemSecondaryAction>
                </ListItem>
                <Divider />

                <ListItem>
                  <ListItemText
                    primary="Cho phép tin nhắn"
                    secondary="Ai có thể gửi tin nhắn cho bạn"
                  />
                  <ListItemSecondaryAction>
                    <Switch
                      checked={privacy.allowMessages}
                      onChange={(e) => handlePrivacyChange("allowMessages", e.target.checked)}
                    />
                  </ListItemSecondaryAction>
                </ListItem>
                <Divider />

                <ListItem>
                  <ListItemText
                    primary="Cho phép gắn thẻ"
                    secondary="Người khác có thể gắn thẻ bạn trong bài viết"
                  />
                  <ListItemSecondaryAction>
                    <Switch
                      checked={privacy.allowTagging}
                      onChange={(e) => handlePrivacyChange("allowTagging", e.target.checked)}
                    />
                  </ListItemSecondaryAction>
                </ListItem>
                <Divider />

                <ListItem>
                  <ListItemText
                    primary="Cho phép tìm kiếm"
                    secondary="Hồ sơ của bạn xuất hiện trong kết quả tìm kiếm"
                  />
                  <ListItemSecondaryAction>
                    <Switch
                      checked={privacy.searchable}
                      onChange={(e) => handlePrivacyChange("searchable", e.target.checked)}
                    />
                  </ListItemSecondaryAction>
                </ListItem>
              </List>

              <Box sx={{ display: "flex", justifyContent: "flex-end", mt: 4 }}>
                <Button
                  variant="contained"
                  onClick={handleSavePrivacy}
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
                  Lưu cài đặt
                </Button>
              </Box>
            </Card>
          )}

          {/* Tab 2: Notification Settings */}
          {tabValue === 2 && (
            <Card
              elevation={0}
              sx={(t) => ({
                borderRadius: 4,
                p: 4,
                boxShadow: t.shadows[1],
                border: "1px solid",
                borderColor: "divider",
                bgcolor: "background.paper",
              })}
            >
              <Typography variant="h6" fontWeight={700} mb={3}>
                Cài đặt thông báo
              </Typography>

              <List>
                <ListItem>
                  <ListItemText
                    primary="Thông báo qua Email"
                    secondary="Nhận thông báo qua email"
                  />
                  <ListItemSecondaryAction>
                    <Switch
                      checked={notifications.emailNotifications}
                      onChange={(e) => handleNotificationChange("emailNotifications", e.target.checked)}
                    />
                  </ListItemSecondaryAction>
                </ListItem>
                <Divider />

                <ListItem>
                  <ListItemText
                    primary="Thông báo đẩy"
                    secondary="Nhận thông báo đẩy trên trình duyệt"
                  />
                  <ListItemSecondaryAction>
                    <Switch
                      checked={notifications.pushNotifications}
                      onChange={(e) => handleNotificationChange("pushNotifications", e.target.checked)}
                    />
                  </ListItemSecondaryAction>
                </ListItem>
                <Divider />

                <ListItem>
                  <ListItemText
                    primary="Lời mời kết bạn"
                    secondary="Thông báo khi có lời mời kết bạn mới"
                  />
                  <ListItemSecondaryAction>
                    <Switch
                      checked={notifications.friendRequests}
                      onChange={(e) => handleNotificationChange("friendRequests", e.target.checked)}
                    />
                  </ListItemSecondaryAction>
                </ListItem>
                <Divider />

                <ListItem>
                  <ListItemText
                    primary="Bình luận"
                    secondary="Thông báo khi có người bình luận bài viết"
                  />
                  <ListItemSecondaryAction>
                    <Switch
                      checked={notifications.comments}
                      onChange={(e) => handleNotificationChange("comments", e.target.checked)}
                    />
                  </ListItemSecondaryAction>
                </ListItem>
                <Divider />

                <ListItem>
                  <ListItemText
                    primary="Lượt thích"
                    secondary="Nhận thông báo khi có người thích bài viết"
                  />
                  <ListItemSecondaryAction>
                    <Switch
                      checked={notifications.likes}
                      onChange={(e) => handleNotificationChange("likes", e.target.checked)}
                    />
                  </ListItemSecondaryAction>
                </ListItem>
                <Divider />

                <ListItem>
                  <ListItemText
                    primary="Tin nhắn"
                    secondary="Thông báo khi có tin nhắn mới"
                  />
                  <ListItemSecondaryAction>
                    <Switch
                      checked={notifications.messages}
                      onChange={(e) => handleNotificationChange("messages", e.target.checked)}
                    />
                  </ListItemSecondaryAction>
                </ListItem>
                <Divider />

                <ListItem>
                  <ListItemText
                    primary="Lời mời nhóm"
                    secondary="Thông báo khi được mời vào nhóm"
                  />
                  <ListItemSecondaryAction>
                    <Switch
                      checked={notifications.groupInvites}
                      onChange={(e) => handleNotificationChange("groupInvites", e.target.checked)}
                    />
                  </ListItemSecondaryAction>
                </ListItem>
              </List>

              <Box sx={{ display: "flex", justifyContent: "flex-end", mt: 4 }}>
                <Button
                  variant="contained"
                  onClick={handleSaveNotifications}
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
                  Lưu cài đặt
                </Button>
              </Box>
            </Card>
          )}

          {/* Tab 3: Security Settings */}
          {tabValue === 3 && (
            <Card
              elevation={0}
              sx={(t) => ({
                borderRadius: 4,
                p: 4,
                boxShadow: t.shadows[1],
                border: "1px solid",
                borderColor: "divider",
                bgcolor: "background.paper",
              })}
            >
              <Typography variant="h6" fontWeight={700} mb={3}>
                Bảo mật
              </Typography>

              <List>
                <ListItem>
                  <ListItemText
                    primary="Xác thực hai yếu tố"
                    secondary="Thêm lớp bảo mật cho tài khoản của bạn"
                  />
                  <ListItemSecondaryAction>
                    <Switch
                      checked={security.twoFactorAuth}
                      onChange={(e) => handleSecurityChange("twoFactorAuth", e.target.checked)}
                    />
                  </ListItemSecondaryAction>
                </ListItem>
                <Divider />

                <ListItem>
                  <ListItemText
                    primary="Cảnh báo đăng nhập"
                    secondary="Nhận thông báo khi có đăng nhập từ thiết bị mới"
                  />
                  <ListItemSecondaryAction>
                    <Switch
                      checked={security.loginAlerts}
                      onChange={(e) => handleSecurityChange("loginAlerts", e.target.checked)}
                    />
                  </ListItemSecondaryAction>
                </ListItem>
                <Divider />

                <ListItem>
                  <ListItemText
                    primary="Hiển thị trạng thái hoạt động"
                    secondary="Người khác có thể thấy bạn đang online"
                  />
                  <ListItemSecondaryAction>
                    <Switch
                      checked={security.showActiveStatus}
                      onChange={(e) => handleSecurityChange("showActiveStatus", e.target.checked)}
                    />
                  </ListItemSecondaryAction>
                </ListItem>
              </List>

              <Divider sx={{ my: 3 }} />

              <Alert severity="info" sx={{ mb: 3, borderRadius: 3 }}>
                <Typography variant="body2" fontWeight={600} mb={1}>
                  Đổi mật khẩu
                </Typography>
                <Typography variant="body2" color="text.secondary" mb={2}>
                  Để bảo vệ tài khoản, bạn nên đổi mật khẩu định kỳ
                </Typography>
                <Button
                  variant="outlined"
                  size="small"
                  onClick={handleChangePassword}
                  sx={{
                    textTransform: "none",
                    fontWeight: 600,
                    borderRadius: 2,
                  }}
                >
                  Đổi mật khẩu
                </Button>
              </Alert>

              <Alert severity="warning" sx={{ mb: 3, borderRadius: 3 }}>
                <Typography variant="body2" fontWeight={600} mb={1}>
                  Phiên đăng nhập hoạt động
                </Typography>
                <Typography variant="body2" color="text.secondary" mb={2}>
                  Bạn đang đăng nhập trên 3 thiết bị
                </Typography>
                <Stack spacing={1}>
                  <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                    <Typography variant="body2">
                      Chrome trên Windows • Hà Nội
                    </Typography>
                    <Chip label="Hiện tại" size="small" color="success" />
                  </Box>
                  <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                    <Typography variant="body2">
                      Safari trên iPhone • TP. Hồ Chí Minh
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      2 giờ trước
                    </Typography>
                  </Box>
                  <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                    <Typography variant="body2">
                      Firefox trên MacOS • Đà Nẵng
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      1 ngày trước
                    </Typography>
                  </Box>
                </Stack>
                <Button
                  variant="outlined"
                  size="small"
                  color="error"
                  sx={{
                    textTransform: "none",
                    fontWeight: 600,
                    borderRadius: 2,
                    mt: 2,
                  }}
                >
                  Đăng xuất tất cả thiết bị khác
                </Button>
              </Alert>

              <Alert severity="error" sx={{ borderRadius: 3 }}>
                <Typography variant="body2" fontWeight={600} mb={1}>
                  Xóa tài khoản
                </Typography>
                <Typography variant="body2" color="text.secondary" mb={2}>
                  Hành động này không thể hoàn tác. Tất cả dữ liệu của bạn sẽ bị xóa vĩnh viễn.
                </Typography>
                <Button
                  variant="outlined"
                  size="small"
                  color="error"
                  startIcon={<DeleteIcon />}
                  onClick={() => setDeleteDialogOpen(true)}
                  sx={{
                    textTransform: "none",
                    fontWeight: 600,
                    borderRadius: 2,
                  }}
                >
                  Xóa tài khoản
                </Button>
              </Alert>

              <Box sx={{ display: "flex", justifyContent: "flex-end", mt: 4 }}>
                <Button
                  variant="contained"
                  onClick={handleSaveSecurity}
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
                  Lưu cài đặt
                </Button>
              </Box>
            </Card>
          )}
        </Box>
      </Box>

      {/* Delete Account Dialog */}
      <Dialog
        open={deleteDialogOpen}
        onClose={() => setDeleteDialogOpen(false)}
        PaperProps={{
          sx: { borderRadius: 4, maxWidth: 500 },
        }}
      >
        <DialogTitle sx={{ fontWeight: 700 }}>Xác nhận xóa tài khoản</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Bạn có chắc chắn muốn xóa tài khoản? Hành động này không thể hoàn tác và tất cả dữ liệu của bạn sẽ bị xóa vĩnh viễn.
          </DialogContentText>
        </DialogContent>
        <DialogActions sx={{ p: 3, pt: 0 }}>
          <Button
            onClick={() => setDeleteDialogOpen(false)}
            sx={{
              textTransform: "none",
              fontWeight: 600,
              borderRadius: 2,
            }}
          >
            Hủy
          </Button>
          <Button
            onClick={handleDeleteAccount}
            color="error"
            variant="contained"
            sx={{
              textTransform: "none",
              fontWeight: 600,
              borderRadius: 2,
            }}
          >
            Xóa tài khoản
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
          sx={{ borderRadius: 2, minWidth: 240, boxShadow: 3 }}
          action={
            <IconButton
              size="small"
              aria-label="close"
              color="inherit"
              onClick={handleCloseSnackbar}
            >
              <CloseIcon fontSize="small" />
            </IconButton>
          }
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Scene>
  );
}
