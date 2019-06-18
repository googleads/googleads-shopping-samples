package shopping.content.v2_1.samples.accounts;

import com.google.api.services.content.model.Account;
import com.google.api.services.content.model.AccountAdsLink;
import com.google.api.services.content.model.AccountUser;

/** Utility class for storing actions performed on Accounts. */
public class AccountUtils {
  public static final String SAMPLE_ACCOUNT_NAME = "sampleAccount123";

  public static void printAccount(Account account) {
    System.out.printf("Information for account %d:%n", account.getId());
    if (account.getName() == null) {
      System.out.println("- No display name found.");
    } else {
      System.out.printf("- Display name: %s%n", account.getName());
    }
    if (account.getWebsiteUrl() == null) {
      System.out.println("- No website URL information found.");
    } else {
      System.out.printf("- Website URL: %s%n", account.getWebsiteUrl());
    }
    if (account.getUsers() == null) {
      System.out.println("- No registered users for this Merchant Center account.");
    } else {
      System.out.println("- Registered users:");
      for (AccountUser user : account.getUsers()) {
        System.out.printf("  - %s%s%n", user.getAdmin() ? "(ADMIN) " : "", user.getEmailAddress());
      }
    }
    if (account.getAdsLinks() == null) {
      System.out.println("- No links to Ads accounts for this Merchant Center account.");
    } else {
      System.out.println("- Links to Ads accounts:");
      for (AccountAdsLink link : account.getAdsLinks()) {
        System.out.printf("  - %d: %s%n", link.getAdsId(), link.getStatus());
      }
    }
    System.out.println();
  }
}

