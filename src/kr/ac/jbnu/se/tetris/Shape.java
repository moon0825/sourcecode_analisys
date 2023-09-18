package kr.ac.jbnu.se.tetris;

import java.util.Random;

public class Shape {

    private Tetrominoes pieceShape; // 현재 블록의 모양
    private int coords[][]; // 블록의 좌표
    private int[][][] coordsTable; // 블록의 회전 정보를 저장하는 배열

    public Shape() {
        coords = new int[4][2];
        setShape(Tetrominoes.NoShape); // 기본 모양은 NoShape
    }

    // 블록 모양을 설정하는 메서드
    public void setShape(Tetrominoes shape) {

        coordsTable = new int[][][] {
            // 각 블록 모양의 좌표 정보
            // { {x0, y0}, {x1, y1}, {x2, y2}, {x3, y3} }
            { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } }, // NoShape
            { { 0, -1 }, { 0, 0 }, { -1, 0 }, { -1, 1 } }, // ZShape
            { { 0, -1 }, { 0, 0 }, { 1, 0 }, { 1, 1 } }, // SShape
            { { 0, -1 }, { 0, 0 }, { 0, 1 }, { 0, 2 } }, // LineShape
            { { -1, 0 }, { 0, 0 }, { 1, 0 }, { 0, 1 } }, // TShape
            { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 } }, // SquareShape
            { { -1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } }, // LShape
            { { 1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } } // MirroredLShape
        };

        // 블록의 좌표를 설정
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; ++j) {
                coords[i][j] = coordsTable[shape.ordinal()][i][j];
            }
        }
        pieceShape = shape;
    }

    // 특정 인덱스의 x 좌표를 설정하는 메서드
    private void setX(int index, int x) {
        coords[index][0] = x;
    }

    // 특정 인덱스의 y 좌표를 설정하는 메서드
    private void setY(int index, int y) {
        coords[index][1] = y;
    }

    // 특정 인덱스의 x 좌표를 반환하는 메서드
    public int x(int index) {
        return coords[index][0];
    }

    // 특정 인덱스의 y 좌표를 반환하는 메서드
    public int y(int index) {
        return coords[index][1];
    }

    // 현재 블록의 모양을 반환하는 메서드
    public Tetrominoes getShape() {
        return pieceShape;
    }

    // 무작위로 블록 모양을 설정하는 메서드
    public void setRandomShape() {
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 7 + 1; // 1부터 7까지의 난수 생성
        Tetrominoes[] values = Tetrominoes.values();
        setShape(values[x]); // 무작위로 선택된 모양으로 블록 설정
    }

    // 현재 블록의 x 좌표 중 가장 작은 값 반환
    public int minX() {
        int m = coords[0][0];
        for (int i = 0; i < 4; i++) {
            m = Math.min(m, coords[i][0]);
        }
        return m;
    }

    // 현재 블록의 y 좌표 중 가장 작은 값 반환
    public int minY() {
        int m = coords[0][1];
        for (int i = 0; i < 4; i++) {
            m = Math.min(m, coords[i][1]);
        }
        return m;
    }

    // 블록을 왼쪽으로 회전시키는 메서드
    public Shape rotateLeft() {
        if (pieceShape == Tetrominoes.SquareShape)
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {
            result.setX(i, y(i));
            result.setY(i, -x(i));
        }
        return result;
    }

    // 블록을 오른쪽으로 회전시키는 메서드
    public Shape rotateRight() {
        if (pieceShape == Tetrominoes.SquareShape)
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {
            result.setX(i, -y(i));
            result.setY(i, x(i));
        }
        return result;
    }
}
