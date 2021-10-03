[![Maven](https://img.shields.io/maven-central/v/ch.astorm/jchess.svg)](https://search.maven.org/search?q=g:ch.astorm%20AND%20a:jchess)
[![Build](https://app.travis-ci.com/ctabin/jchess.svg?branch=master)](https://app.travis-ci.com/github/ctabin/jchess)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/ctabin/jchess.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/ctabin/jchess/context:java)

# jchess

Simple Java Chess game API.

## About this project

The goal is to provide as simple, easy-to-use API to manipulate chess games in Java.
It is also easy to extend in order to create extended chess rules, new pieces and so on.

This API does NOT provide any chess engine for position analysis. However, this API could
be very easily used by an engine to do such task.

## Installation (maven)

Use the following dependency in your `pom.xml`:

```xml
<dependency>
    <groupId>ch.astorm</groupId>
    <artifactId>jchess</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick Start Guide

A standard chess game can be very easily created:

```java
//new chess game with standard board and rules
JChessGame game = JChessGame.newGame();

//parser to easily make moves by using algebraic notation
MoveParser parser = new MoveParser(game);

//apply the move to the game
parser.doMove("e4");

//cancel the previous move
game.back();

//moves can be grouped in a list
parser.doMoves(Arrays.asList("e4","e5","Nf3","Nc6","Bb5","a6","Ba4","Nf6","O-O","Be7",
                             "Re1","b5","Bb3","d6","c3","O-O","h3","Nb8","d4","Nbd7",
                             "c4","c6","cxb5","axb5","Nc3","Bb7","Bg5","b4","Nb1","h6",
                             "Bh4","c5","dxe5","Nxe4","Bxe7","Qxe7","exd6","Qf6","Nbd2","Nxd6",
                             "Nc4","Nxc4","Bxc4","Nb6","Ne5","Rae8","Bxf7+","Rxf7","Nxf7","Rxe1+",
                             "Qxe1","Kxf7","Qe3","Qg5","Qxg5","hxg5","b3","Ke6","a3","Kd6",
                             "axb4","cxb4","Ra5","Nd5","f3","Bc8","Kf2","Bf5","Ra7","g6",
                             "Ra6+","Kc5","Ke1","Nf4","g3","Nxh3","Kd2","Kb5","Rd6","Kc5",
                             "Ra6","Nf2","g4","Bd3","Re6"));
```

## Usage

Here are some usages about specific parts of the API.

### Game initialization

The creation of a new game is straightforward:

```java
JChessGame game = JChessGame.newGame();
```

Once the game is created, it has already the standard chess rules and the initial
position built in. Hence, the game is ready.

### Start from a custom position

In some cases, one may want to starts from a custom position. In order to use
the `JChessGame` instance, it is mandatory to have at least the two kings in
the position before retrieving the available moves.

```java
JChessGame game = JChessGame.newEmptyGame(Color.WHITE);
game.getPosition().put(0, 0, new King(Color.WHITE));
game.getPosition().put(7, 7, new King(Color.BLACK));
```

### Position's legal moves

It is very easy to retrieves all the legal move in a position for the color
that has the move:

```java
List<Move> legalMoves = game.getAvailableMoves();
```

It is also possible to retrieves the legal moves for a given entity:

```java
Coordinate kingCoordinate = game.getPosition().findLocation(King.class, Color.WHITE);
Moveable king = game.getPosition().get(kingCoordinate);
List<Move> kingMoves = game.getAvailableMoves(king);
```

### Play moves

To play the moves in the game, it is recommanded to use the `MoveParser` interface, since
it is much more readable than using board indexes. This allows to use the [algebraic notation](https://en.wikipedia.org/wiki/Algebraic_notation_(chess))
to update the position.

```java
JChessGame game = JChessGame.newGame();
MoveParser parser = new MoveParser(game);

parser.doMoves(Arrays.asList("e4","e5"));
parser.doMoves(Arrays.asList("Nc3","e6"));
```

Each move will update the current position and automatically switch the `Color` being on the move.
It is possible to go back in the move by using the `game.back()` method.

### Game status

After each move, one can retrieve the game status that can be one of the following values:

 Status | Description | Continue |
 ------ | ----------- | -------------- |
 `NOT_FINISHED` | The game is not finished. | Yes |
 `WIN_WHITE` | The whites have won. | *No* |
 `WIN_BLACK` | The blacks have won. | *No* |
 `DRAW` | The game is draw either by agreement (see `JChessGame.draw()`) or by insufficiant material. | Yes |
 `DRAW_STALEMATE` | The game is a draw by stalemate. One of the player has no legal move that does not put his king in check. | *No* |
 `DRAW_REPETITION` | The game is a draw by repeating the same position 5 times. | *No* |
 `DRAW_NOCAPTURE` | The game is a draw by playing 75 moves without any capture nor pawn move. | *No* |

It is possible to continue to play even if the status is `DRAW` because it is generally not enforced to stop
(see [dead positions](https://en.wikipedia.org/wiki/Rules_of_chess#Dead_position)).

The status can simply be retrieved after any move with the code below:

```java
Status status = game.getStatus();
```

### Import games from PGN files

It is possible to import basic [PGN](https://en.wikipedia.org/wiki/Portable_Game_Notation) files
as `JChessGame`. Note that the API does not support extended PGN with remarks and deviations.

```java
try(PGNParser pgnParser = new PGNParser(/* reader */) {
    JChessGame game = pgnParser.readGame();
    while(game!=null) {
        /* handle game */
        
        //read next game, if any
        game = pgnParser.readGame();
    }
}
```

### Metadata

The PGN parser supports metadata. This is simply a list of key/value pairs that are stored
during the parsing of the file.

```
JChessGame game = pgnParser.readGame();

Map<String,String> metadata = game.getMetadata();
String result = metadata.get("Result");
```

### Printing a position

Mainly for debug purposes, jchess provides a simple API to print a position in CLI:

```java
System.out.println(PositionRenderer.render(game.getPosition()));

/*
  |---|---|---|---|---|---|---|---|
8 | r | n | b | q | k | b | n | r |
  |---|---|---|---|---|---|---|---|
7 | . | . | . | . | . | . | . | . |
  |---|---|---|---|---|---|---|---|
6 |   |   |   |   |   |   |   |   |
  |---|---|---|---|---|---|---|---|
5 |   |   |   |   |   |   |   |   |
  |---|---|---|---|---|---|---|---|
4 |   |   |   |   |   |   |   |   |
  |---|---|---|---|---|---|---|---|
3 |   |   |   |   |   |   |   |   |
  |---|---|---|---|---|---|---|---|
2 | ^ | ^ | ^ | ^ | ^ | ^ | ^ | ^ |
  |---|---|---|---|---|---|---|---|
1 | R | N | B | Q | K | B | N | R |
  |---|---|---|---|---|---|---|---|
    a   b   c   d   e   f   g   h  
*/
```
