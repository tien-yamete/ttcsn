// src/pages/Home.jsx
import { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import {
  Box, Card, CircularProgress, Typography, Fab, Popover, TextField,
  Button, Snackbar, Alert, IconButton
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import CloseIcon from "@mui/icons-material/Close";
import { isAuthenticated, logOut } from "../services/authenticationService";
import Scene from "./Scene";
import Post from "../components/Post";
import { getMyPosts, createPost } from "../services/postService";
import MediaUpload from "../components/MediaUpload";

export default function Home() {
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [hasMore, setHasMore] = useState(false);
  const observer = useRef();
  const lastPostElementRef = useRef();
  const [anchorEl, setAnchorEl] = useState(null);
  const [newPostContent, setNewPostContent] = useState("");
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState("");
  const [snackbarSeverity, setSnackbarSeverity] = useState("success");

  // NEW: store only File[] for upload; preview logic is inside MediaUpload
  const [mediaFiles, setMediaFiles] = useState([]);
  const mediaUploadRef = useRef(null);

  const navigate = useNavigate();

  const handleCreatePostClick = (e) => setAnchorEl(e.currentTarget);

  const handleClosePopover = () => {
    setAnchorEl(null);
    setNewPostContent("");
    // clear files inside MediaUpload (it will also notify parent via onFilesChange)
    if (mediaUploadRef.current?.clear) mediaUploadRef.current.clear();
    setMediaFiles([]);
  };

  const handleSnackbarClose = (_, r) => {
    if (r !== "clickaway") setSnackbarOpen(false);
  };

  const handleEditPost = (id, content) => {
    setPosts((prev) => prev.map((p) => (p.id === id ? { ...p, content } : p)));
    setSnackbarMessage("Post updated successfully!");
    setSnackbarSeverity("success");
    setSnackbarOpen(true);
  };

  const handleDeletePost = (id) => {
    setPosts((prev) => prev.filter((p) => p.id !== id));
    setSnackbarMessage("Post deleted successfully!");
    setSnackbarSeverity("success");
    setSnackbarOpen(true);
  };

  const open = Boolean(anchorEl);
  const popoverId = open ? "post-popover" : undefined;

  useEffect(() => {
    if (isAuthenticated()) loadPosts(page);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  const loadPosts = (page) => {
    setLoading(true);
    getMyPosts(page)
      .then((res) => {
        setTotalPages(res.data.result.totalPages);
        setPosts((prev) => [...prev, ...res.data.result.data]);
        setHasMore(res.data.result.data.length > 0);
      })
      .catch((error) => {
        if (error.response?.status === 401) {
          logOut();
          navigate("/login");
        }
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    if (!hasMore) return;
    if (observer.current) observer.current.disconnect();
    observer.current = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting && page < totalPages) setPage((prev) => prev + 1);
    });
    if (lastPostElementRef.current) observer.current.observe(lastPostElementRef.current);
    setHasMore(false);
  }, [hasMore, page, totalPages]);

  // NEW: callback from MediaUpload
  const handleMediaFilesChange = (files) => {
    setMediaFiles(files || []);
  };

  const handlePostContent = () => {
    const formData = new FormData();
    formData.append('content', newPostContent);

    // Get files from MediaUpload via ref (primary) or from state (fallback)
    const filesFromRef = mediaUploadRef.current?.getFiles?.() ?? mediaFiles;
    (filesFromRef || []).forEach((file) => {
      formData.append('media', file);
    });

    // Close popover (UI) while request in flight
    setAnchorEl(null);

    createPost(formData)
      .then((res) => {
        setPosts((prev) => [res.data.result, ...prev]);
        setNewPostContent("");
        setSnackbarMessage("Post created successfully!");
        setSnackbarSeverity("success");
        setSnackbarOpen(true);
        // clear media upload UI + parent state
        if (mediaUploadRef.current?.clear) mediaUploadRef.current.clear();
        setMediaFiles([]);
      })
      .catch(() => {
        setSnackbarMessage("Failed to create post. Please try again.");
        setSnackbarSeverity("error");
        setSnackbarOpen(true);
      });
  };

  return (
    <Scene>
      <Box sx={{ display: "flex", justifyContent: "center", width: "100%", mt: 4, px: 2 }}>
        <Card
          elevation={0}
          sx={(t) => ({
            width: "100%",
            maxWidth: 820,
            borderRadius: 4,
            p: 3.5,
            boxShadow: t.shadows[1],
            border: "1px solid",
            borderColor: "divider",
            bgcolor: "background.paper",
          })}
        >
          <Typography
            sx={{
              fontSize: 22,
              fontWeight: 700,
              mb: 2.5,
              background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
              WebkitBackgroundClip: "text",
              WebkitTextFillColor: "transparent",
            }}
          >
            Your posts
          </Typography>

          {posts.map((post, index) => {
            const isLast = posts.length === index + 1;
            return (
              <Post
                ref={isLast ? lastPostElementRef : null}
                key={post.id}
                post={post}
                onEdit={handleEditPost}
                onDelete={handleDeletePost}
              />
            );
          })}

          {loading && (
            <Box sx={{ display: "flex", justifyContent: "center", py: 4 }}>
              <CircularProgress size="32px" color="primary" />
            </Box>
          )}
        </Card>
      </Box>

      {/* Floating Action Button */}
      <Fab
        color="primary"
        aria-label="add"
        onClick={handleCreatePostClick}
        sx={{
          position: "fixed",
          bottom: 32,
          right: 32,
          background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
          boxShadow: "0 4px 20px rgba(0,0,0,0.25)",
          "&:hover": {
            background: "linear-gradient(135deg, #5568d3 0%, #63428a 100%)",
            transform: "scale(1.12) rotate(90deg)",
          },
          transition: "all 0.3s ease",
        }}
      >
        <AddIcon />
      </Fab>

      {/* Popover for new post */}
      <Popover
        id={popoverId}
        open={open}
        anchorEl={anchorEl}
        onClose={handleClosePopover}
        anchorOrigin={{ vertical: "top", horizontal: "center" }}
        transformOrigin={{ vertical: "bottom", horizontal: "center" }}
        slotProps={{
          paper: {
            sx: (t) => ({
              borderRadius: 4,
              p: 3.5,
              width: 620,
              maxWidth: "90vw",
              maxHeight: "85vh",
              overflow: "auto",
              boxShadow: t.shadows[6],
              border: "1px solid",
              borderColor: "divider",
              bgcolor: "background.paper",
            }),
          },
        }}
      >
        <Typography variant="h6" sx={{ mb: 2.5, fontWeight: 700, fontSize: 19, color: "text.primary" }}>
          Create new Post
        </Typography>

        <TextField
          fullWidth
          multiline
          rows={4}
          placeholder="What's on your mind?"
          value={newPostContent}
          onChange={(e) => setNewPostContent(e.target.value)}
          variant="outlined"
          sx={{
            mb: 2,
            "& .MuiOutlinedInput-root": {
              borderRadius: 3,
              fontSize: 14.5,
              bgcolor: (t) => (t.palette.mode === "dark" ? "rgba(255,255,255,0.04)" : "background.paper"),
              "& fieldset": { borderColor: "divider" },
              "&:hover fieldset": { borderColor: "primary.main" },
              "&.Mui-focused fieldset": { borderColor: "primary.main", borderWidth: 2 },
            },
          }}
        />

        {/* NEW: Use MediaUpload component (it manages previews & files) */}
        <MediaUpload
          ref={mediaUploadRef}
          onFilesChange={handleMediaFilesChange}
          maxFiles={8}
          maxFileSize={25 * 1024 * 1024}
          addButtonLabel="Add Photos or Videos"
        />

        <Box sx={{ display: "flex", justifyContent: "flex-end", gap: 1.5 }}>
          <Button
            variant="outlined"
            onClick={handleClosePopover}
            sx={{
              textTransform: "none",
              fontWeight: 600,
              borderRadius: 3,
              px: 3,
              py: 1,
              fontSize: 14,
              borderColor: "divider",
              color: "text.secondary",
              "&:hover": { borderColor: "divider", backgroundColor: "action.hover" },
            }}
          >
            Cancel
          </Button>
          <Button
            variant="contained"
            onClick={handlePostContent}
            disabled={!newPostContent.trim() && mediaFiles.length === 0}
            sx={{
              textTransform: "none",
              fontWeight: 600,
              borderRadius: 3,
              px: 3.5,
              py: 1,
              fontSize: 14,
              background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
              "&:hover": { background: "linear-gradient(135deg, #5568d3 0%, #63428a 100%)" },
              "&:disabled": { background: "action.disabledBackground", color: "text.disabled" },
            }}
          >
            Post
          </Button>
        </Box>
      </Popover>

      {/* Snackbar */}
      <Snackbar
        open={snackbarOpen}
        autoHideDuration={6000}
        onClose={handleSnackbarClose}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
        sx={{ mt: "64px" }}
      >
        <Alert
          onClose={handleSnackbarClose}
          severity={snackbarSeverity}
          variant="filled"
          sx={{ width: "100%", borderRadius: 3, boxShadow: 3, fontWeight: 500 }}
        >
          {snackbarMessage}
        </Alert>
      </Snackbar>
    </Scene>
  );
}
