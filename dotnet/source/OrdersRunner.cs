using System;

namespace ShoppingSamples.Content
{
    public class OrdersRunner
    {
        [STAThread]
        internal static void Main(string[] args)
        {
            var samples = new OrdersSample();
            samples.startSamples(args);
        }
    }
}

