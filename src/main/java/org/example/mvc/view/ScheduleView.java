package org.example.mvc.view;

import org.example.global.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ScheduleView extends BaseView {
    private final Scanner sc;

    public ScheduleView(DataInputStream in, DataOutputStream out) {
        super(in, out);
        this.sc = new Scanner(System.in);
    }

    public void displayMenu() {
        System.out.println("=== 선발 일정 및 비용 확인 ===");
        System.out.println("1. 선발 일정 조회");
        System.out.println("2. 비용 조회");
        System.out.print("원하는 작업 번호를 입력하세요: ");
        try {
            switch (sc.nextInt()) {
                case 1 -> viewSchedule();
                case 2 -> viewFee();
                default -> System.out.println("잘못된 입력입니다.");
            }
        } catch (InputMismatchException e) {
            System.out.println("정수를 입력해주세요.");
            sc.nextLine();
        }
    }

    private void viewSchedule() {
        sendRequest(Protocol.CODE_SCHEDULE_VIEW, "");
        receiveResponse();  // BaseView의 receiveResponse 사용
    }

    private void viewFee() {
        sendRequest(Protocol.CODE_SCHEDULE_FEE_VIEW, "");
        receiveResponse();
    }
}