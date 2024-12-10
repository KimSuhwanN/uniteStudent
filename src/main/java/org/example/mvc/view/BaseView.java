package org.example.mvc.view;

import org.example.global.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

public abstract class BaseView {
    protected final DataInputStream in;
    protected final DataOutputStream out;

    public BaseView(DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
    }

    // 요청 전송 메서드
    protected void sendRequest(byte type, byte code, String data) {
        try {
            Protocol packet = new Protocol();
            packet.setType(type);
            packet.setCode(code);
            packet.setData(data);
            byte[] packetData = packet.getPacket();

            for (byte packetDatum : packetData) {
                out.writeByte(packetDatum);
            }
            out.flush();
        } catch (IOException e) {
            System.err.println("요청 전송 오류: " + e.getMessage());
        }
    }

    // 응답 처리 메서드
    protected void receiveResponse() {
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
            default -> type;
        };
    }
}