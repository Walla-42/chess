# ♕ Multiplayer Terminal Chess Application

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

![Sequence Diagram](/shared/project_images/10k-architecture.png)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

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

## Program Design
Design documents have been created to guide the creation of this project and include the following:  

<a href="https://www.sequencediagram.org/index.html?presentationMode=readOnly&shrinkToFit=true#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDAKoDOlAEsGgCYK1c+QsR7MA4sAC2KbnwFQ6wxmPgC8c-rSUNRpdpQrVCKHSKYwAggFcwACyNUTZlaUkzHzobouIUeABEAI08QUxc9ZmsOKADLAHkIi2BbOzjE73NVAHNpFHTMTF5gMGAg4A4YXiDCgFp6hsamppgAJRRspDYwKBKkCDQYZuGR+sLsqAhrbBgAYmRsuzAghGsUNo6unr6BuZXQAGtMRFRSWoA+ckoaKAAuGABtAAV4sgAVAF0YAHpoygAdNAAbwARH8oGg8iDbiCYCCADRw3BsNgAd2gvGhsIRcJQUmASAQWLhAF9MI5KENLgYoJoFPdQeDITJiTiQci0RjWYiQXiCUSYaTMDS6ZSLqwYqEUPcoJtupQaQAKdqdeVQdoARzW3QAlIUaVKqRLDNcTPcQHYUCADjBZaqwJRNdqwAA6Jl5RU6wEWq02u1bR0oLUoboujnoqC8T3ey3W21yh3qoPOl18wmewpBWXAG0AWS6bCQaGyMHBVRKwGFktNYSNFLuMAAQsBeE6Q2AAKIADzC2AIA3J1zFlxO6DA9wALAAGKeA0EyFHAbIoaFw-MooslgIVmBFjbB7ogsnoXj66vGWvi8Hpe7LsBK-1qtuh90yT1668JI3Ve4AMSLvDGrEO5BAAnqWMTMuEWb+DaNIwMACDZrw4EoF2WxsJgn7xEM4oGjW0pAdupRViaF7rOK9b3DSUFvDm6Ddr2-ZoIO1DDmopzjjA04AMxziCC5sEuK73CC8GIchqHod0bBHpgJ6FNhuHUueThhPcaDWAgCBnmRakUZc2HmtmDpKjSxHAB+MTpN+QT3JYvCAeZFa6VAhrijY9hSre6CUCUKCeXYGaBe5lwhQR1EsAAkgELq9HwEBSCw0UBMFqShRBlA3jAkAHOglhIf4KHdhhiopPYbwQHlaBWVlX7ij+8CxjauXoKWaBIMGaAhphSn1GF6URTAABmiEcJg4XkcpQHeQhqSVdVrkZZN+nGf4DqBYqgUWXqK0mLZ9mOVYqQWUtBHTSKPBaA2KoBkmbDYAMHCKq+KCIuVdgLegeqXfIYqUUODaMpBUIwKJcI8h9X1oLC4NyfW02jhg9wAEwzvxr2w3CENwlDVXoFj2LHnwdSjGTMAADIQJ0gxk3TYyYBMUwzPMSCLGAEwoG1VM03sCCHMcyBjnWgP3M8ryfD8HAbgMGMgyygpsuGXKK2SCN4TEopA2C8sidiPLK5GrJkr911Gvh5H3Ag1NFkqPNFs+YB6qRbnnRrelmjAPpxtbNOO26uvRmg3s2r7DvJu2YYVJykYZpgME5jA66FsWXuyrwY5IGNLsZVRTYto7jEoH2-QserI5C8j3Ho8CAk9cJq4grm0TMGOlKvQhfAwIbmLE6eOdu4Z1kJD594xIqYdoP7r3vlhw84Q1dkwP+XfOaUMBgZlEJ5Jm2ZwTEMBoBAzDDVMJN9e7ruW4fWk6Rb+ki2xDYsJCqTQEgABeKC8JYIBhCiRcS4DnLhxMck4pwAEZ+KCQbqJGieQYDQG7tHCMgEui7mDtAWU4A5IKTnnVBel9Zpr0rPffaRChogESkIFATwUEYjYHHBONoopoCoIhJAgEe4DymgDJ+1FX72Hfl-H+f8eqAOYqxG4RokZcWnFA2uMDlyN3gTIRBsAe67jYBgqhUBsFgFwSTHhD8PKDWvsubqvQNqpDSl5Qex07HX2SjFOKV1ErONSrtMxJih4EPuK1NABVJIlRkmVea+Mar4NiPVS4jU4DNRyhE9qnU1jdRRFEmy-UHEOCGqNBA409qXgodfPGi1jHkIGo41aad1oBRsdtCsXiqkVKqEvBygEGkkXKUUlSXArr0kpjbKeIZHpoGeq9d64Tqo-U1v0-6lw87A0oFBbkuMpkE1VlI+ZoCq5o1nLXTGitIbrJhpsvB9MRiDOyFMZgFyybjEmNMOYujEJXJuXzAWsjH43DFgEDsFMOxvA7F8b40tCyy0CW-KAn8dhoHuAAHlKegc4WzYCXy1vZKFMLmIIqRWgFFpsFDm1Up7X2Ny7bUxuY7Z2ZDLyVJyeYlAYBNp4qaQynxrS-wAWyRZDe4E8W71gjAVh7DkCAQCRNbx5DinVM0tpbpBkrj8NYIIuwwjv6-3-mwCRpdUUyMrnIyB0D67KNEiKjhHTUgwGhoY-utLFWFMIhnAQ1j7BhIqhEtlGVGoBBQC69YnTKwKourMv6DYeZUpGU9FAGZCXbMWWrQGiMDWoxrkCPupM7mNHgCZFA7gKJZuGA85mcwnAQBdTm2p+aPnWkFpxb5lAxYvHeCC3IMhASBWEbC3FJzzj8TbSgAAclCTZID80YuyV25iMAADUG8IAoUwOOuZaLLj5tmiAXN+bFRwC3XkalhR132KPdfEONT-L5v9gO4db5nbMKTgWTcMAB2Hx3iejledmytgjt0HVwCk3ilkeA-Z84TUiThFIFu3dJhOAzghZ9CCVkZqXXkZaUr1LPqZSyk5Xr7GNRXpa+wvLN4CvjnvYVbCLVzQ9YtR10132ezlTpBjdKlU-JVR9dVoitV-rLgBiunFwEKNA4uU1cJrCqq41YMRKJbWHtQ-Ygd2VN1Vo9NevIeolMxM5RRmIzBuqohgPm06WnCFroU9fYzLkUMeDduKZdYb7i7tUzIdoD1o2KgHTFPUDmzZZMWSCLzARjZ6qyUBmAez+15BiiF85haGgwAAFIQCLNW+LzRi1PNmNAHgy4kspbQNW2Y+xa1fL4ex54LBJbfAHR2rFH9u0wERb2qLMgYu3AABoG35qBSgcBy3QCxAAdU4FFIF8N+NGbyBOzt0KGvTrnUEBdoEbOyBXUaFjhEABWBXt3JdS-un9Tt5O2d4eZ071Sz07YO65o7LogtByu7tw7B5XTYB631gbUAmHkeTk+4aqAEC8EwpthtDYv2Fx7MXSRIDwvThA3XUT4GQTlEArKV7cnVtoeaRhu82GaPfUlTjxV+HuWBr5dRz6ETBWJ3NWKyn0MifsuledlAs0mNY-sXnF+nG5sapk9qqHQC+NP31YJ6uwnEdCTE2CSTfPe7ySMaD8UpnR7bqC5pvImTYlLwI1NmQxHwJBcZlr+q9mLPVKs105XrON3NX69bKAlh2GEjKISJAYBQKKit8AREbx-BSAd9AO95Gg+wFKItG3bHG365QHRaqvHQsCbAdXPiiiwONyoY7hChUWzgQjwTZDUfNv3GmMUB0sfeWog93YLeUFOdnYQwbkepZsDl7zR6Mv-leD5s183heOvqJt-8pX6zyvzcyAnftwrh33NjJjT56b63-OixgOm0LgGU0RbTch9L9RKZbHzdovfjRMss2wEWG0FND95G0cV-mpWDVg7FuIQFra8h1aEXNxrzWCf4qT7HjNvVj-niiir5kShPmzkNMgN0EfoqNfrAbfgeg3hyo6qPPjlTtMkzt6rrmTidCBPyicjTiwpRvTgKnRlkiXjfPKlHtznLp-PzjxkLjDpNnDkaunkjiovQSItJlqpjlHqrjADAWAHAX3vkNpo1O0GANYBCDnggKPiRKZvRhbp7AgWAPCkfvEEEFtlaGACiigSzoASuvcGoUfm5qMs9AOmwIvpPsvuVjHqCFYdCA8C6K4R8ImqLmFlvpFrXE4bcC4W4bvnvmoP4LEI2LhCfrUGfnMAsEsC8vIX4IEOEffp8k-vYQ2A8H8gCkCiCtUAAYkbSMYSEXgI7BmAUVrPqqEcEBuuoN9gUcgQUdUfYh9MprURmC0RIUvL6v6nIZTjAGONCiGIUI0SEPYkZF7G0bVNEgPjpt0UyusIhPIWWAMagJhILFUaMY3oISAJMSbv3gdDAHMRXosU3usCsUMesYEJsSYpvqEROvUVGvPmUbURUSvsquvurJvuLj4evieEAA">UML Diagram</a>

## ♞ Gameplay Features   
This terminal-based chess application supports real-time multiplayer games with rich features via WebSocket communication and persistent backend support.

### Game Listing and Creation
After logging in, users can view all active games and create new ones.
<p align="center"> <img src="/shared/project_images/ListGames.png" alt="List Games"> </p>

### Join as White or Black
Players can choose to join a game as either white or black. The board orientation reflects the player's perspective.
<p align="center"> <img src="/shared/project_images/JoinGameWhite.jpg" width="400" alt="Join Game as White"> <img src="/shared/project_images/JoinGameBlack.jpg" width="400" alt="Join Game as Black"> </p>

### Observer Mode  
Users may observe any in-progress game in real time.
<p align="center"> <img src="/shared/project_images/JoinAsObserver.png"  alt="Observer Mode"> </p>

### Highlight Moves
Players can highlight a piece to view its legal moves, threats, and type.
<p align="center"> <img src="/shared/project_images/HighlightPieceMoves.png" alt="Highlight Moves"> </p>

### In-Game Notifications
Real-time WebSocket notifications inform players of events such as new moves, resignations, and connections.
<p align="center"> <img src="/shared/project_images/InGameNotifications.png" alt="In-Game Notifications"> </p>


