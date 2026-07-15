"use client";

import MenuRoundedIcon from "@mui/icons-material/MenuRounded";
import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Chip from "@mui/material/Chip";
import Drawer from "@mui/material/Drawer";
import IconButton from "@mui/material/IconButton";
import Stack from "@mui/material/Stack";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import useMediaQuery from "@mui/material/useMediaQuery";
import { useTheme } from "@mui/material/styles";
import { useState } from "react";
import { MessageComposer } from "@/components/chat/MessageComposer";
import { WelcomePanel } from "@/components/chat/WelcomePanel";
import { Sidebar, SIDEBAR_WIDTH } from "@/components/navigation/Sidebar";

export function ChatWorkspace() {
  const theme = useTheme();
  const desktop = useMediaQuery(theme.breakpoints.up("md"));
  const [mobileOpen, setMobileOpen] = useState(false);

  return (
    <Box sx={{ display: "flex", minHeight: "100dvh", bgcolor: "background.default" }}>
      <Drawer
        variant={desktop ? "permanent" : "temporary"}
        open={desktop || mobileOpen}
        onClose={() => setMobileOpen(false)}
        ModalProps={{ keepMounted: true }}
        sx={{ width: desktop ? SIDEBAR_WIDTH : 0, flexShrink: 0, "& .MuiDrawer-paper": { width: SIDEBAR_WIDTH, border: 0 } }}
      >
        <Sidebar />
      </Drawer>
      <Stack component="main" sx={{ flex: 1, minWidth: 0, minHeight: "100dvh" }}>
        <AppBar position="static" color="transparent" elevation={0} sx={{ borderBottom: 1, borderColor: "divider" }}>
          <Toolbar>
            {!desktop && (
              <IconButton edge="start" aria-label="Open navigation" onClick={() => setMobileOpen(true)} sx={{ mr: 1 }}>
                <MenuRoundedIcon />
              </IconButton>
            )}
            <Typography variant="h6">DeepSeek Chat</Typography>
            <Chip
              label="Foundation"
              size="small"
              variant="outlined"
              sx={{ ml: 1.5, display: { xs: "none", sm: "inline-flex" } }}
            />
          </Toolbar>
        </AppBar>
        <Box sx={{ flex: 1, display: "grid", placeItems: "center", py: 5, overflowY: "auto" }}>
          <WelcomePanel />
        </Box>
        <Box sx={{ display: "flex", justifyContent: "center" }}><MessageComposer /></Box>
      </Stack>
    </Box>
  );
}
