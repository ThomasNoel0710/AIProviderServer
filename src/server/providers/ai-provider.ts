export type ChatRole = "system" | "user" | "assistant";

export interface ChatMessage {
  role: ChatRole;
  content: string;
}

export interface AIProvider {
  stream(messages: ChatMessage[]): AsyncIterable<string>;
}

// This interface is the boundary between our application and any AI vendor.
// The DeepSeek implementation will be added in a later learning module.
