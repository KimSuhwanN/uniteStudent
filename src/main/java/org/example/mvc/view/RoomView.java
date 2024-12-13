package org.example.mvc.view;

import org.example.global.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * 생활관 합격 여부와 호실 정보를 조회하는 뷰 클래스.
 * BaseView를 상속받아 프로토콜 기반의 조회 기능을 구현함.
 */

public class RoomView extends BaseView {
    private final Scanner sc;

    public RoomView(DataInputStream in, DataOutputStream out) {
        super(in, out);
        this.sc = new Scanner(System.in);
    }

    public void displayMenu() {
        System.out.println("=== 합격 여부 및 호실 확인 ===");
        System.out.println("1. 합격 여부 조회");
        System.out.println("2. 생활관 호실 조회");
        System.out.print("원하는 작업 번호를 입력하세요: ");
        try {
            int choice = sc.nextInt();
            sc.nextLine(); // 버퍼 비우기
            switch (choice) {
                case 1 -> checkPassStatus(); // 1. 합격 여부 조회
                case 2 -> checkRoomInfo(); // 2. 생활관 호실 조회
                default -> System.out.println("1 또는 2를 입력해주세요.");
            }
        } catch (InputMismatchException e) {
            System.out.println("정수를 입력해주세요.");
            sc.nextLine(); // 오류 발생 시 버퍼 비우기
        }
    }

    // 1. 합격 여부 조회
    private void checkPassStatus() {
        System.out.print("학번을 입력하세요: ");
        String studentId = sc.next();
        sendRequest(Protocol.TYPE_ROOM, Protocol.CODE_ROOM_PASS_CHECK, studentId);

        getResponse(); // 서버 응답 처리(BaseView 클래스의 protected 메소드)
    }

    private void checkRoomInfo() {
        System.out.print("학번을 입력하세요: ");
        String studentId = sc.next();
        sendRequest(Protocol.TYPE_ROOM, Protocol.CODE_ROOM_INFO, studentId);

        getResponse(); // 서버 응답 처리(BaseView 클래스의 protected 메소드)
    }
}