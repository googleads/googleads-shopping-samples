package shopping.content.v2_1.samples.accountstatuses;

import com.google.api.services.content.model.AccountStatus;
import com.google.api.services.content.model.AccountStatusAccountLevelIssue;
import java.util.List;

/** Utility class for methods like printing AccountStatus objects. */
public class AccountstatusUtils {
    public static void printAccountStatus(AccountStatus accountStatus) {
        System.out.printf("- Account ID %s\n", accountStatus.getAccountId());

        List<AccountStatusAccountLevelIssue> issues = accountStatus.getAccountLevelIssues();

        if (issues != null) {
            System.out.printf("  There are %d account level issue(s)%n", issues.size());
            for (AccountStatusAccountLevelIssue issue : issues) {
                System.out.printf(
                        "  - Issue (%s) [%s] %s%n",
                        issue.getSeverity(), issue.getId(), issue.getTitle());
                if (issue.getCountry() != null) {
                    System.out.printf("    country: %s%n", issue.getCountry());
                }
                if (issue.getDestination() != null) {
                    System.out.printf("    destination: %s%n", issue.getDestination());
                }
                if (issue.getDetail() != null) {
                    System.out.printf("    details: %s%n", issue.getDetail());
                }
                if (issue.getDocumentation() != null) {
                    System.out.printf("    documentation: %s%n", issue.getDocumentation());
                }
            }
        }
        System.out.println();
    }
}
