import {
  Box,
  Avatar,
  Typography,
  Paper,
  IconButton,
  Menu,
  MenuItem,
  TextField,
  Button,
  Collapse,
  Divider,
  Popover,
  Tooltip,
} from "@mui/material";
import React, { forwardRef, useState } from "react";
import {
  ThumbUp,
  ThumbUpOutlined,
  ChatBubbleOutline,
  Share,
  MoreVert,
  Send,
} from "@mui/icons-material";

const REACTIONS = [
  { emoji: "👍", label: "Like" },
  { emoji: "❤️", label: "Love" },
  { emoji: "😂", label: "Haha" },
  { emoji: "😮", label: "Wow" },
  { emoji: "😢", label: "Sad" },
  { emoji: "😡", label: "Angry" },
];

const Post = forwardRef((props, ref) => {
  const { avatar, username, created, content, id } = props.post;
  const { onEdit, onDelete } = props;

  const [anchorEl, setAnchorEl] = useState(null);
  const [reactionAnchor, setReactionAnchor] = useState(null);
  const [reaction, setReaction] = useState(null);
  const [likeCount, setLikeCount] = useState(0);
  const [showComments, setShowComments] = useState(false);
  const [commentText, setCommentText] = useState("");
  const [comments, setComments] = useState([]);
  const [isEditing, setIsEditing] = useState(false);
  const [editedContent, setEditedContent] = useState(content);

  // --- Menu ---
  const handleMenuOpen = (e) => setAnchorEl(e.currentTarget);
  const handleMenuClose = () => setAnchorEl(null);

  // --- Reactions ---
  const handleLike = (e) => {
    if (reaction) {
      setReaction(null);
      setLikeCount((prev) => Math.max(prev - 1, 0));
    } else {
      setReaction({ emoji: "👍", label: "Like" });
      setLikeCount((prev) => prev + 1);
    }
  };

  const handleOpenReactions = (e) => {
    setReactionAnchor(e.currentTarget);
  };

  const handleSelectReaction = (react) => {
    setReaction(react);
    setReactionAnchor(null);
    setLikeCount(1);
  };

  const handleCloseReactions = () => setReactionAnchor(null);

  // --- Comments ---
  const handleComment = () => {
    if (commentText.trim()) {
      setComments((prev) => [
        ...prev,
        { id: Date.now(), text: commentText, author: "You", time: "Just now" },
      ]);
      setCommentText("");
    }
  };

  // --- Edit/Delete ---
  const handleEdit = () => {
    setIsEditing(true);
    handleMenuClose();
  };
  const handleSaveEdit = () => {
    if (onEdit) onEdit(id, editedContent);
    setIsEditing(false);
  };
  const handleCancelEdit = () => {
    setEditedContent(content);
    setIsEditing(false);
  };
  const handleDelete = () => {
    if (onDelete) onDelete(id);
    handleMenuClose();
  };

  return (
    <Paper
      ref={ref}
      elevation={0}
      sx={{
        mb: 2.5,
        borderRadius: 4,
        backgroundColor: "#fff",
        border: "1px solid #e8e8e8",
        transition: "all 0.3s ease",
        "&:hover": {
          boxShadow: "0 4px 20px rgba(0,0,0,0.08)",
          borderColor: "#d0d0d0",
        },
      }}
    >
      {/* Header */}
      <Box sx={{ p: 3, pb: 2 }}>
        <Box sx={{ display: "flex", justifyContent: "space-between" }}>
          <Box sx={{ display: "flex", alignItems: "center" }}>
            <Avatar
              src={avatar}
              sx={{
                width: 48,
                height: 48,
                mr: 2,
                background:
                  "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                color: "#fff",
                fontWeight: 600,
              }}
            >
              {username?.charAt(0)}
            </Avatar>
            <Box>
              <Typography sx={{ fontWeight: 700, fontSize: 15 }}>
                {username}
              </Typography>
              <Typography sx={{ fontSize: 13, color: "#999" }}>
                {created}
              </Typography>
            </Box>
          </Box>
          <IconButton onClick={handleMenuOpen} size="small">
            <MoreVert sx={{ color: "#999" }} />
          </IconButton>
        </Box>

        {/* Content */}
        {isEditing ? (
          <Box sx={{ mt: 2 }}>
            <TextField
              fullWidth
              multiline
              rows={3}
              value={editedContent}
              onChange={(e) => setEditedContent(e.target.value)}
              sx={{
                mb: 2,
                "& .MuiOutlinedInput-root": { borderRadius: 2 },
              }}
            />
            <Box sx={{ display: "flex", justifyContent: "flex-end", gap: 1 }}>
              <Button
                variant="outlined"
                size="small"
                onClick={handleCancelEdit}
                sx={{ borderRadius: 2, textTransform: "none" }}
              >
                Cancel
              </Button>
              <Button
                variant="contained"
                size="small"
                onClick={handleSaveEdit}
                sx={{
                  borderRadius: 2,
                  textTransform: "none",
                  background:
                    "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                }}
              >
                Save
              </Button>
            </Box>
          </Box>
        ) : (
          <Typography
            sx={{
              fontSize: 15,
              lineHeight: 1.7,
              color: "#333",
              mt: 2,
              whiteSpace: "pre-line",
            }}
          >
            {editedContent}
          </Typography>
        )}

        {/* Stats */}
        {(likeCount > 0 || comments.length > 0) && (
          <Box
            sx={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              mt: 2,
              pt: 2,
              borderTop: "1px solid #f0f0f0",
            }}
          >
            {likeCount > 0 && (
              <Typography sx={{ fontSize: 13, color: "#666" }}>
                {reaction?.emoji} {likeCount}
              </Typography>
            )}
            {comments.length > 0 && (
              <Typography
                sx={{
                  fontSize: 13,
                  color: "#666",
                  cursor: "pointer",
                  "&:hover": { textDecoration: "underline" },
                }}
                onClick={() => setShowComments(!showComments)}
              >
                {comments.length} comment{comments.length > 1 ? "s" : ""}
              </Typography>
            )}
          </Box>
        )}
      </Box>

      {/* Actions */}
      <Divider />
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-around",
          alignItems: "center",
          p: 1,
        }}
      >
        {/* Like button with long-press reactions */}
        <Box
          onMouseEnter={handleOpenReactions}
          onMouseLeave={handleCloseReactions}
          sx={{ position: "relative" }}
        >
          <IconButton
            onClick={handleLike}
            sx={{
              color: reaction ? "#667eea" : "#666",
              borderRadius: 2,
              transition: "all 0.2s",
              "&:hover": {
                backgroundColor: "rgba(102, 126, 234, 0.08)",
              },
            }}
          >
            {reaction ? (
              <span style={{ fontSize: "18px", marginRight: 6 }}>
                {reaction.emoji}
              </span>
            ) : (
              <ThumbUpOutlined sx={{ fontSize: 20, mr: 1 }} />
            )}
            <Typography sx={{ fontSize: 14, fontWeight: 600 }}>
              {reaction ? reaction.label : "Like"}
            </Typography>
          </IconButton>

          {/* Reactions popover */}
          <Popover
            open={Boolean(reactionAnchor)}
            anchorEl={reactionAnchor}
            onClose={handleCloseReactions}
            anchorOrigin={{
              vertical: "top",
              horizontal: "center",
            }}
            transformOrigin={{
              vertical: "bottom",
              horizontal: "center",
            }}
            PaperProps={{
              sx: {
                px: 1.5,
                py: 1,
                borderRadius: 4,
                display: "flex",
                gap: 1,
                boxShadow: "0 4px 20px rgba(0,0,0,0.15)",
              },
            }}
          >
            {REACTIONS.map((r) => (
              <Tooltip title={r.label} key={r.label}>
                <Box
                  onClick={() => handleSelectReaction(r)}
                  sx={{
                    fontSize: 26,
                    cursor: "pointer",
                    transition: "transform 0.15s ease",
                    "&:hover": { transform: "scale(1.3)" },
                  }}
                >
                  {r.emoji}
                </Box>
              </Tooltip>
            ))}
          </Popover>
        </Box>

        {/* Comment */}
        <IconButton
          onClick={() => setShowComments(!showComments)}
          sx={{
            color: "#666",
            borderRadius: 2,
            "&:hover": {
              backgroundColor: "rgba(102, 126, 234, 0.08)",
              color: "#667eea",
            },
          }}
        >
          <ChatBubbleOutline sx={{ fontSize: 20, mr: 1 }} />
          <Typography sx={{ fontSize: 14, fontWeight: 600 }}>Comment</Typography>
        </IconButton>

        {/* Share */}
        <IconButton
          sx={{
            color: "#666",
            borderRadius: 2,
            "&:hover": {
              backgroundColor: "rgba(102, 126, 234, 0.08)",
              color: "#667eea",
            },
          }}
        >
          <Share sx={{ fontSize: 20, mr: 1 }} />
          <Typography sx={{ fontSize: 14, fontWeight: 600 }}>Share</Typography>
        </IconButton>
      </Box>

      {/* Comments */}
      <Collapse in={showComments}>
        <Divider />
        <Box sx={{ p: 3, pt: 2 }}>
          {comments.map((c) => (
            <Box key={c.id} sx={{ display: "flex", gap: 1.5, mb: 2 }}>
              <Avatar sx={{ width: 32, height: 32, bgcolor: "#667eea" }}>
                {c.author.charAt(0)}
              </Avatar>
              <Box>
                <Box
                  sx={{
                    background: "#f0f0f0",
                    borderRadius: 3,
                    p: 1.2,
                    maxWidth: "100%",
                  }}
                >
                  <Typography sx={{ fontSize: 13, fontWeight: 600 }}>
                    {c.author}
                  </Typography>
                  <Typography sx={{ fontSize: 13.5 }}>{c.text}</Typography>
                </Box>
                <Typography sx={{ fontSize: 12, color: "#999", mt: 0.5 }}>
                  {c.time}
                </Typography>
              </Box>
            </Box>
          ))}

          {/* New comment */}
          <Box sx={{ display: "flex", gap: 1.5, mt: 2 }}>
            <Avatar sx={{ width: 32, height: 32, bgcolor: "#667eea" }}>Y</Avatar>
            <TextField
              fullWidth
              size="small"
              placeholder="Write a comment..."
              value={commentText}
              onChange={(e) => setCommentText(e.target.value)}
              onKeyPress={(e) => {
                if (e.key === "Enter" && !e.shiftKey) {
                  e.preventDefault();
                  handleComment();
                }
              }}
              InputProps={{
                endAdornment: (
                  <IconButton
                    size="small"
                    onClick={handleComment}
                    disabled={!commentText.trim()}
                    sx={{
                      color: "#667eea",
                      "&:disabled": { color: "#ccc" },
                    }}
                  >
                    <Send sx={{ fontSize: 18 }} />
                  </IconButton>
                ),
              }}
              sx={{
                "& .MuiOutlinedInput-root": {
                  borderRadius: 3,
                  backgroundColor: "#f8f8f8",
                  fontSize: 14,
                  "& fieldset": { border: "none" },
                  "&:hover": { backgroundColor: "#f0f0f0" },
                  "&.Mui-focused": {
                    backgroundColor: "#fff",
                    boxShadow: "0 0 0 2px rgba(102,126,234,0.2)",
                  },
                },
              }}
            />
          </Box>
        </Box>
      </Collapse>

      {/* Menu */}
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleMenuClose}
        PaperProps={{
          sx: {
            borderRadius: 2,
            boxShadow: "0 4px 20px rgba(0,0,0,0.1)",
            minWidth: 160,
          },
        }}
      >
        <MenuItem onClick={handleEdit} sx={{ fontSize: 14, py: 1.2 }}>
          Edit Post
        </MenuItem>
        <MenuItem
          onClick={handleDelete}
          sx={{
            fontSize: 14,
            py: 1.2,
            color: "error.main",
            "&:hover": { backgroundColor: "rgba(211,47,47,0.08)" },
          }}
        >
          Delete Post
        </MenuItem>
      </Menu>
    </Paper>
  );
});

export default Post;
