package org.example.mvc.view;

import org.example.global.Protocol;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Scanner;

public class DocumentView extends BaseView {
    private final Scanner sc;
    private static final int MAX_PACKET_SIZE = 1024; // 최대 패킷 크기

    public DocumentView(DataInputStream in, DataOutputStream out) {
        super(in, out);
        this.sc = new Scanner(System.in);
    }

    public void displayMenu() {
        System.out.println("=== 결핵진단서 제출 ===");
        submitDocument();
    }

    private void submitDocument() {
        System.out.print("학번을 입력하세요: ");
        String studentId = sc.nextLine();

        // 먼저 학번 전송
        Protocol packet = new Protocol(Protocol.TYPE_DOCUMENT, Protocol.CODE_DOCUMENT_SUBMIT);
        packet.setCode(Protocol.CODE_DOCUMENT_SUBMIT);
        packet.setData(studentId.getBytes(StandardCharsets.UTF_8));

        try {
            // 학번 전송
            sendPacket(packet);

            // 응답 확인
            Protocol response = receiveResponse();
            if (response.getCode() != Protocol.CODE_SUCCESS) {
                System.out.println("오류: " + new String(response.getData(), StandardCharsets.UTF_8));
                return;
            }

            // 파일 전송 시작
            System.out.print("결핵진단서 파일 경로를 입력하세요: ");
            String filePath = sc.nextLine();

            if (!filePath.toLowerCase().endsWith(".jpg") && !filePath.toLowerCase().endsWith(".png")) {
                System.out.println("jpg 또는 png 파일만 제출 가능합니다.");
                return;
            }

            try {
                byte[] fileContent = Files.readAllBytes(Path.of(filePath));
                String encodedFile = Base64.getEncoder().encodeToString(fileContent);

                // 파일 데이터를 청크로 나누어 전송
                sendLargeFile(encodedFile);

                // 최종 응답 처리
                response = receiveResponse();
                String responseMessage = new String(response.getData(), StandardCharsets.UTF_8);
                if (response.getCode() == Protocol.CODE_SUCCESS) {
                    System.out.println("성공: " + responseMessage);
                } else {
                    System.out.println("오류: " + responseMessage);
                }

            } catch (IOException e) {
                System.err.println("파일 읽기 오류: " + e.getMessage());
            }

        } catch (IOException e) {
            System.err.println("통신 오류: " + e.getMessage());
        }
    }

    private void sendLargeFile(String encodedFile) throws IOException {
        byte[] dataBytes = encodedFile.getBytes(StandardCharsets.UTF_8);
        int offset = 0;

        while (offset < dataBytes.length) {
            int chunkSize = Math.min(MAX_PACKET_SIZE, dataBytes.length - offset);
            byte[] chunk = new byte[chunkSize];
            System.arraycopy(dataBytes, offset, chunk, 0, chunkSize);

            Protocol packet = new Protocol(Protocol.TYPE_DOCUMENT, Protocol.CODE_DOCUMENT_FILE);
            packet.setCode(Protocol.CODE_DOCUMENT_FILE);
            packet.setData(chunk);

            // 패킷 전송 전 잠시 대기
            try {
                Thread.sleep(10); // 10ms 대기
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            sendPacket(packet);
            offset += chunkSize;

            // 진행률 표시
            double progress = (double) offset / dataBytes.length * 100;
            System.out.printf("\r전송 진행률: %.1f%%", progress);
        }
        System.out.println();
    }

    private Protocol receiveResponse() throws IOException {
        byte type = in.readByte();
        byte code = in.readByte();
        return new Protocol(type, code);
    }

    private void sendPacket(Protocol packet) throws IOException {
        out.write(packet.getPacket());
        out.flush();
    }
}