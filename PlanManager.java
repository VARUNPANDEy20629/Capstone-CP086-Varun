import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * FitTrack - Gym Membership and Access Management System
 * Manages membership plans and their persistence.
 * 
 * Collaborative Project: Varun Pandey & Sujan Khadka (CP-086)
 */
public class PlanManager {
    private static final String CSV_FILE_PATH = "data/plans.csv";
    private final List<MembershipPlan> plans;

    public PlanManager() {
        this.plans = new ArrayList<>();
        ensureDataDirectoryExists();
        loadPlans();
        if (plans.isEmpty()) {
            initDefaultPlans();
        }
    }

    private void ensureDataDirectoryExists() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    private void initDefaultPlans() {
        plans.add(new MembershipPlan("P01", "Monthly Standard", 1, 49.99));
        plans.add(new MembershipPlan("P02", "Quarterly Pro", 3, 129.99));
        plans.add(new MembershipPlan("P03", "Half-Yearly Elite", 6, 229.99));
        plans.add(new MembershipPlan("P04", "Annual Ultimate", 12, 399.99));
        savePlans();
    }

    public synchronized void loadPlans() {
        plans.clear();
        File file = new File(CSV_FILE_PATH);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (isHeader) {
                    isHeader = false;
                    // Check if line is header
                    if (line.toLowerCase().startsWith("planid") || line.toLowerCase().startsWith("id")) {
                        continue;
                    }
                }
                MembershipPlan plan = MembershipPlan.fromCsv(line);
                if (plan != null && !containsPlanId(plan.getPlanId())) {
                    plans.add(plan);
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not read plans file: " + e.getMessage());
        }
    }

    public synchronized boolean savePlans() {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH))) {
            writer.write("planId,name,durationMonths,price");
            writer.newLine();
            for (MembershipPlan plan : plans) {
                writer.write(plan.toCsv());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving plans to CSV: " + e.getMessage());
            return false;
        }
    }

    public synchronized boolean addPlan(MembershipPlan plan) {
        if (plan == null || plan.getPlanId().isEmpty() || plan.getName().isEmpty()) {
            return false;
        }
        if (containsPlanId(plan.getPlanId())) {
            return false;
        }
        plans.add(plan);
        return savePlans();
    }

    public synchronized MembershipPlan getPlanById(String planId) {
        if (planId == null) return null;
        for (MembershipPlan p : plans) {
            if (p.getPlanId().equalsIgnoreCase(planId.trim())) {
                return p;
            }
        }
        return null;
    }

    public synchronized boolean containsPlanId(String planId) {
        return getPlanById(planId) != null;
    }

    public synchronized List<MembershipPlan> getAllPlans() {
        return new ArrayList<>(plans);
    }

    public void displayPlans() {
        System.out.println("\n+--------------------------------------------------------------------------+");
        System.out.println("|                      FITTRACK MEMBERSHIP PLANS                           |");
        System.out.println("+---------+----------------------+-------------------+---------------------+");
        System.out.printf("| %-7s | %-20s | %-17s | %-19s |\n", "Plan ID", "Plan Name", "Duration", "Price");
        System.out.println("+---------+----------------------+-------------------+---------------------+");
        if (plans.isEmpty()) {
            System.out.println("| No membership plans currently available.                                 |");
        } else {
            for (MembershipPlan p : plans) {
                String dur = p.getDurationMonths() + (p.getDurationMonths() == 1 ? " month" : " months");
                System.out.printf("| %-7s | %-20s | %-17s | $%-18.2f |\n",
                        p.getPlanId(), p.getName(), dur, p.getPrice());
            }
        }
        System.out.println("+---------+----------------------+-------------------+---------------------+");
    }
}
