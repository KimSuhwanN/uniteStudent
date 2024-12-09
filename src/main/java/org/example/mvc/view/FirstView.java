package org.example.mvc.view;

import java.util.InputMismatchException;
import java.util.Scanner;

public class FirstView {
    private final Scanner sc;
    private final LoginView loginView;

    public FirstView(LoginView loginView) {
        this.sc = new Scanner(System.in);
        this.loginView = loginView;
    }

    public void firstView() {
        System.out.println("안녕하세요. 기숙사 관리 시스템입니다.");
        System.out.println("수행할 일을 아래 번호로 입력해주세요.");
        System.out.printf("%-10s %-8s %-10s\n", "1. 로그인", "2. 회원가입", "3. 나가기");
        try {
            switch (sc.nextInt()) {
                case 1 -> loginView.printSignIn();
                case 2 -> loginView.printSignUp();
                case 3 -> {
                    System.out.println("안녕히 가세요.");
                    return;
                }
                default -> {
                    System.out.println("1에서 3 사이의 번호를 입력해주세요.");
                    firstView();
                }
            };
        } catch(InputMismatchException e) {
            System.out.println("정수를 입력해주세요.");
            sc.nextLine();
            firstView();
        }
    }
}