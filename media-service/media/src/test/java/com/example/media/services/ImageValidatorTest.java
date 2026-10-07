package com.example.media.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class ImageValidatorTest {

    private static final byte[] PNG = { (byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A };
    private static final byte[] JPEG = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0 };
    private static final byte[] WEBP = "RIFF\0\0\0\0WEBPVP8 ".getBytes(StandardCharsets.ISO_8859_1);

    @Test
    void allowsOnlyRasterImageTypes() {
        assertTrue(ImageValidator.isAllowedMimeType("image/png"));
        assertTrue(ImageValidator.isAllowedMimeType("IMAGE/JPEG; charset=binary"));
        assertFalse(ImageValidator.isAllowedMimeType("image/svg+xml"));
        assertFalse(ImageValidator.isAllowedMimeType("text/html"));
        assertFalse(ImageValidator.isAllowedMimeType(null));
    }

    @Test
    void requiresMatchingSignature() {
        assertTrue(ImageValidator.matchesSignature("image/png", PNG));
        assertTrue(ImageValidator.matchesSignature("image/jpeg", JPEG));
        assertTrue(ImageValidator.matchesSignature("image/webp", WEBP));
        assertFalse(ImageValidator.matchesSignature("image/png", JPEG));
        assertFalse(ImageValidator.matchesSignature("image/png", "<svg/>".getBytes(StandardCharsets.UTF_8)));
        assertFalse(ImageValidator.matchesSignature("image/png", new byte[0]));
    }

    @Test
    void downgradesUnsafeStoredTypes() {
        assertEquals("image/png", ImageValidator.safeContentType("image/png"));
        assertEquals("application/octet-stream", ImageValidator.safeContentType("image/svg+xml"));
        assertEquals("application/octet-stream", ImageValidator.safeContentType(null));
    }
}
