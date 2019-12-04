namespace ShoppingSamples
{
    /// <summary>
    /// A data class for storing the info needed to authenticate API calls.
    /// </summary>
    abstract class BaseConfig
    {
        public abstract string ConfigDir { get; set; }
        internal abstract string ConfigFile { get; set; }
    }
}

