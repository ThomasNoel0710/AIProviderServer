"use client";

import { createTheme } from "@mui/material/styles";

export const theme = createTheme({
  cssVariables: true,
  palette: {
    mode: "light",
    primary: {
      main: "#0b57d0",
      light: "#d3e3fd",
      dark: "#0842a0",
      contrastText: "#ffffff",
    },
    background: { default: "#f8fafd", paper: "#ffffff" },
    text: { primary: "#1f1f1f", secondary: "#5f6368" },
    divider: "#e0e3e7",
  },
  typography: {
    fontFamily: "Roboto, Arial, sans-serif",
    h4: { fontWeight: 500, letterSpacing: "-0.02em" },
    h6: { fontWeight: 500 },
    button: { fontWeight: 500, textTransform: "none" },
  },
  shape: { borderRadius: 16 },
  components: {
    MuiButton: {
      defaultProps: { disableElevation: true },
      styleOverrides: { root: { borderRadius: 999, paddingInline: 20 } },
    },
    MuiIconButton: { styleOverrides: { root: { borderRadius: 999 } } },
    MuiListItemButton: { styleOverrides: { root: { borderRadius: 999 } } },
  },
});
