import java.util.Objects;

/**
 * FitTrack - Gym Membership and Access Management System
 * Represents a membership plan available at the gym.
 * 
 * Collaborative Project: Varun Pandey & Sujan Khadka (CP-086)
 */
public class MembershipPlan {
    private String planId;
    private String name;
    private int durationMonths;
    private double price;

    public MembershipPlan(String planId, String name, int durationMonths, double price) {
        this.planId = planId != null ? planId.trim() : "";
        this.name = name != null ? name.trim() : "";
        this.durationMonths = durationMonths;
        this.price = price;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId != null ? planId.trim() : "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : "";
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Converts this plan into a CSV-formatted string.
     */
    public String toCsv() {
        return String.format("%s,%s,%d,%.2f",
                escapeCsv(planId),
                escapeCsv(name),
                durationMonths,
                price);
    }

    /**
     * Creates a MembershipPlan object from a CSV line.
     */
    public static MembershipPlan fromCsv(String csvLine) {
        if (csvLine == null || csvLine.trim().isEmpty()) {
            return null;
        }
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 4) {
            return null;
        }
        try {
            String planId = parts[0].trim();
            String name = parts[1].trim();
            int durationMonths = Integer.parseInt(parts[2].trim());
            double price = Double.parseDouble(parts[3].trim());
            return new MembershipPlan(planId, name, durationMonths, price);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String escapeCsv(String value) {
        if (value == null) return "";
        return value.replace(",", ";"); // Prevent CSV breakage
    }

    @Override
    public String toString() {
        return String.format("[%s] %-20s | Duration: %2d month(s) | Price: $%.2f",
                planId, name, durationMonths, price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MembershipPlan that = (MembershipPlan) o;
        return Objects.equals(planId, that.planId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(planId);
    }
}
