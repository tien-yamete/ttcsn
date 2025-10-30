import React, { useState } from "react";
import {
  Box,
  Card,
  Typography,
  Grid,
  Chip,
  Button,
  TextField,
  InputAdornment,
  IconButton,
  Avatar,
  Stack,
  Paper,
  alpha,
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import FavoriteBorderIcon from "@mui/icons-material/FavoriteBorder";
import FavoriteIcon from "@mui/icons-material/Favorite";
import LocationOnIcon from "@mui/icons-material/LocationOn";
import Scene from "./Scene";

const MARKETPLACE_ITEMS = [
  {
    id: 1,
    title: "MacBook Pro 16-inch",
    price: 2499,
    location: "San Francisco, CA",
    image: "https://picsum.photos/400/300?random=1",
    seller: { name: "John Doe", avatar: "https://i.pravatar.cc/150?img=1" },
    category: "Electronics",
  },
  {
    id: 2,
    title: "Vintage Leather Sofa",
    price: 599,
    location: "Los Angeles, CA",
    image: "https://picsum.photos/400/300?random=2",
    seller: { name: "Sarah Smith", avatar: "https://i.pravatar.cc/150?img=2" },
    category: "Furniture",
  },
  {
    id: 3,
    title: "Mountain Bike - Trek",
    price: 899,
    location: "Seattle, WA",
    image: "https://picsum.photos/400/300?random=3",
    seller: { name: "Mike Johnson", avatar: "https://i.pravatar.cc/150?img=3" },
    category: "Sports",
  },
  {
    id: 4,
    title: "Designer Sunglasses",
    price: 199,
    location: "Miami, FL",
    image: "https://picsum.photos/400/300?random=4",
    seller: { name: "Emma Wilson", avatar: "https://i.pravatar.cc/150?img=4" },
    category: "Fashion",
  },
  {
    id: 5,
    title: "Canon EOS R5 Camera",
    price: 3899,
    location: "New York, NY",
    image: "https://picsum.photos/400/300?random=5",
    seller: { name: "David Brown", avatar: "https://i.pravatar.cc/150?img=5" },
    category: "Electronics",
  },
  {
    id: 6,
    title: "Acoustic Guitar - Fender",
    price: 449,
    location: "Austin, TX",
    image: "https://picsum.photos/400/300?random=6",
    seller: { name: "Lisa Garcia", avatar: "https://i.pravatar.cc/150?img=6" },
    category: "Music",
  },
];

const CATEGORIES = ["All", "Electronics", "Furniture", "Sports", "Fashion", "Music", "Books", "Home"];

export default function Marketplace() {
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("All");
  const [favorites, setFavorites] = useState([]);

  const handleToggleFavorite = (id) => {
    setFavorites((prev) =>
      prev.includes(id) ? prev.filter((fav) => fav !== id) : [...prev, id]
    );
  };

  const filteredItems = MARKETPLACE_ITEMS.filter((item) => {
    const matchesSearch = item.title.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesCategory = selectedCategory === "All" || item.category === selectedCategory;
    return matchesSearch && matchesCategory;
  });

  return (
    <Scene>
      <Box sx={{ width: "100%", maxWidth: 1400, mx: "auto", px: 2 }}>
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
          <Typography
            variant="h4"
            sx={{
              fontWeight: 700,
              mb: 3,
              background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
              WebkitBackgroundClip: "text",
              WebkitTextFillColor: "transparent",
            }}
          >
            Marketplace
          </Typography>

          {/* Search */}
          <TextField
            fullWidth
            placeholder="Search for items..."
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
        <Grid container spacing={2.5}>
          {filteredItems.map((item) => (
            <Grid item xs={12} sm={6} md={4} lg={3} key={item.id}>
              <Card
                elevation={0}
                sx={(t) => ({
                  borderRadius: 4,
                  overflow: "hidden",
                  border: "1px solid",
                  borderColor: "divider",
                  transition: "all 0.3s ease",
                  position: "relative",
                  "&:hover": {
                    boxShadow: t.shadows[4],
                    transform: "translateY(-8px)",
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
                      height: 220,
                      objectFit: "cover",
                    }}
                  />
                  <IconButton
                    onClick={() => handleToggleFavorite(item.id)}
                    sx={{
                      position: "absolute",
                      top: 12,
                      right: 12,
                      bgcolor: "background.paper",
                      "&:hover": { bgcolor: "background.default" },
                    }}
                  >
                    {favorites.includes(item.id) ? (
                      <FavoriteIcon sx={{ color: "error.main" }} />
                    ) : (
                      <FavoriteBorderIcon />
                    )}
                  </IconButton>
                  <Chip
                    label={item.category}
                    size="small"
                    sx={{
                      position: "absolute",
                      bottom: 12,
                      left: 12,
                      bgcolor: "rgba(0,0,0,0.7)",
                      color: "white",
                      fontWeight: 600,
                    }}
                  />
                </Box>

                {/* Content */}
                <Box sx={{ p: 2.5 }}>
                  <Typography
                    variant="h6"
                    sx={{
                      fontWeight: 700,
                      fontSize: 16,
                      mb: 1,
                      overflow: "hidden",
                      textOverflow: "ellipsis",
                      whiteSpace: "nowrap",
                    }}
                  >
                    {item.title}
                  </Typography>

                  <Typography
                    variant="h5"
                    sx={{
                      fontWeight: 700,
                      color: "primary.main",
                      mb: 1.5,
                    }}
                  >
                    ${item.price.toLocaleString()}
                  </Typography>

                  <Box sx={{ display: "flex", alignItems: "center", gap: 0.5, mb: 2 }}>
                    <LocationOnIcon sx={{ fontSize: 16, color: "text.secondary" }} />
                    <Typography variant="caption" color="text.secondary">
                      {item.location}
                    </Typography>
                  </Box>

                  <Box sx={{ display: "flex", alignItems: "center", gap: 1.5, mb: 2 }}>
                    <Avatar src={item.seller.avatar} sx={{ width: 32, height: 32 }} />
                    <Typography variant="body2" sx={{ fontWeight: 600, fontSize: 13 }}>
                      {item.seller.name}
                    </Typography>
                  </Box>

                  <Button
                    fullWidth
                    variant="contained"
                    sx={{
                      textTransform: "none",
                      fontWeight: 600,
                      borderRadius: 2.5,
                      py: 1,
                      background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                      "&:hover": {
                        background: "linear-gradient(135deg, #5568d3 0%, #63428a 100%)",
                      },
                    }}
                  >
                    View Details
                  </Button>
                </Box>
              </Card>
            </Grid>
          ))}
        </Grid>

        {filteredItems.length === 0 && (
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
              No items found
            </Typography>
            <Typography variant="body2" color="text.disabled" sx={{ mt: 1 }}>
              Try adjusting your search or filters
            </Typography>
          </Paper>
        )}
      </Box>
    </Scene>
  );
}
