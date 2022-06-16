package dare.daremall.service;

import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Service
public class CertificationService {

    // 사용자 인증시 사용
    public String PhoneNumberCheck(String to) throws CoolsmsException {

        String api_key = "NCSAZ5LKT10MKKDR";
        String api_secret = "KBG0V7RWINPSRIQNSAT4X2YUU6ANFEZ8";
        Message coolsms = new Message(api_key, api_secret);


        Random rand  = new Random();
        String numStr = "";
        for(int i=0; i<4; i++) {
            String ran = Integer.toString(rand.nextInt(10));
            numStr+=ran;
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", to);    // 수신전화번호 (ajax로 view 화면에서 받아온 값으로 넘김)
        params.put("from", "01030697380");    // 발신전화번호. 테스트시에는 발신,수신 둘다 본인 번호로 하면 됨
        params.put("type", "sms");
        params.put("text", "인증번호는 [" + numStr + "] 입니다.");

        coolsms.send(params); // 메시지 전송

        return numStr;

    }

    // 상품 수량 update시 문자 전송
    public void itemNotSaleOrderCancel(List<String> phones, String name) throws CoolsmsException {

        String api_key = "NCSAZ5LKT10MKKDR";
        String api_secret = "KBG0V7RWINPSRIQNSAT4X2YUU6ANFEZ8";
        Message coolsms = new Message(api_key, api_secret);

        for(String to : phones) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("to", to);    // 수신전화번호 (ajax로 view 화면에서 받아온 값으로 넘김)
            params.put("from", "01030697380");    // 발신전화번호. 테스트시에는 발신,수신 둘다 본인 번호로 하면 됨
            params.put("type", "sms");
            params.put("text", "DARE MALL ["+name+"] 상품이 판매 중단되어 해당 상품 주문이 취소되었습니다.");

            coolsms.send(params); // 메시지 전송

            System.out.println("send "+params);
        }
    }
}
