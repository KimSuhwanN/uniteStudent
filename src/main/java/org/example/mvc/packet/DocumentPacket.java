package org.example.mvc.packet;

import org.example.global.Protocol;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentPacket extends Protocol {
    public DocumentPacket(byte code, String data) {
        this.type = Protocol.TYPE_DOCUMENT;
        this.code = code;
        this.length = (short) data.length();
        setData(data.getBytes());
    }
}