import ApiRoundedIcon from '@mui/icons-material/ApiRounded'
import AutoAwesomeRoundedIcon from '@mui/icons-material/AutoAwesomeRounded'
import CodeRoundedIcon from '@mui/icons-material/CodeRounded'
import AppBar from '@mui/material/AppBar'
import Box from '@mui/material/Box'
import Chip from '@mui/material/Chip'
import Container from '@mui/material/Container'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import Toolbar from '@mui/material/Toolbar'
import Typography from '@mui/material/Typography'
import { Route, Routes } from 'react-router'

const modules = [
  {
    icon: <CodeRoundedIcon color="primary" />,
    title: 'React frontend',
    detail: 'Vite, TypeScript and Material UI are ready.',
  },
  {
    icon: <ApiRoundedIcon color="primary" />,
    title: 'Spring Boot backend',
    detail: 'The Java service exposes a basic health endpoint.',
  },
  {
    icon: <AutoAwesomeRoundedIcon color="primary" />,
    title: 'DeepSeek integration',
    detail: 'Reserved for the next learning module.',
  },
]

function HomePage() {
  return (
    <Box sx={{ minHeight: '100dvh', bgcolor: 'background.default' }}>
      <AppBar
        position="static"
        color="transparent"
        elevation={0}
        sx={{ borderBottom: 1, borderColor: 'divider' }}
      >
        <Toolbar>
          <AutoAwesomeRoundedIcon color="primary" sx={{ mr: 1.5 }} />
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            CRS
          </Typography>
          <Chip label="Project skeleton" size="small" variant="outlined" />
        </Toolbar>
      </AppBar>

      <Container component="main" maxWidth="lg" sx={{ py: { xs: 6, md: 10 } }}>
        <Stack spacing={2} sx={{ maxWidth: 720, mb: 6 }}>
          <Typography variant="overline" color="primary.main">
            Foundation module
          </Typography>
          <Typography component="h1" variant="h3">
            A clean foundation for your AI chat project.
          </Typography>
          <Typography variant="h6" color="text.secondary" sx={{ fontWeight: 400 }}>
            The frontend and backend are separated, runnable and ready for us to
            expand one module at a time.
          </Typography>
        </Stack>

        <Box
          sx={{
            display: 'grid',
            gridTemplateColumns: { xs: '1fr', md: 'repeat(3, 1fr)' },
            gap: 2,
          }}
        >
          {modules.map((module) => (
            <Paper key={module.title} variant="outlined" sx={{ p: 3, minHeight: 190 }}>
              <Stack spacing={2}>
                {module.icon}
                <Typography variant="h6">{module.title}</Typography>
                <Typography color="text.secondary">{module.detail}</Typography>
              </Stack>
            </Paper>
          ))}
        </Box>
      </Container>
    </Box>
  )
}

export default function App() {
  return (
    <Routes>
      <Route path="*" element={<HomePage />} />
    </Routes>
  )
}
