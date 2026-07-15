import AttachFileRoundedIcon from "@mui/icons-material/AttachFileRounded";
import SendRoundedIcon from "@mui/icons-material/SendRounded";
import IconButton from "@mui/material/IconButton";
import InputAdornment from "@mui/material/InputAdornment";
import Stack from "@mui/material/Stack";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";

export function MessageComposer() {
  return (
    <Stack spacing={1} sx={{ width: "100%", maxWidth: 840, px: 2, pb: 2 }}>
      <TextField
        fullWidth multiline maxRows={6} placeholder="Ask CRS anything" aria-label="Message"
        slotProps={{ input: {
          startAdornment: <InputAdornment position="start"><IconButton aria-label="Attach a file" disabled><AttachFileRoundedIcon /></IconButton></InputAdornment>,
          endAdornment: <InputAdornment position="end"><IconButton color="primary" aria-label="Send message" disabled><SendRoundedIcon /></IconButton></InputAdornment>,
        } }}
        sx={{ "& .MuiOutlinedInput-root": { borderRadius: 7, bgcolor: "background.paper", pr: 1 } }}
      />
      <Typography variant="caption" color="text.secondary" sx={{ textAlign: "center" }}>
        CRS can make mistakes. Check important information.
      </Typography>
    </Stack>
  );
}
