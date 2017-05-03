using System;

namespace ShoppingSamples.Content
{
    public class ContentSamplesRunner
    {
        [STAThread]
        internal static void Main(string[] args)
        {
            var samples = new ShoppingContentSample();
            samples.startSamples(args);
        }
    }
}

