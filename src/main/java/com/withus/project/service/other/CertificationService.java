package com.withus.project.service.other;


import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class CertificationService {

    // 본인의 API KEY, API SECRET, 발신번호를 입력 (테스트용)
    private final String api_key = "NCSFZBWMGLJ5LCLJ";
    private final String api_secret = "VQJ4XISDY0KBJCURSKJ4CYEYBTRL54PQ";
    private final String fromPhoneNumber = "01052334153"; // 테스트시에는 본인 번호로 가능

    public void certifiedPhoneNumber(String phoneNumber, String cerNum) {
        Message coolsms = new Message(api_key, api_secret);

        // 필수 파라미터 4개: to, from, type, text
        HashMap<String, String> params = new HashMap<>();
        params.put("to", phoneNumber);   // 수신전화번호
        params.put("from", fromPhoneNumber); // 발신전화번호
        params.put("type", "SMS");
        params.put("text", "핫띵크 휴대폰인증 테스트 메시지 : 인증번호는 [" + cerNum + "] 입니다.");
        params.put("app_version", "test app 1.2");

        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString());
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }
    }
}
