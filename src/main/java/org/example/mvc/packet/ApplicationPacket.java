package org.example.mvc.packet;

import org.example.global.Protocol;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationPacket extends Protocol {
    public ApplicationPacket(byte code, String data) {
        this.type = Protocol.TYPE_APPLICATION;
        this.code = code;
        this.length = (short) data.length();
        setData(data.getBytes());
    }
}