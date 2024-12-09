package org.example.mvc.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ResponseProcessor {
    private static final DecimalFormat formatter = new DecimalFormat("#,###");

    public void processResponse(String responseType, String responseCode, String responseData) {
        switch (responseCode) {
            case "01" -> {
                if (responseData.contains("ROOM_") || responseData.contains("MEAL_")) {
                    processCostData(responseData);
                }
            }
            case "02" -> System.out.println("요청 실패: " + responseData);
            case "03" -> System.out.println("권한 없음: " + responseData);
            case "04" -> System.out.println("잘못된 요청: " + responseData);
            default -> System.out.println("알 수 없는 응답 코드: " + responseCode);
        }
    }

    private void processCostData(String responseData) {
        String[] lines = responseData.split("\\n");
        List<String[]> parsedData = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length == 3) {
                parsedData.add(parts);
            }
        }

        System.out.println("------------------------------------");
        System.out.printf("%-10s %-8s %-10s\n", "생활관", "유형", "비용");
        System.out.println("------------------------------------");

        for (String[] data : parsedData) {
            String facility = data[0];
            String type = translateType(data[1]);
            String cost = formatter.format(Integer.parseInt(data[2])) + "원";
            System.out.printf("%-10s %-8s %-10s\n", facility, type, cost);
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