package org.example.mvc.packet;

import org.example.global.Protocol;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginPacket extends Protocol {
    public LoginPacket(String data, String code) {
        this.type = Protocol.TYPE_AUTH;
        this.code = Objects.equals(code, "id")
                ? Protocol.CODE_AUTH_ID_REQ : Protocol.CODE_AUTH_PW_REQ;
        this.length = (short) data.length();
        setData(data.getBytes());
    }
}