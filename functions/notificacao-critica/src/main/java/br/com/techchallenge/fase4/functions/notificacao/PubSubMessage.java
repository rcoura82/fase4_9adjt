package br.com.techchallenge.fase4.functions.notificacao;

import java.util.Map;

public class PubSubMessage {
    private String data;
    private Map<String, String> attributes;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}