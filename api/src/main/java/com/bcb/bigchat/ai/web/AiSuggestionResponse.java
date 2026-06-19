package com.bcb.bigchat.ai.web;

import java.util.List;

public record AiSuggestionResponse(List<String> suggestions, boolean fallback) {}
