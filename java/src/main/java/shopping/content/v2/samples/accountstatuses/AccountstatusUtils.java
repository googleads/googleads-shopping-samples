package shopping.content.v2.samples.accountstatuses;

import com.google.api.services.content.model.AccountStatus;
import com.google.api.services.content.model.AccountStatusDataQualityIssue;
import com.google.api.services.content.model.AccountStatusExampleItem;
import java.util.List;

/** Utility class for methods like printing AccountStatus objects. */
public class AccountstatusUtils {
  public static void printAccountStatus(AccountStatus accountStatus) {
    System.out.printf("- Account ID %s\n", accountStatus.getAccountId());

    List<AccountStatusDataQualityIssue> issues = accountStatus.getDataQualityIssues();
    if (issues != null) {
      System.out.printf("  There are %d data quality issue(s)%n", issues.size());
      for (AccountStatusDataQualityIssue issue : issues) {
        System.out.printf(
            "  - Issue (%s) [%s] on %d items%n",
            issue.getSeverity(), issue.getId(), issue.getNumItems());
        if (issue.getExampleItems() != null) {
          System.out.printf("    %d example items:%n", issue.getExampleItems().size());
          for (AccountStatusExampleItem item : issue.getExampleItems()) {
            System.out.printf("    - %s%n", item.getItemId());
          }
        }
      }
    }
    System.out.println();
  }
}
