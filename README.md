BibTeXTools
===========

Java project containing a number of tools to create, clean and handle BibTeX files.

Also see [this](http://blog.macuyiko.com/post/2014/converting-plain-text-references-to-bibtex-or-endnote-or.html) blog post to learn how to convert a list of plain text references to a BibTeX one, based on [this code](https://gist.github.com/Macuyiko/9237026#file-scholar-cite-py).

DBLPUtils
---------

Utility class to perform DBLP queries. Check this first if everything stops working.

MakeDBLPBibTeX
--------------

This tool will take your existing BibTeX file and attempt to replace your entries with a DBLP one.

MakeDBLPQueryBibTeX
-------------------

Add results from a DBLP search to a BibTeX file (and remove duplicates). This is especially handy if you quickly want to throw together a bibliography based on an author, topic, title, ..., and don't mind a few redundant entries.

MakeMergedBibTeX
----------------

Recurse a directory tree, fetch all ".bib" files and merge them into one gigantic output file.

MakeUniqueBibTeX
----------------

Remove duplicate entries based on simple string matching.
