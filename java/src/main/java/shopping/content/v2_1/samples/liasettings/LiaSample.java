package shopping.content.v2_1.samples.liasettings;

import java.io.File;
import java.io.IOException;
import shopping.content.v2_1.samples.ContentSample;

/** Base class for the Local Inventory Ads API samples. */
public abstract class LiaSample extends ContentSample {
  public LiaSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  protected void loadConfig(File path) throws IOException {
    config = LiaConfig.load(path);
  }

  protected LiaConfig getLiaConfig() {
    return (LiaConfig) this.config;
  }
}
