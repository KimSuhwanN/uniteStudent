package org.example.mvc.packet;

import org.example.global.Protocol;

public class WithdrawalPacket extends Protocol {
    public WithdrawalPacket(byte code, String data) {
        this.type = Protocol.TYPE_WITHDRAWAL;
        this.code = code;
        this.length = (short) data.length();
        setData(data.getBytes());
    }
}