# kahoot-hack

Reverse engineering kahoot.it

This has similar functionality to the code here https://github.com/unixpickle/kahoot-hack but is more portable as it is written in Java. The tools here contain more functionality. One of the primary goals here is to build a command line text based client for Kahoot.

There is a bug with team mode that causes the program to answer twice, but the program will still work with it. Only the first set of answers for a question will be accepted. I am working on fixing this bug.

Working as of November 9, 2016, but with bugs in answer correctness reporting that should be fixed soon.

## Building

This program uses the Gradle build system. Dependencies are managed by Gradle. Open a terminal/cmd window and navigate to the kahoot-hack folder.

To build on \*nix like systems: `./gradlew build`

To build on Windows: `gradlew build`

The build shouldn't take more than a few minutes on the first run. The final built jar can be found in the build/libs folder. The built jar is portable, meaning you can copy it to any spot on your computer and run it from there.

## Running

You can run the built jar from a terminal/cmd window: `java -jar kahoot-hack.jar`

Or: `java -jar build/libs/kahoot-hack.jar`

## Contributing

You are welcome to contribute by submitting a pull request or opening an issue on this repository. Any issues or PRs that are trolls will simply be closed.

