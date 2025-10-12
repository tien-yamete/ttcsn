// src/pages/Scene.jsx
import * as React from "react";
import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Drawer from "@mui/material/Drawer";
import IconButton from "@mui/material/IconButton";
import MenuIcon from "@mui/icons-material/Menu";
import Toolbar from "@mui/material/Toolbar";
import { alpha, useTheme } from "@mui/material/styles";
import Header from "../components/Header";
import SideMenu from "../components/SideMenu";
import { ColorModeContext } from "../App";

const drawerWidth = 300;

function Scene({ children }) {
  const [mobileOpen, setMobileOpen] = React.useState(false);
  const [isClosing, setIsClosing] = React.useState(false);
  const theme = useTheme();

  // Lấy mode + toggle từ App (để truyền vào Header y như chức năng cũ)
  const { mode, toggle } = React.useContext(ColorModeContext);

  const handleDrawerClose = () => {
    setIsClosing(true);
    setMobileOpen(false);
  };
  const handleDrawerTransitionEnd = () => setIsClosing(false);
  const handleDrawerToggle = () => {
    if (!isClosing) setMobileOpen((prev) => !prev);
  };

  return (
    <Box sx={{ display: "flex", flexDirection: "column", bgcolor: "background.default", color: "text.primary", minHeight: "100vh" }}>
      {/* AppBar như code cũ nhưng theme-aware + full breadth theo layout 2 cột */}
      <AppBar
        position="fixed"
        elevation={0}
        sx={{
          ml: { sm: `${drawerWidth}px` },                        // chừa chỗ cho permanent drawer trên sm+
          zIndex: theme.zIndex.drawer + 1,                       // nổi hơn drawer
          bgcolor:
            theme.palette.mode === "dark"
              ? alpha(theme.palette.grey[900], 0.85)             // xám đậm mờ, không đen hẳn
              : alpha(theme.palette.background.paper, 0.9),
          backdropFilter: "saturate(180%) blur(10px)",
          borderBottom: "1px solid",
          borderColor: "divider",
          color: "inherit",
        }}
      >
        <Toolbar sx={{ minHeight: 64 }}>
          {/* Nút mở slide menu (mobile) */}
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mr: 2, display: { sm: "none" }, "&:hover": { bgcolor: "action.hover" } }}
          >
            <MenuIcon />
          </IconButton>

          {/* Header cũ của bạn: giữ nguyên chức năng, chỉ nhận theme từ App */}
          <Header isDarkMode={mode === "dark"} onToggleTheme={toggle} />
        </Toolbar>
      </AppBar>

      {/* Khối chứa sidebar + main như code cũ */}
      <Box sx={{ display: "flex", flexDirection: "row" }}>
        {/* NAV: Drawer trái */}
        <Box component="nav" sx={{ width: { sm: drawerWidth }, flexShrink: { sm: 0 } }} aria-label="side menu">
          {/* Mobile: temporary (slide) */}
          <Drawer
            variant="temporary"
            open={mobileOpen}
            onTransitionEnd={handleDrawerTransitionEnd}
            onClose={handleDrawerClose}
            ModalProps={{ keepMounted: true }}
            sx={{
              display: { xs: "block", sm: "none" },
              "& .MuiDrawer-paper": {
                boxSizing: "border-box",
                width: drawerWidth,
                bgcolor: "background.paper",
                borderRight: "1px solid",
                borderColor: "divider",
              },
            }}
          >
            <SideMenu />
          </Drawer>

          {/* Desktop: permanent */}
          <Drawer
            variant="permanent"
            sx={{
              display: { xs: "none", sm: "block" },
              "& .MuiDrawer-paper": {
                boxSizing: "border-box",
                width: drawerWidth,
                bgcolor: "background.paper",
                borderRight: "1px solid",
                borderColor: "divider",
              },
            }}
            open
          >
            <SideMenu />
          </Drawer>
        </Box>

        {/* MAIN */}
        <Box
          component="main"
          sx={{
            flexGrow: 1,
            width: { sm: `calc(100% - ${drawerWidth}px)` },
          }}
        >
          {/* Khoảng trống bù cho AppBar (giống code cũ) */}
          <Toolbar />
          <Box sx={{ display: "flex", justifyContent: "center", width: "100%", height: "100%", px: { xs: 1.5, md: 3 }, py: { xs: 2, md: 3 } }}>
            {children}
          </Box>
        </Box>
      </Box>
    </Box>
  );
}

export default Scene;
