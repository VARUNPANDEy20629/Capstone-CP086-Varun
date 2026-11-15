import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * FitTrack - Gym Membership and Access Management System
 * Represents a registered gym member.
 * 
 * Collaborative Project: Varun Pandey & Sujan Khadka (CP-086)
 */
public class Member {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private String memberId;
    private String name;
    private String contact;
    private String planId;
    private String planName;
    private LocalDate startDate;
    private LocalDate expiryDate;
    private String assignedTrainerId; // "None" or Trainer ID
    private boolean isFrozen;
    private LocalDate freezeStartDate;
    private int frozenDays; // Cumulative days frozen

    public Member(String memberId, String name, String contact, String planId, String planName,
                  LocalDate startDate, LocalDate expiryDate) {
        this(memberId, name, contact, planId, planName, startDate, expiryDate, "None", false, null, 0);
    }

    public Member(String memberId, String name, String contact, String planId, String planName,
                  LocalDate startDate, LocalDate expiryDate, String assignedTrainerId,
                  boolean isFrozen, LocalDate freezeStartDate, int frozenDays) {
        this.memberId = memberId != null ? memberId.trim() : "";
        this.name = name != null ? name.trim() : "";
        this.contact = contact != null ? contact.trim() : "";
        this.planId = planId != null ? planId.trim() : "";
        this.planName = planName != null ? planName.trim() : "";
        this.startDate = startDate != null ? startDate : LocalDate.now();
        this.expiryDate = expiryDate != null ? expiryDate : this.startDate.plusMonths(1);
        this.assignedTrainerId = (assignedTrainerId != null && !assignedTrainerId.trim().isEmpty())
                ? assignedTrainerId.trim() : "None";
        this.isFrozen = isFrozen;
        this.freezeStartDate = freezeStartDate;
        this.frozenDays = Math.max(0, frozenDays);
    }

    // --- Status Verification Methods ---

    /**
     * Checks if the membership is currently expired relative to the given date.
     */
    public boolean isExpired(LocalDate currentDate) {
        if (currentDate == null) currentDate = LocalDate.now();
        return currentDate.isAfter(expiryDate);
    }

    /**
     * Checks if the membership is active and valid for check-in on the given date.
     */
    public boolean isActive(LocalDate currentDate) {
        if (currentDate == null) currentDate = LocalDate.now();
        return !isExpired(currentDate) && !isFrozen;
    }

    /**
     * Checks if the membership will expire within the specified number of days (inclusive).
     */
    public boolean isExpiringSoon(LocalDate currentDate, int daysWindow) {
        if (currentDate == null) currentDate = LocalDate.now();
        if (isExpired(currentDate)) {
            return false;
        }
        LocalDate threshold = currentDate.plusDays(daysWindow);
        return !expiryDate.isAfter(threshold);
    }

    /**
     * Returns the number of days remaining until expiry.
     * Returns negative value if already expired.
     */
    public long getDaysUntilExpiry(LocalDate currentDate) {
        if (currentDate == null) currentDate = LocalDate.now();
        return ChronoUnit.DAYS.between(currentDate, expiryDate);
    }

    /**
     * Freeze membership for a specified number of days (maximum 30 days).
     * Adjusts the expiry date forward by the frozen duration.
     * 
     * @param days Number of days to freeze (1-30)
     * @param freezeDate Starting date of the freeze
     * @return true if successfully frozen, false otherwise
     */
    public boolean freezeMembership(int days, LocalDate freezeDate) {
        if (days < 1 || days > 30) {
            return false;
        }
        if (this.isFrozen) {
            return false; // Already frozen
        }
        if (isExpired(freezeDate)) {
            return false; // Cannot freeze an expired membership
        }

        this.isFrozen = true;
        this.freezeStartDate = freezeDate != null ? freezeDate : LocalDate.now();
        this.frozenDays += days;
        this.expiryDate = this.expiryDate.plusDays(days); // Adjust expiry date based on freeze duration
        return true;
    }

    /**
     * Unfreezes a currently frozen membership.
     */
    public boolean unfreezeMembership() {
        if (!this.isFrozen) {
            return false;
        }
        this.isFrozen = false;
        this.freezeStartDate = null;
        return true;
    }

    public String getStatusDescription(LocalDate currentDate) {
        if (currentDate == null) currentDate = LocalDate.now();
        if (isFrozen) {
            return "FROZEN";
        }
        if (isExpired(currentDate)) {
            long daysAgo = Math.abs(getDaysUntilExpiry(currentDate));
            return "EXPIRED (" + daysAgo + "d ago)";
        }
        long remaining = getDaysUntilExpiry(currentDate);
        if (remaining <= 7) {
            return "EXPIRING SOON (" + remaining + "d left)";
        }
        return "ACTIVE (" + remaining + "d left)";
    }

    // --- Getters and Setters ---

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId != null ? memberId.trim() : "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : "";
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact != null ? contact.trim() : "";
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId != null ? planId.trim() : "";
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName != null ? planName.trim() : "";
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getAssignedTrainerId() {
        return assignedTrainerId;
    }

    public void setAssignedTrainerId(String assignedTrainerId) {
        this.assignedTrainerId = (assignedTrainerId != null && !assignedTrainerId.trim().isEmpty())
                ? assignedTrainerId.trim() : "None";
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }

    public LocalDate getFreezeStartDate() {
        return freezeStartDate;
    }

    public void setFreezeStartDate(LocalDate freezeStartDate) {
        this.freezeStartDate = freezeStartDate;
    }

    public int getFrozenDays() {
        return frozenDays;
    }

    public void setFrozenDays(int frozenDays) {
        this.frozenDays = frozenDays;
    }

    // --- CSV Serialization ---

    public String toCsv() {
        String freezeDateStr = freezeStartDate != null ? freezeStartDate.format(DATE_FORMATTER) : "";
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%b,%s,%d",
                escapeCsv(memberId),
                escapeCsv(name),
                escapeCsv(contact),
                escapeCsv(planId),
                escapeCsv(planName),
                startDate.format(DATE_FORMATTER),
                expiryDate.format(DATE_FORMATTER),
                escapeCsv(assignedTrainerId),
                isFrozen,
                freezeDateStr,
                frozenDays);
    }

    public static Member fromCsv(String csvLine) {
        if (csvLine == null || csvLine.trim().isEmpty()) {
            return null;
        }
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 7) {
            return null;
        }
        try {
            String memberId = parts[0].trim();
            String name = parts[1].trim();
            String contact = parts[2].trim();
            String planId = parts[3].trim();
            String planName = parts[4].trim();
            LocalDate startDate = LocalDate.parse(parts[5].trim(), DATE_FORMATTER);
            LocalDate expiryDate = LocalDate.parse(parts[6].trim(), DATE_FORMATTER);

            String trainerId = (parts.length > 7 && !parts[7].trim().isEmpty()) ? parts[7].trim() : "None";
            boolean isFrozen = parts.length > 8 && Boolean.parseBoolean(parts[8].trim());
            LocalDate freezeStart = null;
            if (parts.length > 9 && !parts[9].trim().isEmpty()) {
                try {
                    freezeStart = LocalDate.parse(parts[9].trim(), DATE_FORMATTER);
                } catch (DateTimeParseException ignored) {
                }
            }
            int frozenDays = 0;
            if (parts.length > 10 && !parts[10].trim().isEmpty()) {
                try {
                    frozenDays = Integer.parseInt(parts[10].trim());
                } catch (NumberFormatException ignored) {
                }
            }

            return new Member(memberId, name, contact, planId, planName,
                    startDate, expiryDate, trainerId, isFrozen, freezeStart, frozenDays);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static String escapeCsv(String value) {
        if (value == null) return "";
        return value.replace(",", ";");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(memberId, member.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId);
    }

    @Override
    public String toString() {
        return String.format("[%s] %-18s | Plan: %-16s | Expires: %s | Status: %s",
                memberId, name, planName, expiryDate.format(DATE_FORMATTER), getStatusDescription(LocalDate.now()));
    }
}
