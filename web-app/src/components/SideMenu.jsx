// src/components/SideMenu.jsx
import * as React from "react";
import Divider from "@mui/material/Divider";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import Toolbar from "@mui/material/Toolbar";
import HomeIcon from "@mui/icons-material/Home";
import PeopleIcon from "@mui/icons-material/People";
import GroupsIcon from "@mui/icons-material/Groups";
import ChatIcon from "@mui/icons-material/Chat";
import FlagIcon from "@mui/icons-material/Flag";
import StorefrontIcon from "@mui/icons-material/Storefront";
import BookmarkIcon from "@mui/icons-material/Bookmark";
import { Link, useLocation } from "react-router-dom";

function SideMenu() {
  const location = useLocation();
  const [activeItem, setActiveItem] = React.useState(() => {
    const p = location.pathname || "/";
    if (p.startsWith("/friends")) return "friends";
    if (p.startsWith("/groups")) return "groups";
    if (p.startsWith("/chat")) return "chat";
    if (p.startsWith("/pages")) return "pages";
    if (p.startsWith("/marketplace")) return "marketplace";
    if (p.startsWith("/saved")) return "saved";
    return "home";
  });

  React.useEffect(() => {
    const p = location.pathname || "/";
    if (p.startsWith("/friends")) setActiveItem("friends");
    else if (p.startsWith("/groups")) setActiveItem("groups");
    else if (p.startsWith("/chat")) setActiveItem("chat");
    else if (p.startsWith("/pages")) setActiveItem("pages");
    else if (p.startsWith("/marketplace")) setActiveItem("marketplace");
    else if (p.startsWith("/saved")) setActiveItem("saved");
    else setActiveItem("home");
  }, [location.pathname]);

  const menuItems = [
    { key: "home", icon: <HomeIcon />, text: "News Feed", to: "/" },
    { key: "chat", icon: <ChatIcon />, text: "Messages", to: "/chat" },
    { key: "friends", icon: <PeopleIcon />, text: "Friends", to: "/friends" },
    { key: "groups", icon: <GroupsIcon />, text: "Groups", to: "/groups" },
    { key: "pages", icon: <FlagIcon />, text: "Pages", to: "/pages" },
    { key: "marketplace", icon: <StorefrontIcon />, text: "Marketplace", to: "/marketplace" },
    { key: "saved", icon: <BookmarkIcon />, text: "Saved", to: "/saved" },
  ];

  return (
    <>
      <Toolbar />
      <List sx={{ px: 2, py: 2 }}>
        {menuItems.map((item) => {
          const isActive = activeItem === item.key;
          return (
            <ListItem key={item.key} disablePadding sx={{ mb: 1 }}>
              <ListItemButton
                component={Link}
                to={item.to}
                onClick={() => setActiveItem(item.key)} // giữ để UX mượt ngay khi click
                selected={isActive} // MUI selected support
                sx={{
                  borderRadius: 3,
                  py: 2,
                  transition: "all 0.25s ease",
                  background: isActive
                    ? "linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%)"
                    : "transparent",
                  "&:hover": {
                    background: isActive
                      ? "linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%)"
                      : "rgba(0, 0, 0, 0.04)",
                    transform: "translateX(4px)",
                  },
                }}
              >
                <ListItemIcon
                  sx={{
                    minWidth: 44,
                    color: isActive ? "#667eea" : "text.secondary",
                    transition: "all 0.25s ease",
                  }}
                >
                  {item.icon}
                </ListItemIcon>
                <ListItemText
                  primary={item.text}
                  primaryTypographyProps={{
                    fontWeight: isActive ? 700 : 600,
                    fontSize: 15,
                    color: isActive ? "#667eea" : "text.primary",
                  }}
                />
              </ListItemButton>
            </ListItem>
          );
        })}
      </List>
      <Divider sx={{ mx: 2 }} />
    </>
  );
}

export default SideMenu;
