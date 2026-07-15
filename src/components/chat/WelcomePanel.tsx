import AutoAwesomeRoundedIcon from "@mui/icons-material/AutoAwesomeRounded";
import CodeRoundedIcon from "@mui/icons-material/CodeRounded";
import LightbulbOutlinedIcon from "@mui/icons-material/LightbulbOutlined";
import SchoolOutlinedIcon from "@mui/icons-material/SchoolOutlined";
import Box from "@mui/material/Box";
import Paper from "@mui/material/Paper";
import Stack from "@mui/material/Stack";
import Typography from "@mui/material/Typography";

const suggestions = [
  { icon: <SchoolOutlinedIcon />, text: "Explain a difficult topic step by step" },
  { icon: <CodeRoundedIcon />, text: "Help me understand a piece of code" },
  { icon: <LightbulbOutlinedIcon />, text: "Brainstorm a small project idea" },
];

export function WelcomePanel() {
  return (
    <Stack spacing={4} sx={{ width: "100%", maxWidth: 760, px: 3 }}>
      <Box>
        <AutoAwesomeRoundedIcon color="primary" sx={{ fontSize: 36, mb: 2 }} />
        <Typography variant="h4" component="h1" gutterBottom>Hello. How can I help?</Typography>
        <Typography color="text.secondary">
          This is the frontend foundation. DeepSeek connectivity comes in a later module.
        </Typography>
      </Box>
      <Box sx={{ display: "grid", gridTemplateColumns: { xs: "1fr", sm: "repeat(3, 1fr)" }, gap: 2 }}>
        {suggestions.map((suggestion) => (
          <Paper key={suggestion.text} variant="outlined" sx={{ p: 2.5, minHeight: 150, bgcolor: "background.paper" }}>
            <Stack sx={{ height: "100%", justifyContent: "space-between" }}>
              <Typography>{suggestion.text}</Typography>
              <Box sx={{ color: "primary.main", alignSelf: "flex-end" }}>{suggestion.icon}</Box>
            </Stack>
          </Paper>
        ))}
      </Box>
    </Stack>
  );
}
