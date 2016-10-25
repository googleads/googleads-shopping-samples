using System;
using System.Collections.Generic;
using Google.Apis.Requests;
using Google.Apis.ShoppingContent.v2.Data;

namespace ContentShoppingSamples
{
    internal class ShoppingUtil
    {
        private int unique_id_increment = 0;

        /// <summary>
        /// Generates a unique ID based on the UNIX timestamp and a runtime increment.
        /// </summary>
        internal String GetUniqueId()
        {
            unique_id_increment += 1;
            String unixTimestamp =
                ((Int32)(DateTime.UtcNow.Subtract(new DateTime(1970, 1, 1))).TotalSeconds).ToString();
            return unixTimestamp + unique_id_increment.ToString();
        }

        /// <summary>
        /// Prints out a list of error results from the Content API.
        /// </summary>
        internal void PrintErrors(IList<SingleError> errors)
        {
            Console.WriteLine("Received the following errors:");
            foreach (SingleError err in errors)
            {
                Console.WriteLine(" - [" + err.Reason + "] " + err.Message);
            }
        }

        /// <summary>
        /// Prints out a list of warning results from the Content API.
        /// </summary>
        internal void PrintWarnings(IList<Error> warnings)
        {
            if (warnings == null) return;
            Console.WriteLine("Received the following warnings:");
            foreach (Error warn in warnings)
            {
                Console.WriteLine(" - [" + warn.Reason + "] " + warn.Message);
            }
        }
    }
}
