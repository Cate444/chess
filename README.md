# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Chess Server Design
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5xDAaTgALdvYoALIoAIyY9lAQAK7YAMQALADMABwATG4gMHHI9r5gOgjRhgBKKPZIqhZySBBomIiopAC0AHzklDRQAFwwANoACgDyZAAqALowAPTRBlAAOmgA3gBEs5RowAC2KCvdKzArADSHuOoA7tAcewfHhyhbwEgIN4cAvpjCXTBtrOxclF6q3WUE2O1edxWZ1Ulyg132hxOKweTxeCJWHzYnG4sF+X1EvSg5UqYEoAAoyhUqpQygBHYpVACUn06oh+7Vk8iUKnUvUCYAAqnMySCwShmZzFMo1Kp2UYdN0AGJITgwIWUSUwHSWGCi7ZiHRE4AAazVcxg5yQYF8urmYpgwAQRo4OpQAA8SRpJdyZT88ayVL11VBJSyRCo-e0voCYAonShgC7Rib0ABRN0qbAEOph77NX6Ncy9BJOJKLVY7dTAQJ7Q6pqBRHq2jb6h3xxM6+TG9AYsycTDe6XqSMdcMoXpoaIIBC5tm-Qc81TdEBG0nBkVzSUS7Q+4e-YzdBQcDhmjXaWcR+c7odLlcJ0kKaLWsnAJ++LcD6+LuUHo8nx-WqG+KXr8WIAk2lIkjSahTlgYE4iO0ZNsCdr6gcvS3Eir7WqMEDdmg6GHO8uaUCOhYYL0aROE45ZrKh4IwBhiKHNhvi4fhhG3B86AcKYkQxPE0ABIYcSKnAqbSHACgwAAMhAFT1ORzD+tQMaDCMEzTAY6i1Gg5Z6jsJzQrCHAfEhcrwTGKEtuC6JIsZVwQpi-wISpVAEjACDySqZJyQpdIMmAzLAYYV5cjefIoIKwoGeKn7hd++4KsqqrBpq2rNqC+qmIaCamsGFpWjasVGBAahoAA5Mw7qevFUqJW5HkAHJTjOIWIZ0MZoBAYCKjEnDppm2b1OZ+btEpxalrRlaqNWuwYfWja9CV3XMAAZv11zcf2mC5SaMAAJJoCA0BEuAMAOXCpgLr6Y2ju5ganiG57tXdSHLZsb7QEgABeKAcINKBZrpJG4gWyBFjAACQfQlqEkxLIcM1zbWKyLdAH2sd9f3XDA228XVu6ym9AbjplQGk3KN28necgoABvgbpQTX6tuCW3e0v7HrGb4U2OI6WU2fkqmUqiwZggsdapyF0TZuyMURkKsexPYK1xoNkRDFEwFRNHLLLWUMUxStvirBFq8RPF8VEsRxJEKDoNkuT5Pbjt+TEWBKXK739NIqYyamoyppMUzaaoumLMreHoBroEuVZKwrFHHEYerkuNY97tPr58kewFahBReoUcl+Mq9BwKAIA+b4vqb0doGz9Uc-K5eV1FsrJzHr1x9iMZZ2AouwRo6duQnSxvL2o3g00YCUdRtHj72VsRDb8REiecTYCqpoySSMAAOL6hoXuj02fT74HIf2Pqkd1-hsftILQKJ53BH7G8MAS-HuJRqTkVgAzMkDN85MiLlTUuvIYD8kAa-RuRMfwKigVFB0d8Y67SNKaJqPUebWmxv9QmN4pYPTJpOacYCSbS0xl9KAv1-qA2BjmKe40taz2hrDJw8MYCIxWMjGsC0GwY11J9XBNCcYHHxtdCBxMM5k11qYV6Jd2aQOQFUQ+lYCGJU5og-kDpZJ7wgGtA+R8NG3Rkb0XeVQDFqLUH0cY5Ce7gXMSSKxR9bGmBHr-ShXDE7X0rLWPoqxfEoAOtIPYSJzi+CtCgYMYobiQkKKAY0MS0L7EhEElmOxUkf3GInMynUwbMJnnPPWqwVhBNUP4wJ+oQlhMOBEqJyTbKJyRAkkAST6LzWaYcdJ+oskwByRIleAk4gcAAOxuCcCgJw2RUxJDgOJAAbPAVchhrGfxPp4rovR1JjCvjfYRbF67lh6TsD4D8-i9xlknVBb8MSf3Tpsjy0Ca7AJQPSAuwVKZhSbpA55z5YEmL3Fo-+KCcL1xyhgmAWDmAMzwbxam0jHmPVIW1L5p8qEiNoQDDMQNhqx2npDGGcMEZIxlCjfhS0hFY1Ef9cRfYCYIqIR5OR5DFE-KXDAWmpJrFkiCSEk4KKjiLBgIKqB+oMkoBOEEuBhCkq9ETCeIJph7GP2-nyap0gv6XKIQnPloS1Z5OlprIpOt576z1bWQZ-FbaWErl5c4TskB5DALa6cEAHUACkIAqiMTsbIrTjQNBYd7fJ2yBgCk0lMIJt8wX4XLNgBAwBbVQDgBALyUApUasNXmVVlzn7XNjT2d+9zv4hrHP-QBrz3mgIUTIKRFaa4AsZXKpBzBX4QrylC7BsKaXwqkUy5FrUVX3RjNEA5cL6F4qYfAFhvQiUcJJTwslfC6wCKbGO6lWK6XL2bWY01Th5H5Ismq7xCak2UFTem2pZSNWo2ctqvda0VQcB5Xqz5-NvlE16E+zgr6NUys0S3H9L79S8v-cqvapo4zOh1Mme+u6kUkKHd3TZMZHQwbg2mHFDCRpHruhNNhxKuGHHYUkRdvD5qrspehhMLoYBdlVhPPG9LlUIfuk87KKGLmOLFTsLV4EdUywtWrM9ybL3QHVtOgjutaLCYwqJi9aaJPERY0M22kQk2OudRpnUiAEywGANgBNhAah1HWcG9Fvt-aB2DtMYw5yn7eIrtwUkd7+OuUQ63FzZJ33EPAUojlzmoo+cBcTYFQWwAheMJGBFxTQsDtkdRYdjm5EeJHU2ORUnZ37pY0AA

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
