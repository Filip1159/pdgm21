package controller.connection

import controller.KalahaController
import controller.actionlistenersimpl.*
import controller.animation.MoveAnimation
import status.GameStatus
import view.KalahaGuiCreator

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.net.{InetAddress, Socket}
import javax.swing.{JButton, JLabel, JTextField}

class KalahaClientConnection(controller: KalahaController):
    var socket: Socket = null
    var reader: BufferedReader = null
    var writer: PrintWriter = null

    def connectToServer(): Unit =
        socket = new Socket("127.0.0.1", 6666)
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        writer = new PrintWriter(socket.getOutputStream(), true)
        new Thread(new SocketReadingRunnable(reader, controller)).start()

    def commandJoinGame(): Unit =
        println("commandJoinGame")
        writer.println(s"joinGame${controller.gameStatus.name}")

    def commandGetPlayers(): Unit =
        println("commandGetPlayers")
        writer.println("showPlayers")

    def commandStartGame(): Unit =
        println("commandStartGame")
        writer.println("startGame")

    def commandMakeMove(holeNumber: Int) =
        println("commandMakeMove(" + holeNumber + ")")
        if holeNumber < 0 || holeNumber > 5 then throw new IllegalArgumentException("Illegal hole number!")
        writer.println(s"makeMove ${controller.gameStatus.name}; $holeNumber")

    def commandDisconnect(): Unit =
        println("commandDisconnect")
        writer.println("disconnect")

    def commandAnimationDone(): Unit =
        println("commandAnimationDone")
        writer.println("animationDone")