// 3개의 클래스와 1개의 enum을 가지고 있음
package kr.ac.jbnu.se.tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    // 게임 보드의 가로와 세로 크기
    final int BoardWidth = 10;
    final int BoardHeight = 22;

    Timer timer; // 게임 루프용 타이머
    boolean isFallingFinished = false; // 블록이 떨어지는 중인지 여부
    boolean isStarted = false; // 게임이 시작되었는지 여부
    boolean isPaused = false; // 게임이 일시 중지되었는지 여부
    int numLinesRemoved = 0; // 제거된 줄 수
    int curX = 0; // 현재 블록의 x 좌표
    int curY = 0; // 현재 블록의 y 좌표
    JLabel statusbar; // 게임 상태 표시 레이블
    Shape curPiece; // 현재 블록
    Tetrominoes[] board; // 게임 보드

    // 게임 보드 생성자
    public Board(Tetris parent) {

        setFocusable(true);
        curPiece = new Shape();
        timer = new Timer(400, this); // 400 밀리초마다 actionPerformed() 호출
        timer.start();

        statusbar = parent.getStatusBar(); // Tetris 클래스에서 상태바 레이블 가져오기
        board = new Tetrominoes[BoardWidth * BoardHeight];
        addKeyListener(new TAdapter()); // 키 이벤트 리스너 등록
        clearBoard();
    }

    // 타이머에서 호출되는 메서드
    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece(); // 새로운 블록 생성
        } else {
            oneLineDown(); // 블록을 한 칸 아래로 이동
        }
    }

    // 게임 보드의 각 정사각형의 가로 크기를 반환
    int squareWidth() {
        return (int) getSize().getWidth() / BoardWidth;
    }

    // 게임 보드의 각 정사각형의 세로 크기를 반환
    int squareHeight() {
        return (int) getSize().getHeight() / BoardHeight;
    }

    // 지정된 위치의 Tetrominoes를 반환
    Tetrominoes shapeAt(int x, int y) {
        return board[(y * BoardWidth) + x];
    }

    // 게임을 시작하는 메서드
    public void start() {
        if (isPaused)
            return;

        isStarted = true;
        isFallingFinished = false;
        numLinesRemoved = 0;
        clearBoard();

        newPiece(); // 새로운 블록 생성
        timer.start(); // 타이머 시작
    }

    // 게임 일시 중지 메서드
    private void pause() {
        if (!isStarted)
            return;

        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
            statusbar.setText("paused"); // 상태바에 "paused" 표시
        } else {
            timer.start();
            statusbar.setText(String.valueOf(numLinesRemoved)); // 상태바에 제거된 줄 수 표시
        }
        repaint();
    }

    // 게임 화면 그리기
    public void paint(Graphics g) {
        super.paint(g);

        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();

        // 게임 보드에 있는 블록들 그리기
        for (int i = 0; i < BoardHeight; ++i) {
            for (int j = 0; j < BoardWidth; ++j) {
                Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
                if (shape != Tetrominoes.NoShape)
                    drawSquare(g, 0 + j * squareWidth(), boardTop + i * squareHeight(), shape);
            }
        }

        // 현재 블록 그리기
        if (curPiece.getShape() != Tetrominoes.NoShape) {
            for (int i = 0; i < 4; ++i) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, 0 + x * squareWidth(), boardTop + (BoardHeight - y - 1) * squareHeight(),
                        curPiece.getShape());
            }
        }
    }

    // 현재 블록을 아래로 떨어뜨리는 메서드
    private void dropDown() {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1))
                break;
            --newY;
        }
        pieceDropped(); // 블록을 떨어뜨린 후의 처리
    }

    // 현재 블록을 한 칸 아래로 이동하는 메서드
    private void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1))
            pieceDropped(); // 블록을 떨어뜨린 후의 처리
    }

    // 게임 보드 초기화
    private void clearBoard() {
        for (int i = 0; i < BoardHeight * BoardWidth; ++i)
            board[i] = Tetrominoes.NoShape;
    }

    // 블록이 떨어진 후의 처리
    private void pieceDropped() {
        for (int i = 0; i < 4; ++i) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BoardWidth) + x] = curPiece.getShape();
        }

        removeFullLines(); // 가득 찬 줄 제거

        if (!isFallingFinished)
            newPiece(); // 새로운 블록 생성
    }

    // 새로운 블록 생성
    private void newPiece() {
        curPiece.setRandomShape();
        curX = BoardWidth / 2 + 1;
        curY = BoardHeight - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(Tetrominoes.NoShape);
            timer.stop();
            isStarted = false;
            statusbar.setText("game over"); // 상태바에 "game over" 표시
        }
    }

    // 블록을 이동하려는 위치로 이동할 수 있는지 확인하는 메서드
    private boolean tryMove(Shape newPiece, int newX, int newY) {
        for (int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
                return false;
            if (shapeAt(x, y) != Tetrominoes.NoShape)
                return false;
        }

        curPiece = newPiece;
        curX = newX;
        curY = newY;
        repaint();
        return true;
    }

    // 가득 찬 줄을 제거하는 메서드
    private void removeFullLines() {
        int numFullLines = 0;

        for (int i = BoardHeight - 1; i >= 0; --i) {
            boolean lineIsFull = true;

            for (int j = 0; j < BoardWidth; ++j) {
                if (shapeAt(j, i) == Tetrominoes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                ++numFullLines;
                for (int k = i; k < BoardHeight - 1; ++k) {
                    for (int j = 0; j < BoardWidth; ++j)
                        board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
                }
            }
        }

        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            statusbar.setText(String.valueOf(numLinesRemoved));
            isFallingFinished = true;
            curPiece.setShape(Tetrominoes.NoShape);
            repaint();
        }
    }

    // 정사각형을 그리는 메서드
    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
        Color colors[] = { new Color(0, 0, 0), new Color(204, 102, 102), new Color(102, 204, 102),
                new Color(102, 102, 204), new Color(204, 204, 102), new Color(204, 102, 204), new Color(102, 204, 204),
                new Color(218, 170, 0) };

        Color color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
    }

    // 키 이벤트 리스너 클래스
    class TAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {

            if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape) {
                return;
            }

            int keycode = e.getKeyCode();

            if (keycode == 'p' || keycode == 'P') {
                pause(); // 게임 일시 중지/재개
                return;
            }

            if (isPaused)
                return;

            switch (keycode) {
                case KeyEvent.VK_LEFT:
                    tryMove(curPiece, curX - 1, curY); // 왼쪽으로 이동
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMove(curPiece, curX + 1, curY); // 오른쪽으로 이동
                    break;
                case KeyEvent.VK_DOWN:
                    tryMove(curPiece.rotateRight(), curX, curY); // 시계 방향으로 회전
                    break;
                case KeyEvent.VK_UP:
                    tryMove(curPiece.rotateLeft(), curX, curY); // 반시계 방향으로 회전
                    break;
                case KeyEvent.VK_SPACE:
                    dropDown(); // 블록을 떨어뜨림
                    break;
                case 'd':
                    oneLineDown(); // 한 칸 아래로 이동
                    break;
                case 'D':
                    oneLineDown(); // 한 칸 아래로 이동
                    break;
            }

        }
    }
}
