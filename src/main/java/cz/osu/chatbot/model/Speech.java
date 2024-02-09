package cz.osu.chatbot.model;

import lombok.Data;

import java.util.List;

@Data
public class Speech {
    private String personality;
    private String speaker;
    private String date;
    private String event;
    private List<String> dialog;

}