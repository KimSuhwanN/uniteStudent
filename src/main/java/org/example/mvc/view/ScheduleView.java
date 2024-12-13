package org.example.mvc.view;

import org.example.global.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * 생활관 선발 일정과 관련 비용을 조회하는 뷰 클래스.
 * BaseView를 상속받아 프로토콜 기반의 일정 및 비용 조회 기능을 구현함.
 */

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
            int choice = sc.nextInt();
            sc.nextLine(); // 버퍼 비우기
            switch (choice) {
                case 1 -> viewSchedule(); // 1. 선발 일정 조회
                case 2 -> viewFee(); // 2. 비용 조회
                default -> System.out.println("잘못된 입력입니다.");
            }
        } catch (InputMismatchException e) {
            System.out.println("정수를 입력해주세요.");
            sc.nextLine(); // 오류 발생 시 버퍼 비우기
        }
    }

    // 1. 선발 일정 조회
    private void viewSchedule() {
        // 서버에 요청 데이터 전송
        sendRequest(Protocol.TYPE_SCHEDULE, Protocol.CODE_SCHEDULE_VIEW, "");
        // 데이터 수신 처리
        try {
            // 응답 패킷의 기본 필드 읽기
            byte type = in.readByte(); // 응답 타입
            byte code = in.readByte(); // 응답 코드
            short length = in.readShort(); // 데이터 길이

            // 디버그용 출력 (응답 필드 확인)
            System.out.printf("응답 타입: %02X, 코드: %02X, 길이: %d%n", type, code, length);

            // 응답 데이터를 저장할 문자열 초기화
            String responseData = "";

            // 데이터 길이가 0보다 크면 데이터 읽기
            if (length > 0) {
                byte[] data = new byte[length]; // 데이터 저장용 바이트 배열
                in.readFully(data); // 데이터를 배열에 저장
                responseData = new String(data, StandardCharsets.UTF_8); // UTF-8로 변환
            }

            // 응답 코드에 따른 처리
            switch (String.format("%02X", code)) {
                case "01" -> processScheduleData(responseData);
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

    // 수신된 일정 데이터를 테이블 형식으로 출력
    private void processScheduleData(String responseData) {
        String[] lines = responseData.split("\\n");

        String lin = "--------------------------------------------------------------------";
        // 테이블 형식 출력을 위한 포맷 문자열
        String headerFormat = "%-20s %-20s %-20s\n";
        String rowFormat = "%-20s %-20s %-20s\n";

        // 상단 출력
        System.out.println(lin);
        System.out.printf(headerFormat, "일정", "시작일", "종료일");
        System.out.println(lin);

        // 데이터 출력
        Arrays.stream(lines)
                .map(line -> line.split(","))
                .filter(parts -> parts.length == 3)
                .forEach(parts -> System.out.printf(rowFormat, parts[0], parts[1], parts[2]));
        System.out.println(lin);
    }

    // 2. 비용 조회
    private void viewFee() {
        // 서버에 요청 데이터 전송
        sendRequest(Protocol.TYPE_SCHEDULE, Protocol.CODE_SCHEDULE_FEE_VIEW, "");
        // 데이터 수신 처리
        try {
            // 응답 패킷의 기본 필드 읽기
            byte type = in.readByte(); // 응답 타입
            byte code = in.readByte(); // 응답 코드
            short length = in.readShort(); // 데이터 길이

            // 디버그용 출력 (응답 필드 확인)
            System.out.printf("응답 타입: %02X, 코드: %02X, 길이: %d%n", type, code, length);

            // 응답 데이터를 저장할 문자열 초기화
            String responseData = "";

            // 데이터 길이가 0보다 크면 데이터 읽기
            if (length > 0) {
                byte[] data = new byte[length]; // 데이터 저장용 바이트 배열
                in.readFully(data); // 데이터를 배열에 저장
                responseData = new String(data, StandardCharsets.UTF_8); // UTF-8로 변환
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

    // 수신된 비용 데이터를 테이블 형식으로 출력
    private void processCostData(String responseData) {
        // 천 단위 구분을 위한 포맷터 설정
        DecimalFormat formatter = new DecimalFormat("#,###");
        String[] lines = responseData.split("\\n");

        // 테이블 형식 출력을 위한 포맷 문자열
        String headerFormat = "%-10s %-10s %-10s\n";
        String rowFormat = "%-10s %-10s %-10s\n";

        // 상단 출력
        System.out.println("-----------------------------------");
        System.out.printf(headerFormat, "생활관", "유형", "비용");
        System.out.println("-----------------------------------");

        // 데이터 출력
        Arrays.stream(lines)
                .map(line -> line.split(","))
                .filter(parts -> parts.length == 3)
                .forEach(parts -> System.out.printf(
                        rowFormat,
                        parts[0],
                        translateType(parts[1]),
                        formatter.format(Integer.parseInt(parts[2])) + "원"
                ));
        System.out.println("-----------------------------------");
    }

    // 룸타입과 식사 유형 코드를 한글로 변환
    private String translateType(String type) {
        return switch (type) {
            case "ROOM_2" -> "2인실";
            case "ROOM_4" -> "4인실";
            case "MEAL_5" -> "5일식";
            case "MEAL_7" -> "7일식";
            case "MEAL_0" -> "식사X";
            default -> type;
        };
    }
}