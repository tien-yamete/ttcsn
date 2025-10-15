// src/pages/Scene.jsx  (CHỈNH SỬA)
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
    <Box
      sx={{
        display: "flex",
        flexDirection: "column",
        bgcolor: "background.default",
        color: "text.primary",
        height: "100vh", // đảm bảo full viewport
        overflow: "hidden", // ngăn scroll ngoài
      }}
    >
      <AppBar
        position="fixed"
        elevation={0}
        sx={{
          ml: { sm: `${drawerWidth}px` },
          zIndex: theme.zIndex.drawer + 1,
          bgcolor:
            theme.palette.mode === "dark"
              ? alpha(theme.palette.grey[900], 0.85)
              : alpha(theme.palette.background.paper, 0.9),
          backdropFilter: "saturate(180%) blur(10px)",
          borderBottom: "1px solid",
          borderColor: "divider",
          color: "inherit",
        }}
      >
        <Toolbar sx={{ minHeight: 64 }}>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mr: 2, display: { sm: "none" }, "&:hover": { bgcolor: "action.hover" } }}
          >
            <MenuIcon />
          </IconButton>

          <Header isDarkMode={mode === "dark"} onToggleTheme={toggle} />
        </Toolbar>
      </AppBar>

      <Box sx={{ display: "flex", flexDirection: "row", flex: "1 1 auto", minHeight: 0 }}>
        <Box component="nav" sx={{ width: { sm: drawerWidth }, flexShrink: { sm: 0 } }} aria-label="side menu">
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
            flex: "1 1 auto",
            display: "flex",
            flexDirection: "column",
            width: { sm: `calc(100% - ${drawerWidth}px)` },
            minHeight: 0, // critical: allow children with overflow to shrink
          }}
        >
          {/* Spacer cho AppBar */}
          <Toolbar />

          {/* Wrapper chính chứa page content */}
          <Box
            sx={{
              display: "flex",
              justifyContent: "center",
              width: "100%",
              flex: "1 1 auto",   // fill remaining vertical space
              minHeight: 0,
              px: { xs: 1.5, md: 3 },
              py: { xs: 2, md: 3 },
              overflow: "hidden", // important: page content should control its own scrolling
            }}
          >
            {children}
          </Box>
        </Box>
      </Box>
    </Box>
  );
}

export default Scene;
  