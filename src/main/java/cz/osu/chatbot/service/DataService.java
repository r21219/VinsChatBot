package cz.osu.chatbot.service;

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
            "they will not even know the mentioned terms and will not explain them";

    public String processAndStoreDataset(MultipartFile file) {
        if (!file.getContentType().equals("text/csv") && !file.getContentType().equals("text/plain")) {
            return "Invalid file format. Please upload a CSV file.";
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            StringBuilder datasetBuilder = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                datasetBuilder.append(line).append("\n");
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

