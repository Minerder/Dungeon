package starter;

import com.badlogic.gdx.Gdx;

import contrib.entities.EntityFactory;

import core.Game;
import core.System;
import core.components.PositionComponent;
import core.utils.SoundPlayer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

public class GameOverScreen implements ActionListener, WindowListener {
    private final JButton btnQuit, btnRestart;
    private final JFrame frame;

    /** Creates a new Game Over Screen with the options to restart or quit the game */
    public GameOverScreen() {
        Game.systems.forEach(System::stop);

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setTitle("Game-Over");
        frame.addWindowListener(this);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(49, 47, 47));

        JPanel panelButton = new JPanel(new GridLayout());

        final JLabel lblGameOver = new JLabel("Game Over!", SwingConstants.CENTER);
        lblGameOver.setForeground(new Color(224, 102, 102));
        lblGameOver.setFont(new Font(null, Font.BOLD, 20));
        panel.add(lblGameOver, BorderLayout.CENTER);

        btnQuit = new JButton("Quit");
        btnQuit.setPreferredSize(new Dimension(100, 50));
        btnQuit.addActionListener(this);
        btnQuit.setForeground(Color.white);
        btnQuit.setBackground(new Color(49, 47, 47));
        btnQuit.setBorderPainted(false);
        panelButton.add(btnQuit);

        btnRestart = new JButton("New Game");
        btnRestart.setPreferredSize(new Dimension(100, 50));
        btnRestart.addActionListener(this);
        btnRestart.setForeground(Color.white);
        btnRestart.setBackground(new Color(49, 47, 47));
        btnRestart.setBorderPainted(false);
        panelButton.add(btnRestart);

        panel.add(panelButton, BorderLayout.SOUTH);
        frame.add(panel);
        frame.setVisible(true);
        SoundPlayer.play("sounds/gameover/game_over.mp3");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        frame.dispose();
        Game.systems.forEach(System::run);
        if (source.equals(btnQuit)) {
            closeGame();
        } else if (source.equals(btnRestart)) {
            Game.setHero(EntityFactory.getHero());
            PositionComponent heroPC =
                    (PositionComponent)
                            Game.getHero()
                                    .orElseThrow()
                                    .getComponent(PositionComponent.class)
                                    .orElseThrow();
            Game.resetDepth();
            Game.setHero(Game.getHero().orElseThrow());
            heroPC.setPosition(Game.currentLevel.getEndTile().getCoordinateAsPoint());
        }
    }

    private void closeGame() {
        Gdx.app.exit();
    }

    @Override
    public void windowClosing(WindowEvent e) {
        closeGame();
    }

    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}
}
