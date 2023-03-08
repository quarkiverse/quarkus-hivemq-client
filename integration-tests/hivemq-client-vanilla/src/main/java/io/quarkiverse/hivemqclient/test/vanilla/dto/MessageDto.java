package io.quarkiverse.hivemqclient.test.vanilla.dto;

public class MessageDto {

    private String value;

    public MessageDto() {
    }

    public MessageDto(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
