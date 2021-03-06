# Copenhagen BioHackathon2020 - Team BioCrawCraw

This is the home repository of team BioCrawCraw's contributions in Copenhagen BioHackathon 2020.

[![Build Status](https://travis-ci.com/biocrawler/md-web-scrapers.svg?branch=master)](https://travis-ci.com/biocrawler/md-web-scrapers.svg?branch=master)


Web Scraper initial design:
===============
___

![](initial_design.png)

___

The Components listed above are:

Queue Producers:
---------------

  These should be maintained under the `boiler_plate` directory. They supply, or produce study data that is parsed
  or crawled from external sites.
  Hosting for producers is provided by:
  
  
  [![FossHost](FosshostLogo_s.png)](https://fosshost.org/)
   
Queue:
------
 
   We currently use aws SQS for storing messages in a cost effective way
   
Queue Consumer:
---------------
 
   This component is scheduled to be built with the intent to consume messages that are delivered
   to the queue. This component will process the messages and store them in the database. Will will use
   a cost effective database initlally for storing messages.
   
REST API:
---------

  This component is scheduled to be built where by studies stored in the database can be queried
  based on study attribute (i.e. author, publish date, keyword)
