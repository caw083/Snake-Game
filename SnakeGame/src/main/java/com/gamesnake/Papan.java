package com.gamesnake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Papan extends JPanel implements ActionListener {
    private final int LEBAR_PAPAN = 400;
    private final int TINGGI_PAPAN = 400;
    private final int UKURAN_TILE = 10;
    private final int TOTAL_JUMLAH_TILE = 900;
    private final int RAND_POS = 29;
    private final int DELAY = 140;
    private final int x[] = new int[TOTAL_JUMLAH_TILE];
    private final int y[] = new int[TOTAL_JUMLAH_TILE];
    private int dots;
    private int apple_x;
    private int apple_y;
    private boolean arahKiri = false;
    private boolean arahKanan = true;
    private boolean arahAtas = false;
    private boolean arahBawah = false;
    private boolean inGame = true;
    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;

    public Papan() {
        initBoard();
    }

    private void initBoard() {
        addKeyListener(new KeyAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(LEBAR_PAPAN, TINGGI_PAPAN));
        loadImages();
        initGame();
    }

   

    private void loadImages() {
        try {
            ImageIcon iid = new ImageIcon(getClass().getResourceAsStream("/dot.png").readAllBytes());
            ball = iid.getImage();

            ImageIcon iia = new ImageIcon(getClass().getResourceAsStream("/apple.png").readAllBytes());
            apple = iia.getImage();

            ImageIcon iih = new ImageIcon(getClass().getResourceAsStream("/head.png").readAllBytes());
            head = iih.getImage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initGame() {
        dots = 3;
        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 50;
        }
        hitungPosisiApple();
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        if (inGame) {
            g.drawImage(apple, apple_x, apple_y, this);
            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.drawImage(head, x[z], y[z], this);
                } else {
                    g.drawImage(ball, x[z], y[z], this);
                }
            }
            Toolkit.getDefaultToolkit().sync();
        } else {
            gameOver(g);
        }
    }

    private void gameOver(Graphics g) {
        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (LEBAR_PAPAN - metr.stringWidth(msg)) / 2, TINGGI_PAPAN / 2);
    }

    private void checkApple() {
        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            dots++;
            
            hitungPosisiApple();
            // Menambahkan elemen baru ke array x dan y
            for (int z = dots - 1; z > 0; z--) {
                x[z] = x[z - 1];
                y[z] = y[z - 1];
            }
        }
    }

    private void move() {
        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }

        if (arahKiri) {
            x[0] -= UKURAN_TILE;
        }

        if (arahKanan) {
            x[0] += UKURAN_TILE;
        }

        if (arahAtas) {
            y[0] -= UKURAN_TILE;
        }

        if (arahBawah) {
            y[0] += UKURAN_TILE;
        }
    }

    private void checkTumbrukan() {
        for (int z = dots; z > 0; z--) {
            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }

        if (y[0] >= TINGGI_PAPAN) {
            inGame = false;
        }

        if (y[0] < 0) {
            inGame = false;
        }

        if (x[0] >= LEBAR_PAPAN) {
            inGame = false;
        }

        if (x[0] < 0) {
            inGame = false;
        }

        if (!inGame) {
            timer.stop();
        }
    }

    private void hitungPosisiApple() {
        int r = (int) (Math.random() * RAND_POS);
        apple_x = ((r * UKURAN_TILE));

        r = (int) (Math.random() * RAND_POS);
        apple_y = ((r * UKURAN_TILE));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkTumbrukan();
            checkApple();
            move();
        }
        repaint();
    }
    private void restartGame() {
        inGame = true;
        dots = 3;
        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 50;
        }
        hitungPosisiApple();
        timer.start();
}

    private class KeyAdapter extends java.awt.event.KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if ((key == KeyEvent.VK_LEFT) && (!arahKanan)) {
                arahKiri = true;
                arahAtas = false;
                arahBawah = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!arahKiri)) {
                arahKanan = true;
                arahAtas = false;
                arahBawah = false;
            }

            if ((key == KeyEvent.VK_UP) && (!arahBawah)) {
                arahAtas = true;
                arahKanan = false;
                arahKiri = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!arahAtas)) {
                arahBawah = true;
                arahKanan = false;
                arahKiri = false;
            }

            if ((key == KeyEvent.VK_SPACE)) {
                // restart game
                inGame = true;
                arahKiri = false;
                arahKanan = true;
                arahAtas = false;
                arahBawah = false;
                restartGame();
            }
        }
    }
}