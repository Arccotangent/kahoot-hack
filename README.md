# Project Discontinued

I don't have sufficient time or motivation to update this tool anymore with my school, career, and personal life using most of my free time, so as of August 27, 2019 I'm discontinuing the project for an indefinite amount of time.

It's been over 2 1/2 years since I last updated this and the Kahoot protocol has likely gone through major changes ever since.
As can be expected, the changes to the Kahoot protocol have broken the tool and rendered it effectively useless.
This fact combined with the code quality and various redundancies would make it even more difficult to cleanup and update.

This will likely be the last commit to this repository (at least for a while).
The project is being licensed under the GNU AGPLv3, effectively making it free software.
Find the full license text in LICENSE.md.

If you're looking for a more updated Kahoot tool, check out this repository (this project was based on the knowledge and code in unixpickle's repository): https://github.com/unixpickle/kahoot-hack

If the tool is ever updated, it will be rewritten from scratch and uploaded to a new repository.

TLDR: The tool is very broken and likely won't be updated anymore. If it is updated, it will be rewritten from scratch.

The contents of the readme file are below as an archive.

# kahoot-hack

Reverse engineering kahoot.it

This has similar functionality to the code here https://github.com/unixpickle/kahoot-hack but is more portable as it is written in Java. The tools here contain more functionality. One of the primary goals here is to build a command line text based client for Kahoot.

There is a bug with team mode that causes the program to answer twice, but the program will still work with it. Only the first set of answers for a question will be accepted. I am working on fixing this bug.

Working as of December 10, 2016

## Building

This program uses the Gradle build system. Dependencies are managed by Gradle. Open a terminal/cmd window and navigate to the kahoot-hack folder.

To build on \*nix like systems: `./gradlew build`

To build on Windows: `gradlew build`

The build shouldn't take more than a few minutes on the first run. The final built jar can be found in the build/libs folder. The built jar is portable, meaning you can copy it to any spot on your computer (or on any computer) and run it from there.

## Running

You can run the built jar from a terminal/cmd window: `java -jar kahoot-hack.jar`

Or: `java -jar build/libs/kahoot-hack.jar`

There are 2 scripts you can use to automatically build and run the Kahoot hack.

*nix based systems: `./run` (run)

Windows: `run` (run.bat)

## Contributing

You are welcome to contribute by submitting a pull request or opening an issue on this repository. Any issues or PRs that are trolls will simply be closed.

