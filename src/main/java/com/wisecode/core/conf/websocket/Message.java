package com.wisecode.core.conf.websocket;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Message implements Serializable {

    private String from;
    private String text;
}
