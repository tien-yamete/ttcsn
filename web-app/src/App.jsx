// src/App.jsx
import React from "react";
import { CssBaseline, ThemeProvider, createTheme } from "@mui/material";
import AppRoutes from "./routes/AppRoutes";

export const ColorModeContext = React.createContext({
  mode: "light",
  toggle: () => {},
});

const getInitialMode = () => {
  const saved = localStorage.getItem("color-mode");
  if (saved === "light" || saved === "dark") return saved;
  return window.matchMedia?.("(prefers-color-scheme: dark)").matches ? "dark" : "light";
};

export default function App() {
  const [mode, setMode] = React.useState(getInitialMode);

  const colorMode = React.useMemo(
    () => ({
      mode,
      toggle: () =>
        setMode((prev) => {
          const next = prev === "light" ? "dark" : "light";
          localStorage.setItem("color-mode", next);
          return next;
        }),
    }),
    [mode]
  );

  const theme = React.useMemo(
    () =>
      createTheme({
        palette: {
          mode,
          background: {
            default: mode === "dark" ? "#0b0c10" : "#f7f7fb",
            paper: mode === "dark" ? "#111319" : "#ffffff",
          },
        },
        shape: { borderRadius: 12 },
      }),
    [mode]
  );

  return (
    <ColorModeContext.Provider value={colorMode}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <AppRoutes />
      </ThemeProvider>
    </ColorModeContext.Provider>
  );
}
