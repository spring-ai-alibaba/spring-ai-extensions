/*
 * Copyright 2024-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.dashscope.rag.util;

/**
 * Utility class for formatting file sizes to human-readable strings
 *
 * <p>Converts byte sizes to appropriate units (B, KB, MB, GB, TB)
 * with proper formatting.
 *
 * <p>Examples:
 * <ul>
 *   <li>512 bytes → "512 B"</li>
 *   <li>1536 bytes → "1.50 KB"</li>
 *   <li>1048576 bytes → "1.00 MB"</li>
 *   <li>1073741824 bytes → "1.00 GB"</li>
 * </ul>
 *
 * @author kevin
 * @since 2025/11/27
 */
public final class FileSizeFormatter {

    /**
     * 1 KB in bytes
     */
    private static final long KILOBYTE = 1024L;

    /**
     * 1 MB in bytes
     */
    private static final long MEGABYTE = KILOBYTE * 1024L;

    /**
     * 1 GB in bytes
     */
    private static final long GIGABYTE = MEGABYTE * 1024L;

    /**
     * 1 TB in bytes
     */
    private static final long TERABYTE = GIGABYTE * 1024L;

    /**
     * Private constructor to prevent instantiation
     */
    private FileSizeFormatter() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    /**
     * Formats file size to human-readable string with 2 decimal places
     *
     * <p>Automatically selects the most appropriate unit (B, KB, MB, GB, TB)
     * based on the size.
     *
     * @param bytes file size in bytes
     * @return formatted string (e.g., "10.50 MB", "1.23 GB")
     */
    public static String format(long bytes) {
        return format(bytes, 2);
    }

    /**
     * Formats file size to human-readable string with specified decimal places
     *
     * @param bytes         file size in bytes
     * @param decimalPlaces number of decimal places (0-10)
     * @return formatted string
     * @throws IllegalArgumentException if decimalPlaces is out of range
     */
    public static String format(long bytes, int decimalPlaces) {
        if (decimalPlaces < 0 || decimalPlaces > 10) {
            throw new IllegalArgumentException(
                    "Decimal places must be between 0 and 10, got: " + decimalPlaces);
        }

        if (bytes < 0) {
            return "Invalid size";
        }

        if (bytes < KILOBYTE) {
            return bytes + " B";
        } else if (bytes < MEGABYTE) {
            return formatWithUnit(bytes, KILOBYTE, "KB", decimalPlaces);
        } else if (bytes < GIGABYTE) {
            return formatWithUnit(bytes, MEGABYTE, "MB", decimalPlaces);
        } else if (bytes < TERABYTE) {
            return formatWithUnit(bytes, GIGABYTE, "GB", decimalPlaces);
        } else {
            return formatWithUnit(bytes, TERABYTE, "TB", decimalPlaces);
        }
    }

    /**
     * Formats file size with specific unit
     *
     * @param bytes         file size in bytes
     * @param divisor       divisor for the unit
     * @param unit          unit name
     * @param decimalPlaces number of decimal places
     * @return formatted string
     */
    private static String formatWithUnit(long bytes, long divisor, String unit, int decimalPlaces) {
        double value = (double) bytes / divisor;
        String formatPattern = "%." + decimalPlaces + "f %s";
        return String.format(formatPattern, value, unit);
    }

    /**
     * Parses human-readable file size string to bytes
     *
     * <p>Supports formats like:
     * <ul>
     *   <li>"100" or "100B" → 100 bytes</li>
     *   <li>"1.5KB" or "1.5 KB" → 1536 bytes</li>
     *   <li>"10MB" or "10 MB" → 10485760 bytes</li>
     *   <li>"1GB" or "1 GB" → 1073741824 bytes</li>
     * </ul>
     *
     * @param sizeStr size string to parse
     * @return size in bytes
     * @throws IllegalArgumentException if format is invalid
     */
    public static long parse(String sizeStr) {
        if (sizeStr == null || sizeStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Size string cannot be null or empty");
        }

        String trimmed = sizeStr.trim().toUpperCase();

        // Remove spaces between number and unit
        trimmed = trimmed.replaceAll("\\s+", "");

        // Try to extract number and unit
        String numberPart;
        String unitPart;

        if (trimmed.matches("^[0-9.]+$")) {
            // Pure number, assume bytes
            numberPart = trimmed;
            unitPart = "B";
        } else if (trimmed.matches("^[0-9.]+[A-Z]+$")) {
            // Number followed by unit
            int unitIndex = trimmed.length();
            for (int i = 0; i < trimmed.length(); i++) {
                if (Character.isLetter(trimmed.charAt(i))) {
                    unitIndex = i;
                    break;
                }
            }
            numberPart = trimmed.substring(0, unitIndex);
            unitPart = trimmed.substring(unitIndex);
        } else {
            throw new IllegalArgumentException("Invalid size format: " + sizeStr);
        }

        // Parse the number
        double number;
        try {
            number = Double.parseDouble(numberPart);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + numberPart, e);
        }

        // Parse the unit and calculate bytes
        long multiplier;
        switch (unitPart) {
            case "B":
                multiplier = 1L;
                break;
            case "KB":
            case "K":
                multiplier = KILOBYTE;
                break;
            case "MB":
            case "M":
                multiplier = MEGABYTE;
                break;
            case "GB":
            case "G":
                multiplier = GIGABYTE;
                break;
            case "TB":
            case "T":
                multiplier = TERABYTE;
                break;
            default:
                throw new IllegalArgumentException("Unknown unit: " + unitPart);
        }

        return (long) (number * multiplier);
    }

    /**
     * Formats file size in a compact format (no decimal places for small sizes)
     *
     * @param bytes file size in bytes
     * @return compact formatted string
     */
    public static String formatCompact(long bytes) {
        if (bytes < KILOBYTE) {
            return bytes + " B";
        } else if (bytes < MEGABYTE) {
            long kb = bytes / KILOBYTE;
            return kb + " KB";
        } else if (bytes < GIGABYTE) {
            long mb = bytes / MEGABYTE;
            return mb + " MB";
        } else if (bytes < TERABYTE) {
            long gb = bytes / GIGABYTE;
            return gb + " GB";
        } else {
            long tb = bytes / TERABYTE;
            return tb + " TB";
        }
    }

    /**
     * Converts bytes to kilobytes
     *
     * @param bytes size in bytes
     * @return size in kilobytes
     */
    public static double toKilobytes(long bytes) {
        return (double) bytes / KILOBYTE;
    }

    /**
     * Converts bytes to megabytes
     *
     * @param bytes size in bytes
     * @return size in megabytes
     */
    public static double toMegabytes(long bytes) {
        return (double) bytes / MEGABYTE;
    }

    /**
     * Converts bytes to gigabytes
     *
     * @param bytes size in bytes
     * @return size in gigabytes
     */
    public static double toGigabytes(long bytes) {
        return (double) bytes / GIGABYTE;
    }

    /**
     * Converts bytes to terabytes
     *
     * @param bytes size in bytes
     * @return size in terabytes
     */
    public static double toTerabytes(long bytes) {
        return (double) bytes / TERABYTE;
    }
}
