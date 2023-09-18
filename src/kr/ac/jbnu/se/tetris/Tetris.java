package kr.ac.jbnu.se.tetris;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Tetris extends JFrame {

    JLabel statusbar; // 게임 상태를 표시하는 레이블

    public Tetris() {

        statusbar = new JLabel(" 0"); // 초기 상태바 레이블 설정
        add(statusbar, BorderLayout.SOUTH); // 상태바를 프레임 아래쪽에 추가
        Board board = new Board(this); // 게임 보드 생성
        add(board); // 게임 보드를 프레임에 추가
        board.start(); // 게임 시작

        setSize(200, 400); // 프레임의 크기 설정 (가로 200, 세로 400)
        setTitle("Tetris"); // 프레임 제목 설정
        setDefaultCloseOperation(EXIT_ON_CLOSE); // 프레임을 닫을 때 프로그램 종료
    }

    // 상태바 레이블을 반환하는 메서드
    public JLabel getStatusBar() {
        return statusbar;
    }

    public static void main(String[] args) {
        Tetris game = new Tetris(); // Tetris 게임 객체 생성
        game.setLocationRelativeTo(null); // 프레임을 화면 가운데에 위치시킴
        game.setVisible(true); // 프레임을 화면에 표시
    }
}
