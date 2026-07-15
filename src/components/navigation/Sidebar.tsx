import AddRoundedIcon from "@mui/icons-material/AddRounded";
import ChatBubbleOutlineRoundedIcon from "@mui/icons-material/ChatBubbleOutlineRounded";
import SettingsOutlinedIcon from "@mui/icons-material/SettingsOutlined";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Divider from "@mui/material/Divider";
import List from "@mui/material/List";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import Stack from "@mui/material/Stack";
import Typography from "@mui/material/Typography";

export const SIDEBAR_WIDTH = 280;

export function Sidebar() {
  return (
    <Stack sx={{ height: "100%", p: 2, bgcolor: "#f0f4f9" }}>
      <Typography variant="h6" sx={{ px: 1.5, py: 1 }}>CRS</Typography>
      <Button variant="contained" startIcon={<AddRoundedIcon />} sx={{ alignSelf: "flex-start", my: 2 }}>
        New chat
      </Button>
      <Typography variant="overline" color="text.secondary" sx={{ px: 1.5 }}>Recent</Typography>
      <List aria-label="Recent conversations">
        <ListItemButton selected>
          <ListItemIcon sx={{ minWidth: 40 }}><ChatBubbleOutlineRoundedIcon fontSize="small" /></ListItemIcon>
          <ListItemText primary="Welcome to CRS" slotProps={{ primary: { noWrap: true } }} />
        </ListItemButton>
      </List>
      <Box sx={{ flexGrow: 1 }} />
      <Divider sx={{ mb: 1 }} />
      <List>
        <ListItemButton>
          <ListItemIcon sx={{ minWidth: 40 }}><SettingsOutlinedIcon fontSize="small" /></ListItemIcon>
          <ListItemText primary="Settings" />
        </ListItemButton>
      </List>
    </Stack>
  );
}
