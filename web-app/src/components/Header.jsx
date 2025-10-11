import * as React from "react";
import { styled, alpha } from "@mui/material/styles";
import Box from "@mui/material/Box";
import IconButton from "@mui/material/IconButton";
import InputBase from "@mui/material/InputBase";
import Badge from "@mui/material/Badge";
import MenuItem from "@mui/material/MenuItem";
import Menu from "@mui/material/Menu";
import SearchIcon from "@mui/icons-material/Search";
import AccountCircle from "@mui/icons-material/AccountCircle";
import MailIcon from "@mui/icons-material/Mail";
import NotificationsIcon from "@mui/icons-material/Notifications";
import MoreIcon from "@mui/icons-material/MoreVert";
import { isAuthenticated, logOut } from "../services/authenticationService";

const Search = styled("div")(({ theme }) => ({
  position: "relative",
  borderRadius: 24,
  backgroundColor: alpha(theme.palette.common.white, 0.15),
  border: "1px solid rgba(255, 255, 255, 0.2)",
  "&:hover": {
    backgroundColor: alpha(theme.palette.common.white, 0.25),
    borderColor: "rgba(255, 255, 255, 0.3)",
  },
  "&:focus-within": {
    backgroundColor: alpha(theme.palette.common.white, 0.3),
    borderColor: "rgba(255, 255, 255, 0.5)",
    boxShadow: "0 0 0 3px rgba(255, 255, 255, 0.1)"
  },
  marginRight: theme.spacing(2),
  marginLeft: 0,
  width: "100%",
  transition: "all 0.3s ease",
  [theme.breakpoints.up("sm")]: {
    marginLeft: theme.spacing(3),
    width: "auto",
  },
}));

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
    padding: theme.spacing(1.2, 1.2, 1.2, 0),
    paddingLeft: `calc(1em + ${theme.spacing(4)})`,
    transition: theme.transitions.create("width"),
    width: "100%",
    [theme.breakpoints.up("md")]: {
      width: "20ch",
    },
  },
}));

export default function Header() {
  const [anchorEl, setAnchorEl] = React.useState(null);
  const [mobileMoreAnchorEl, setMobileMoreAnchorEl] = React.useState(null);

  const isMenuOpen = Boolean(anchorEl);
  const isMobileMenuOpen = Boolean(mobileMoreAnchorEl);

  const handleProfileMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMobileMenuClose = () => {
    setMobileMoreAnchorEl(null);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    handleMobileMenuClose();
  };

  const handleOpenProfile = () => {
    setAnchorEl(null);
    window.location.href = "/profile";
  };

  const handleMobileMenuOpen = (event) => {
    setMobileMoreAnchorEl(event.currentTarget);
  };

  const handleLogout = (event) => {
    handleMenuClose();
    logOut();
    window.location.href = "/login";
  };

  const menuId = "primary-search-account-menu";
  const renderMenu = (
    <Menu
      anchorEl={anchorEl}
      anchorOrigin={{
        vertical: "top",
        horizontal: "right",
      }}
      id={menuId}
      keepMounted
      transformOrigin={{
        vertical: "top",
        horizontal: "right",
      }}
      open={isMenuOpen}
      onClose={handleMenuClose}
      PaperProps={{
        elevation: 0,
        sx: {
          mt: 1.5,
          borderRadius: 3,
          boxShadow: "0 4px 20px rgba(0,0,0,0.1)",
          minWidth: 200,
          border: "1px solid #e8e8e8",
          overflow: 'visible',
          '&:before': {
            content: '""',
            display: 'block',
            position: 'absolute',
            top: 0,
            right: 14,
            width: 10,
            height: 10,
            bgcolor: 'background.paper',
            transform: 'translateY(-50%) rotate(45deg)',
            zIndex: 0,
            borderLeft: "1px solid #e8e8e8",
            borderTop: "1px solid #e8e8e8",
          },
        }
      }}
    >
      {isAuthenticated() ? (
        <>
          <MenuItem 
            onClick={handleOpenProfile}
            sx={{ 
              py: 1.5,
              px: 2.5,
              fontSize: 14,
              fontWeight: 500,
              borderRadius: 2,
              mx: 1,
              my: 0.5,
              "&:hover": { 
                backgroundColor: "rgba(102, 126, 234, 0.08)",
                color: "#667eea"
              }
            }}
          >
            Profile
          </MenuItem>
          <MenuItem 
            onClick={handleMenuClose}
            sx={{ 
              py: 1.5,
              px: 2.5,
              fontSize: 14,
              fontWeight: 500,
              borderRadius: 2,
              mx: 1,
              my: 0.5,
              "&:hover": { 
                backgroundColor: "rgba(102, 126, 234, 0.08)",
                color: "#667eea"
              }
            }}
          >
            Settings
          </MenuItem>
          <MenuItem 
            onClick={handleLogout}
            sx={{ 
              py: 1.5,
              px: 2.5,
              fontSize: 14,
              fontWeight: 500,
              borderRadius: 2,
              mx: 1,
              my: 0.5,
              color: "error.main",
              "&:hover": { 
                backgroundColor: "rgba(211, 47, 47, 0.08)"
              }
            }}
          >
            Log Out
          </MenuItem>
        </>
      ) : (
        <MenuItem
          onClick={() => {
            handleMenuClose();
            window.location.href = "/login";
          }}
          sx={{ 
            py: 1.5,
            px: 2.5,
            fontSize: 14,
            fontWeight: 500,
            borderRadius: 2,
            mx: 1,
            my: 0.5,
            "&:hover": { 
              backgroundColor: "rgba(102, 126, 234, 0.08)",
              color: "#667eea"
            }
          }}
        >
          Login
        </MenuItem>
      )}
    </Menu>
  );

  const mobileMenuId = "primary-search-account-menu-mobile";
  const renderMobileMenu = (
    <Menu
      anchorEl={mobileMoreAnchorEl}
      anchorOrigin={{
        vertical: "top",
        horizontal: "right",
      }}
      id={mobileMenuId}
      keepMounted
      transformOrigin={{
        vertical: "top",
        horizontal: "right",
      }}
      open={isMobileMenuOpen}
      onClose={handleMobileMenuClose}
      PaperProps={{
        sx: {
          mt: 1.5,
          borderRadius: 3,
          boxShadow: "0 4px 20px rgba(0,0,0,0.1)",
          border: "1px solid #e8e8e8"
        }
      }}
    >
      {isAuthenticated() ? (
        <>
          <MenuItem sx={{ py: 1.5, px: 2 }}>
            <IconButton size="large" aria-label="show 2 new mails" color="inherit">
              <Badge badgeContent={2} color="error">
                <MailIcon />
              </Badge>
            </IconButton>
            <p style={{ margin: 0, marginLeft: 12, fontSize: 14, fontWeight: 500 }}>Messages</p>
          </MenuItem>
          <MenuItem sx={{ py: 1.5, px: 2 }}>
            <IconButton size="large" aria-label="show 4 new notifications" color="inherit">
              <Badge badgeContent={4} color="error">
                <NotificationsIcon />
              </Badge>
            </IconButton>
            <p style={{ margin: 0, marginLeft: 12, fontSize: 14, fontWeight: 500 }}>Notifications</p>
          </MenuItem>
          <MenuItem onClick={handleProfileMenuOpen} sx={{ py: 1.5, px: 2 }}>
            <IconButton
              size="large"
              aria-label="account of current user"
              aria-controls="primary-search-account-menu"
              aria-haspopup="true"
              color="inherit"
            >
              <AccountCircle />
            </IconButton>
            <p style={{ margin: 0, marginLeft: 12, fontSize: 14, fontWeight: 500 }}>Profile</p>
          </MenuItem>
        </>
      ) : (
        <MenuItem
          onClick={() => {
            handleMobileMenuClose();
            window.location.href = "/login";
          }}
          sx={{ py: 1.5, px: 2 }}
        >
          <IconButton size="large" color="inherit">
            <AccountCircle />
          </IconButton>
          <p style={{ margin: 0, marginLeft: 12, fontSize: 14, fontWeight: 500 }}>Login</p>
        </MenuItem>
      )}
    </Menu>
  );

  return (
    <>
      <IconButton
        size="large"
        edge="start"
        color="inherit"
        aria-label="open drawer"
        onClick={() => (window.location.href = "/")}
      >
        <Box
          component={"img"}
          style={{
            width: "50px",
            height: "50px",
            borderRadius: 8,
          }}
          src="/logo/logo-white.png"
        ></Box>
      </IconButton>
      <Search>
        <SearchIconWrapper>
          <SearchIcon />
        </SearchIconWrapper>
        <StyledInputBase
          placeholder="Search…"
          inputProps={{ "aria-label": "search" }}
        />
      </Search>
      <Box sx={{ flexGrow: 1 }} />
      {/* Desktop */}
      <Box sx={{ display: { xs: "none", md: "flex" }, gap: 0.5 }}>
        {isAuthenticated() ? (
          <>
            <IconButton 
              size="large" 
              aria-label="show 4 new mails" 
              color="inherit"
              sx={{
                transition: "all 0.3s ease",
                "&:hover": {
                  backgroundColor: "rgba(255, 255, 255, 0.15)",
                  transform: "scale(1.1)"
                }
              }}
            >
              <Badge badgeContent={4} color="error">
                <MailIcon />
              </Badge>
            </IconButton>
            <IconButton
              size="large"
              aria-label="show 17 new notifications"
              color="inherit"
              sx={{
                transition: "all 0.3s ease",
                "&:hover": {
                  backgroundColor: "rgba(255, 255, 255, 0.15)",
                  transform: "scale(1.1)"
                }
              }}
            >
              <Badge badgeContent={17} color="error">
                <NotificationsIcon />
              </Badge>
            </IconButton>
            <IconButton
              size="large"
              edge="end"
              aria-label="account of current user"
              aria-controls={menuId}
              aria-haspopup="true"
              onClick={handleProfileMenuOpen}
              color="inherit"
              sx={{
                transition: "all 0.3s ease",
                "&:hover": {
                  backgroundColor: "rgba(255, 255, 255, 0.15)",
                  transform: "scale(1.1)"
                }
              }}
            >
              <AccountCircle />
            </IconButton>
          </>
        ) : (
          <IconButton
            size="large"
            edge="end"
            color="inherit"
            onClick={() => (window.location.href = "/login")}
            sx={{
              transition: "all 0.3s ease",
              "&:hover": {
                backgroundColor: "rgba(255, 255, 255, 0.15)"
              }
            }}
          >
            <AccountCircle />
            <span style={{ marginLeft: 6, fontSize: 14, fontWeight: 500 }}>Login</span>
          </IconButton>
        )}
      </Box>

      <Box sx={{ display: { xs: "flex", md: "none" } }}>
        <IconButton
          size="large"
          aria-label="show more"
          aria-controls={mobileMenuId}
          aria-haspopup="true"
          onClick={handleMobileMenuOpen}
          color="inherit"
        >
          <MoreIcon />
        </IconButton>
      </Box>
      {renderMobileMenu}
      {renderMenu}
    </>
  );
}