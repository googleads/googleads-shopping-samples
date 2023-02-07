package shopping.manufacturers.v1.samples.productcerts;

import com.google.api.services.manufacturers.v1.ManufacturerCenter;
import com.google.api.services.manufacturers.v1.model.ListProductCertificationsResponse;
import com.google.api.services.manufacturers.v1.model.ProductCertification;
import java.io.IOException;
import shopping.manufacturers.v1.samples.ManufacturersSample;

/** A sample of listing Product Certifications. */
public final class ListProductCertificationsSample extends ManufacturersSample {

  public ListProductCertificationsSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    ManufacturerCenter.Accounts.Languages.ProductCertifications.List pcList =
        manufacturers
            .accounts()
            .languages()
            .productCertifications()
            .list(getManufacturerId() + "/languages/-");

    do {
      ListProductCertificationsResponse page = pcList.execute();
      if (page.getProductCertifications() == null) {
        System.out.println("No product certifications found.");
        return;
      }
      for (ProductCertification pc : page.getProductCertifications()) {
        pc.setFactory(jsonFactory);
        System.out.println(pc.toPrettyString() + "\n");
      }
      pcList.setPageToken(page.getNextPageToken());
    } while (pcList.getPageToken() != null);
  }

  public static void main(String[] args) throws IOException {
    new ListProductCertificationsSample(args).execute();
  }
}
