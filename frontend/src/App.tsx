import {
  useEffect,
  useRef,
  useState,
  type ReactNode,
} from 'react'
import AddRoundedIcon from '@mui/icons-material/AddRounded'
import ArrowUpwardRoundedIcon from '@mui/icons-material/ArrowUpwardRounded'
import AutoAwesomeRoundedIcon from '@mui/icons-material/AutoAwesomeRounded'
import ChatBubbleOutlineRoundedIcon from '@mui/icons-material/ChatBubbleOutlineRounded'
import DeleteOutlineRoundedIcon from '@mui/icons-material/DeleteOutlineRounded'
import EditRoundedIcon from '@mui/icons-material/EditRounded'
import ExpandMoreRoundedIcon from '@mui/icons-material/ExpandMoreRounded'
import HelpOutlineRoundedIcon from '@mui/icons-material/HelpOutlineRounded'
import MenuOpenRoundedIcon from '@mui/icons-material/MenuOpenRounded'
import MenuRoundedIcon from '@mui/icons-material/MenuRounded'
import SearchRoundedIcon from '@mui/icons-material/SearchRounded'
import SettingsOutlinedIcon from '@mui/icons-material/SettingsOutlined'
import Alert from '@mui/material/Alert'
import Avatar from '@mui/material/Avatar'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import CircularProgress from '@mui/material/CircularProgress'
import Divider from '@mui/material/Divider'
import Dialog from '@mui/material/Dialog'
import DialogActions from '@mui/material/DialogActions'
import DialogContent from '@mui/material/DialogContent'
import DialogTitle from '@mui/material/DialogTitle'
import IconButton from '@mui/material/IconButton'
import List from '@mui/material/List'
import ListItem from '@mui/material/ListItem'
import ListItemButton from '@mui/material/ListItemButton'
import ListItemIcon from '@mui/material/ListItemIcon'
import ListItemText from '@mui/material/ListItemText'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { Route, Routes } from 'react-router'
import {
  ApiError,
  createConversation,
  deleteConversation,
  getConversation,
  listConversations,
  renameConversation,
  sendConversationMessage,
  type ConversationSummary,
} from './services/api'

function BrandMark({ size = 36 }: { size?: number }) {
  return (
    <Box
      sx={{
        display: 'grid',
        width: size,
        height: size,
        flexShrink: 0,
        placeItems: 'center',
        borderRadius: '12px',
        color: 'common.white',
        background:
          'linear-gradient(135deg, #4285f4 0%, #5e97f6 48%, #7b61ff 100%)',
        boxShadow: '0 5px 14px rgba(66, 133, 244, 0.22)',
      }}
    >
      <AutoAwesomeRoundedIcon sx={{ fontSize: size * 0.55 }} />
    </Box>
  )
}

function SidebarItem({
  icon,
  label,
  selected = false,
  onClick,
  onRename,
  onDelete,
}: {
  icon: ReactNode
  label: string
  selected?: boolean
  onClick?: () => void
  onRename?: () => void
  onDelete?: () => void
}) {
  const item = (
    <ListItemButton
      selected={selected}
      aria-label={label}
      onClick={onClick}
      sx={{
        minHeight: 44,
        mx: { xs: 1, md: 1.5 },
        px: { xs: 1.25, md: 1.5 },
        pr: onRename || onDelete ? { xs: 1.25, md: 10 } : undefined,
        borderRadius: 3,
        justifyContent: { xs: 'center', md: 'flex-start' },
        color: selected ? 'primary.dark' : 'text.primary',
        '&.Mui-selected': {
          bgcolor: '#d3e3fd',
          '&:hover': { bgcolor: '#c9dcfa' },
        },
      }}
    >
      <ListItemIcon
        sx={{
          minWidth: { xs: 0, md: 38 },
          color: 'inherit',
          justifyContent: 'center',
        }}
      >
        {icon}
      </ListItemIcon>
      <ListItemText
        primary={label}
        sx={{ display: { xs: 'none', md: 'block' } }}
        slotProps={{
          primary: {
            variant: 'body2',
            noWrap: true,
          },
        }}
      />
    </ListItemButton>
  )

  if (!onRename && !onDelete) {
    return item
  }

  return (
    <ListItem
      disablePadding
      secondaryAction={(
        <Stack
          direction="row"
          spacing={0.25}
          sx={{ display: { xs: 'none', md: 'flex' }, mr: 1 }}
        >
          {onRename && (
            <IconButton
              aria-label={`Rename ${label}`}
              title={`Rename ${label}`}
              onClick={onRename}
              size="small"
            >
              <EditRoundedIcon sx={{ fontSize: 17 }} />
            </IconButton>
          )}
          {onDelete && (
            <IconButton
              aria-label={`Delete ${label}`}
              title={`Delete ${label}`}
              color="error"
              onClick={onDelete}
              size="small"
            >
              <DeleteOutlineRoundedIcon sx={{ fontSize: 18 }} />
            </IconButton>
          )}
        </Stack>
      )}
    >
      {item}
    </ListItem>
  )
}

function Sidebar({
  conversations,
  activeConversationId,
  onNewChat,
  onSelectConversation,
  onRenameConversation,
  onDeleteConversation,
}: {
  conversations: ConversationSummary[]
  activeConversationId: string | null
  onNewChat: () => void
  onSelectConversation: (conversationId: string) => void
  onRenameConversation: (conversation: ConversationSummary) => void
  onDeleteConversation: (conversation: ConversationSummary) => void
}) {
  return (
    <Box
      component="aside"
      sx={{
        width: { xs: 76, md: 280 },
        height: '100dvh',
        flexShrink: 0,
        display: 'flex',
        flexDirection: 'column',
        borderRight: '1px solid',
        borderColor: '#e3e7ee',
        bgcolor: '#f4f7fc',
        overflow: 'hidden',
      }}
    >
      <Stack
        direction="row"
        spacing={1.25}
        sx={{
          minHeight: 76,
          px: { xs: 2.5, md: 2.25 },
          alignItems: 'center',
        }}
      >
        <BrandMark />
        <Typography
          variant="h6"
          sx={{ display: { xs: 'none', md: 'block' }, letterSpacing: '-0.02em' }}
        >
          CRS
        </Typography>
      </Stack>

      <List disablePadding>
        <SidebarItem
          selected={activeConversationId === null}
          icon={<AddRoundedIcon />}
          label="New chat"
          onClick={onNewChat}
        />
        <SidebarItem icon={<SearchRoundedIcon />} label="Search chats" />
      </List>

      <Paper
        variant="outlined"
        sx={{
          mx: { xs: 1, md: 1.5 },
          mt: 2,
          p: { xs: 1.15, md: 1.25 },
          display: 'flex',
          alignItems: 'center',
          justifyContent: { xs: 'center', md: 'flex-start' },
          gap: 1.25,
          borderRadius: 3,
          borderColor: '#dfe4ec',
          bgcolor: 'rgba(255, 255, 255, 0.78)',
        }}
      >
        <AutoAwesomeRoundedIcon
          color="primary"
          sx={{ flexShrink: 0, fontSize: 20 }}
        />
        <Box
          sx={{
            minWidth: 0,
            flexGrow: 1,
            display: { xs: 'none', md: 'block' },
          }}
        >
          <Typography variant="caption" color="text.secondary">
            Current model
          </Typography>
          <Typography variant="body2" noWrap sx={{ fontWeight: 500 }}>
            DeepSeek V4 Flash
          </Typography>
        </Box>
        <ExpandMoreRoundedIcon
          sx={{
            display: { xs: 'none', md: 'block' },
            color: 'text.secondary',
            fontSize: 20,
          }}
        />
      </Paper>

      <Box
        sx={{
          flexGrow: 1,
          minHeight: 0,
          mt: 3,
          overflowY: 'auto',
        }}
      >
        <Typography
          variant="caption"
          color="text.secondary"
          sx={{
            display: { xs: 'none', md: 'block' },
            px: 3,
            mb: 0.75,
            fontWeight: 500,
          }}
        >
          Recent
        </Typography>
        <List disablePadding>
          {conversations.map((conversation) => (
            <SidebarItem
              key={conversation.id}
              icon={<ChatBubbleOutlineRoundedIcon sx={{ fontSize: 19 }} />}
              label={conversation.title}
              selected={conversation.id === activeConversationId}
              onClick={() => onSelectConversation(conversation.id)}
              onRename={() => onRenameConversation(conversation)}
              onDelete={() => onDeleteConversation(conversation)}
            />
          ))}
        </List>
      </Box>

      <Divider sx={{ mx: 2, borderColor: '#dfe4ec' }} />

      <List disablePadding sx={{ py: 1 }}>
        <SidebarItem icon={<HelpOutlineRoundedIcon />} label="Help" />
        <SidebarItem icon={<SettingsOutlinedIcon />} label="Settings" />
      </List>

      <Stack
        direction="row"
        spacing={1.25}
        sx={{
          minHeight: 72,
          px: { xs: 2.25, md: 2 },
          borderTop: '1px solid',
          borderColor: '#e3e7ee',
          alignItems: 'center',
          justifyContent: { xs: 'center', md: 'flex-start' },
        }}
      >
        <Avatar
          sx={{
            width: 34,
            height: 34,
            bgcolor: '#1a73e8',
            fontSize: '0.9rem',
          }}
        >
          T
        </Avatar>
        <Box
          sx={{
            minWidth: 0,
            flexGrow: 1,
            display: { xs: 'none', md: 'block' },
          }}
        >
          <Typography variant="body2" noWrap sx={{ fontWeight: 500 }}>
            Thomas
          </Typography>
          <Typography variant="caption" color="text.secondary" noWrap>
            Local account
          </Typography>
        </Box>
        <ExpandMoreRoundedIcon
          sx={{
            display: { xs: 'none', md: 'block' },
            color: 'text.secondary',
            fontSize: 20,
          }}
        />
      </Stack>
    </Box>
  )
}

interface ChatMessage {
  id: string
  role: 'user' | 'assistant'
  content: string
}

function MessageBubble({ message }: { message: ChatMessage }) {
  const isUser = message.role === 'user'

  return (
    <Stack
      direction="row"
      spacing={1.25}
      sx={{
        width: '100%',
        alignItems: 'flex-start',
        justifyContent: isUser ? 'flex-end' : 'flex-start',
      }}
    >
      {!isUser && <BrandMark size={32} />}
      <Paper
        variant={isUser ? undefined : 'outlined'}
        elevation={0}
        sx={{
          maxWidth: 'min(680px, 82%)',
          px: 2,
          py: 1.5,
          borderColor: '#e0e4ea',
          borderRadius: isUser ? '20px 6px 20px 20px' : '6px 20px 20px 20px',
          bgcolor: isUser ? '#e8f0fe' : 'background.paper',
        }}
      >
        <Typography
          component="div"
          sx={{
            lineHeight: 1.72,
            whiteSpace: 'pre-wrap',
            overflowWrap: 'anywhere',
          }}
        >
          {message.content}
        </Typography>
      </Paper>
    </Stack>
  )
}

function ThinkingBubble() {
  return (
    <Stack
      direction="row"
      spacing={1.25}
      sx={{ width: '100%', alignItems: 'flex-start' }}
    >
      <BrandMark size={32} />
      <Paper
        variant="outlined"
        sx={{
          display: 'flex',
          alignItems: 'center',
          gap: 1.25,
          px: 2,
          py: 1.5,
          borderColor: '#e0e4ea',
          borderRadius: '6px 20px 20px 20px',
        }}
      >
        <CircularProgress size={17} thickness={5} />
        <Typography color="text.secondary" variant="body2">
          Thinking…
        </Typography>
      </Paper>
    </Stack>
  )
}

function ChatHomePage() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true)
  const [draft, setDraft] = useState('')
  const [messages, setMessages] = useState<ChatMessage[]>([])
  const [conversations, setConversations] = useState<ConversationSummary[]>([])
  const [activeConversationId, setActiveConversationId] =
    useState<string | null>(null)
  const [renameTarget, setRenameTarget] =
    useState<ConversationSummary | null>(null)
  const [renameDraft, setRenameDraft] = useState('')
  const [isRenaming, setIsRenaming] = useState(false)
  const [deleteTarget, setDeleteTarget] =
    useState<ConversationSummary | null>(null)
  const [isDeleting, setIsDeleting] = useState(false)
  const [deleteError, setDeleteError] = useState<string | null>(null)
  const [isConversationLoading, setIsConversationLoading] = useState(false)
  const [isSending, setIsSending] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const messagesEndRef = useRef<HTMLDivElement | null>(null)
  const requestControllerRef = useRef<AbortController | null>(null)
  const activeConversation = conversations.find(
    (conversation) => conversation.id === activeConversationId,
  ) ?? null

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages, isSending, error])

  useEffect(() => {
    return () => requestControllerRef.current?.abort()
  }, [])

  useEffect(() => {
    const controller = new AbortController()

    void listConversations(controller.signal)
      .then(setConversations)
      .catch((requestError: unknown) => {
        if (
          requestError instanceof DOMException
          && requestError.name === 'AbortError'
        ) {
          return
        }

        setError(
          requestError instanceof ApiError
            ? requestError.message
            : 'Unable to load conversation history.',
        )
      })

    return () => controller.abort()
  }, [])

  const startNewChat = () => {
    requestControllerRef.current?.abort()
    requestControllerRef.current = null
    setActiveConversationId(null)
    setMessages([])
    setDraft('')
    setError(null)
    setIsConversationLoading(false)
    setIsSending(false)
  }

  const openRenameDialog = (conversation: ConversationSummary) => {
    setRenameTarget(conversation)
    setRenameDraft(conversation.title)
  }

  const closeRenameDialog = () => {
    if (isRenaming) {
      return
    }

    setRenameTarget(null)
    setRenameDraft('')
  }

  const submitRename = async () => {
    const title = renameDraft.trim()

    if (!renameTarget || !title || isRenaming) {
      return
    }

    setIsRenaming(true)
    setError(null)

    try {
      const renamed = await renameConversation(renameTarget.id, title)
      setConversations((current) => current.map((conversation) => (
        conversation.id === renamed.id ? renamed : conversation
      )))
      setRenameTarget(null)
      setRenameDraft('')
    } catch (requestError) {
      setError(
        requestError instanceof ApiError
          ? requestError.message
          : 'Unable to rename the conversation.',
      )
    } finally {
      setIsRenaming(false)
    }
  }

  const openDeleteDialog = (conversation: ConversationSummary) => {
    setDeleteTarget(conversation)
    setDeleteError(null)
  }

  const closeDeleteDialog = () => {
    if (isDeleting) {
      return
    }

    setDeleteTarget(null)
    setDeleteError(null)
  }

  const submitDelete = async () => {
    if (!deleteTarget || isDeleting) {
      return
    }

    const conversationId = deleteTarget.id
    setIsDeleting(true)
    setDeleteError(null)

    try {
      await deleteConversation(conversationId)
      setConversations((current) => current.filter(
        (conversation) => conversation.id !== conversationId,
      ))

      if (activeConversationId === conversationId) {
        requestControllerRef.current?.abort()
        requestControllerRef.current = null
        setActiveConversationId(null)
        setMessages([])
        setDraft('')
        setIsConversationLoading(false)
        setIsSending(false)
      }

      setDeleteTarget(null)
    } catch (requestError) {
      setDeleteError(
        requestError instanceof ApiError
          ? requestError.message
          : 'Unable to delete the conversation.',
      )
    } finally {
      setIsDeleting(false)
    }
  }

  const selectConversation = async (conversationId: string) => {
    requestControllerRef.current?.abort()
    const controller = new AbortController()
    requestControllerRef.current = controller
    setActiveConversationId(conversationId)
    setMessages([])
    setError(null)
    setIsSending(false)
    setIsConversationLoading(true)

    try {
      const conversation = await getConversation(
        conversationId,
        controller.signal,
      )
      setMessages(
        conversation.messages.map((message) => ({
          id: message.id,
          role: message.role,
          content: message.content,
        })),
      )
    } catch (requestError) {
      if (
        requestError instanceof DOMException
        && requestError.name === 'AbortError'
      ) {
        return
      }

      setError(
        requestError instanceof ApiError
          ? requestError.message
          : 'Unable to load the conversation.',
      )
    } finally {
      if (requestControllerRef.current === controller) {
        requestControllerRef.current = null
        setIsConversationLoading(false)
      }
    }
  }

  const sendMessage = async () => {
    const message = draft.trim()

    if (!message || isSending || isConversationLoading) {
      return
    }

    const controller = new AbortController()
    requestControllerRef.current = controller

    setMessages((current) => [
      ...current,
      {
        id: crypto.randomUUID(),
        role: 'user',
        content: message,
      },
    ])
    setDraft('')
    setError(null)
    setIsSending(true)

    try {
      let conversationId = activeConversationId

      if (conversationId === null) {
        const conversation = await createConversation(controller.signal)
        conversationId = conversation.id
        setActiveConversationId(conversation.id)
        setConversations((current) => [conversation, ...current])
      }

      const response = await sendConversationMessage(
        conversationId,
        message,
        controller.signal,
      )

      setMessages((current) => [
        ...current,
        {
          id: response.id,
          role: 'assistant',
          content: response.content,
        },
      ])

      try {
        setConversations(await listConversations(controller.signal))
      } catch (historyError) {
        if (
          historyError instanceof DOMException
          && historyError.name === 'AbortError'
        ) {
          throw historyError
        }
      }
    } catch (requestError) {
      if (
        requestError instanceof DOMException
        && requestError.name === 'AbortError'
      ) {
        return
      }

      setError(
        requestError instanceof ApiError
          ? requestError.message
          : 'An unexpected error occurred while sending the message. Please try again.',
      )
    } finally {
      if (requestControllerRef.current === controller) {
        requestControllerRef.current = null
        setIsSending(false)
      }
    }
  }

  return (
    <Box
      sx={{
        minHeight: '100dvh',
        display: 'flex',
        bgcolor: '#ffffff',
      }}
    >
      {isSidebarOpen && (
        <Sidebar
          conversations={conversations}
          activeConversationId={activeConversationId}
          onNewChat={startNewChat}
          onSelectConversation={(conversationId) => {
            void selectConversation(conversationId)
          }}
          onRenameConversation={openRenameDialog}
          onDeleteConversation={openDeleteDialog}
        />
      )}

      <Box
        component="main"
        sx={{
          position: 'relative',
          minWidth: 0,
          height: '100dvh',
          flexGrow: 1,
          display: 'flex',
          flexDirection: 'column',
          background:
            'radial-gradient(circle at 50% 36%, rgba(232, 240, 254, 0.65), transparent 32%), #ffffff',
        }}
      >
        <IconButton
          onClick={() => setIsSidebarOpen((open) => !open)}
          aria-label={isSidebarOpen ? 'Collapse sidebar' : 'Expand sidebar'}
          title={isSidebarOpen ? 'Collapse sidebar' : 'Expand sidebar'}
          sx={{
            position: 'absolute',
            top: 16,
            left: 16,
            zIndex: 2,
            width: 42,
            height: 42,
            border: '1px solid',
            borderColor: '#e0e4ea',
            bgcolor: 'rgba(255, 255, 255, 0.9)',
            boxShadow: '0 2px 8px rgba(60, 64, 67, 0.08)',
            '&:hover': { bgcolor: '#f1f5fb' },
          }}
        >
          {isSidebarOpen ? <MenuOpenRoundedIcon /> : <MenuRoundedIcon />}
        </IconButton>

        {activeConversation && (
          <IconButton
            aria-label={`Delete ${activeConversation.title}`}
            title={`Delete ${activeConversation.title}`}
            color="error"
            onClick={() => openDeleteDialog(activeConversation)}
            sx={{
              position: 'absolute',
              top: 16,
              right: 16,
              zIndex: 2,
              width: 42,
              height: 42,
              border: '1px solid',
              borderColor: '#f1c7c7',
              bgcolor: 'rgba(255, 255, 255, 0.9)',
              boxShadow: '0 2px 8px rgba(60, 64, 67, 0.08)',
              '&:hover': { bgcolor: '#fce8e6' },
            }}
          >
            <DeleteOutlineRoundedIcon />
          </IconButton>
        )}

        <Box
          sx={{
            flexGrow: 1,
            minHeight: 0,
            overflowY: 'auto',
          }}
        >
          {isConversationLoading ? (
            <Box
              sx={{
                height: '100%',
                display: 'grid',
                placeItems: 'center',
              }}
            >
              <CircularProgress />
            </Box>
          ) : messages.length === 0 && !isSending ? (
            <Box
              sx={{
                height: '100%',
                display: 'grid',
                placeItems: 'center',
                px: 3,
                pb: { xs: 8, sm: 11 },
              }}
            >
              <Typography
                component="h1"
                variant="h3"
                sx={{
                  textAlign: 'center',
                  fontSize: { xs: '2rem', sm: '2.7rem', lg: '3rem' },
                  background:
                    'linear-gradient(90deg, #1a73e8 0%, #7b61ff 55%, #d96570 100%)',
                  backgroundClip: 'text',
                  color: 'transparent',
                }}
              >
                What would you like to talk about today?
              </Typography>
            </Box>
          ) : (
            <Stack
              spacing={2.75}
              sx={{
                width: '100%',
                maxWidth: 820,
                mx: 'auto',
                px: { xs: 2, sm: 3 },
                pt: { xs: 9, sm: 10 },
                pb: 3,
              }}
            >
              {messages.map((message) => (
                <MessageBubble key={message.id} message={message} />
              ))}
              {isSending && <ThinkingBubble />}
              <Box ref={messagesEndRef} />
            </Stack>
          )}
        </Box>

        <Box
          sx={{
            width: '100%',
            maxWidth: 820,
            mx: 'auto',
            px: { xs: 2, sm: 3 },
            pb: { xs: 2, sm: 3 },
          }}
        >
          {error && (
            <Alert
              severity="error"
              onClose={() => setError(null)}
              sx={{ mb: 1.5, borderRadius: 3 }}
            >
              {error}
            </Alert>
          )}

          <Paper
            component="form"
            onSubmit={(event) => {
              event.preventDefault()
              void sendMessage()
            }}
            elevation={0}
            sx={{
              display: 'flex',
              alignItems: 'flex-end',
              gap: 1,
              p: 1,
              pl: 2.25,
              border: '1px solid',
              borderColor: '#dfe3e7',
              borderRadius: 5,
              bgcolor: 'background.paper',
              boxShadow:
                '0 1px 2px rgba(60, 64, 67, 0.08), 0 10px 30px rgba(60, 64, 67, 0.1)',
            }}
          >
            <TextField
              fullWidth
              multiline
              maxRows={5}
              value={draft}
              disabled={isSending || isConversationLoading}
              placeholder={
                isSending
                  ? 'Waiting for a response…'
                  : 'Type a message'
              }
              onChange={(event) => setDraft(event.target.value)}
              onKeyDown={(event) => {
                if (
                  event.key === 'Enter'
                  && !event.shiftKey
                  && !event.nativeEvent.isComposing
                ) {
                  event.preventDefault()
                  void sendMessage()
                }
              }}
              variant="standard"
              slotProps={{
                htmlInput: {
                  'aria-label': 'Message input',
                },
                input: {
                  disableUnderline: true,
                  sx: {
                    py: 1,
                    fontSize: '1rem',
                    lineHeight: 1.55,
                  },
                },
              }}
            />
            <IconButton
              type="submit"
              disabled={
                !draft.trim()
                || isSending
                || isConversationLoading
              }
              aria-label="Send message"
              sx={{
                width: 42,
                height: 42,
                mb: 0.15,
                bgcolor: 'primary.main',
                color: 'common.white',
                '&:hover': { bgcolor: 'primary.dark' },
                '&.Mui-disabled': {
                  bgcolor: '#e8eaed',
                  color: '#9aa0a6',
                },
              }}
            >
              {isSending ? (
                <CircularProgress size={20} color="inherit" />
              ) : (
                <ArrowUpwardRoundedIcon />
              )}
            </IconButton>
          </Paper>
        </Box>
      </Box>

      <Dialog
        open={renameTarget !== null}
        onClose={closeRenameDialog}
        fullWidth
        maxWidth="xs"
      >
        <DialogTitle>Rename conversation</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            fullWidth
            label="Conversation title"
            value={renameDraft}
            disabled={isRenaming}
            onChange={(event) => setRenameDraft(event.target.value)}
            onKeyDown={(event) => {
              if (event.key === 'Enter') {
                event.preventDefault()
                void submitRename()
              }
            }}
            slotProps={{
              htmlInput: {
                maxLength: 200,
              },
            }}
            sx={{ mt: 1 }}
          />
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={closeRenameDialog} disabled={isRenaming}>
            Cancel
          </Button>
          <Button
            variant="contained"
            onClick={() => {
              void submitRename()
            }}
            disabled={!renameDraft.trim() || isRenaming}
          >
            {isRenaming ? 'Saving…' : 'Save'}
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog
        open={deleteTarget !== null}
        onClose={closeDeleteDialog}
        fullWidth
        maxWidth="xs"
      >
        <DialogTitle>Delete conversation?</DialogTitle>
        <DialogContent>
          <Typography>
            “{deleteTarget?.title}” and all of its messages will be permanently
            deleted. This action cannot be undone.
          </Typography>
          {deleteError && (
            <Alert severity="error" sx={{ mt: 2 }}>
              {deleteError}
            </Alert>
          )}
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={closeDeleteDialog} disabled={isDeleting}>
            Cancel
          </Button>
          <Button
            variant="contained"
            color="error"
            onClick={() => {
              void submitDelete()
            }}
            disabled={isDeleting}
          >
            {isDeleting ? 'Deleting…' : 'Delete'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}

export default function App() {
  return (
    <Routes>
      <Route path="*" element={<ChatHomePage />} />
    </Routes>
  )
}
