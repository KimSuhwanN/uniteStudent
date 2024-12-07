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

    public boolean firstView() {
        System.out.println("안녕하세요. 기숙사 관리 시스템입니다.");
        System.out.println("수행할 일을 아래 번호로 입력해주세요.");
        System.out.println("1. 로그인\t2. 회원가입");
        try {
            switch (sc.nextInt()) {
                case 1: return signInView();
                // case 2: signUpView(); break;
                default:
                    System.out.println("1 또는 2를 입력해주세요.");
                    return false;
            }
        } catch(InputMismatchException e) {
            System.out.println("정수를 입력해주세요.");
            sc.nextLine();
            return firstView();
        }
    }
    public boolean signInView() {
        return loginView.printSignIn();
    }
//    public void signUpView() {
//        loginView.printSignUp();
//    }
}