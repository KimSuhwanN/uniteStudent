package org.example.mvc.view;

import org.example.global.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
        System.out.print("결핵진단서 파일 경로를 입력하세요: ");
        String filePath = sc.nextLine();

        if (!filePath.toLowerCase().endsWith(".jpg") && !filePath.toLowerCase().endsWith(".png")) {
            System.out.println("jpg 또는 png 파일만 제출 가능합니다.");
            return;
        }

        try {
            byte[] fileContent = Files.readAllBytes(Path.of(filePath));
            String encodedFile = Base64.getEncoder().encodeToString(fileContent);

            sendRequest(Protocol.TYPE_DOCUMENT, Protocol.CODE_DOCUMENT_SUBMIT, encodedFile);
            receiveResponse();
        } catch (IOException e) {
            System.err.println("파일 읽기 오류: " + e.getMessage());
        }
    }
}