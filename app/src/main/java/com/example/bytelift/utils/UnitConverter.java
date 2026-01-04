package com.example.bytelift.utils;

public class UnitConverter {

    // Weight conversions
    public static double kgToLb(double kg) {
        return kg * 2.20462;
    }

    public static double lbToKg(double lb) {
        return lb / 2.20462;
    }

    public static String formatWeight(double value, String unit) {
        return String.format("%.1f %s", value, unit);
    }

    // Height conversions
    public static double cmToInches(double cm) {
        return cm / 2.54;
    }

    public static double inchesToCm(double inches) {
        return inches * 2.54;
    }

    public static String formatHeightCm(double cm) {
        return String.format("%.0f cm", cm);
    }

    public static String formatHeightFtIn(double cm) {
        double totalInches = cmToInches(cm);
        int feet = (int) (totalInches / 12);
        int inches = (int) (totalInches % 12);
        return String.format("%d'%d\"", feet, inches);
    }

    // Display conversions based on user preference
    public static String convertWeight(double canonicalKg, String preferredUnit) {
        if (preferredUnit.equalsIgnoreCase("lb")) {
            return formatWeight(kgToLb(canonicalKg), "lb");
        }
        return formatWeight(canonicalKg, "kg");
    }

    public static String convertHeight(double canonicalCm, String preferredUnit) {
        if (preferredUnit.equalsIgnoreCase("ft")) {
            return formatHeightFtIn(canonicalCm);
        }
        return formatHeightCm(canonicalCm);
    }
}