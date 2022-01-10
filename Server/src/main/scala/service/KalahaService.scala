package service

import model.{KalahaAI, KalahaPlayer}

class KalahaService():
    var status: String = "not started"     // not started, waiting for move, animating, finished
    var turn: String = ""
    var turnSwitched: Boolean = false
    var makeMoveDeadline: Long = 0

    var playersRegistered: Int = 0
    var firstPlayer: KalahaPlayer = new KalahaAI("AliceAI")
    var secondPlayer: KalahaPlayer = new KalahaAI("JohnnyAI")

    def makeMove(player: KalahaPlayer, holeNumber: Int, opponent: KalahaPlayer): Boolean =
        var pl = player
        var num = holeNumber
        var stonesToMove = pl.stonesInHoles(num)
        pl.stonesInHoles(num) = 0
        while stonesToMove > 0 do
            num += 1
            if num == 6 then
                num = -1
                if pl == player then
                    pl.score += 1
                    stonesToMove -= 1
                pl = (if pl == player then opponent else player)
            else
                pl.stonesInHoles(num) += 1
                stonesToMove -= 1
        if num == -1 then true
        else
            if pl.stonesInHoles(num) == 1 && pl == player then
                pl.score += (if pl == player then opponent else player).stonesInHoles(5-num)
                (if pl == player then opponent else player).stonesInHoles(5-num) = 0
                pl.score += 1
                pl.stonesInHoles(num) = 0
            false

    def updatePredictions(ai: KalahaAI): Unit =
        for (i <- 0 until 6)
            val aiCopy = ai.copy()
            val opponentCopy = (if ai == firstPlayer then secondPlayer else firstPlayer).copy()
            makeMove(aiCopy, i, opponentCopy)
            ai.predictions(i) = aiCopy.score

    def printBoard(): Unit =
        for (i <- (1 until 6).reverse)
            print(s"${secondPlayer.stonesInHoles(i)} | ")
        println(secondPlayer.stonesInHoles(0))
        println(s"${secondPlayer.score}                     ${firstPlayer.score}")
        for (i <- 0 until 5)
            print(s"${firstPlayer.stonesInHoles(i)} | ")
        println(firstPlayer.stonesInHoles(5))
    
    def checkGameOver(): Boolean =
        val stones1 = firstPlayer.stonesInHoles
        val stones2 = secondPlayer.stonesInHoles
        (stones1(0) == 0 && stones1(1) == 0 && stones1(2) == 0 && stones1(3) == 0 && stones1(4) == 0 && stones1(5) == 0) ||
          (stones2(0) == 0 && stones2(1) == 0 && stones2(2) == 0 && stones2(3) == 0 && stones2(4) == 0 && stones2(5) == 0)
        
    def getWinner(): String =
        for stones <- firstPlayer.stonesInHoles do firstPlayer.score += stones
        for stones <- secondPlayer.stonesInHoles do secondPlayer.score += stones
        if firstPlayer.score > secondPlayer.score then firstPlayer.name
        else if firstPlayer.score < secondPlayer.score then secondPlayer.name
        else "draw"

    def registerPlayer(name: String): Unit =
        if name == firstPlayer.name || name == secondPlayer.name then throw new IllegalArgumentException("Given name is already in use!")
        if playersRegistered == 0 then
            firstPlayer = new KalahaPlayer(name)
            playersRegistered += 1
        else if playersRegistered == 1 then
            secondPlayer = new KalahaPlayer(name)
            playersRegistered += 1
        else throw new IllegalStateException("All players joined!")

    def getPlayerByUsername(name: String): KalahaPlayer =
        if firstPlayer.name == name then firstPlayer
        else if secondPlayer.name == name then secondPlayer
        else throw new IllegalArgumentException("No such user!")

    def getOpponentByUsername(name: String): KalahaPlayer =
        if firstPlayer.name == name then secondPlayer
        else if secondPlayer.name == name then firstPlayer
        else throw new IllegalArgumentException("No such opponent!")

    def startGame(): Unit =
        if status != "not started" then throw IllegalStateException("Game started before!")
        status = "waiting for move"
        turn = firstPlayer.name
        makeMoveDeadline = System.currentTimeMillis() + 30*1000
        
    def onAnimationDone(): Unit =
        status = "waiting for move"
        makeMoveDeadline = System.currentTimeMillis() + 30*1000
        
    def onTimeoutDefeat(): Unit =
        status = "finished"
        firstPlayer = new KalahaAI("AliceAI")
        secondPlayer = new KalahaAI("JohnnyAI")