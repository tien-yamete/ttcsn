import { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import {
  Box,
  Card,
  CircularProgress,
  Typography,
  Fab,
  Popover,
  TextField,
  Button,
  Snackbar,
  Alert,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import { isAuthenticated, logOut } from "../services/authenticationService";
import Scene from "./Scene";
import Post from "../components/Post";
import { getMyPosts, createPost } from "../services/postService";

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

  const navigate = useNavigate();

  const handleCreatePostClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClosePopover = () => {
    setAnchorEl(null);
    setNewPostContent("");
  };

  const handleSnackbarClose = (event, reason) => {
    if (reason === "clickaway") return;
    setSnackbarOpen(false);
  };

  const handlePostContent = () => {
    handleClosePopover();

    createPost(newPostContent)
      .then((response) => {
        setPosts((prev) => [response.data.result, ...prev]);
        setNewPostContent("");
        setSnackbarMessage("Post created successfully!");
        setSnackbarSeverity("success");
        setSnackbarOpen(true);
      })
      .catch(() => {
        setSnackbarMessage("Failed to create post. Please try again.");
        setSnackbarSeverity("error");
        setSnackbarOpen(true);
      });
  };

  const handleEditPost = (postId, newContent) => {
    setPosts((prev) =>
      prev.map((post) =>
        post.id === postId ? { ...post, content: newContent } : post
      )
    );
    setSnackbarMessage("Post updated successfully!");
    setSnackbarSeverity("success");
    setSnackbarOpen(true);
  };

  const handleDeletePost = (postId) => {
    setPosts((prev) => prev.filter((post) => post.id !== postId));
    setSnackbarMessage("Post deleted successfully!");
    setSnackbarSeverity("success");
    setSnackbarOpen(true);
  };

  const open = Boolean(anchorEl);
  const popoverId = open ? "post-popover" : undefined;

  useEffect(() => {
    if (isAuthenticated()) loadPosts(page);
  }, [page]);

  const loadPosts = (page) => {
    setLoading(true);
    getMyPosts(page)
      .then((response) => {
        setTotalPages(response.data.result.totalPages);
        setPosts((prev) => [...prev, ...response.data.result.data]);
        setHasMore(response.data.result.data.length > 0);
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
      if (entries[0].isIntersecting && page < totalPages) {
        setPage((prev) => prev + 1);
      }
    });
    if (lastPostElementRef.current) {
      observer.current.observe(lastPostElementRef.current);
    }
    setHasMore(false);
  }, [hasMore]);

  return (
    <Scene>
      <Box
        sx={{
          display: "flex",
          justifyContent: "center",
          width: "100%",
          mt: 4,
          px: 2,
        }}
      >
        <Card
          elevation={0}
          sx={{
            width: "100%",
            maxWidth: 820, // 👈 rộng hơn, đẹp trên màn hình lớn
            borderRadius: 4,
            p: 3.5,
            boxShadow: "0 2px 12px rgba(0,0,0,0.06)",
            border: "1px solid #e8e8e8",
            background: "#fff",
          }}
        >
          <Typography
            sx={{
              fontSize: 22,
              fontWeight: 700,
              mb: 2.5,
              color: "#1a1a1a",
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
            <Box
              sx={{
                display: "flex",
                justifyContent: "center",
                py: 4,
              }}
            >
              <CircularProgress size="32px" sx={{ color: "#667eea" }} />
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
          boxShadow: "0 4px 20px rgba(102, 126, 234, 0.4)",
          transition: "all 0.3s cubic-bezier(0.4, 0, 0.2, 1)",
          "&:hover": {
            background: "linear-gradient(135deg, #5568d3 0%, #63428a 100%)",
            transform: "scale(1.15) rotate(90deg)",
            boxShadow: "0 6px 28px rgba(102, 126, 234, 0.5)",
          },
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
        anchorOrigin={{
          vertical: "top",
          horizontal: "center",
        }}
        transformOrigin={{
          vertical: "bottom",
          horizontal: "center",
        }}
        slotProps={{
          paper: {
            sx: {
              borderRadius: 4,
              p: 3.5,
              width: 520,
              maxWidth: "90vw",
              boxShadow: "0 8px 32px rgba(0,0,0,0.12)",
              border: "1px solid #e8e8e8",
            },
          },
        }}
      >
        <Typography
          variant="h6"
          sx={{
            mb: 2.5,
            fontWeight: 700,
            fontSize: 19,
            color: "#1a1a1a",
          }}
        >
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
            mb: 3,
            "& .MuiOutlinedInput-root": {
              borderRadius: 3,
              fontSize: 14.5,
              "&:hover fieldset": {
                borderColor: "#667eea",
              },
              "&.Mui-focused fieldset": {
                borderColor: "#667eea",
                borderWidth: 2,
              },
            },
          }}
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
              borderColor: "#e0e0e0",
              color: "#666",
              "&:hover": {
                borderColor: "#c0c0c0",
                backgroundColor: "#f8f8f8",
              },
            }}
          >
            Cancel
          </Button>
          <Button
            variant="contained"
            onClick={handlePostContent}
            disabled={!newPostContent.trim()}
            sx={{
              textTransform: "none",
              fontWeight: 600,
              borderRadius: 3,
              px: 3.5,
              py: 1,
              fontSize: 14,
              background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
              boxShadow: "0 2px 12px rgba(102, 126, 234, 0.3)",
              "&:hover": {
                background: "linear-gradient(135deg, #5568d3 0%, #63428a 100%)",
                boxShadow: "0 4px 16px rgba(102, 126, 234, 0.4)",
                transform: "translateY(-1px)",
              },
              "&:disabled": {
                background: "#e0e0e0",
                color: "#999",
              },
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
        sx={{ marginTop: "64px" }}
      >
        <Alert
          onClose={handleSnackbarClose}
          severity={snackbarSeverity}
          variant="filled"
          sx={{
            width: "100%",
            borderRadius: 3,
            boxShadow: "0 4px 20px rgba(0,0,0,0.15)",
            fontWeight: 500,
          }}
        >
          {snackbarMessage}
        </Alert>
      </Snackbar>
    </Scene>
  );
}
