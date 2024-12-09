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
            String id = promptInput("아이디: ");
            String pwd = promptInput("비밀번호: ");

            if (!verifyId(id)) {
                System.out.println("ID 검증 실패");
                return false;
            }
            if (!verifyPassword(id, pwd)) {
                System.out.println("로그인 실패");
                return false;
            }

            System.out.println("로그인 성공!");
            displayMainMenu();
            return true;
        } catch (Exception e) {
            System.out.println("로그인 오류: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String promptInput(String message) {
        System.out.print(message);
        return sc.next();
    }

    private boolean verifyId(String id) throws Exception {
        LoginPacket idPacket = new LoginPacket(id, "id");
        sendPacket(idPacket);
        Protocol response = receiveResponse();

        return response.getCode() == Protocol.CODE_FAIL;
    }

    private boolean verifyPassword(String id, String pwd) throws Exception {
        LoginPacket pwdPacket = new LoginPacket(id + "," + pwd, "pwd");
        sendPacket(pwdPacket);
        Protocol response = receiveResponse();

        return response.getType() == Protocol.TYPE_RESPONSE && response.getCode() == Protocol.CODE_SUCCESS;
    }

    private void sendPacket(LoginPacket packet) throws Exception {
        System.out.println("전송할 패킷: " + packet);
        byte[] packetData = packet.getPacket();
        for (byte b : packetData) {
            out.writeByte(b);
        }
        out.flush();
    }

    private Protocol receiveResponse() throws Exception {
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
        return response;
    }

    private void displayMainMenu() {
        ScheduleView scheduleView = new ScheduleView(in, out);
        ApplicationView applicationView = new ApplicationView(in, out);
        RoomView roomView = new RoomView(in, out);
        PaymentView paymentView = new PaymentView(in, out);
        DocumentView documentView = new DocumentView(in, out);
        WithdrawalView withdrawalView = new WithdrawalView(in, out);
        RefundView refundView = new RefundView(in, out);

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
            for (int i = 0; i < menuItems.length; i++) {
                System.out.println((i + 1) + ". " + menuItems[i]);
            }

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
                        return;
                    }
                    default -> System.out.println("1에서 8 사이의 번호를 입력해주세요.");
                }
            } catch (InputMismatchException e) {
                System.out.println("정수를 입력해주세요.");
                sc.nextLine();
            }
        }
    }
}