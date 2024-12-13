package org.example.mvc.view;

import org.example.global.Protocol;
import org.example.mvc.packet.LoginPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.stream.IntStream;

public class LoginView {
    private final DataInputStream in; // 서버로부터 데이터를 받는 입력 스트림
    private final DataOutputStream out; // 서버에 데이터를 보내는 출력 스트림
    private final Scanner sc;

    public LoginView(DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
        this.sc = new Scanner(System.in);
    }

    // 1. 로그인
    public void printSignIn() {
        try {
            System.out.println("=== 로그인 ===");
            String id = promptInput("아이디: ");
            String pwd = promptInput("비밀번호: ");

            // 아이디 검증
            if (!verifyId(id)) {
                System.out.println("ID 검증 실패. 프로그램을 종료합니다.");
                return;
            }
            
            // 아이디 및 비밀번호 검증
            if (!verifyPassword(id, pwd)) {
                System.out.println("로그인 실패. 프로그램을 종료합니다.");
                return;
            }

            System.out.println("로그인 성공");
            displayMainMenu(); // 메인 메뉴 출력
        } catch (Exception e) {
            System.out.println("로그인 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 2. 회원가입
    public void printSignUp() {
        try {
            System.out.println("=== 회원가입 ===");
            // 회원가입에 필요한 정보를 입력받음
            String username = promptInput("아이디: ");
            String password = promptInput("비밀번호: ");
            String role = promptInput("역할(예: STUDENT/ADMIN): ");
            String name = promptInput("이름: ");
            String studentNumber = promptInput("학번: ");
            String major = promptInput("전공: ");
            String gpa = promptInput("GPA: ");
            String distance = promptInput("거주 거리(km): ");

            // 입력받은 정보를 하나의 문자열로 결합
            String signupData = String.join
                    (",", username, password, role, name, studentNumber, major, gpa, distance);
            sendRequest(Protocol.TYPE_REGISTER, Protocol.CODE_REGISTER_REQUEST, signupData);

            // 서버 응답 처리
            Protocol response = receiveResponse();
            if (response.getCode() == Protocol.CODE_SUCCESS) {
                System.out.println("회원가입이 성공적으로 완료되었습니다.");
                displayMainMenu();
            } else {
                System.out.println("회원가입 실패: " + response.getDataAsString());
            }
        } catch (Exception e) {
            System.out.println("회원가입 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 서버로 요청 패킷 전송
    public void sendRequest(byte type, byte code, String data) {
        try {
            Protocol packet = new Protocol();
            packet.setType(type);
            packet.setCode(code);
            packet.setData(data);
            byte[] packetData = packet.getPacket(); // 패킷 데이터를 바이트 배열로 변환

            // 데이터를 출력 스트림으로 전송
            for (byte packetDatum : packetData) {
                out.writeByte(packetDatum);
            }
            out.flush(); // 출력 버퍼 비우기
        } catch (IOException e) {
            System.err.println("요청 전송 오류: " + e.getMessage());
        }
    }

    // 사용자로부터 입력을 받는 메소드
    private String promptInput(String message) {
        System.out.print(message);
        return sc.next();
    }

    // 아이디 검증
    private boolean verifyId(String id) throws Exception {
        LoginPacket idPacket = new LoginPacket(id, "id"); // ID 요청 패킷 생성
        sendPacket(idPacket); // 서버로 패킷 전송
        Protocol response = receiveResponse(); // 서버 응답 수신
        // 서버 응답 코드 확인
        return response.getCode() == Protocol.CODE_FAIL;
    }

    // 아이디 및 비밀번호 검증
    private boolean verifyPassword(String id, String pwd) throws Exception {
        LoginPacket pwdPacket = new LoginPacket(id + "," + pwd, "pwd"); // 비밀번호 요청 패킷 생성
        sendPacket(pwdPacket); // 서버로 패킷 전송
        Protocol response = receiveResponse(); // 서버 응답 수신
        // 서버 응답 코드 확인
        return response.getType() == Protocol.TYPE_RESPONSE && response.getCode() == Protocol.CODE_SUCCESS;
    }

    // 패킷 전송
    private void sendPacket(LoginPacket packet) throws Exception {
        System.out.println("전송할 패킷: " + packet);
        byte[] packetData = packet.getPacket();
        // 데이터를 출력 스트림으로 전송
        for (byte b : packetData) {
            out.writeByte(b);
        }
        out.flush(); // 출력 버퍼 비우기
    }

    // 서버 응답 수신
    private Protocol receiveResponse() throws Exception {
        byte type = in.readByte(); // 타입 읽기
        byte code = in.readByte(); // 코드 읽기
        short length = in.readShort(); // 데이터 길이 읽기

        byte[] data = null;
        if (length > 0) {
            data = new byte[length];
            in.readFully(data); // 데이터 읽기
        }

        Protocol response = new Protocol(type, code); // 응답 패킷 생성
        if (data != null) {
            response.setData(data);
        }
        return response;
    }

    // 메인 메뉴 출력
    private void displayMainMenu() {
        // 각 View 객체 생성
        ScheduleView schd = new ScheduleView(in, out);
        ApplicationView app = new ApplicationView(in, out);
        RoomView room = new RoomView(in, out);
        PaymentView pay = new PaymentView(in, out);
        DocumentView dcmn = new DocumentView(in, out);
        WithdrawalView wthd = new WithdrawalView(in, out);
        RefundView refund = new RefundView(in, out);

        String[] menuItems = {
                "선발 일정 및 비용 확인",
                "입사 신청",
                "합격 여부 및 호실 확인",
                "생활관 비용 확인 및 납부",
                "결핵진단서 제출",
                "퇴사 신청",
                "환불 확인",
                "로그아웃"
        };

        while (true) {
            System.out.println("\n=== 메인 메뉴 ===");
            IntStream.range(0, menuItems.length)
                    .mapToObj(i -> (i + 1) + ". " + menuItems[i])
                    .forEach(System.out::println);

            System.out.print("원하는 작업 번호를 입력하세요: ");
            try {
                int choice = sc.nextInt();
                sc.nextLine();
                switch (choice) {
                    case 1 -> schd.displayMenu(); // 선발 일정 및 비용 확인
                    case 2 -> app.displayMenu(); // 입사 신청
                    case 3 -> room.displayMenu(); // 합격 여부 및 호실 확인
                    case 4 -> pay.displayMenu(); // 생활관 비용 확인 및 납부
                    case 5 -> dcmn.displayMenu(); // 결핵진단서 제출(미구현)
                    case 6 -> wthd.displayMenu(); // 퇴사 신청
                    case 7 -> refund.displayMenu(); // 환불 확인
                    case 8 -> {
                        System.out.println("로그아웃 되었습니다.");
                        return;
                    }
                    default -> System.out.println("1에서 8 사이의 번호를 입력해주세요.");
                }
            } catch (InputMismatchException e) {
                System.out.println("정수를 입력해주세요.");
                sc.nextLine(); // 버퍼 비우기
            }
        }
    }
}