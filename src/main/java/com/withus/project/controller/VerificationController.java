package com.withus.project.controller;





import com.withus.project.service.other.CertificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class VerificationController {

    @Autowired
    private CertificationService certificationService;

    // GET 방식으로 전화번호를 받아 4자리 인증번호를 생성한 후 SMS 전송
    @GetMapping("/check/sendSMS")
    public @ResponseBody String sendSMS(String phoneNumber) {
        Random rand = new Random();
        String numStr = "";
        // 4자리 난수 생성
        for (int i = 0; i < 4; i++) {
            String ran = Integer.toString(rand.nextInt(10));
            numStr += ran;
        }

        System.out.println("수신자 번호 : " + phoneNumber);
        System.out.println("인증번호 : " + numStr);
        certificationService.certifiedPhoneNumber(phoneNumber, numStr);
        return numStr;
    }
}
