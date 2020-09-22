Libanius-CLI-ZIO
================

This is a command-line interface to the [Libanius](https://github.com/oranda/libanius) quiz 
library. Everything is implemented in Scala. This CLI is implemented 
using [ZIO](https://github.com/zio/zio).

Suggestions for new features and code improvements will be happily received by:

James McCabe <jjtmccabe@gmail.com>


Installation
============

You need to have Scala installed. This project should work with most recent 2.x versions. It has been 
tested with Scala 2.12.6, Java 8, and sbt 1.3.8.

To install, either download the zip file for this project or clone it with git:

    git clone git://github.com/oranda/libanius-cli-zio


Usage
=====

After installing cd to the libanius-cli-zio directory and run it:

    sbt run
    
You will be presented with a list of quiz groups. Pick the one you're interested in, for instance
the ZIO quiz, and the quiz will start.

There is a lot of repetition in the quiz to aid learning. You have to answer a question 
correctly at least 6 times to be done with it. The first 4 times are multiple-choice: the 
last 2 times, you have to type it in. If you answer a question incorrectly, the count resets 
to zero. Towards the end, you will notice you are left with only the questions you had trouble 
with.


Making Your Own Quizzes
=======================

If you need to learn a subject, consider making a quiz for it. You need to write a quiz file.
The format is simple. Just study the sample quiz files in the `data/resources` folder. Look at
the first line. You can see that each question is asked a certain number of times depending
on the `numCorrectResponsesRequired` parameter, and multiple-choice is used the first x times,
where x is set using the `useMultipleChoiceUntil`.

The quiz files in `data/resources` have the `.txt` extension. If a user exits a quiz before 
finishing it, the state is saved in a `.qgr` file in the `data` folder. This will be read
on running libanius again. Remember to delete `.qgr` files if you want to start from the 
beginning.

Whenever the user answers a question correctly, it is said that s/he has moved up a "memory level" 
with respect to that item. The number of memory levels for a quiz is equal to 
`numCorrectResponsesRequired`. 

In any quiz file, quiz items are grouped into "partitions", where each partition corresponds to 
a memory level. You will observe that in the initial quiz file (`.txt`), all quiz items are in 
the first partition. As the user makes progress, quiz items are moved to other partitions, and 
you can observe this effect by quitting a quiz and looking at the persisted form, i.e. the 
`.qgr` file.

If you learn something using your own quiz file, and feel it is a success, consider 
submitting it to this project (e.g. via a PR) so that other people may learn too.


Other Resources
===============

If you are using this just to learn ZIO, you might consider using it
as a supplement to the exercises written by John De Goes himself, including:

- https://github.com/jdegoes/zio-workshop
- https://github.com/jdegoes/functional-effects


License
=======

Libanius-CLI-ZIO is licensed under the terms of the Apache 2.0 license.
                      
                      

