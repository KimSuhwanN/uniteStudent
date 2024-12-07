package org.example.mvc.view;

import org.example.global.Protocol;
import org.example.mvc.packet.LoginPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class LoginView {
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Scanner sc;

    public LoginView(Socket socket, DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
        this.sc = new Scanner(System.in);
    }

    public boolean printSignIn() {
        try {
            System.out.println("=== 로그인 ===");
            System.out.print("아이디: ");
            String id = sc.next();
            System.out.print("비밀번호: ");
            String pwd = sc.next();

            // ID 검증
            LoginPacket idPacket = new LoginPacket(id, "id");
            System.out.println("전송할 ID 패킷: " + idPacket);

            // 패킷 전송
            byte[] packetData = idPacket.getPacket();
            for (byte packetDatum : packetData) {
                out.writeByte(packetDatum);
            }
            out.flush();

            // 서버 응답 읽기
            byte type = in.readByte();
            byte code = in.readByte();
            short length = in.readShort();

            byte[] data = null;
            if (length > 0) {
                data = new byte[length];
                in.readFully(data);
            }

            Protocol response = new Protocol(type, code);
            if (data != null) {
                response.setData(data);
            }

            // ID 검증 실패 시 종료
            if (response.getCode() != Protocol.CODE_FAIL) {
                System.out.println("ID 검증 실패");
                return false;
            }

            // 비밀번호 검증
            LoginPacket pwdPacket = new LoginPacket(id + "," + pwd, "pwd");
            System.out.println("전송할 Password 패킷: " + pwdPacket);

            packetData = pwdPacket.getPacket();
            for (byte packetDatum : packetData) {
                out.writeByte(packetDatum);
            }
            out.flush();

            // 서버 응답 읽기
            type = in.readByte();
            code = in.readByte();
            length = in.readShort();

            data = null;
            if (length > 0) {
                data = new byte[length];
                in.readFully(data);
            }

            response = new Protocol(type, code);
            if (data != null) {
                response.setData(data);
            }

            // 로그인 성공 시 메뉴 실행
            if (response.getType() == Protocol.TYPE_RESPONSE && response.getCode() == Protocol.CODE_SUCCESS) {
                System.out.println("로그인 성공!");
                ScheduleView scheduleView = new ScheduleView(in, out);
                ApplicationView applicationView = new ApplicationView(in, out);
                RoomView roomView = new RoomView(in, out);
                PaymentView paymentView = new PaymentView(in, out);
                DocumentView documentView = new DocumentView(in, out);
                WithdrawalView withdrawalView = new WithdrawalView(in, out);
                RefundView refundView = new RefundView(in, out);

                while (true) {
                    System.out.println("\n=== 메인 메뉴 ===");
                    System.out.println("1. 선발 일정 및 비용 확인");
                    System.out.println("2. 입사 신청");
                    System.out.println("3. 합격 여부 및 호실 확인");
                    System.out.println("4. 생활관 비용 확인 및 납부");
                    System.out.println("5. 결핵진단서 제출");
                    System.out.println("6. 퇴사 신청");
                    System.out.println("7. 환불 확인");
                    System.out.println("8. 로그아웃");
                    System.out.print("원하는 작업 번호를 입력하세요: ");
                    try {
                        switch (sc.nextInt()) {
                            case 1 -> scheduleView.displayMenu();
                            case 2 -> applicationView.displayMenu();
                            case 3 -> roomView.displayMenu();
                            case 4 -> paymentView.displayMenu();
                            case 5 -> documentView.displayMenu();
                            case 6 -> withdrawalView.displayMenu();
                            case 7 -> refundView.displayMenu();
                            case 8 -> {
                                System.out.println("로그아웃 되었습니다.");
                                return true;
                            }
                            default -> System.out.println("1에서 8 사이의 번호를 입력해주세요.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("정수를 입력해주세요.");
                        sc.nextLine();
                        return false;
                    }
                }
            } else {
                System.out.println("로그인 실패: " + response.getDataAsString());
                return false;
            }
        } catch (Exception e) {
            System.out.println("로그인 오류: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}