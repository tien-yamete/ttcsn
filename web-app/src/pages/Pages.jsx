import React, { useState } from "react";
import {
  Box,
  Card,
  Typography,
  Grid,
  Avatar,
  Button,
  Chip,
  TextField,
  InputAdornment,
  Stack,
  Paper,
  alpha,
  Tabs,
  Tab,
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import AddIcon from "@mui/icons-material/Add";
import VerifiedIcon from "@mui/icons-material/Verified";
import FavoriteIcon from "@mui/icons-material/Favorite";
import Scene from "./Scene";

const PAGES_DATA = [
  {
    id: 1,
    name: "Tech News Daily",
    category: "Media & News",
    followers: "1.2M",
    avatar: "https://i.pravatar.cc/150?img=11",
    verified: true,
    following: false,
  },
  {
    id: 2,
    name: "Design Inspiration",
    category: "Art & Design",
    followers: "856K",
    avatar: "https://i.pravatar.cc/150?img=12",
    verified: true,
    following: true,
  },
  {
    id: 3,
    name: "Fitness Motivation",
    category: "Health & Wellness",
    followers: "2.3M",
    avatar: "https://i.pravatar.cc/150?img=13",
    verified: true,
    following: false,
  },
  {
    id: 4,
    name: "Cooking Recipes",
    category: "Food & Drink",
    followers: "945K",
    avatar: "https://i.pravatar.cc/150?img=14",
    verified: false,
    following: true,
  },
  {
    id: 5,
    name: "Travel Adventures",
    category: "Travel & Tourism",
    followers: "1.5M",
    avatar: "https://i.pravatar.cc/150?img=15",
    verified: true,
    following: false,
  },
  {
    id: 6,
    name: "Photography Tips",
    category: "Photography",
    followers: "678K",
    avatar: "https://i.pravatar.cc/150?img=16",
    verified: false,
    following: false,
  },
];

const SUGGESTED_PAGES = [
  {
    id: 7,
    name: "Gaming Community",
    category: "Gaming",
    followers: "3.1M",
    avatar: "https://i.pravatar.cc/150?img=17",
    verified: true,
  },
  {
    id: 8,
    name: "Book Lovers",
    category: "Literature",
    followers: "542K",
    avatar: "https://i.pravatar.cc/150?img=18",
    verified: false,
  },
  {
    id: 9,
    name: "Tech Startups",
    category: "Business",
    followers: "1.8M",
    avatar: "https://i.pravatar.cc/150?img=19",
    verified: true,
  },
];

export default function Pages() {
  const [searchQuery, setSearchQuery] = useState("");
  const [activeTab, setActiveTab] = useState(0);
  const [pages, setPages] = useState(PAGES_DATA);

  const handleToggleFollow = (id) => {
    setPages((prev) =>
      prev.map((page) =>
        page.id === id ? { ...page, following: !page.following } : page
      )
    );
  };

  const myPages = pages.filter((page) => page.following);
  const discoveredPages = pages.filter((page) => !page.following);

  const filteredPages =
    activeTab === 0
      ? myPages.filter((page) =>
          page.name.toLowerCase().includes(searchQuery.toLowerCase())
        )
      : discoveredPages.filter((page) =>
          page.name.toLowerCase().includes(searchQuery.toLowerCase())
        );

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
          <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 3 }}>
            <Typography
              variant="h4"
              sx={{
                fontWeight: 700,
                background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                WebkitBackgroundClip: "text",
                WebkitTextFillColor: "transparent",
              }}
            >
              Pages
            </Typography>
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              sx={{
                textTransform: "none",
                fontWeight: 600,
                borderRadius: 2.5,
                px: 3,
                background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
              }}
            >
              Create Page
            </Button>
          </Box>

          {/* Search */}
          <TextField
            fullWidth
            placeholder="Search pages..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon color="action" />
                </InputAdornment>
              ),
            }}
            sx={{
              mb: 3,
              "& .MuiOutlinedInput-root": {
                borderRadius: 3,
                bgcolor: (t) =>
                  t.palette.mode === "dark" ? "rgba(255,255,255,0.04)" : "background.default",
                "& fieldset": { borderColor: "divider" },
                "&:hover fieldset": { borderColor: "primary.main" },
                "&.Mui-focused fieldset": { borderColor: "primary.main", borderWidth: 2 },
              },
            }}
          />

          {/* Tabs */}
          <Tabs
            value={activeTab}
            onChange={(e, val) => setActiveTab(val)}
            sx={{
              "& .MuiTab-root": {
                textTransform: "none",
                fontSize: 15,
                fontWeight: 600,
                minHeight: 48,
              },
            }}
          >
            <Tab label={`Your Pages (${myPages.length})`} />
            <Tab label="Discover" />
          </Tabs>
        </Paper>

        {/* Pages Grid */}
        <Grid container spacing={2.5}>
          {filteredPages.map((page) => (
            <Grid item xs={12} sm={6} md={4} key={page.id}>
              <Card
                elevation={0}
                sx={(t) => ({
                  borderRadius: 4,
                  p: 3,
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
                <Box sx={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
                  <Avatar
                    src={page.avatar}
                    sx={{
                      width: 96,
                      height: 96,
                      mb: 2,
                      border: "3px solid",
                      borderColor: "divider",
                    }}
                  />

                  <Stack direction="row" spacing={0.5} alignItems="center" sx={{ mb: 0.5 }}>
                    <Typography variant="h6" sx={{ fontWeight: 700, fontSize: 18 }}>
                      {page.name}
                    </Typography>
                    {page.verified && (
                      <VerifiedIcon sx={{ fontSize: 20, color: "primary.main" }} />
                    )}
                  </Stack>

                  <Chip
                    label={page.category}
                    size="small"
                    sx={{
                      mb: 1,
                      bgcolor: "action.selected",
                      fontWeight: 600,
                      fontSize: 12,
                    }}
                  />

                  <Stack direction="row" spacing={0.5} alignItems="center" sx={{ mb: 3 }}>
                    <FavoriteIcon sx={{ fontSize: 16, color: "text.secondary" }} />
                    <Typography variant="body2" color="text.secondary" fontWeight={600}>
                      {page.followers} followers
                    </Typography>
                  </Stack>

                  {activeTab === 0 ? (
                    <Stack direction="row" spacing={1} width="100%">
                      <Button
                        fullWidth
                        variant="outlined"
                        sx={{
                          textTransform: "none",
                          fontWeight: 600,
                          borderRadius: 2.5,
                        }}
                      >
                        View Page
                      </Button>
                      <Button
                        fullWidth
                        variant="outlined"
                        onClick={() => handleToggleFollow(page.id)}
                        sx={{
                          textTransform: "none",
                          fontWeight: 600,
                          borderRadius: 2.5,
                          borderColor: "error.main",
                          color: "error.main",
                          "&:hover": {
                            borderColor: "error.dark",
                            bgcolor: (t) => alpha(t.palette.error.main, 0.08),
                          },
                        }}
                      >
                        Unfollow
                      </Button>
                    </Stack>
                  ) : (
                    <Button
                      fullWidth
                      variant={page.following ? "outlined" : "contained"}
                      onClick={() => handleToggleFollow(page.id)}
                      sx={{
                        textTransform: "none",
                        fontWeight: 600,
                        borderRadius: 2.5,
                        background: page.following
                          ? "transparent"
                          : "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                        "&:hover": {
                          background: page.following
                            ? "transparent"
                            : "linear-gradient(135deg, #5568d3 0%, #63428a 100%)",
                        },
                      }}
                    >
                      {page.following ? "Following" : "Follow"}
                    </Button>
                  )}
                </Box>
              </Card>
            </Grid>
          ))}
        </Grid>

        {/* Suggested Pages (only show in Discover tab) */}
        {activeTab === 1 && (
          <Box sx={{ mt: 4 }}>
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
              Suggested for You
            </Typography>
            <Grid container spacing={2.5}>
              {SUGGESTED_PAGES.map((page) => (
                <Grid item xs={12} sm={6} md={4} key={page.id}>
                  <Card
                    elevation={0}
                    sx={(t) => ({
                      borderRadius: 4,
                      p: 3,
                      border: "1px solid",
                      borderColor: "divider",
                      transition: "all 0.3s ease",
                      "&:hover": {
                        boxShadow: t.shadows[4],
                        transform: "translateY(-4px)",
                      },
                    })}
                  >
                    <Box sx={{ display: "flex", alignItems: "center", gap: 2, mb: 2 }}>
                      <Avatar src={page.avatar} sx={{ width: 64, height: 64 }} />
                      <Box sx={{ flex: 1 }}>
                        <Stack direction="row" spacing={0.5} alignItems="center">
                          <Typography variant="body1" sx={{ fontWeight: 700 }}>
                            {page.name}
                          </Typography>
                          {page.verified && (
                            <VerifiedIcon sx={{ fontSize: 18, color: "primary.main" }} />
                          )}
                        </Stack>
                        <Typography variant="caption" color="text.secondary">
                          {page.category}
                        </Typography>
                        <Typography variant="caption" color="text.secondary" display="block">
                          {page.followers} followers
                        </Typography>
                      </Box>
                    </Box>
                    <Button
                      fullWidth
                      variant="outlined"
                      sx={{
                        textTransform: "none",
                        fontWeight: 600,
                        borderRadius: 2.5,
                      }}
                    >
                      Follow
                    </Button>
                  </Card>
                </Grid>
              ))}
            </Grid>
          </Box>
        )}

        {filteredPages.length === 0 && (
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
            <SearchIcon sx={{ fontSize: 64, color: "text.disabled", mb: 2 }} />
            <Typography variant="h6" color="text.secondary">
              No pages found
            </Typography>
          </Paper>
        )}
      </Box>
    </Scene>
  );
}
