package org.example.mvc.packet;

import org.example.global.Protocol;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulePacket extends Protocol {

    public SchedulePacket(byte code, String data) {
        this.type = Protocol.TYPE_SCHEDULE;
        this.code = code;
        this.length = (short) data.length();
        setData(data.getBytes());
    }
}