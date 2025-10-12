// src/components/Header.jsx
import * as React from "react";
import { styled, alpha } from "@mui/material/styles";
import {
  AppBar, Toolbar, Box, Avatar, IconButton, InputBase, Badge,
  MenuItem, Menu, Divider, Button, Switch, Typography
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import MailIcon from "@mui/icons-material/Mail";
import NotificationsIcon from "@mui/icons-material/Notifications";
import MoreIcon from "@mui/icons-material/MoreVert";
import SettingsOutlined from "@mui/icons-material/SettingsOutlined";
import LogoutOutlined from "@mui/icons-material/LogoutOutlined";
import WbSunnyOutlined from "@mui/icons-material/WbSunnyOutlined";
import DarkModeOutlined from "@mui/icons-material/DarkModeOutlined";
import { isAuthenticated, logOut } from "../services/authenticationService";

const SearchRoot = styled("div")(({ theme }) => {
  const isDark = theme.palette.mode === "dark";
  const baseBg = isDark ? alpha(theme.palette.common.white, 0.10) : alpha(theme.palette.common.black, 0.04);
  const hoverBg = isDark ? alpha(theme.palette.common.white, 0.16) : alpha(theme.palette.common.black, 0.06);
  const focusBg = isDark ? alpha(theme.palette.common.white, 0.20) : alpha(theme.palette.common.black, 0.08);
  return {
    position: "relative",
    borderRadius: 24,
    backgroundColor: baseBg,
    border: `1px solid ${theme.palette.divider}`,
    transition: "all .25s ease",
    "&:hover": { backgroundColor: hoverBg },
    "&:focus-within": {
      backgroundColor: focusBg,
      boxShadow: `0 0 0 3px ${alpha(theme.palette.primary.main, 0.12)}`,
      borderColor: alpha(theme.palette.primary.main, 0.35),
    },
    marginLeft: theme.spacing(2),
    width: "100%",
    [theme.breakpoints.up("sm")]: { width: "auto" },
  };
});

const SearchIconWrapper = styled("div")(({ theme }) => ({
  padding: theme.spacing(0, 2),
  height: "100%",
  position: "absolute",
  pointerEvents: "none",
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
}));

const StyledInputBase = styled(InputBase)(({ theme }) => ({
  color: "inherit",
  "& .MuiInputBase-input": {
    padding: theme.spacing(1.1, 1.1, 1.1, 0),
    paddingLeft: `calc(1em + ${theme.spacing(4)})`,
    width: "100%",
    [theme.breakpoints.up("md")]: { width: "22ch" },
  },
}));

export default function Header({
  user = { name: "Tạ Văn Tiến", title: "Web Developer", avatar: "/avatar.png" },
  onToggleTheme = () => {},
  isDarkMode = false,
}) {
  const [anchorEl, setAnchorEl] = React.useState(null);
  const [mobileMoreAnchorEl, setMobileMoreAnchorEl] = React.useState(null);
  const isMenuOpen = Boolean(anchorEl);
  const isMobileMenuOpen = Boolean(mobileMoreAnchorEl);

  const handleProfileMenuOpen = (e) => setAnchorEl(e.currentTarget);
  const handleMobileMenuOpen = (e) => setMobileMoreAnchorEl(e.currentTarget);
  const handleMobileMenuClose = () => setMobileMoreAnchorEl(null);
  const handleMenuClose = () => { setAnchorEl(null); handleMobileMenuClose(); };

  const handleOpenProfile = () => { handleMenuClose(); window.location.href = "/profile"; };
  const handleLogout = () => { handleMenuClose(); logOut(); window.location.href = "/login"; };

  const menuId = "primary-profile-menu";
  const mobileMenuId = "primary-profile-menu-mobile";

  const ProfileCardMenu = (
    <Menu
      id={menuId}
      anchorEl={anchorEl}
      open={isMenuOpen}
      onClose={handleMenuClose}
      anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
      transformOrigin={{ vertical: "top", horizontal: "right" }}
      PaperProps={{
        elevation: 0,
        sx: (t) => ({
          mt: 1.5,
          borderRadius: 3,
          minWidth: 300,
          border: `1px solid ${t.palette.divider}`,
          boxShadow: "0 8px 28px rgba(0,0,0,0.12)",
          overflow: "visible",
          p: 1.5,
          "&:before": {
            content: '""', position: "absolute", top: 0, right: 18,
            width: 12, height: 12, bgcolor: "background.paper",
            transform: "translateY(-50%) rotate(45deg)",
            borderLeft: `1px solid ${t.palette.divider}`,
            borderTop: `1px solid ${t.palette.divider}`,
          },
        }),
      }}
    >
      {isAuthenticated() ? (
        <>
          <Box sx={{ display: "flex", alignItems: "center", gap: 1.5, px: 0.5, mb: 1 }}>
            <Avatar src={user.avatar} alt={user.name} sx={{ width: 40, height: 40 }} />
            <Box sx={{ minWidth: 0 }}>
              <Typography noWrap sx={{ fontWeight: 700, fontSize: 16 }}>{user.name}</Typography>
              <Typography noWrap sx={{ color: "text.secondary", fontSize: 13 }}>{user.title}</Typography>
            </Box>
            <Box sx={{ ml: "auto" }}>
              <Button size="small" onClick={handleOpenProfile} variant="contained" disableElevation
                sx={{ textTransform: "none", borderRadius: 2, px: 1.2, py: 0.5 }}>
                View profile
              </Button>
            </Box>
          </Box>

          <Divider sx={{ my: 1 }} />

          <MenuItem
            onClick={() => { handleMenuClose(); window.location.href = "/settings"; }}
            sx={{ py: 1.2, borderRadius: 2, mx: 0.5, "&:hover": { backgroundColor: "action.hover" } }}
          >
            <SettingsOutlined sx={{ mr: 1 }} fontSize="small" />
            <Typography sx={{ fontSize: 14, fontWeight: 500 }}>Settings & Privacy</Typography>
          </MenuItem>

          <MenuItem
            onClick={handleLogout}
            sx={{
              py: 1.2, borderRadius: 2, mx: 0.5, color: "error.main",
              "&:hover": { backgroundColor: (t) => alpha(t.palette.error.main, 0.08) },
            }}
          >
            <LogoutOutlined sx={{ mr: 1 }} fontSize="small" />
            <Typography sx={{ fontSize: 14, fontWeight: 600 }}>Sign Out</Typography>
          </MenuItem>

          <Divider sx={{ my: 1 }} />

          <Box sx={{ display: "flex", alignItems: "center", gap: 1.5, px: 1, py: 0.5 }}>
            <WbSunnyOutlined fontSize="small" />
            <Typography sx={{ fontSize: 14, fontWeight: 500, flex: 1 }}>Dark mode</Typography>
            <DarkModeOutlined fontSize="small" />
            <Switch edge="end" checked={isDarkMode} onChange={(e) => onToggleTheme?.(e.target.checked)} />
          </Box>
        </>
      ) : (
        <Box sx={{ p: 1 }}>
          <Button fullWidth variant="contained" onClick={() => (window.location.href = "/login")}
            sx={{ textTransform: "none", borderRadius: 2 }}>
            Login
          </Button>
        </Box>
      )}
    </Menu>
  );

  const MobileMenu = (
    <Menu
      id={mobileMenuId}
      anchorEl={mobileMoreAnchorEl}
      open={isMobileMenuOpen}
      onClose={handleMobileMenuClose}
      anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
      transformOrigin={{ vertical: "top", horizontal: "right" }}
      PaperProps={{
        sx: (t) => ({
          mt: 1.5,
          borderRadius: 3,
          border: `1px solid ${t.palette.divider}`,
          boxShadow: "0 8px 28px rgba(0,0,0,0.12)",
        }),
      }}
    >
      {isAuthenticated() ? (
        <>
          <MenuItem sx={{ py: 1.2 }}>
            <IconButton size="large" color="inherit" sx={{ "&:hover": { backgroundColor: "action.hover" } }}>
              <Badge badgeContent={2} color="error"><MailIcon /></Badge>
            </IconButton>
            <Typography sx={{ ml: 1, fontSize: 14, fontWeight: 500 }}>Messages</Typography>
          </MenuItem>
          <MenuItem sx={{ py: 1.2 }}>
            <IconButton size="large" color="inherit" sx={{ "&:hover": { backgroundColor: "action.hover" } }}>
              <Badge badgeContent={4} color="error"><NotificationsIcon /></Badge>
            </IconButton>
            <Typography sx={{ ml: 1, fontSize: 14, fontWeight: 500 }}>Notifications</Typography>
          </MenuItem>
          <MenuItem onClick={(e) => { handleMobileMenuClose(); handleProfileMenuOpen(e); }} sx={{ py: 1.2 }}>
            <Avatar src={user.avatar} sx={{ width: 28, height: 28 }} />
            <Typography sx={{ ml: 1, fontSize: 14, fontWeight: 500 }}>Profile</Typography>
          </MenuItem>
        </>
      ) : (
        <MenuItem onClick={() => { handleMobileMenuClose(); window.location.href = "/login"; }} sx={{ py: 1.2 }}>
          <Avatar sx={{ width: 28, height: 28 }} />
          <Typography sx={{ ml: 1, fontSize: 14, fontWeight: 500 }}>Login</Typography>
        </MenuItem>
      )}
    </Menu>
  );

  return (
    <AppBar
      position="fixed"
      elevation={0}
      color="transparent"
      sx={(t) => ({
        // full-bleed: thoát khỏi mọi Container/maxWidth
        width: "100vw",
        left: 0, right: 0,
        ml: "calc(50% - 50vw)",
        mr: "calc(50% - 50vw)",
        // nền xám đậm mờ ở dark, paper mờ ở light
        bgcolor: t.palette.mode === "dark"
          ? alpha(t.palette.grey[900], 0.85)
          : alpha(t.palette.background.paper, 0.9),
        backdropFilter: "saturate(180%) blur(10px)",
        borderBottom: "1px solid",
        borderColor: "divider",
        zIndex: t.zIndex.appBar,
      })}
    >
      <Toolbar sx={{ minHeight: 64, px: { xs: 1.5, md: 3 } }}>
        {/* Logo */}
        <IconButton
          size="large"
          edge="start"
          color="inherit"
          aria-label="logo"
          onClick={() => (window.location.href = "/")}
          sx={{ "&:hover": { backgroundColor: "action.hover" } }}
        >
          <Box component="img" src="/logo/logo.png" alt="logo" sx={{ width: 42, height: 42, borderRadius: 1.25 }} />
        </IconButton>

        {/* Search */}
        <SearchRoot>
          <SearchIconWrapper><SearchIcon /></SearchIconWrapper>
          <StyledInputBase placeholder="Tìm kiếm ..." inputProps={{ "aria-label": "search" }} />
        </SearchRoot>

        <Box sx={{ flexGrow: 1 }} />

        {/* Desktop actions */}
        <Box sx={{ display: { xs: "none", md: "flex" }, alignItems: "center", gap: 0.5 }}>
          {isAuthenticated() ? (
            <>
              <IconButton size="large" aria-label="mails" color="inherit" sx={{ "&:hover": { backgroundColor: "action.hover" } }}>
                <Badge badgeContent={4} color="error"><MailIcon /></Badge>
              </IconButton>
              <IconButton size="large" aria-label="notifications" color="inherit" sx={{ "&:hover": { backgroundColor: "action.hover" } }}>
                <Badge badgeContent={17} color="error"><NotificationsIcon /></Badge>
              </IconButton>
              <IconButton
                size="large"
                edge="end"
                aria-label="account of current user"
                aria-controls={menuId}
                aria-haspopup="true"
                onClick={handleProfileMenuOpen}
                color="inherit"
                sx={{ "&:hover": { backgroundColor: "action.hover" } }}
              >
                <Avatar src={user.avatar} alt={user.name} sx={{ width: 34, height: 34 }} />
              </IconButton>
            </>
          ) : (
            <Button
              onClick={() => (window.location.href = "/login")}
              variant="outlined"
              sx={(t) => ({
                borderColor: alpha(t.palette.common.white, t.palette.mode === "dark" ? 0.35 : 0.45),
                color: "inherit",
                textTransform: "none",
                borderRadius: 2,
                px: 1.5,
                "&:hover": {
                  backgroundColor: "action.hover",
                  borderColor: alpha(t.palette.common.white, t.palette.mode === "dark" ? 0.55 : 0.65),
                },
              })}
            >
              Login
            </Button>
          )}
        </Box>

        {/* Mobile more */}
        <Box sx={{ display: { xs: "flex", md: "none" } }}>
          <IconButton
            size="large"
            aria-label="show more"
            aria-controls={mobileMenuId}
            aria-haspopup="true"
            onClick={handleMobileMenuOpen}
            color="inherit"
            sx={{ "&:hover": { backgroundColor: "action.hover" } }}
          >
            <MoreIcon />
          </IconButton>
        </Box>
      </Toolbar>

      {MobileMenu}
      {ProfileCardMenu}
    </AppBar>
  );
}
