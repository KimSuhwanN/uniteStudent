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
        System.out.print("결핵진단서 파일 경로를 입력하세요: ");
        String filePath = sc.nextLine();

            // 파일 확장자 검증
            if (!filePath.toLowerCase().endsWith(".jpg") && !filePath.toLowerCase().endsWith(".png")) {
                System.out.println("jpg 또는 png 파일만 제출 가능합니다.");
                return;
            }

        try {
            byte[] fileContent = Files.readAllBytes(Path.of(filePath));
            String encodedFile = Base64.getEncoder().encodeToString(fileContent);

            System.out.println(encodedFile);

            String documentData = studentId + "," + encodedFile;

            sendRequest(Protocol.TYPE_DOCUMENT, Protocol.CODE_DOCUMENT_SUBMIT, documentData);
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
        } catch (IOException e) {
            System.err.println("파일 읽기 오류: " + e.getMessage());
        }
    }
}