# MillionaireGame
> CS494 - Internetworking Protocols | Ho Chi Minh University of Science - VNU.

*Fall 2020 Course*

**PROJECT #1**: An online multi-player game based on "Who Wants To Be A Millionaire" TV Show

#### Author:
- 1751064 - Nguyen Hoang Gia 
- 1751063 - Pham Bao Duy


### Prerequisite
- [Java SE 15](https://www.oracle.com/java/technologies/javase-downloads.html) environment. Recommended: macOS, Windows 10 with Java SE 15 installed.
- Download or clone the project.
- Change directory to ./Release.

### How to play
You will need Java SE 15, download at https://www.oracle.com/java/technologies/javase-downloads.html.

#### Method 1: Running directly from .JAR files
As guaranteed, we want this application to be portable, simple and easy to use. So just simply double-click to run the **MillionaireGame.jar** file located in the **/Server** folder for the server, or in the **/Client** for Client(s).

_If you cannot run multiple Client programs, you can try duplicating the .jar file and run it._

![Method 1](https://user-images.githubusercontent.com/40845574/100724572-98e3c480-33f5-11eb-8085-44f6c19ac7b5.png)

#### Method 2: Running from command-line or terminal 
Change dir to the ./Server directory for the server program, or ./Client directory for client(s).
```shell
cd <Path-To-Directory>
```

Run MillionaireGame.jar by the command
```shell
java -jar MillionaireGame.jar
```

_To run multiple Client programs, you need multiple Command-line tools or Terminals._

In this method, you can watch the system output in order if it has any errors. We also print the correct answer on Server whenever the question is generated (as a backdoor).

![Method 2](https://user-images.githubusercontent.com/40845574/100724660-b153df00-33f5-11eb-9eaa-b39096859c28.png)

### MillionaireGame features and techniques
#### Features
- “Who Wants To Be A Millionaire” is a multi-players game suitable for everyone to play with their friends (2 - 10 player). It can be installed on any Desktop and Laptop platforms that support “Java Platform, Standard Edition” version 15. It is funny and easy to play.
- Questions and answers
    - A dataset that has thousands of questions.
    - Provides knowledge in many fields.
    - Diverse questions.
    - Randomly picked when the game started.
- Host can manipulate players
    - Host has many privileges that can control the gameplay or “taking the game hostage”.
    - Host can decide whenever to check players’ answers.
- Countdown time
    - Players must answer the question in a limited time to be qualified.
    - He/she will be disqualified when the time is running out.
- Competition
    - 2 - 10 players compete against each other to find the Winner(s).

#### Technique
- Socket
- ServerSocket
- InputBuffer
- SQLiteDatabase
- Cursor
- CountdownTimer
- GradientShape
- Animation
- PopupWindow
- ConstraintLayout
- Cardview
- LayoutInflater
- database