package org.example.mvc.view;

import java.util.InputMismatchException;
import java.util.Scanner;

public class FirstView {
    private final Scanner sc;
    private final LoginView loginView; // 로그인 화면 처리를 위한 객체

    public FirstView(LoginView loginView) {
        this.sc = new Scanner(System.in);
        this.loginView = loginView;
    }

    // 초기 메뉴 메소드
    public void firstView() {
        System.out.println("안녕하세요. 기숙사 관리 시스템입니다.");
        System.out.println("수행할 일을 아래 번호로 입력해주세요.");
        System.out.printf("%-10s %-10s %-10s\n", "1. 로그인", "2. 회원가입", "3. 나가기");

        try {
            int choice = sc.nextInt();
            sc.nextLine(); // 입력 버퍼 비우기
            switch (choice) {
                case 1 -> loginView.printSignIn(); // 1. 로그인
                case 2 -> loginView.printSignUp(); // 2. 회원가입
                case 3 -> {
                    // 프로그램 종료 메시지 출력
                    System.out.println("또 만나요! :)");
                }
                default -> {
                    // 유효하지 않은 입력 시 재귀 호출
                    System.out.println("1에서 3 사이의 번호를 입력해주세요.");
                    firstView();
                }
            }
        } catch (InputMismatchException e) {
            // 정수가 아닌 값 입력 시 재귀호출
            System.out.println("정수를 입력해주세요.");
            sc.nextLine(); // 잘못된 입력을 버퍼에서 제거
            firstView();
        }
    }
}