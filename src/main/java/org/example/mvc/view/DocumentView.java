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

/**
 * 입사 신청과 관련된 작업을 처리하는 뷰 클래스.
 * BaseView를 상속받아 대용량 파일 전송 및 프로토콜 기반 통신 메소드를 구현하려 시도함.
 * 미구현 상태.
 */

public class DocumentView extends BaseView {
    private final Scanner sc;
    private static final int MAX_PACKET_SIZE = 1024; // 대용량 파일 전송을 위한 최대 패킷 크기

    public DocumentView(DataInputStream in, DataOutputStream out) {
        super(in, out);
        this.sc = new Scanner(System.in);
    }

    public void displayMenu() {
        System.out.println("=== 결핵진단서 제출 ===");
        submitDocument();
    }

    // 결핵진단서 제출 프로세스를 처리하는 메소드
    private void submitDocument() {
        // 학번 입력 받기
        System.out.print("학번을 입력하세요: ");
        String studentId = sc.nextLine();

        // 프로토콜 패킷 생성 및 학번 전송
        Protocol packet = new Protocol(Protocol.TYPE_DOCUMENT, Protocol.CODE_DOCUMENT_SUBMIT);
        packet.setCode(Protocol.CODE_DOCUMENT_SUBMIT);
        packet.setData(studentId.getBytes(StandardCharsets.UTF_8));

        try {
            // 학번 패킷 전송
            sendPacket(packet);

            // 서버 응답 확인
            Protocol response = receiveResponse();
            if (response.getCode() != Protocol.CODE_SUCCESS) {
                System.out.println("오류: " + new String(response.getData(), StandardCharsets.UTF_8));
                return;
            }

            // 파일 경로 입력
            System.out.print("결핵진단서 파일 경로를 입력하세요: ");
            String filePath = sc.nextLine();

            // 파일 형식 검증
            if (!filePath.toLowerCase().endsWith(".jpg") && !filePath.toLowerCase().endsWith(".png")) {
                System.out.println("jpg 또는 png 파일만 제출 가능합니다.");
                return;
            }

            try {
                // 파일 내용을 Base64로 인코딩
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

    // 대용량 파일을 최대 패킷 크기로 분할하여 전송하는 메소드
    private void sendLargeFile(String encodedFile) throws IOException {
        // 파일 데이터를 바이트로 변환
        byte[] dataBytes = encodedFile.getBytes(StandardCharsets.UTF_8);
        int offset = 0;

        // 파일 데이터를 청크 단위로 분할하여 전송
        while (offset < dataBytes.length) {
            // 최대 패킷 크기만큼 청크 생성
            int chunkSize = Math.min(MAX_PACKET_SIZE, dataBytes.length - offset);
            byte[] chunk = new byte[chunkSize];
            System.arraycopy(dataBytes, offset, chunk, 0, chunkSize);

            // 패킷 생성 및 청크 데이터 설정
            Protocol packet = new Protocol(Protocol.TYPE_DOCUMENT, Protocol.CODE_DOCUMENT_FILE);
            packet.setCode(Protocol.CODE_DOCUMENT_FILE);
            packet.setData(chunk);

            // 네트워크 부하 방지를 위한 짧은 대기
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 패킷 전송
            sendPacket(packet);
            offset += chunkSize;

            // 진행률 표시
            double progress = (double) offset / dataBytes.length * 100;
            System.out.printf("\r전송 진행률: %.1f%%", progress);
        }
        System.out.println();
    }

    // 서버로부터 응답 프로토콜을 수신하는 메소드
    private Protocol receiveResponse() throws IOException {
        byte type = in.readByte();
        byte code = in.readByte();
        return new Protocol(type, code);
    }

    // 프로토콜 패킷을 서버로 전송하는 메소드
    private void sendPacket(Protocol packet) throws IOException {
        out.write(packet.getPacket());
        out.flush(); // 버퍼 비우기
    }
}