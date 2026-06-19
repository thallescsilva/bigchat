package com.bcb.bigchat.auth.web;

import java.util.UUID;

public record AuthResponse(String token, UUID clientId, String name) {}
