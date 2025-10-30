import React, { useState } from "react";
import {
  Box,
  Card,
  Typography,
  Grid,
  Chip,
  IconButton,
  Stack,
  Paper,
  Tabs,
  Tab,
  alpha,
} from "@mui/material";
import BookmarkIcon from "@mui/icons-material/Bookmark";
import BookmarkBorderIcon from "@mui/icons-material/BookmarkBorder";
import DeleteIcon from "@mui/icons-material/Delete";
import LinkIcon from "@mui/icons-material/Link";
import ArticleIcon from "@mui/icons-material/Article";
import ImageIcon from "@mui/icons-material/Image";
import VideoLibraryIcon from "@mui/icons-material/VideoLibrary";
import Scene from "./Scene";

const SAVED_ITEMS = [
  {
    id: 1,
    type: "article",
    title: "10 Tips for Better Web Design",
    description: "Learn the essential principles of modern web design",
    image: "https://picsum.photos/400/300?random=1",
    savedDate: "2 days ago",
    category: "Design",
  },
  {
    id: 2,
    type: "video",
    title: "React Tutorial for Beginners",
    description: "Complete guide to learning React from scratch",
    image: "https://picsum.photos/400/300?random=2",
    savedDate: "1 week ago",
    category: "Programming",
  },
  {
    id: 3,
    type: "link",
    title: "Best Productivity Tools 2024",
    description: "A curated list of tools to boost your productivity",
    image: "https://picsum.photos/400/300?random=3",
    savedDate: "3 days ago",
    category: "Productivity",
  },
  {
    id: 4,
    type: "image",
    title: "Design Inspiration Gallery",
    description: "Beautiful UI/UX design examples",
    image: "https://picsum.photos/400/300?random=4",
    savedDate: "5 days ago",
    category: "Design",
  },
  {
    id: 5,
    type: "article",
    title: "Machine Learning Basics",
    description: "Introduction to ML algorithms and concepts",
    image: "https://picsum.photos/400/300?random=5",
    savedDate: "1 week ago",
    category: "AI",
  },
  {
    id: 6,
    type: "video",
    title: "Figma Advanced Techniques",
    description: "Master advanced Figma features",
    image: "https://picsum.photos/400/300?random=6",
    savedDate: "2 weeks ago",
    category: "Design",
  },
];

const CATEGORIES = ["All", "Design", "Programming", "Productivity", "AI", "Business"];

export default function Saved() {
  const [activeTab, setActiveTab] = useState(0);
  const [selectedCategory, setSelectedCategory] = useState("All");
  const [items, setItems] = useState(SAVED_ITEMS);

  const handleRemove = (id) => {
    setItems((prev) => prev.filter((item) => item.id !== id));
  };

  const getTypeIcon = (type) => {
    switch (type) {
      case "article":
        return <ArticleIcon />;
      case "video":
        return <VideoLibraryIcon />;
      case "image":
        return <ImageIcon />;
      case "link":
        return <LinkIcon />;
      default:
        return <BookmarkIcon />;
    }
  };

  const filteredItems = items.filter((item) => {
    const matchesTab =
      activeTab === 0 ||
      (activeTab === 1 && item.type === "article") ||
      (activeTab === 2 && item.type === "video") ||
      (activeTab === 3 && item.type === "link") ||
      (activeTab === 4 && item.type === "image");

    const matchesCategory =
      selectedCategory === "All" || item.category === selectedCategory;

    return matchesTab && matchesCategory;
  });

  return (
    <Scene>
      <Box sx={{ width: "100%", maxWidth: 1200, mx: "auto", px: 2 }}>
        {/* Header */}
        <Paper
          elevation={0}
          sx={{
            borderRadius: 4,
            p: 3,
            mb: 3,
            border: "1px solid",
            borderColor: "divider",
            bgcolor: "background.paper",
          }}
        >
          <Box sx={{ display: "flex", alignItems: "center", gap: 2, mb: 3 }}>
            <BookmarkIcon
              sx={{
                fontSize: 40,
                color: "primary.main",
              }}
            />
            <Typography
              variant="h4"
              sx={{
                fontWeight: 700,
                background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                WebkitBackgroundClip: "text",
                WebkitTextFillColor: "transparent",
              }}
            >
              Saved Items
            </Typography>
          </Box>

          {/* Tabs */}
          <Tabs
            value={activeTab}
            onChange={(e, val) => setActiveTab(val)}
            variant="scrollable"
            scrollButtons="auto"
            sx={{
              mb: 3,
              "& .MuiTab-root": {
                textTransform: "none",
                fontSize: 15,
                fontWeight: 600,
                minHeight: 48,
              },
            }}
          >
            <Tab label={`All (${items.length})`} />
            <Tab
              label={`Articles (${
                items.filter((i) => i.type === "article").length
              })`}
            />
            <Tab
              label={`Videos (${items.filter((i) => i.type === "video").length})`}
            />
            <Tab
              label={`Links (${items.filter((i) => i.type === "link").length})`}
            />
            <Tab
              label={`Images (${items.filter((i) => i.type === "image").length})`}
            />
          </Tabs>

          {/* Categories */}
          <Stack direction="row" spacing={1} sx={{ flexWrap: "wrap", gap: 1 }}>
            {CATEGORIES.map((category) => (
              <Chip
                key={category}
                label={category}
                onClick={() => setSelectedCategory(category)}
                sx={(t) => ({
                  fontWeight: 600,
                  cursor: "pointer",
                  bgcolor:
                    selectedCategory === category
                      ? "primary.main"
                      : t.palette.mode === "dark"
                      ? "rgba(255,255,255,0.08)"
                      : "rgba(0,0,0,0.04)",
                  color: selectedCategory === category ? "white" : "text.primary",
                  "&:hover": {
                    bgcolor:
                      selectedCategory === category
                        ? "primary.dark"
                        : t.palette.mode === "dark"
                        ? "rgba(255,255,255,0.12)"
                        : "rgba(0,0,0,0.08)",
                  },
                })}
              />
            ))}
          </Stack>
        </Paper>

        {/* Items Grid */}
        {filteredItems.length > 0 ? (
          <Grid container spacing={2.5}>
            {filteredItems.map((item) => (
              <Grid item xs={12} sm={6} md={4} key={item.id}>
                <Card
                  elevation={0}
                  sx={(t) => ({
                    borderRadius: 4,
                    overflow: "hidden",
                    border: "1px solid",
                    borderColor: "divider",
                    transition: "all 0.3s ease",
                    "&:hover": {
                      boxShadow: t.shadows[4],
                      transform: "translateY(-4px)",
                      borderColor: alpha(t.palette.primary.main, 0.5),
                    },
                  })}
                >
                  {/* Image */}
                  <Box sx={{ position: "relative" }}>
                    <Box
                      component="img"
                      src={item.image}
                      alt={item.title}
                      sx={{
                        width: "100%",
                        height: 180,
                        objectFit: "cover",
                      }}
                    />
                    <Chip
                      icon={getTypeIcon(item.type)}
                      label={item.type}
                      size="small"
                      sx={{
                        position: "absolute",
                        top: 12,
                        left: 12,
                        bgcolor: "rgba(0,0,0,0.7)",
                        color: "white",
                        fontWeight: 600,
                        textTransform: "capitalize",
                      }}
                    />
                    <IconButton
                      onClick={() => handleRemove(item.id)}
                      sx={{
                        position: "absolute",
                        top: 12,
                        right: 12,
                        bgcolor: "background.paper",
                        "&:hover": {
                          bgcolor: "error.main",
                          color: "white",
                        },
                      }}
                      size="small"
                    >
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                  </Box>

                  {/* Content */}
                  <Box sx={{ p: 2.5 }}>
                    <Chip
                      label={item.category}
                      size="small"
                      sx={{
                        mb: 1.5,
                        bgcolor: "action.selected",
                        fontWeight: 600,
                        fontSize: 11,
                        height: 22,
                      }}
                    />
                    <Typography
                      variant="h6"
                      sx={{
                        fontWeight: 700,
                        fontSize: 16,
                        mb: 1,
                        overflow: "hidden",
                        textOverflow: "ellipsis",
                        display: "-webkit-box",
                        WebkitLineClamp: 2,
                        WebkitBoxOrient: "vertical",
                      }}
                    >
                      {item.title}
                    </Typography>
                    <Typography
                      variant="body2"
                      color="text.secondary"
                      sx={{
                        mb: 2,
                        overflow: "hidden",
                        textOverflow: "ellipsis",
                        display: "-webkit-box",
                        WebkitLineClamp: 2,
                        WebkitBoxOrient: "vertical",
                      }}
                    >
                      {item.description}
                    </Typography>
                    <Box
                      sx={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                      }}
                    >
                      <Typography variant="caption" color="text.disabled">
                        Saved {item.savedDate}
                      </Typography>
                      <IconButton size="small" color="primary">
                        <BookmarkIcon fontSize="small" />
                      </IconButton>
                    </Box>
                  </Box>
                </Card>
              </Grid>
            ))}
          </Grid>
        ) : (
          <Paper
            elevation={0}
            sx={{
              borderRadius: 4,
              p: 8,
              textAlign: "center",
              border: "1px solid",
              borderColor: "divider",
            }}
          >
            <BookmarkBorderIcon sx={{ fontSize: 64, color: "text.disabled", mb: 2 }} />
            <Typography variant="h6" color="text.secondary" sx={{ mb: 1 }}>
              No saved items
            </Typography>
            <Typography variant="body2" color="text.disabled">
              Save articles, videos, and links to view them later
            </Typography>
          </Paper>
        )}
      </Box>
    </Scene>
  );
}
