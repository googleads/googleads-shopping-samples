// Copyright 2023 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package shopping.content.v2_1.samples.regions;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.Region;
import com.google.api.services.content.model.RegionPostalCodeArea;
import com.google.api.services.content.model.RegionPostalCodeAreaPostalCodeRange;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import shopping.content.v2_1.samples.ContentSample;

/**
 * Creates a region. The region created here can be used with the regional inventory service.
 * Regional availability and pricing lets you provide product availability and variable pricing
 * based on your business presence and the location of your customer base. Regional availability and
 * pricing is available for products advertised through Shopping ads on Google Search, and listed in
 * free listings on the Shopping tab.
 */
public class RegionCreateSample extends ContentSample {
  public RegionCreateSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    checkNonMCA();

    // Creates a List of Postal Code Area Postal Code Ranges.
    // This allows you to flexibly define regions as combinations of postal code
    // ranges. Each postal code range in the list has its own start and end zip code.
    List<RegionPostalCodeAreaPostalCodeRange> postalCodeRanges =
        new ArrayList<RegionPostalCodeAreaPostalCodeRange>();

    // Creates a new postal code range from two postal code values.
    // This range is equivalent to all postal codes in the USA state of New York (00501 - 14925)
    RegionPostalCodeAreaPostalCodeRange postalCodeRange =
        new RegionPostalCodeAreaPostalCodeRange().setBegin("00501").setEnd("14925");

    // Adds the NY State postal code range into the list of postal code ranges that a postal
    // code area accepts.
    postalCodeRanges.add(postalCodeRange);

    // Creates Postal Code Area for the Region that will be inserted, using the NY State postal code
    // ranges, and the US CLDR territory/country code that the postal code ranges applies to.
    RegionPostalCodeArea postalCodeArea =
        new RegionPostalCodeArea().setPostalCodes(postalCodeRanges).setRegionCode("US");

    // Creates a region with example values for displayName and postalCodeArea
    Region region = new Region().setDisplayName("NYState").setPostalCodeArea(postalCodeArea);

    // Tries to create the region, and catches any exceptions
    try {
      System.out.println("Creating region");
      Region result =
          content
              .regions()
              .create(this.config.getMerchantId().longValue(), region)
              .setRegionId("12345678") // User-defined, numeric, minimum of 6 digits
              .execute();
      System.out.println("Listing succesfully created region");
      System.out.println(result);
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new RegionCreateSample(args).execute();
  }
}
