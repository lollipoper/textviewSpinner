package com.ws.textspinner;

public class TextSpinnerItem {
    public TextSpinnerItem(TextSpinnerType type, String content) {
        this.type = type;
        this.content = content;
    }

    private TextSpinnerType type;
    private String content;

    public TextSpinnerType getType() {
        return type;
    }

    public void setType(TextSpinnerType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "TextSpinnerItem{" +
                "type=" + type +
                ", content='" + content + '\'' +
                '}';
    }
}