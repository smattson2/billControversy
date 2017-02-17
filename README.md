# Bill Controversy

# Replication:
  # If you have a Case Western Reserve University network ID:
    Please email sem129@case.edu from your Case email and attach a public SSH key. You will be added to the server, and this is by far the easiest way to access the data or replicate the study.
  
  # If you do not have a CWRU network ID:
    You will, unfortunately, have a few more steps involved. Sorry :(
    
    # Pulling the articles:
    First, obtain a New York Times API key and appropriate credentials (you do not need a subscription for this, but I advocate supporting their commitment to open data!)
    
    Insert your NYT credentials into the appropriate places in NYT_ArchiveAPI_Puller and update the directory field to reflect your filesystem. Run with two arguments: four digit years representing the first and last years you wish to pull from the archive. If only one year is specified, that year through 2016 will be pulled. If no years are specified, 1981-2016 will be pulled. (Note that this also cleans up a couple idiosyncrasies in the formatting of the provided JSONs, so it is recommended that articles pulls are done using this tool, rather than manually, even if you only want a small slice of the archive.)
    
    # Pulling the bills:
    No tool is provided here to pull the bills. The bill data is all courtesy of GovTrack.us, and is available on their site in the form of bulk data downloads using the rsync protocol or more manually. Please be mindful about what bills you actually need, as you can easily pull down several GB of data.
    
    # Executing:
      # Execute in parallel:
        The execution code takes an incredibly long time to run, which is unsurprising, as it is O(n^(a + b)), where a and b are the number of articles and bills, respectively. Therefore, parallel processing is key. Luckily, it's very easy!
        Download omp4j and compile with that (Note: Use a Linux-based command line. If you are a Windows user, I recommend Cygwin.). It should automatically run in parallel then, which is super cool. You may need to check out their help section on how to link it. If you have trouble, email me.
        
      # Executing on one CPU:
        You can still compile and run with javac (or with the Eclipse compiler) with no edits necessary. The code was written in Java 8. I don't think it requires 8, but no guarantees are made about earlier Java compilers.
        
      # Either way you compile, execute as follows:
        Make sure that ArticleBillCombiner has the appropriate file structure in its fields.
        Run ArticleBillCombiner with one or two arguments (zero-arg default is an error).
        First, required, argument is one of the following: short, full, first, last, or a number between 97 and 113, inclusive.
          These are different cuts of congresses to run the code on.
          First and last are equivalent to 97 and 113, respectively. Short is equivalent to first and last. Full is 97 through 113.
        The second, optional, argument can specify a Windows-based file system with "windows" or to run only on Senate-introduced bills with "senate."
        This requires Article, Bill, JSON_Parser, and Searcher, and requires that the articles and bills have already been pulled. The remaining files (Master, MyThread, RelatedBillMerger) are DEPRECATED. 
        
      # Analyzing:
        This will return CSVs (the configuration of which can be modified in Bill's toCSV method) organized by Congress for easy analysis with any tool. If you would like to see the Stata code used by the author, please email me at sem129@case.edu.
    
      # Good luck!!! Again, do not hesistate to email sem129@case.edu with questions.
    
