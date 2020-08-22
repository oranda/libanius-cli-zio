Libanius-CLI-ZIO
================

This is a command-line interface to the [Libanius](https://github.com/oranda/libanius) quiz 
library. Everything is implemented in Scala. This CLI is implemented 
using [ZIO](https://github.com/zio/zio).

Suggestions for new features and code improvements will be happily received by:

James McCabe <jjtmccabe@gmail.com>


Usage
=====

You need to have Scala installed. This project should work with most recent 2.x versions. It has been 
tested with Scala 2.12.6, Java 8, and sbt 1.3.8.

To install, either download the zip file for this project or clone it with git:

    git clone git://github.com/oranda/libanius-cli-zio

Then cd to the libanius-cli-zio directory and run it:

    sbt run
    
You will be presented with a list of quiz groups. Pick the one you're interested in, for instance
the ZIO quiz, and the quiz will start.

There is a lot of repetition in the quiz to aid learning. You have to answer a question 
correctly at least 6 times to be done with it. The first 4 times are multiple-choice: the 
last 2 times, you have to type it in. If you answer a question incorrectly, the count resets 
to zero. Towards the end, you will notice you are left with only the questions you had trouble 
with.

