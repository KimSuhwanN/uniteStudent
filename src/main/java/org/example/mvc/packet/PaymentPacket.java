package org.example.mvc.packet;

import org.example.global.Protocol;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentPacket extends Protocol {
    public PaymentPacket(byte code, String data) {
        this.type = Protocol.TYPE_PAYMENT;
        this.code = code;
        this.length = (short) data.length();
        setData(data.getBytes());
    }
}