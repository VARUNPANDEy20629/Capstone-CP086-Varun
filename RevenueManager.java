import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * FitTrack - Gym Membership and Access Management System
 * Calculates membership revenue, active member metrics, and generates monthly reports.
 * 
 * Collaborative Project: Varun Pandey & Sujan Khadka (CP-086)
 */
public class RevenueManager {
    private static final String OUTPUT_DIR = "output";
    private static final String SUMMARY_FILE = "output/monthly_revenue_summary.txt";

    public RevenueManager() {
        ensureOutputDirectoryExists();
    }

    private void ensureOutputDirectoryExists() {
        File dir = new File(OUTPUT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Calculates monthly revenue grouped by YearMonth of member registration.
     */
    public Map<YearMonth, Double> calculateMonthlyRevenue(MemberManager memberManager, PlanManager planManager) {
        Map<YearMonth, Double> monthlyRevenue = new TreeMap<>();
        List<Member> members = memberManager.getAllMembers();

        for (Member m : members) {
            MembershipPlan plan = planManager.getPlanById(m.getPlanId());
            double price = plan != null ? plan.getPrice() : 0.0;
            YearMonth ym = YearMonth.from(m.getStartDate());
            monthlyRevenue.put(ym, monthlyRevenue.getOrDefault(ym, 0.0) + price);
        }
        return monthlyRevenue;
    }

    /**
     * Calculates total revenue collected across all member registrations.
     */
    public double calculateTotalRevenue(MemberManager memberManager, PlanManager planManager) {
        double total = 0.0;
        for (Member m : memberManager.getAllMembers()) {
            MembershipPlan plan = planManager.getPlanById(m.getPlanId());
            if (plan != null) {
                total += plan.getPrice();
            }
        }
        return total;
    }

    /**
     * Displays a comprehensive revenue and active members summary.
     */
    public void displayRevenueSummary(MemberManager memberManager, PlanManager planManager) {
        LocalDate today = LocalDate.now();
        List<Member> allMembers = memberManager.getAllMembers();
        List<Member> activeMembers = memberManager.getActiveMembers(today);
        List<Member> expiredMembers = memberManager.getExpiredMembers(today);
        List<Member> expiringSoon = memberManager.getExpiringMembers(today, 7);
        Map<YearMonth, Double> monthlyRevenue = calculateMonthlyRevenue(memberManager, planManager);
        double totalRevenue = calculateTotalRevenue(memberManager, planManager);

        YearMonth currentYm = YearMonth.from(today);
        double currentMonthRevenue = monthlyRevenue.getOrDefault(currentYm, 0.0);

        StringBuilder sb = new StringBuilder();
        sb.append("\n========================================================================================\n");
        sb.append("                  FITTRACK REVENUE & MEMBERSHIP METRICS SUMMARY                         \n");
        sb.append("========================================================================================\n");
        sb.append(String.format(" Report Generated On: %s\n", today));
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(" MEMBERSHIP METRICS:\n");
        sb.append(String.format("   • Total Registered Members:  %d\n", allMembers.size()));
        sb.append(String.format("   • Total Active Members:      %d (%.1f%% of total)\n",
                activeMembers.size(), allMembers.isEmpty() ? 0.0 : (activeMembers.size() * 100.0 / allMembers.size())));
        sb.append(String.format("   • Members Expiring in 7 Days: %d\n", expiringSoon.size()));
        sb.append(String.format("   • Expired Members:           %d\n", expiredMembers.size()));
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(" FINANCIAL METRICS:\n");
        sb.append(String.format("   • Current Month Revenue (%s): $%,.2f\n", currentYm, currentMonthRevenue));
        sb.append(String.format("   • Total Lifetime Revenue:        $%,.2f\n", totalRevenue));
        sb.append(String.format("   • Average Revenue Per Member:    $%,.2f\n",
                allMembers.isEmpty() ? 0.0 : (totalRevenue / allMembers.size())));
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(" MONTHLY REVENUE BREAKDOWN:\n");
        sb.append("+-----------------+-------------------+---------------------+--------------------------+\n");
        sb.append(String.format("| %-15s | %-17s | %-19s | %-24s |\n", "Year-Month", "Registrations", "Monthly Revenue", "Share of Total"));
        sb.append("+-----------------+-------------------+---------------------+--------------------------+\n");

        DateTimeFormatter ymFmt = DateTimeFormatter.ofPattern("MMMM yyyy");
        for (Map.Entry<YearMonth, Double> entry : monthlyRevenue.entrySet()) {
            YearMonth ym = entry.getKey();
            double rev = entry.getValue();
            long count = allMembers.stream()
                    .filter(m -> YearMonth.from(m.getStartDate()).equals(ym))
                    .count();
            double share = totalRevenue > 0 ? (rev / totalRevenue) * 100.0 : 0.0;
            sb.append(String.format("| %-15s | %-17d | $%-18.2f | %-23.1f%% |\n",
                    ym.toString(), count, rev, share));
        }
        sb.append("+-----------------+-------------------+---------------------+--------------------------+\n");
        sb.append(String.format(" Total Revenue: $%,.2f across %d registration(s)\n", totalRevenue, allMembers.size()));
        sb.append("========================================================================================\n");

        System.out.print(sb.toString());

        // Save report to output file
        saveSummaryToFile(sb.toString());
    }

    private void saveSummaryToFile(String content) {
        ensureOutputDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SUMMARY_FILE))) {
            writer.write(content);
            System.out.println("📄 Summary report successfully saved to: " + SUMMARY_FILE);
        } catch (IOException e) {
            System.err.println("Warning: Could not save revenue summary file: " + e.getMessage());
        }
    }
}
