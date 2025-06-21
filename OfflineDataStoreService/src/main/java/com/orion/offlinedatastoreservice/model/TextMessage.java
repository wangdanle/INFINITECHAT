package com.orion.offlinedatastoreservice.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TextMessage extends MessageDTO {

    private TextMessageBody body;

    @Override
    public String toString() {
        return super.toString();
    }
}
