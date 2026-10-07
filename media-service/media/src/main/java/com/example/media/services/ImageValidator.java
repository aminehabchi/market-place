package com.example.media.services;

import java.util.Locale;
import java.util.Set;

/**
 * Upload rules shared by product images and avatars. Media is served from the
 * same origin as the app, so anything a browser could execute (SVG, HTML, ...)
 * must never be stored or served with its declared type.
 */
public final class ImageValidator {

    public static final long MAX_IMAGE_BYTES = 5L * 1024 * 1024;

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/png", "image/jpeg", "image/gif", "image/webp");

    private ImageValidator() {
    }

    /** Lower-cased media type without parameters, or null. */
    public static String normalize(String mimeType) {
        if (mimeType == null) {
            return null;
        }
        int separator = mimeType.indexOf(';');
        String base = separator >= 0 ? mimeType.substring(0, separator) : mimeType;
        return base.trim().toLowerCase(Locale.ROOT);
    }

    public static boolean isAllowedMimeType(String mimeType) {
        String normalized = normalize(mimeType);
        return normalized != null && ALLOWED_MIME_TYPES.contains(normalized);
    }

    /** True when the leading bytes are the signature of the declared type. */
    public static boolean matchesSignature(String mimeType, byte[] bytes) {
        if (bytes == null) {
            return false;
        }
        String normalized = normalize(mimeType);
        if (normalized == null) {
            return false;
        }
        return switch (normalized) {
            case "image/png" -> startsWith(bytes, 0, 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A);
            case "image/jpeg" -> startsWith(bytes, 0, 0xFF, 0xD8, 0xFF);
            case "image/gif" -> startsWith(bytes, 0, 'G', 'I', 'F', '8');
            case "image/webp" -> startsWith(bytes, 0, 'R', 'I', 'F', 'F') && startsWith(bytes, 8, 'W', 'E', 'B', 'P');
            default -> false;
        };
    }

    /** Content type to serve a stored file with; legacy unsafe types are downgraded. */
    public static String safeContentType(String storedMimeType) {
        return isAllowedMimeType(storedMimeType) ? normalize(storedMimeType) : "application/octet-stream";
    }

    private static boolean startsWith(byte[] bytes, int offset, int... signature) {
        if (bytes.length < offset + signature.length) {
            return false;
        }
        for (int i = 0; i < signature.length; i++) {
            if ((bytes[offset + i] & 0xFF) != signature[i]) {
                return false;
            }
        }
        return true;
    }
}
