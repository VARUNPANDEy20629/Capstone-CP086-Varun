import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FitTrack - Gym Membership and Access Management System
 * Manages gym members, trainer assignments, expiry tracking, and membership freezing.
 * 
 * Collaborative Project: Varun Pandey & Sujan Khadka (CP-086)
 */
public class MemberManager {
    private static final String MEMBERS_CSV = "data/members.csv";
    private static final String TRAINERS_CSV = "data/trainers.csv";

    private final List<Member> members;
    private final List<Trainer> trainers;

    public MemberManager() {
        this.members = new ArrayList<>();
        this.trainers = new ArrayList<>();
        ensureDataDirectoryExists();
        loadTrainers();
        if (trainers.isEmpty()) {
            initDefaultTrainers();
        }
        loadMembers();
        if (members.isEmpty()) {
            initDefaultMembers();
        }
    }

    private void ensureDataDirectoryExists() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    // =========================================================================
    // TRAINER MANAGEMENT
    // =========================================================================

    private void initDefaultTrainers() {
        trainers.add(new Trainer("T01", "Marcus Vance", "Strength & Conditioning", "555-0101"));
        trainers.add(new Trainer("T02", "Elena Rostova", "Yoga & Flexibility", "555-0102"));
        trainers.add(new Trainer("T03", "David Kim", "HIIT & Cardio", "555-0103"));
        saveTrainers();
    }

    public synchronized void loadTrainers() {
        trainers.clear();
        File file = new File(TRAINERS_CSV);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (isHeader) {
                    isHeader = false;
                    if (line.toLowerCase().startsWith("trainerid") || line.toLowerCase().startsWith("id")) {
                        continue;
                    }
                }
                Trainer t = Trainer.fromCsv(line);
                if (t != null && getTrainerById(t.getTrainerId()) == null) {
                    trainers.add(t);
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not read trainers file: " + e.getMessage());
        }
    }

    public synchronized boolean saveTrainers() {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRAINERS_CSV))) {
            writer.write("trainerId,name,specialization,contact");
            writer.newLine();
            for (Trainer t : trainers) {
                writer.write(t.toCsv());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving trainers: " + e.getMessage());
            return false;
        }
    }

    public synchronized List<Trainer> getAllTrainers() {
        return new ArrayList<>(trainers);
    }

    public synchronized Trainer getTrainerById(String trainerId) {
        if (trainerId == null) return null;
        for (Trainer t : trainers) {
            if (t.getTrainerId().equalsIgnoreCase(trainerId.trim())) {
                return t;
            }
        }
        return null;
    }

    public synchronized boolean addTrainer(Trainer trainer) {
        if (trainer == null || trainer.getTrainerId().isEmpty() || trainer.getName().isEmpty()) {
            return false;
        }
        if (getTrainerById(trainer.getTrainerId()) != null) {
            return false;
        }
        trainers.add(trainer);
        return saveTrainers();
    }

    public synchronized String generateNextTrainerId() {
        int maxId = 0;
        for (Trainer t : trainers) {
            String idStr = t.getTrainerId();
            if (idStr.toUpperCase().startsWith("T")) {
                try {
                    int num = Integer.parseInt(idStr.substring(1));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("T%02d", maxId + 1);
    }

    // =========================================================================
    // MEMBER MANAGEMENT & PERSISTENCE
    // =========================================================================

    private void initDefaultMembers() {
        LocalDate today = LocalDate.now();
        // 1. Active member with Annual plan
        members.add(new Member("M1001", "Alex Smith", "555-1001", "P04", "Annual Ultimate",
                today.minusMonths(2), today.minusMonths(2).plusMonths(12), "T01", false, null, 0));

        // 2. Member expiring within 7 days
        members.add(new Member("M1002", "Sarah Connor", "555-1002", "P01", "Monthly Standard",
                today.minusMonths(1).plusDays(4), today.plusDays(4), "T02", false, null, 0));

        // 3. Expired member (for access control testing)
        members.add(new Member("M1003", "Mike Tyson", "555-1003", "P01", "Monthly Standard",
                today.minusMonths(2), today.minusDays(5), "T01", false, null, 0));

        // 4. Frozen member (frozen for 14 days)
        members.add(new Member("M1004", "Alice Cooper", "555-1004", "P02", "Quarterly Pro",
                today.minusMonths(1), today.plusMonths(2).plusDays(14), "T03", true, today.minusDays(2), 14));

        // 5. Active member with Half-Yearly plan
        members.add(new Member("M1005", "Bruce Wayne", "555-1005", "P03", "Half-Yearly Elite",
                today.minusMonths(1), today.minusMonths(1).plusMonths(6), "T01", false, null, 0));

        saveMembers();
    }

    public synchronized void loadMembers() {
        members.clear();
        File file = new File(MEMBERS_CSV);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (isHeader) {
                    isHeader = false;
                    if (line.toLowerCase().startsWith("memberid") || line.toLowerCase().startsWith("id")) {
                        continue;
                    }
                }
                Member m = Member.fromCsv(line);
                if (m != null && getMemberById(m.getMemberId()) == null) {
                    members.add(m);
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not read members file: " + e.getMessage());
        }
    }

    public synchronized boolean saveMembers() {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MEMBERS_CSV))) {
            writer.write("memberId,name,contact,planId,planName,startDate,expiryDate,assignedTrainerId,isFrozen,freezeStartDate,frozenDays");
            writer.newLine();
            for (Member m : members) {
                writer.write(m.toCsv());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving members to CSV: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generates a guaranteed unique Member ID.
     */
    public synchronized String generateNextMemberId() {
        int maxId = 1000;
        for (Member m : members) {
            String idStr = m.getMemberId();
            if (idStr.toUpperCase().startsWith("M")) {
                try {
                    int num = Integer.parseInt(idStr.substring(1));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        return "M" + (maxId + 1);
    }

    /**
     * Registers a new gym member and automatically calculates the expiry date.
     */
    public synchronized Member registerMember(String name, String contact, MembershipPlan plan, LocalDate startDate) {
        if (name == null || name.trim().isEmpty() || plan == null) {
            return null;
        }
        if (startDate == null) {
            startDate = LocalDate.now();
        }

        String memberId = generateNextMemberId();
        LocalDate expiryDate = startDate.plusMonths(plan.getDurationMonths());

        Member member = new Member(memberId, name.trim(), contact != null ? contact.trim() : "",
                plan.getPlanId(), plan.getName(), startDate, expiryDate);

        members.add(member);
        saveMembers();
        return member;
    }

    public synchronized Member getMemberById(String memberId) {
        if (memberId == null) return null;
        for (Member m : members) {
            if (m.getMemberId().equalsIgnoreCase(memberId.trim())) {
                return m;
            }
        }
        return null;
    }

    public synchronized List<Member> getAllMembers() {
        return new ArrayList<>(members);
    }

    /**
     * Returns members who are active on the given date (not expired and not frozen).
     */
    public synchronized List<Member> getActiveMembers(LocalDate currentDate) {
        if (currentDate == null) currentDate = LocalDate.now();
        LocalDate finalDate = currentDate;
        return members.stream()
                .filter(m -> m.isActive(finalDate))
                .collect(Collectors.toList());
    }

    /**
     * Returns members whose memberships have expired on or before the given date.
     */
    public synchronized List<Member> getExpiredMembers(LocalDate currentDate) {
        if (currentDate == null) currentDate = LocalDate.now();
        LocalDate finalDate = currentDate;
        return members.stream()
                .filter(m -> m.isExpired(finalDate))
                .collect(Collectors.toList());
    }

    /**
     * Returns members whose memberships will expire within the given number of days.
     */
    public synchronized List<Member> getExpiringMembers(LocalDate currentDate, int daysWindow) {
        if (currentDate == null) currentDate = LocalDate.now();
        LocalDate finalDate = currentDate;
        return members.stream()
                .filter(m -> m.isExpiringSoon(finalDate, daysWindow))
                .collect(Collectors.toList());
    }

    /**
     * Freezes a member's subscription for up to 30 days and automatically pushes out expiry.
     */
    public synchronized boolean freezeMember(String memberId, int days) {
        Member m = getMemberById(memberId);
        if (m == null) return false;
        boolean success = m.freezeMembership(days, LocalDate.now());
        if (success) {
            saveMembers();
        }
        return success;
    }

    /**
     * Unfreezes a member's subscription.
     */
    public synchronized boolean unfreezeMember(String memberId) {
        Member m = getMemberById(memberId);
        if (m == null) return false;
        boolean success = m.unfreezeMembership();
        if (success) {
            saveMembers();
        }
        return success;
    }

    /**
     * Assigns a trainer to a member.
     */
    public synchronized boolean assignTrainerToMember(String memberId, String trainerId) {
        Member m = getMemberById(memberId);
        if (m == null) return false;
        if (!trainerId.equalsIgnoreCase("None") && getTrainerById(trainerId) == null) {
            return false;
        }
        m.setAssignedTrainerId(trainerId);
        return saveMembers();
    }

    /**
     * Returns all members assigned to a specific trainer.
     */
    public synchronized List<Member> getMembersByTrainer(String trainerId) {
        if (trainerId == null) return new ArrayList<>();
        return members.stream()
                .filter(m -> m.getAssignedTrainerId().equalsIgnoreCase(trainerId.trim()))
                .collect(Collectors.toList());
    }

    // =========================================================================
    // FORMATTED DISPLAY METHODS
    // =========================================================================

    public void displayMemberTable(List<Member> list, String title) {
        LocalDate today = LocalDate.now();
        System.out.println("\n+--------------------------------------------------------------------------------------------------------------------+");
        System.out.printf("| %-114s |\n", String.format("%s (Total: %d)", title, list.size()));
        System.out.println("+---------+----------------------+----------------+--------------------+------------+------------+---------------+");
        System.out.printf("| %-7s | %-20s | %-14s | %-18s | %-10s | %-10s | %-13s |\n",
                "ID", "Name", "Contact", "Plan", "Start", "Expiry", "Status");
        System.out.println("+---------+----------------------+----------------+--------------------+------------+------------+---------------+");
        if (list.isEmpty()) {
            System.out.println("| No matching members found.                                                                                         |");
        } else {
            for (Member m : list) {
                System.out.printf("| %-7s | %-20s | %-14s | %-18s | %-10s | %-10s | %-13s |\n",
                        m.getMemberId(),
                        truncate(m.getName(), 20),
                        truncate(m.getContact(), 14),
                        truncate(m.getPlanName(), 18),
                        m.getStartDate(),
                        m.getExpiryDate(),
                        truncate(m.getStatusDescription(today), 13));
            }
        }
        System.out.println("+---------+----------------------+----------------+--------------------+------------+------------+---------------+");
    }

    public void displayAllMembers() {
        displayMemberTable(members, "ALL REGISTERED GYM MEMBERS");
    }

    public void displayActiveMembers() {
        displayMemberTable(getActiveMembers(LocalDate.now()), "ACTIVE GYM MEMBERS");
    }

    public void displayExpiredMembers() {
        displayMemberTable(getExpiredMembers(LocalDate.now()), "EXPIRED GYM MEMBERS");
    }

    public void displayExpiringMembers(int days) {
        displayMemberTable(getExpiringMembers(LocalDate.now(), days),
                "MEMBERS EXPIRING WITHIN " + days + " DAYS");
    }

    public void displayTrainers() {
        System.out.println("\n+-----------------------------------------------------------------------------------------+");
        System.out.println("|                              FITTRACK CERTIFIED TRAINERS                                |");
        System.out.println("+---------+----------------------+------------------------------+-------------------------+");
        System.out.printf("| %-7s | %-20s | %-28s | %-23s |\n", "ID", "Trainer Name", "Specialization", "Contact");
        System.out.println("+---------+----------------------+------------------------------+-------------------------+");
        if (trainers.isEmpty()) {
            System.out.println("| No trainers found in the system.                                                        |");
        } else {
            for (Trainer t : trainers) {
                System.out.printf("| %-7s | %-20s | %-28s | %-23s |\n",
                        t.getTrainerId(), truncate(t.getName(), 20),
                        truncate(t.getSpecialization(), 28), truncate(t.getContact(), 23));
            }
        }
        System.out.println("+---------+----------------------+------------------------------+-------------------------+");
    }

    private String truncate(String val, int maxLen) {
        if (val == null) return "";
        if (val.length() <= maxLen) return val;
        return val.substring(0, maxLen - 2) + "..";
    }
}
