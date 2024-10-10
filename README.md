# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

Link to my Sequence Diagram: (https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43CgBSOlyQADMYOwwhDUAgOKpoVBDBtsGBLN1+qN1MB7Pc+gBRKDeMowPQcGAogCOPjUYD6BzBIIK2XMpQqABYnE5Gli+jjVHiCcTSeUKVSULT6YzMICLrBTkdROUUdFYpQABSRdWZKCRaWxACUh2KohOhVk8iUKnU5XxYAAqh1NTc7iarYplGokadjKUAGJITgwZ2UT3ksJu4CjTCem0+3Kgs0qUoosA+W6hjqe00iFRJ-Ly4GlSGSjO3JEAQjQPgQCDl52BheZpWxPuFK1F0HKwAQKOAHDC8gA1uhZczC2yMBzuQBmPm9AUd-Fdkk96H9lCD4fAMdoCcpwynePeu0gAeZMNQTXXz0e7QJ9QWow6UoKDiUu-aPNUc0FU9bVUUoL23TIFB8MBUk1CCoPvONHzPX1Cn9D9KVg1Jc0AxNk3zFA0xQCskmASDUnqCB91-f9TmLS4IiiGI9UiVQ6ywWjFSVYo6OuDo7kmcpVieEioPI-d+KmfZf0oE5TmnMBygqAAmHlFx4oYYzGGABMeKZhLIijxy0iTZXQDhTC8Xx-ACaB2HxGAABkIGiJIAjSDIsmQcwX1bSpagaZoWgMdQEjQflo1GXYvheN4PlWJkuMVQp2LbAZeI0yKnmipZIoBJtKFwv9UwQJzg1dNLRlmLL3hNZUC3ybC7Qda8yvU0YH2tJCCn9IMQ2-eRIxgcKxAa1QWyPAiiOzcMf1qlBC2Sss+uAGAAF4VpgWt60bIF8oKy521xVcBO7Mlrw2iBmAhXxOEPahdvyOSOQAVhU-lBU7Y711Ojpzsu67lnivD5ryspFpzbQABIqspKt1uh7aFTGu6ygOoV8U+sVBuCUjoCQAAvFAAaklkHs8mcuScABGVTl0O-CphO8ofGxqDcYJonZoAxCgJAy8UAwzU9PgkaurfQd0NIrDuZw-JZomzNiNI0T0CogsCmS9MFZgYrnMlFiEDYkGkZKFKhoy3SlYMg9tNukopzJ+SlNepczc+ITLbEm2DjMThzO8PxAi8FB0AcpzfGYVz0kyTA5O8hKFOkQl7MJepCQCoLVBC7o9OVtBifVkGUpzq2coR5tZfGnXw8Fj30Bqo8uY6nmHQF4v93ar0gNF0o4U4JE9LZ+JEhgCFvGGaFa7zkWCpVNAIAn1moHxoekiu5mzNVuaC520G0CoPskEpNuVfY439tptH7gZr6mZZ1I2cJ237seinqbelcr6JG+sYHpf2dlaeFc8LyyzL-ZeBBEib0bp3H0pQOAoG4OBUiMFJ4dyfKNfI-p4GIMMBhKWTcZZy01lmCwdIoHbwVCApIVdIJ61YmXe6rYeiAztqyB2HJlK8ixN7UyftLKBBRJSeyjEYAAHENJIkju5GODs47IwUqIlOAV7AaWzpPfORZC79GPtbYyuUd4zyKoxcROIa4iStvXIG9VpaNUIq3VBCECHqG7r3I+OM-4rxHmPBe+lKKAKIYRLWLdSJ0INuQzRO9SxQj0r9Ee-0GEkyYRfD619MbMzAf-Fhz92Gvxpu9I6qSNzpPcfjR+LDRDQPQaUZAsQTFqE1GgzqmC3wOhgCo0Y2tGKOJgc4wx+FiHUJERAKE7S1DhI1oErMNTmDDLaRIhJZ8UZTFGaocYlR+ijIAJLSDWZTRSc5ORPDclecqBIvg6AQKAEc14+ITC+KMgAchpO5ewYCNCySTF+nDVLLIkWsioGyNLbN2fsw5UxjkoBuelO5TwLlXKhRFGFvzRhPNGC8t5BxeGeH9lZbAmZsDcHgHzMRGkUhRw8jkZgnF5G+TqE0Vooy1HmP3FiR5GkPkUJLNoyeay+iQDEk8NliKph8X0YjPaoMoRwgQYiGAaIMQwFRikr+mMJQ0jpLEJ+nycnci4UufJn9GbkkHJKQ0DIxXlzlqBOQKA6lmN8egWYoz3RQOsU44CwSoL2tzo0ruzSe7BjcYvcBIUvEQHHjo7p6DjYqgGW0uxISUT6ywJzTl4JokhLnn9deCyJUpQNbyo1xTg2ZPzmwyls4qZ5I-oW7+xb75-zKa6kavMwK2o0pqIVKBfWJn9dazIcyOnJAyKkDapLZldqjZ1PpVDB0oG2eMwuWzpC5qASbHoyL507PKHsg5MAOXlvZE7PVG6+jLpBXuzFvtsX8ICJYBBxVkgwAAFIQGDCSjpAQ4UgBHDIylciTa+UdPSlojK765yxAS4A96oBwAgMVKATqgXSA5REyh3LmXjgmPy8cmVLkwbgwhlYAAheyCg4AAGkWgAHUWCbNTvc5DKxgUWsYfHMs0qERInlWEJVBSVUbjVVKDV5qy2kwrRTE9yT+NGqE2a2Up810qgAFZvrQHanRSHRgus5m6npHqE1ep0T25x-rXE+MHhA1e3jI3+PGnGz1o6k30NTWhksZYYlZriTm0+ea+OGrrXfB+HMEr2wk9yN++qa0YyKUFxtIWrEtodHa5dJmMH+nM6MgaC67PANGbmVzC0oRQZgyAeD0AYAxE3AOIc0Jgr2DQITGAJ0YDzlXUwqYBaYtkj7DV3cYl91iZfvOH50mAuY169uWro5DLlLqi2vwWhMgpY0p6Sq+HKBlYQ7MIaaXu6LZtR+lA+D9MxtTHG0hYhCuFwu1AMhvm137QPeJo93zuE+w3hZAOAQvDQfgNwPA0JsAEsIJ4qR0dY7UsAxUROydU4BWMBo5KzCFlKdTIgbcN5LGFTmnpqp2DCL83rA0qdfqsEIMJ1NKAJ3o3+oJwOvBP4RZ04pwOupNPp1o-wpdxdkSecPaYc9r5zsUe8KAA)

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

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
