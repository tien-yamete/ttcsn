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
import { Link } from "react-router-dom";

function SideMenu() {
  const [activeItem, setActiveItem] = React.useState("home");

  const menuItems = [
    { key: "home", icon: <HomeIcon />, text: "Home", to: "/" },
    { key: "friends", icon: <PeopleIcon />, text: "Friends", to: "/friends" },
    { key: "groups", icon: <GroupsIcon />, text: "Groups", to: "/groups" },
    { key: "chat", icon: <ChatIcon />, text: "Chat", to: "/chat" }
  ];

  return (
    <>
      <Toolbar />
      <List sx={{ px: 2, py: 2 }}>
        {menuItems.map((item) => (
          <ListItem key={item.key} disablePadding sx={{ mb: 1 }}>
            <ListItemButton
              component={Link}
              to={item.to}
              onClick={() => setActiveItem(item.key)}
              sx={{
                borderRadius: 3,
                py: 2,
                transition: "all 0.3s ease",
                background: activeItem === item.key 
                  ? "linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%)"
                  : "transparent",
                "&:hover": {
                  background: activeItem === item.key 
                    ? "linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%)"
                    : "rgba(0, 0, 0, 0.04)",
                  transform: "translateX(4px)"
                }
              }}
            >
              <ListItemIcon
                sx={{
                  minWidth: 44,
                  color: activeItem === item.key ? "#667eea" : "text.secondary",
                  transition: "all 0.3s ease"
                }}
              >
                {item.icon}
              </ListItemIcon>
              <ListItemText
                primary={item.text}
                primaryTypographyProps={{
                  fontWeight: activeItem === item.key ? 700 : 600,
                  fontSize: 15,
                  color: activeItem === item.key ? "#667eea" : "text.primary"
                }}
              />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
      <Divider sx={{ mx: 2 }} />
    </>
  );
}

export default SideMenu;