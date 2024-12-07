package org.example.mvc.view;

import org.example.global.Protocol;
import org.example.mvc.packet.DocumentPacket;
import java.io.*;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class DocumentView {
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Scanner sc;

    public DocumentView(DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
        this.sc = new Scanner(System.in);
    }

    public void displayMenu() {
        System.out.println("=== 결핵진단서 제출 ===");
        submitDocument();
    }

    private void submitDocument() {
        System.out.print("결핵진단서 파일 경로를 입력하세요: ");
        String filePath = sc.nextLine();

        if (!filePath.toLowerCase().endsWith(".jpg") && !filePath.toLowerCase().endsWith(".png")) {
            System.out.println("jpg 또는 png 파일만 제출 가능합니다.");
            return;
        }

        try {
            byte[] fileContent = Files.readAllBytes(Path.of(filePath));
            String encodedFile = Base64.getEncoder().encodeToString(fileContent);

            sendRequest(Protocol.CODE_DOCUMENT_SUBMIT, encodedFile);
            receiveResponse();
        } catch (IOException e) {
            System.err.println("파일 읽기 오류: " + e.getMessage());
        }
    }

    private void sendRequest(byte code, String data) {
        try {
            DocumentPacket packet = new DocumentPacket(code, data);
            byte[] packetData = packet.getPacket();
            for (byte packetDatum : packetData) {
                out.writeByte(packetDatum);
            }
            out.flush();
        } catch (Exception e) {
            System.err.println("요청 전송 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void receiveResponse() {
        try {
            byte type = in.readByte();
            byte code = in.readByte();
            short length = in.readShort();
            System.out.printf("응답 타입: %02X, 코드: %02X, 길이: %d%n", type, code, length);

            byte[] data = null;
            if (length > 0) {
                data = new byte[length];
                in.readFully(data);
            }

            Protocol response = new Protocol(type, code);
            if (data != null) response.setData(data);
            response.getDataAsString();
        } catch (Exception e) {
            System.err.println("응답 처리 오류: " + e.getMessage());
        }
    }
}