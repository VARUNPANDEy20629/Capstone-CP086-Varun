import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * FitTrack – Gym Membership and Access Management System
 * Console Application Entry Point and User Interface.
 * 
 * Collaborative Project: Varun Pandey & Sujan Khadka (CP-086)
 */
public class Main {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final PlanManager planManager;
    private final MemberManager memberManager;
    private final AttendanceManager attendanceManager;
    private final RevenueManager revenueManager;
    private final Scanner scanner;

    public Main() {
        this.planManager = new PlanManager();
        this.memberManager = new MemberManager();
        this.attendanceManager = new AttendanceManager();
        this.revenueManager = new RevenueManager();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }

    public void run() {
        printBanner();
        boolean running = true;

        while (running) {
            displayMainMenu();
            int choice = readIntChoice(1, 11);

            switch (choice) {
                case 1:
                    handleManagePlans();
                    break;
                case 2:
                    handleRegisterMember();
                    break;
                case 3:
                    handleViewAllMembers();
                    break;
                case 4:
                    handleTrackExpiry();
                    break;
                case 5:
                    handleRecordCheckIn();
                    break;
                case 6:
                    handleViewAttendance();
                    break;
                case 7:
                    handleViewMonthlyRevenue();
                    break;
                case 8:
                    handleViewActiveMembers();
                    break;
                case 9:
                    handleManageTrainers();
                    break;
                case 10:
                    handleFreezeMembership();
                    break;
                case 11:
                    running = false;
                    exitApplication();
                    break;
                default:
                    System.out.println("Invalid selection. Please choose an option between 1 and 11.");
            }
        }
    }

    private void printBanner() {
        System.out.println("========================================================================================");
        System.out.println("    💪 FITTRACK - GYM MEMBERSHIP AND ACCESS MANAGEMENT SYSTEM                           ");
        System.out.println("    CP-086 Capstone Project | Collaborative Development                                 ");
        System.out.println("    Developers: Varun Pandey & Sujan Khadka                                             ");
        System.out.println("========================================================================================");
    }

    private void displayMainMenu() {
        System.out.println("\n========================================================");
        System.out.println("                       MAIN MENU                        ");
        System.out.println("========================================================");
        System.out.println("  1. Manage Membership Plans");
        System.out.println("  2. Register New Member");
        System.out.println("  3. View All Members");
        System.out.println("  4. Track Membership Expiry");
        System.out.println("  5. Record Member Check-in");
        System.out.println("  6. View Attendance Records");
        System.out.println("  7. View Monthly Revenue");
        System.out.println("  8. View Active Members");
        System.out.println("  9. Manage Trainers");
        System.out.println("  10. Freeze Membership");
        System.out.println("  11. Exit");
        System.out.println("========================================================");
        System.out.print("Enter your choice (1-11): ");
    }

    // =========================================================================
    // 1. MANAGE MEMBERSHIP PLANS
    // =========================================================================
    private void handleManagePlans() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- [Manage Membership Plans] ---");
            System.out.println("1. View Available Plans");
            System.out.println("2. Add New Plan");
            System.out.println("3. Back to Main Menu");
            System.out.print("Select an option (1-3): ");
            int option = readIntChoice(1, 3);

            switch (option) {
                case 1:
                    planManager.displayPlans();
                    break;
                case 2:
                    addNewPlan();
                    break;
                case 3:
                    back = true;
                    break;
            }
        }
    }

    private void addNewPlan() {
        System.out.println("\n--- Add New Membership Plan ---");
        System.out.print("Enter Unique Plan ID (e.g. P05): ");
        String planId = scanner.nextLine().trim();
        if (planId.isEmpty()) {
            System.out.println("❌ Plan ID cannot be empty.");
            return;
        }
        if (planManager.containsPlanId(planId)) {
            System.out.println("❌ Plan ID '" + planId + "' already exists! Please use a unique ID.");
            return;
        }

        System.out.print("Enter Plan Name (e.g. Student Semester): ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("❌ Plan Name cannot be empty.");
            return;
        }

        System.out.print("Enter Duration (in months, 1-36): ");
        int duration = readIntChoice(1, 36);

        System.out.print("Enter Plan Price (in USD, e.g. 79.99): ");
        double price = readDoublePositive();

        MembershipPlan newPlan = new MembershipPlan(planId, name, duration, price);
        boolean saved = planManager.addPlan(newPlan);
        if (saved) {
            System.out.println("✅ Plan '" + name + "' (" + planId + ") added successfully!");
        } else {
            System.out.println("❌ Failed to save new plan.");
        }
    }

    // =========================================================================
    // 2. REGISTER NEW MEMBER
    // =========================================================================
    private void handleRegisterMember() {
        System.out.println("\n--- Register New Member ---");
        System.out.print("Enter Member Full Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("❌ Member name cannot be empty.");
            return;
        }

        System.out.print("Enter Contact Number: ");
        String contact = scanner.nextLine().trim();
        if (contact.isEmpty()) {
            contact = "N/A";
        }

        // Show available plans
        List<MembershipPlan> plans = planManager.getAllPlans();
        if (plans.isEmpty()) {
            System.out.println("❌ No membership plans found! Please create a plan first.");
            return;
        }

        planManager.displayPlans();
        System.out.print("Enter Plan ID for this member: ");
        String planId = scanner.nextLine().trim();
        MembershipPlan selectedPlan = planManager.getPlanById(planId);
        if (selectedPlan == null) {
            System.out.println("❌ Invalid Plan ID '" + planId + "'. Registration aborted.");
            return;
        }

        // Start Date selection
        System.out.print("Enter Start Date (YYYY-MM-DD) or press ENTER for today (" + LocalDate.now() + "): ");
        String dateInput = scanner.nextLine().trim();
        LocalDate startDate = LocalDate.now();
        if (!dateInput.isEmpty()) {
            try {
                startDate = LocalDate.parse(dateInput, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("⚠️  Invalid date format. Defaulting start date to today (" + LocalDate.now() + ").");
                startDate = LocalDate.now();
            }
        }

        Member member = memberManager.registerMember(name, contact, selectedPlan, startDate);
        if (member != null) {
            System.out.println("\n✅ Member successfully registered!");
            System.out.println("   --------------------------------------");
            System.out.println("   Member ID:    " + member.getMemberId());
            System.out.println("   Name:         " + member.getName());
            System.out.println("   Contact:      " + member.getContact());
            System.out.println("   Plan:         " + selectedPlan.getName() + " ($" + selectedPlan.getPrice() + ")");
            System.out.println("   Start Date:   " + member.getStartDate());
            System.out.println("   Expiry Date:  " + member.getExpiryDate());
            System.out.println("   Status:       " + member.getStatusDescription(LocalDate.now()));
            System.out.println("   --------------------------------------");
            System.out.println("Data automatically saved to data/members.csv.");
        } else {
            System.out.println("❌ Registration failed due to an unexpected error.");
        }
    }

    // =========================================================================
    // 3. VIEW ALL MEMBERS
    // =========================================================================
    private void handleViewAllMembers() {
        memberManager.displayAllMembers();
    }

    // =========================================================================
    // 4. TRACK MEMBERSHIP EXPIRY
    // =========================================================================
    private void handleTrackExpiry() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- [Track Membership Expiry] ---");
            System.out.println("1. Members Expiring Within Next 7 Days");
            System.out.println("2. Expired Members (Blocked from Entry)");
            System.out.println("3. Active Members");
            System.out.println("4. Custom Expiry Window (e.g. 14 or 30 days)");
            System.out.println("5. Back to Main Menu");
            System.out.print("Select an option (1-5): ");
            int option = readIntChoice(1, 5);

            switch (option) {
                case 1:
                    memberManager.displayExpiringMembers(7);
                    break;
                case 2:
                    memberManager.displayExpiredMembers();
                    break;
                case 3:
                    memberManager.displayActiveMembers();
                    break;
                case 4:
                    System.out.print("Enter number of days ahead to check (1-365): ");
                    int days = readIntChoice(1, 365);
                    memberManager.displayExpiringMembers(days);
                    break;
                case 5:
                    back = true;
                    break;
            }
        }
    }

    // =========================================================================
    // 5. RECORD MEMBER CHECK-IN
    // =========================================================================
    private void handleRecordCheckIn() {
        System.out.println("\n--- [Record Member Check-in] ---");
        System.out.print("Enter Member ID (e.g. M1001): ");
        String memberId = scanner.nextLine().trim();

        if (memberId.isEmpty()) {
            System.out.println("❌ Member ID cannot be empty.");
            return;
        }

        String result = attendanceManager.recordCheckIn(memberId, memberManager);
        System.out.println("\n" + result);
    }

    // =========================================================================
    // 6. VIEW ATTENDANCE RECORDS
    // =========================================================================
    private void handleViewAttendance() {
        attendanceManager.displayAttendanceRecords();
    }

    // =========================================================================
    // 7. VIEW MONTHLY REVENUE
    // =========================================================================
    private void handleViewMonthlyRevenue() {
        revenueManager.displayRevenueSummary(memberManager, planManager);
    }

    // =========================================================================
    // 8. VIEW ACTIVE MEMBERS
    // =========================================================================
    private void handleViewActiveMembers() {
        memberManager.displayActiveMembers();
    }

    // =========================================================================
    // 9. MANAGE TRAINERS
    // =========================================================================
    private void handleManageTrainers() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- [Manage Trainers & Assignments] ---");
            System.out.println("1. View All Trainers");
            System.out.println("2. Add New Trainer");
            System.out.println("3. Assign Trainer to Member");
            System.out.println("4. View Members Assigned to a Trainer");
            System.out.println("5. Back to Main Menu");
            System.out.print("Select an option (1-5): ");
            int option = readIntChoice(1, 5);

            switch (option) {
                case 1:
                    memberManager.displayTrainers();
                    break;
                case 2:
                    addNewTrainer();
                    break;
                case 3:
                    assignTrainerToMember();
                    break;
                case 4:
                    viewMembersByTrainer();
                    break;
                case 5:
                    back = true;
                    break;
            }
        }
    }

    private void addNewTrainer() {
        System.out.println("\n--- Add New Trainer ---");
        String nextId = memberManager.generateNextTrainerId();
        System.out.println("Suggested Trainer ID: " + nextId);
        System.out.print("Enter Trainer ID (or press Enter to use " + nextId + "): ");
        String idInput = scanner.nextLine().trim();
        String trainerId = idInput.isEmpty() ? nextId : idInput;

        if (memberManager.getTrainerById(trainerId) != null) {
            System.out.println("❌ Trainer with ID '" + trainerId + "' already exists!");
            return;
        }

        System.out.print("Enter Trainer Full Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("❌ Trainer name cannot be empty.");
            return;
        }

        System.out.print("Enter Specialization (e.g. Strength, Yoga, Crossfit): ");
        String spec = scanner.nextLine().trim();
        if (spec.isEmpty()) spec = "General Fitness";

        System.out.print("Enter Contact Information: ");
        String contact = scanner.nextLine().trim();

        Trainer trainer = new Trainer(trainerId, name, spec, contact);
        boolean saved = memberManager.addTrainer(trainer);
        if (saved) {
            System.out.println("✅ Trainer '" + name + "' (" + trainerId + ") added successfully!");
        } else {
            System.out.println("❌ Failed to add trainer.");
        }
    }

    private void assignTrainerToMember() {
        System.out.println("\n--- Assign Trainer to Member ---");
        System.out.print("Enter Member ID: ");
        String memberId = scanner.nextLine().trim();
        Member member = memberManager.getMemberById(memberId);
        if (member == null) {
            System.out.println("❌ Member ID '" + memberId + "' not found.");
            return;
        }

        System.out.println("Selected Member: " + member.getName() + " (Current Trainer: " + member.getAssignedTrainerId() + ")");
        memberManager.displayTrainers();
        System.out.print("Enter Trainer ID to assign (or 'None' to unassign): ");
        String trainerId = scanner.nextLine().trim();

        if (!trainerId.equalsIgnoreCase("None") && memberManager.getTrainerById(trainerId) == null) {
            System.out.println("❌ Trainer ID '" + trainerId + "' does not exist.");
            return;
        }

        boolean success = memberManager.assignTrainerToMember(memberId, trainerId);
        if (success) {
            System.out.println("✅ Trainer assignment updated successfully for member " + member.getName() + "!");
        } else {
            System.out.println("❌ Failed to update trainer assignment.");
        }
    }

    private void viewMembersByTrainer() {
        System.out.println("\n--- View Members by Trainer ---");
        memberManager.displayTrainers();
        System.out.print("Enter Trainer ID: ");
        String trainerId = scanner.nextLine().trim();
        Trainer trainer = memberManager.getTrainerById(trainerId);
        if (trainer == null) {
            System.out.println("❌ Trainer ID '" + trainerId + "' not found.");
            return;
        }

        List<Member> assigned = memberManager.getMembersByTrainer(trainerId);
        memberManager.displayMemberTable(assigned, "MEMBERS ASSIGNED TO: " + trainer.getName() + " (" + trainer.getSpecialization() + ")");
    }

    // =========================================================================
    // 10. FREEZE MEMBERSHIP
    // =========================================================================
    private void handleFreezeMembership() {
        System.out.println("\n--- [Freeze / Unfreeze Membership] ---");
        System.out.print("Enter Member ID: ");
        String memberId = scanner.nextLine().trim();
        Member member = memberManager.getMemberById(memberId);
        if (member == null) {
            System.out.println("❌ Member ID '" + memberId + "' was not found.");
            return;
        }

        System.out.println("\nMember Details:");
        System.out.println("  ID:           " + member.getMemberId());
        System.out.println("  Name:         " + member.getName());
        System.out.println("  Plan:         " + member.getPlanName());
        System.out.println("  Expiry Date:  " + member.getExpiryDate());
        System.out.println("  Status:       " + member.getStatusDescription(LocalDate.now()));

        if (member.isFrozen()) {
            System.out.println("\n⚠️  This membership is currently FROZEN (Started on: " + member.getFreezeStartDate() + ").");
            System.out.print("Would you like to UNFREEZE this membership now? (Y/N): ");
            String ans = scanner.nextLine().trim();
            if (ans.equalsIgnoreCase("Y") || ans.equalsIgnoreCase("YES")) {
                boolean unfrozen = memberManager.unfreezeMember(memberId);
                if (unfrozen) {
                    System.out.println("✅ Membership for " + member.getName() + " has been UNFROZEN and is now ACTIVE!");
                } else {
                    System.out.println("❌ Failed to unfreeze membership.");
                }
            } else {
                System.out.println("Membership remains frozen.");
            }
            return;
        }

        if (member.isExpired(LocalDate.now())) {
            System.out.println("❌ Cannot freeze an EXPIRED membership. Please renew the membership first.");
            return;
        }

        System.out.println("\nMembership Freeze Rules:");
        System.out.println("  • You may freeze a membership for 1 to 30 days.");
        System.out.println("  • The expiry date will be automatically pushed forward by the frozen duration.");
        System.out.print("Enter freeze duration in days (1-30): ");
        int days = readIntChoice(1, 30);

        LocalDate oldExpiry = member.getExpiryDate();
        boolean frozen = memberManager.freezeMember(memberId, days);
        if (frozen) {
            System.out.println("\n✅ Membership successfully FROZEN for " + days + " days!");
            System.out.println("   Original Expiry Date:  " + oldExpiry);
            System.out.println("   NEW Adjusted Expiry:   " + member.getExpiryDate());
            System.out.println("   Status:                FROZEN");
            System.out.println("Changes saved automatically to data/members.csv.");
        } else {
            System.out.println("❌ Could not freeze membership. Please verify criteria.");
        }
    }

    // =========================================================================
    // 11. EXIT
    // =========================================================================
    private void exitApplication() {
        System.out.println("\nSaving all records to persistent CSV storage...");
        planManager.savePlans();
        memberManager.saveMembers();
        memberManager.saveTrainers();
        attendanceManager.saveAttendance();
        System.out.println("========================================================================================");
        System.out.println("    Thank you for using FitTrack - Gym Membership and Access Management System!        ");
        System.out.println("    Goodbye! Have a healthy and active day! 💪                                         ");
        System.out.println("========================================================================================");
    }

    // =========================================================================
    // INPUT UTILITIES
    // =========================================================================
    private int readIntChoice(int min, int max) {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(input);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.print("Please enter a valid number between " + min + " and " + max + ": ");
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number (" + min + "-" + max + "): ");
            }
        }
    }

    private double readDoublePositive() {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                double val = Double.parseDouble(input);
                if (val > 0) {
                    return val;
                }
                System.out.print("Price must be greater than zero. Try again: ");
            } catch (NumberFormatException e) {
                System.out.print("Invalid amount. Please enter a valid decimal number (e.g. 49.99): ");
            }
        }
    }
}
