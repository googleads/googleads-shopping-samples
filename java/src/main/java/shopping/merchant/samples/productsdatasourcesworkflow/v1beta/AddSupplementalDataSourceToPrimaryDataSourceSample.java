// Copyright 2024 Google LLC
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

package shopping.merchant.samples.productsdatasourcesworkflow.v1beta;

import shopping.merchant.samples.datasources.v1beta.UpdateDataSourceSample;
import shopping.merchant.samples.utils.Config;

/**
 * This class demonstrates how to add supplemental DataSources to the primary DataSource's default
 * rule.
 */
public class AddSupplementalDataSourceToPrimaryDataSourceSample {

  // [START add_supplemental_datasource_to_primary_datasource]
  public static void addSupplementalDataSourceToPrimaryDataSource(
      Config config,
      String primaryDataSourceName,
      String firstSupplementalDataSourceName,
      String secondSupplementalDataSourceName)
      throws Exception {

    // Update the primary DataSource's default rule to include both supplemental feeds.
    UpdateDataSourceSample updateDatasource = new UpdateDataSourceSample();
    updateDatasource.updateDataSource(
        config,
        primaryDataSourceName,
        firstSupplementalDataSourceName,
        secondSupplementalDataSourceName);
  }

  // [END add_supplemental_datasource_to_primary_datasource]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // The names of the primary and supplemental datasources.
    String primaryDataSourceName = "accounts/{account_id}/dataSources/{datasource_id}";
    String firstSupplementalDataSourceName = "accounts/{account_id}/dataSources/{datasource_id}";
    String secondSupplementalDataSourceName = "accounts/{account_id}/dataSources/{datasource_id}";

    addSupplementalDataSourceToPrimaryDataSource(
        config,
        primaryDataSourceName,
        firstSupplementalDataSourceName,
        secondSupplementalDataSourceName);
  }
}
