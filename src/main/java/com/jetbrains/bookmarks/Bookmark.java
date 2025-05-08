package com.jetbrains.bookmarks;

import java.time.Instant;

public record Bookmark(Long id, String title, String url, Instant createdAt) {
}
