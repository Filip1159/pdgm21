package view.panels

import controller.connection.KalahaClientConnection

import java.awt.{Color, FlowLayout}
import javax.swing.border.LineBorder
import javax.swing.{JButton, JLabel, JPanel}

class GamePanel extends JPanel:
    var startGameButton: JButton = new JButton("Start game")
    var timeoutLabel: JLabel = new JLabel("Time: 30")
    
    add(startGameButton)
    add(timeoutLabel)
    setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10))