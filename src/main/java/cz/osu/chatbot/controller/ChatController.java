package cz.osu.chatbot.controller;

import cz.osu.chatbot.model.ChatRequest;
import cz.osu.chatbot.model.ChatResponse;
import cz.osu.chatbot.model.UserMessage;
import cz.osu.chatbot.service.DataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    @Qualifier("openaiRestTemplate")
    private final RestTemplate restTemplate;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;
    private List<String> conversationHistory = new ArrayList<>();
    private final DataService dataService;
    private String currentDataset;
    @PostMapping("/upload-dataset")
    public void uploadDataset(@RequestParam("file") MultipartFile file) {
        String newDataSet = dataService.processAndStoreDataset(file);
        if (currentDataset == null|| !currentDataset.equals(newDataSet)){
            currentDataset = newDataSet;
            conversationHistory.clear();
        }
    }

    @PostMapping("/chat")
    public String chat(@RequestBody UserMessage userMessage) {
        conversationHistory.add("User: " + userMessage.getMessage());

        List<String> conversationWithDataset = new ArrayList<>(conversationHistory);
        conversationWithDataset.add(0, currentDataset);

        ChatRequest request = new ChatRequest(model, String.join("\n", conversationWithDataset));
        ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);

        if (response != null && !response.getChoices().isEmpty()) {
            String assistantMessage = response.getChoices().get(0).getMessage().getContent();
            conversationHistory.add("Assistant: " + assistantMessage);
            return assistantMessage;
        }

        return "No response";
    }
}