import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

/**
 * Automated Test Suite for FitTrack
 * Verifies all 5 core compulsory features and stretch features.
 */
public class TestFitTrack {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   RUNNING AUTOMATED TEST SUITE FOR FITTRACK    ");
        System.out.println("=================================================");

        int passed = 0;
        int failed = 0;

        try {
            // Setup fresh managers
            PlanManager planMgr = new PlanManager();
            MemberManager memberMgr = new MemberManager();
            AttendanceManager attMgr = new AttendanceManager();
            RevenueManager revMgr = new RevenueManager();

            // -----------------------------------------------------------------
            // TEST 1: Membership Plan Management
            // -----------------------------------------------------------------
            System.out.println("\n[TEST 1] Membership Plan Management...");
            List<MembershipPlan> plans = planMgr.getAllPlans();
            assertCondition(!plans.isEmpty(), "Plans list should not be empty");
            String dynamicPlanId = "PT" + (System.currentTimeMillis() % 10000);
            MembershipPlan testPlan = new MembershipPlan(dynamicPlanId, "Student Special", 2, 85.00);
            boolean addedPlan = planMgr.addPlan(testPlan);
            assertCondition(addedPlan, "Should successfully add new plan " + dynamicPlanId);
            MembershipPlan fetchedPlan = planMgr.getPlanById(dynamicPlanId);
            assertCondition(fetchedPlan != null && fetchedPlan.getPrice() == 85.00, "Fetched plan matches added price");
            passed++;
            System.out.println("-> Test 1 PASSED");

            // -----------------------------------------------------------------
            // TEST 2: Member Registration & Expiry Calculation
            // -----------------------------------------------------------------
            System.out.println("\n[TEST 2] Member Registration & Automatic Expiry...");
            LocalDate startDate = LocalDate.of(2026, 11, 1);
            Member newMember = memberMgr.registerMember("Robert Paulson", "555-9999", testPlan, startDate);
            assertCondition(newMember != null, "Registered member should not be null");
            assertCondition(newMember.getMemberId().startsWith("M"), "Member ID starts with M");
            LocalDate expectedExpiry = startDate.plusMonths(2); // 2027-01-01
            assertCondition(newMember.getExpiryDate().equals(expectedExpiry),
                    "Expiry date correctly calculated (+2 months): " + newMember.getExpiryDate());
            passed++;
            System.out.println("-> Test 2 PASSED");

            // -----------------------------------------------------------------
            // TEST 3: Membership Expiry Tracking
            // -----------------------------------------------------------------
            System.out.println("\n[TEST 3] Membership Expiry Tracking (Active, Expired, 7-Day Window)...");
            LocalDate today = LocalDate.now();
            List<Member> activeMembers = memberMgr.getActiveMembers(today);
            List<Member> expiredMembers = memberMgr.getExpiredMembers(today);
            List<Member> expiring7Days = memberMgr.getExpiringMembers(today, 7);

            assertCondition(!activeMembers.isEmpty(), "Should have active members");
            assertCondition(!expiredMembers.isEmpty(), "Should identify expired member (M1003)");
            assertCondition(!expiring7Days.isEmpty(), "Should identify member expiring within 7 days (M1002)");

            // Verify M1003 is expired
            Member expiredM = memberMgr.getMemberById("M1003");
            assertCondition(expiredM.isExpired(today), "M1003 isExpired should return true");
            assertCondition(!expiredM.isActive(today), "M1003 isActive should return false");

            // Verify M1002 is expiring in 7 days
            Member expiringM = memberMgr.getMemberById("M1002");
            assertCondition(expiringM.isExpiringSoon(today, 7), "M1002 isExpiringSoon(7) should be true");
            passed++;
            System.out.println("-> Test 3 PASSED");

            // -----------------------------------------------------------------
            // TEST 4: Attendance Management & Automatic Blocking
            // -----------------------------------------------------------------
            System.out.println("\n[TEST 4] Attendance Management & Access Control...");
            // Expired check-in attempt MUST be blocked
            String blockedResult = attMgr.recordCheckIn("M1003", memberMgr);
            assertCondition(blockedResult.contains("ACCESS BLOCKED") && blockedResult.contains("EXPIRED"),
                    "Expired member M1003 should be strictly BLOCKED from check-in");

            // Active member check-in attempt MUST be granted
            String activeResult = attMgr.recordCheckIn(newMember.getMemberId(), memberMgr);
            assertCondition(activeResult.contains("ACCESS GRANTED"),
                    "Active member should be GRANTED access");

            // Frozen member check-in attempt MUST be blocked
            Member frozenM = memberMgr.getMemberById("M1004");
            if (frozenM != null && frozenM.isFrozen()) {
                String frozenResult = attMgr.recordCheckIn("M1004", memberMgr);
                assertCondition(frozenResult.contains("ACCESS BLOCKED") && frozenResult.contains("FROZEN"),
                        "Frozen member M1004 should be BLOCKED from check-in");
            }
            passed++;
            System.out.println("-> Test 4 PASSED");

            // -----------------------------------------------------------------
            // TEST 5: Revenue and Active Members
            // -----------------------------------------------------------------
            System.out.println("\n[TEST 5] Revenue Calculation & Active Member Summaries...");
            double totalRev = revMgr.calculateTotalRevenue(memberMgr, planMgr);
            assertCondition(totalRev > 0, "Total revenue should be greater than 0: " + totalRev);
            Map<YearMonth, Double> monthly = revMgr.calculateMonthlyRevenue(memberMgr, planMgr);
            assertCondition(!monthly.isEmpty(), "Monthly revenue map should not be empty");
            revMgr.displayRevenueSummary(memberMgr, planMgr);
            File summaryFile = new File("output/monthly_revenue_summary.txt");
            assertCondition(summaryFile.exists(), "Revenue summary file should be generated in output/");
            passed++;
            System.out.println("-> Test 5 PASSED");

            // -----------------------------------------------------------------
            // TEST 6: Stretch Feature - Trainer Assignment
            // -----------------------------------------------------------------
            System.out.println("\n[TEST 6] Stretch Feature: Trainer Assignment...");
            boolean assigned = memberMgr.assignTrainerToMember(newMember.getMemberId(), "T01");
            assertCondition(assigned, "Assign trainer T01 to member should succeed");
            List<Member> t01Members = memberMgr.getMembersByTrainer("T01");
            boolean found = t01Members.stream().anyMatch(m -> m.getMemberId().equals(newMember.getMemberId()));
            assertCondition(found, "Member should appear in trainer T01's assigned list");
            passed++;
            System.out.println("-> Test 6 PASSED");

            // -----------------------------------------------------------------
            // TEST 7: Stretch Feature - Membership Freeze & Expiry Adjustment
            // -----------------------------------------------------------------
            System.out.println("\n[TEST 7] Stretch Feature: Freeze Membership & Expiry Adjustment...");
            LocalDate preFreezeExpiry = newMember.getExpiryDate();
            int freezeDays = 15;
            boolean freezeOk = memberMgr.freezeMember(newMember.getMemberId(), freezeDays);
            assertCondition(freezeOk, "Freezing member for 15 days should succeed");
            assertCondition(newMember.isFrozen(), "Member status should be isFrozen=true");
            assertCondition(newMember.getExpiryDate().equals(preFreezeExpiry.plusDays(freezeDays)),
                    "Expiry date should be extended by exactly " + freezeDays + " days");

            // Cannot freeze beyond 30 days
            boolean invalidFreeze = newMember.freezeMembership(35, LocalDate.now());
            assertCondition(!invalidFreeze, "Freeze over 30 days should be rejected");

            // Unfreeze
            boolean unfreezeOk = memberMgr.unfreezeMember(newMember.getMemberId());
            assertCondition(unfreezeOk && !newMember.isFrozen(), "Unfreeze member should succeed");
            passed++;
            System.out.println("-> Test 7 PASSED");

            // -----------------------------------------------------------------
            // TEST 8: Data Persistence & Reloading
            // -----------------------------------------------------------------
            System.out.println("\n[TEST 8] Data Persistence Across Restarts...");
            MemberManager reloadedMgr = new MemberManager();
            Member reloadedM = reloadedMgr.getMemberById(newMember.getMemberId());
            assertCondition(reloadedM != null, "Persisted member should be loaded on restart");
            assertCondition(reloadedM.getName().equals("Robert Paulson"), "Persisted member name matches");
            assertCondition(reloadedM.getAssignedTrainerId().equals("T01"), "Trainer assignment persisted");
            passed++;
            System.out.println("-> Test 8 PASSED");

        } catch (AssertionError | Exception e) {
            System.err.println("❌ TEST FAILURE: " + e.getMessage());
            e.printStackTrace();
            failed++;
        }

        System.out.println("\n=================================================");
        System.out.printf("   TEST RESULTS: %d PASSED, %d FAILED\n", passed, failed);
        System.out.println("=================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Assertion failed: " + message);
        }
    }
}
