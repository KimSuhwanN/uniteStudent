package org.example.mvc.view;

import org.example.global.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Scanner;

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
            switch (sc.nextInt()) {
                case 1 -> checkPassStatus();
                case 2 -> checkRoomInfo();
                default -> System.out.println("1 또는 2를 입력해주세요.");
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("정수를 입력해주세요.");
            sc.nextLine();
        }
    }

    private void checkPassStatus() {
        System.out.print("학번을 입력하세요: ");
        String studentId = sc.next();
        sendRequest(Protocol.TYPE_ROOM, Protocol.CODE_ROOM_PASS_CHECK, studentId);
        receiveResponse();
    }

    private void checkRoomInfo() {
        System.out.print("학번을 입력하세요: ");
        String studentId = sc.next();
        sendRequest(Protocol.TYPE_ROOM, Protocol.CODE_ROOM_INFO, studentId);
        receiveResponse();
    }
}