package io.quarkiverse.hivemqclient.test.vanilla.dto;

public class PublishMsgRespDto {

    private final String topic;

    public PublishMsgRespDto(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

}
