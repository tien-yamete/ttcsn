// src/components/MediaUpload.jsx
import React, { useRef, useState, useEffect, forwardRef, useImperativeHandle } from "react";
import PropTypes from "prop-types";
import {
  Box,
  Button,
  IconButton,
  Chip,
  Snackbar,
  Alert,
  Typography,
} from "@mui/material";
import ImageIcon from "@mui/icons-material/Image";
import VideoLibraryIcon from "@mui/icons-material/VideoLibrary";
import CloseIcon from "@mui/icons-material/Close";

/**
 * MediaUpload
 * - Fully manages selected File[] and previews (object URLs).
 * - Validates file types (image/*, video/*) and max size.
 * - Exposes imperative methods: clear(), getFiles()
 * - Notifies parent of file changes via onFilesChange(files)
 *
 * Usage:
 *  const ref = useRef();
 *  <MediaUpload ref={ref} onFilesChange={files => setFiles(files)} />
 *  ref.current.clear(); // clear files & previews
 */
const MediaUpload = forwardRef(function MediaUpload(props, ref) {
  const {
    accept = "image/*,video/*",
    multiple = true,
    onFilesChange,
    maxFiles = 8,
    maxFileSize = 25 * 1024 * 1024, // 25MB default
    addButtonLabel = "Add Photos or Videos",
    initialPreviews = [], // optional: array of {url,type,name} for editing existing post
  } = props;

  const inputRef = useRef(null);
  const [files, setFiles] = useState([]); // actual File objects
  const [previews, setPreviews] = useState(initialPreviews || []); // {url, type, name}
  const [snack, setSnack] = useState({ open: false, message: "", severity: "warning" });

  // Expose imperative API
  useImperativeHandle(ref, () => ({
    clear: () => {
      // revoke current previews created by this component (only those created via createObjectURL)
      previews.forEach((p) => {
        // Only revoke if it's an object URL (startsWith blob:)
        if (p?.url?.startsWith?.("blob:")) {
          try { URL.revokeObjectURL(p.url); } catch (e) {}
        }
      });
      setFiles([]);
      setPreviews([]);
      onFilesChange && onFilesChange([]);
      // also reset input so same file can be reselected
      if (inputRef.current) inputRef.current.value = "";
    },
    getFiles: () => files,
  }), [files, previews, onFilesChange]);

  // Validate & add selected files
  const handleInputChange = (e) => {
    const chosen = e.target.files ? Array.from(e.target.files) : [];
    if (chosen.length === 0) return;

    // enforce max file count
    if (files.length + chosen.length > maxFiles) {
      setSnack({
        open: true,
        message: `You can upload up to ${maxFiles} files.`,
        severity: "warning",
      });
      // Optionally accept first N that fit
    }

    const allowedToAdd = chosen.slice(0, Math.max(0, maxFiles - files.length));

    const validFiles = [];
    const newPreviews = [];

    allowedToAdd.forEach((file) => {
      const isImage = file.type.startsWith("image/");
      const isVideo = file.type.startsWith("video/");
      if (!isImage && !isVideo) {
        // skip invalid type
        return setSnack({
          open: true,
          message: `Skipped ${file.name}: unsupported file type.`,
          severity: "warning",
        });
      }
      if (file.size > maxFileSize) {
        return setSnack({
          open: true,
          message: `Skipped ${file.name}: file too large (max ${(maxFileSize / (1024 * 1024)).toFixed(0)}MB).`,
          severity: "warning",
        });
      }

      // Create preview as object URL
      const url = URL.createObjectURL(file);
      validFiles.push(file);
      newPreviews.push({
        url,
        type: isImage ? "image" : "video",
        name: file.name,
      });
    });

    if (validFiles.length === 0) {
      // reset input (so user can reselect same file later)
      if (inputRef.current) inputRef.current.value = "";
      return;
    }

    setFiles((prev) => {
      const merged = [...prev, ...validFiles].slice(0, maxFiles);
      onFilesChange && onFilesChange(merged);
      return merged;
    });

    setPreviews((prev) => {
      const merged = [...prev, ...newPreviews].slice(0, maxFiles);
      return merged;
    });

    // reset input to allow same-file selection later
    if (inputRef.current) inputRef.current.value = "";
  };

  const handleRemove = (index) => {
    // revoke object URL if applicable
    const p = previews[index];
    if (p?.url?.startsWith?.("blob:")) {
      try { URL.revokeObjectURL(p.url); } catch (e) {}
    }

    setPreviews((prev) => {
      const next = prev.filter((_, i) => i !== index);
      return next;
    });

    setFiles((prev) => {
      const next = prev.filter((_, i) => i !== index);
      onFilesChange && onFilesChange(next);
      return next;
    });
  };

  // cleanup on unmount: revoke object URLs that were created
  useEffect(() => {
    return () => {
      previews.forEach((p) => {
        if (p?.url?.startsWith?.("blob:")) {
          try { URL.revokeObjectURL(p.url); } catch (e) {}
        }
      });
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleSnackClose = (_, reason) => {
    if (reason === "clickaway") return;
    setSnack((s) => ({ ...s, open: false }));
  };

  return (
    <>
      {/* hidden input */}
      <input
        ref={inputRef}
        type="file"
        accept={accept}
        multiple={multiple}
        onChange={handleInputChange}
        style={{ display: "none" }}
      />

      {/* Add button */}
      <Button
        variant="outlined"
        startIcon={<ImageIcon />}
        onClick={() => inputRef.current?.click()}
        fullWidth
        sx={{
          mb: 3,
          textTransform: "none",
          fontWeight: 600,
          borderRadius: 3,
          py: 1.2,
          fontSize: 14,
          borderColor: "divider",
          color: "text.secondary",
          borderStyle: "dashed",
          "&:hover": {
            borderColor: "primary.main",
            backgroundColor: "action.hover",
            borderStyle: "dashed",
          },
        }}
      >
        {addButtonLabel}
      </Button>

      {/* small helper text */}
      <Typography variant="caption" display="block" sx={{ mb: 1, color: "text.secondary" }}>
        {files.length} / {maxFiles} files selected. Max size {(maxFileSize / (1024 * 1024)).toFixed(0)}MB each.
      </Typography>

      {/* previews */}
      {previews.length > 0 && (
        <Box
          sx={{
            mb: 2,
            display: "grid",
            gridTemplateColumns: previews.length === 1 ? "1fr" : "repeat(auto-fill, minmax(180px, 1fr))",
            gap: 1.5,
            p: 2,
            bgcolor: (t) => (t.palette.mode === "dark" ? "rgba(255,255,255,0.02)" : "rgba(0,0,0,0.02)"),
            borderRadius: 3,
            border: "1px solid",
            borderColor: "divider",
          }}
        >
          {previews.map((preview, index) => (
            <Box
              key={index}
              sx={{
                position: "relative",
                paddingTop: previews.length === 1 ? "56.25%" : "100%",
                borderRadius: 2,
                overflow: "hidden",
                bgcolor: "background.default",
                border: "1px solid",
                borderColor: "divider",
              }}
            >
              {preview.type === "image" ? (
                <img
                  src={preview.url}
                  alt={preview.name || `media-${index}`}
                  style={{
                    position: "absolute",
                    top: 0,
                    left: 0,
                    width: "100%",
                    height: "100%",
                    objectFit: "cover",
                  }}
                />
              ) : (
                <video
                  src={preview.url}
                  style={{
                    position: "absolute",
                    top: 0,
                    left: 0,
                    width: "100%",
                    height: "100%",
                    objectFit: "cover",
                  }}
                  controls
                />
              )}

              <IconButton
                size="small"
                onClick={() => handleRemove(index)}
                sx={{
                  position: "absolute",
                  top: 6,
                  right: 6,
                  bgcolor: "rgba(0,0,0,0.6)",
                  color: "white",
                  "&:hover": { bgcolor: "rgba(0,0,0,0.8)" },
                }}
              >
                <CloseIcon fontSize="small" />
              </IconButton>

              <Chip
                icon={preview.type === "image" ? <ImageIcon /> : <VideoLibraryIcon />}
                label={preview.type}
                size="small"
                sx={{
                  position: "absolute",
                  bottom: 6,
                  left: 6,
                  bgcolor: "rgba(0,0,0,0.6)",
                  color: "white",
                  fontSize: 11,
                  height: 22,
                }}
              />
            </Box>
          ))}
        </Box>
      )}

      <Snackbar open={snack.open} autoHideDuration={5000} onClose={handleSnackClose}>
        <Alert onClose={handleSnackClose} severity={snack.severity} variant="filled" sx={{ width: "100%" }}>
          {snack.message}
        </Alert>
      </Snackbar>
    </>
  );
});

MediaUpload.propTypes = {
  accept: PropTypes.string,
  multiple: PropTypes.bool,
  onFilesChange: PropTypes.func.isRequired,
  maxFiles: PropTypes.number,
  maxFileSize: PropTypes.number,
  addButtonLabel: PropTypes.string,
  initialPreviews: PropTypes.array,
};

export default MediaUpload;
