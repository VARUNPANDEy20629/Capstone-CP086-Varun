import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * FitTrack - Gym Membership and Access Management System
 * Records and monitors daily member check-ins and enforces access control rules.
 * Automatically blocks expired and frozen members.
 * 
 * Collaborative Project: Varun Pandey & Sujan Khadka (CP-086)
 */
public class AttendanceManager {
    public static final String ATTENDANCE_CSV = "data/attendance.csv";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static class AttendanceRecord {
        private String recordId;
        private String memberId;
        private String memberName;
        private LocalDateTime timestamp;
        private String status; // GRANTED, BLOCKED_EXPIRED, BLOCKED_FROZEN, NOT_FOUND
        private String remarks;

        public AttendanceRecord(String recordId, String memberId, String memberName,
                                LocalDateTime timestamp, String status, String remarks) {
            this.recordId = recordId;
            this.memberId = memberId;
            this.memberName = memberName;
            this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
            this.status = status;
            this.remarks = remarks != null ? remarks : "";
        }

        public String getRecordId() { return recordId; }
        public String getMemberId() { return memberId; }
        public String getMemberName() { return memberName; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getStatus() { return status; }
        public String getRemarks() { return remarks; }

        public String toCsv() {
            return String.format("%s,%s,%s,%s,%s,%s",
                    recordId, memberId, escape(memberName),
                    timestamp.format(TIMESTAMP_FORMATTER),
                    status, escape(remarks));
        }

        public static AttendanceRecord fromCsv(String csvLine) {
            if (csvLine == null || csvLine.trim().isEmpty()) return null;
            String[] parts = csvLine.split(",", -1);
            if (parts.length < 5) return null;
            try {
                String id = parts[0].trim();
                String mId = parts[1].trim();
                String mName = parts[2].trim();
                LocalDateTime ts = LocalDateTime.parse(parts[3].trim(), TIMESTAMP_FORMATTER);
                String status = parts[4].trim();
                String remarks = parts.length > 5 ? parts[5].trim() : "";
                return new AttendanceRecord(id, mId, mName, ts, status, remarks);
            } catch (Exception e) {
                return null;
            }
        }

        private static String escape(String s) {
            if (s == null) return "";
            return s.replace(",", ";");
        }
    }

    private final List<AttendanceRecord> records;

    public AttendanceManager() {
        this.records = new ArrayList<>();
        ensureDataDirectoryExists();
        loadAttendance();
        if (records.isEmpty()) {
            initSampleAttendance();
        }
    }

    private void ensureDataDirectoryExists() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    private void initSampleAttendance() {
        LocalDateTime now = LocalDateTime.now();
        records.add(new AttendanceRecord("A1001", "M1001", "Alex Smith",
                now.minusHours(4), "GRANTED", "Access Approved - Active Annual Plan"));
        records.add(new AttendanceRecord("A1002", "M1005", "Bruce Wayne",
                now.minusHours(2), "GRANTED", "Access Approved - Active Half-Yearly Plan"));
        records.add(new AttendanceRecord("A1003", "M1003", "Mike Tyson",
                now.minusMinutes(45), "BLOCKED_EXPIRED", "DENIED: Membership expired on " + LocalDate.now().minusDays(5)));
        saveAttendance();
    }

    public synchronized void loadAttendance() {
        records.clear();
        File file = new File(ATTENDANCE_CSV);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (isHeader) {
                    isHeader = false;
                    if (line.toLowerCase().startsWith("recordid") || line.toLowerCase().startsWith("id")) {
                        continue;
                    }
                }
                AttendanceRecord r = AttendanceRecord.fromCsv(line);
                if (r != null) {
                    records.add(r);
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not read attendance file: " + e.getMessage());
        }
    }

    public synchronized boolean saveAttendance() {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ATTENDANCE_CSV))) {
            writer.write("recordId,memberId,memberName,timestamp,status,remarks");
            writer.newLine();
            for (AttendanceRecord r : records) {
                writer.write(r.toCsv());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving attendance records: " + e.getMessage());
            return false;
        }
    }

    public synchronized String generateNextRecordId() {
        int maxId = 1000;
        for (AttendanceRecord r : records) {
            String idStr = r.getRecordId();
            if (idStr.toUpperCase().startsWith("A")) {
                try {
                    int num = Integer.parseInt(idStr.substring(1));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        return "A" + (maxId + 1);
    }

    /**
     * Checks in a member with strict automated access verification:
     * - Blocks expired memberships
     * - Blocks frozen memberships
     * - Grants access to active members
     * 
     * @param memberId Target member ID
     * @param memberManager MemberManager reference to fetch live member status
     * @return Result message describing access grant or blockage
     */
    public synchronized String recordCheckIn(String memberId, MemberManager memberManager) {
        if (memberId == null || memberId.trim().isEmpty()) {
            return "ERROR: Member ID cannot be empty.";
        }

        Member member = memberManager.getMemberById(memberId.trim());
        if (member == null) {
            return "ERROR: Member ID '" + memberId + "' was not found in the system.";
        }

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        String recId = generateNextRecordId();

        // 1. Check if Membership is Expired
        if (member.isExpired(today)) {
            String reason = "DENIED: Membership expired on " + member.getExpiryDate() + ". Please renew plan.";
            AttendanceRecord rec = new AttendanceRecord(recId, member.getMemberId(), member.getName(),
                    now, "BLOCKED_EXPIRED", reason);
            records.add(rec);
            saveAttendance();
            return "🚫 ACCESS BLOCKED! Member '" + member.getName() + "' (" + member.getMemberId() +
                   ") has an EXPIRED membership.\n   Expired Date: " + member.getExpiryDate() +
                   "\n   Status: Check-in rejected. Membership renewal required.";
        }

        // 2. Check if Membership is Frozen
        if (member.isFrozen()) {
            String reason = "DENIED: Membership is currently FROZEN. Unfreeze required.";
            AttendanceRecord rec = new AttendanceRecord(recId, member.getMemberId(), member.getName(),
                    now, "BLOCKED_FROZEN", reason);
            records.add(rec);
            saveAttendance();
            return "❄️ ACCESS BLOCKED! Member '" + member.getName() + "' (" + member.getMemberId() +
                   ") has a FROZEN membership.\n   Status: Check-in rejected until unfreezing.";
        }

        // 3. Active Membership - Access Granted
        String reason = "Access Approved - Plan: " + member.getPlanName();
        AttendanceRecord rec = new AttendanceRecord(recId, member.getMemberId(), member.getName(),
                now, "GRANTED", reason);
        records.add(rec);
        saveAttendance();

        StringBuilder sb = new StringBuilder();
        sb.append("✅ ACCESS GRANTED! Welcome, ").append(member.getName()).append("!\n");
        sb.append("   Member ID:    ").append(member.getMemberId()).append("\n");
        sb.append("   Plan:         ").append(member.getPlanName()).append("\n");
        sb.append("   Valid Until:  ").append(member.getExpiryDate()).append("\n");
        sb.append("   Check-in at:  ").append(now.format(TIMESTAMP_FORMATTER));

        if (member.isExpiringSoon(today, 7)) {
            sb.append("\n   ⚠️  NOTICE: Your membership expires in ")
              .append(member.getDaysUntilExpiry(today))
              .append(" day(s). Please renew soon!");
        }

        return sb.toString();
    }

    public synchronized List<AttendanceRecord> getAllRecords() {
        return new ArrayList<>(records);
    }

    public synchronized List<AttendanceRecord> getTodayRecords() {
        LocalDate today = LocalDate.now();
        List<AttendanceRecord> todayList = new ArrayList<>();
        for (AttendanceRecord r : records) {
            if (r.getTimestamp().toLocalDate().equals(today)) {
                todayList.add(r);
            }
        }
        return todayList;
    }

    public void displayAttendanceRecords() {
        System.out.println("\n+-------------------------------------------------------------------------------------------------------------------+");
        System.out.printf("| %-113s |\n", "FITTRACK ATTENDANCE LOGS (Total: " + records.size() + ")");
        System.out.println("+---------+---------+--------------------+---------------------+-----------------+------------------------------+");
        System.out.printf("| %-7s | %-7s | %-18s | %-19s | %-15s | %-28s |\n",
                "Log ID", "Mem ID", "Member Name", "Timestamp", "Status", "Remarks");
        System.out.println("+---------+---------+--------------------+---------------------+-----------------+------------------------------+");
        if (records.isEmpty()) {
            System.out.println("| No attendance records recorded yet.                                                                               |");
        } else {
            // Display most recent records last or first
            for (AttendanceRecord r : records) {
                System.out.printf("| %-7s | %-7s | %-18s | %-19s | %-15s | %-28s |\n",
                        r.getRecordId(),
                        r.getMemberId(),
                        truncate(r.getMemberName(), 18),
                        r.getTimestamp().format(TIMESTAMP_FORMATTER),
                        truncate(r.getStatus(), 15),
                        truncate(r.getRemarks(), 28));
            }
        }
        System.out.println("+---------+---------+--------------------+---------------------+-----------------+------------------------------+");
    }

    private String truncate(String val, int maxLen) {
        if (val == null) return "";
        if (val.length() <= maxLen) return val;
        return val.substring(0, maxLen - 2) + "..";
    }
}
