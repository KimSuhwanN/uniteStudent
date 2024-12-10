package org.example.mvc.view;

import org.example.global.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
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
        sendRequest(Protocol.TYPE_SCHEDULE, Protocol.CODE_SCHEDULE_VIEW, "");
        try {
            byte type = in.readByte();
            byte code = in.readByte();
            short length = in.readShort();
            System.out.printf("응답 타입: %02X, 코드: %02X, 길이: %d%n", type, code, length);

            String responseData = "";
            if (length > 0) {
                byte[] data = new byte[length];
                in.readFully(data);
                responseData = new String(data, StandardCharsets.UTF_8);
            }
            System.out.println(responseData);

        } catch (Exception e) {
            System.err.println("응답 처리 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void viewFee() {
        sendRequest(Protocol.TYPE_SCHEDULE, Protocol.CODE_SCHEDULE_FEE_VIEW, "");
        try {
            byte type = in.readByte();
            byte code = in.readByte();
            short length = in.readShort();
            System.out.printf("응답 타입: %02X, 코드: %02X, 길이: %d%n", type, code, length);

            String responseData = "";
            if (length > 0) {
                byte[] data = new byte[length];
                in.readFully(data);
                responseData = new String(data, StandardCharsets.UTF_8);
            }

            // 응답 코드에 따른 처리
            switch (String.format("%02X", code)) {
                case "01" -> {
                    if (responseData.contains("ROOM_") || responseData.contains("MEAL_")) {
                        processCostData(responseData);
                    }
                }
                case "02" -> System.out.println("요청 실패: " + responseData);
                case "03" -> System.out.println("권한 없음: " + responseData);
                case "04" -> System.out.println("잘못된 요청: " + responseData);
                default -> System.out.println("알 수 없는 응답 코드: " + String.format("%02X", code));
            }
        } catch (Exception e) {
            System.err.println("응답 처리 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processCostData(String responseData) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        String[] lines = responseData.split("\\n");

        System.out.println("------------------------------------");
        System.out.printf("%-10s %-8s %-10s\n", "생활관", "유형", "비용");
        System.out.println("------------------------------------");

        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length == 3) {
                String facility = parts[0];
                String type = translateType(parts[1]);
                String cost = formatter.format(Integer.parseInt(parts[2])) + "원";
                System.out.printf("%-10s %-8s %-10s\n", facility, type, cost);
            }
        }
    }

    private String translateType(String type) {
        return switch (type) {
            case "ROOM_2" -> "2인실";
            case "ROOM_4" -> "4인실";
            case "MEAL_5" -> "5일식";
            case "MEAL_7" -> "7일식";
            case "MEAL_0" -> "식사 안 함";
            default -> type;
        };
    }
}