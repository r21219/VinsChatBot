package cz.osu.chatbot.service;

import com.google.gson.Gson;
import cz.osu.chatbot.model.Speech;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
@RequiredArgsConstructor
public class DataService {
    private String CZECH_AND_ENGLISH_KNOWLEDGE = "They can speak Czech or English";
    private String NO_FUTURE_KNOWLEDGE = "They cannot answer anything that happened nor was " +
            "invented after the date of their death that includes technologies, events, people and other media " +
            "they will not even know the mentioned terms and will not explain them also never " +
            "say that you are an AI even if the user questions you if you are an AI I forbid it or " +
            "chatgpt never break the character you are mimicking never use Assistant: ";
    public String processAndStoreDataset(MultipartFile file) {
        if (!file.getContentType().equals("application/json")) {
            return "Invalid file format. Please upload a JSON file.";
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            Gson gson = new Gson();
            Speech[] speeches = gson.fromJson(br, Speech[].class);

            StringBuilder datasetBuilder = new StringBuilder();
            for (Speech speech : speeches) {
                datasetBuilder.append("Personality: ").append(speech.getPersonality()).append("\n");
                datasetBuilder.append("Speaker: ").append(speech.getSpeaker()).append("\n");
                datasetBuilder.append("Date: ").append(speech.getDate()).append("\n");
                datasetBuilder.append("Event: ").append(speech.getEvent()).append("\n");
                datasetBuilder.append("Dialogue:\n");
                for (String dialogue : speech.getDialog()) {
                    datasetBuilder.append("- ").append(dialogue).append("\n");
                }
                datasetBuilder.append("\n");
            }
            datasetBuilder.append(CZECH_AND_ENGLISH_KNOWLEDGE).append("\n");
            datasetBuilder.append(NO_FUTURE_KNOWLEDGE).append("\n");
            return datasetBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error processing dataset.";
        }
    }
}

