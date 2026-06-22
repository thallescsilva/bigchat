package com.bcb.bigchat.shared.security;

import java.util.UUID;

public record AuthenticatedClient(UUID clientId, String documentId, boolean admin) {}
