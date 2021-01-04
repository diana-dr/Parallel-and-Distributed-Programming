using System;
using System.Linq;

namespace Lab4
{
    class Program
    {

        static void Main()
        {
            var hosts = new[]
                {
                    "www.cs.ubbcluj.ro/~rlupsa/edu/pdp/lab-4-futures-continuations.html",
                    "www.cs.ubbcluj.ro/~rlupsa/edu/pdp/lab-3-parallel-simple.html",
                    "www.cs.ubbcluj.ro/~rlupsa/edu/pdp/",
                    "www.cs.ubbcluj.ro/~rlupsa/edu/pdp/lab-1-noncooperative-mt.html",
                    "www.cs.ubbcluj.ro/~rlupsa/edu/pdp/lab-2-producer-consumer.html"
                }.ToList();

            //TaskMethod.Run(hosts);
            TaskAsync.Run(hosts);
            //Callback.Run(hosts);
        }
    }
}
