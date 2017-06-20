# Google Content API for Shopping and Manufacturer Center API C#/.NET Samples

This is a set of simple samples written in C#/.NET, which provide a minimal
example of Google Shopping integration within a command line application.

This starter project provides a great place to start your experimentation into
the Google Content API for Shopping and/or the Manufacturer Center API.

## Prerequisites

Please make sure that you're using either

* [Visual Studio](https://www.visualstudio.com/),
* [Xamarin Studio](https://www.xamarin.com/studio), or
* [MonoDevelop](http://www.monodevelop.com/)

with [NuGet package management](https://www.nuget.org/).
NuGet will handle getting all the dependencies for you with the package
configuration in these projects.

## Setup Authentication and Sample Configuration

If you have not already done so, please read the top-level `README` to discover
how to set up both authentication and the common sample configuration.  The rest
of this document assumes you have performed both tasks.

## Running the Samples

We are assuming you've checked out the code and are reading this from a local
directory. If not, check out the code to a local directory and load up the
solution. You will need to
[Restore NuGet Packages](https://docs.nuget.org/ndocs/consume-packages/package-restore)
as well to pull in the dependencies. (The IDEs listed above all support this.)

1. Build and run any of the included projects.  There are five:

   * Samples4.0 - .NET Framework 4.0 compatible project for main Content API samples
   * Samples4.5 - .NET Framework 4.5 compatible project for main Content API samples
   * Orders4.0 - .NET Framework 4.0 compatible project for Content API Orders service workflow sample
   * Orders4.5 - .NET Framework 4.5 compatible project for Content API Orders service workflow sample
   * Manufacturers - .NET Framework 4.5 compatible project for the Manufacturer Center API

2. Examine your shell output, be inspired and start hacking an amazing new app!
