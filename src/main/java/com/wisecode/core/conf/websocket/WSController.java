package com.wisecode.core.conf.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class WSController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/hello")
    public Message hello(Message requestMessage) {
        System.out.println("Receive message:" + requestMessage);
        Message meg = new Message();
        meg.setFrom("5");
        meg.setText(requestMessage.getText());
        simpMessagingTemplate.convertAndSendToUser("5","/updates",requestMessage);
        return meg;
    }


}
