package org.example.mvc.packet;

import org.example.global.Protocol;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomPacket extends Protocol {
    public RoomPacket(byte code, String data) {
        this.type = Protocol.TYPE_ROOM;
        this.code = code;
        this.length = (short) data.length();
        setData(data.getBytes());
    }
}