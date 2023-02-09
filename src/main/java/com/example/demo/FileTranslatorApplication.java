package com.example.demo;

import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

@SpringBootApplication
public class FileTranslatorApplication {


    public static void main(String[] args) throws IOException, InterruptedException {
        SpringApplication.run(FileTranslatorApplication.class, args);

        String englishFileName = getFileNameFromInput("english");
        String arabicFileName = getFileNameFromInput("arabic");
        String translatedText = readAndTranslate(englishFileName);
        writeOnFile(translatedText, arabicFileName);

    }

    private static String getFileNameFromInput(String fileLanguage) {
        System.out.println("******** Enter the " + fileLanguage + " file name ********");
        try (Scanner scanner = new Scanner(System.in)) {
            String line = scanner.nextLine();
            return line;
        }
    }

    private static String readAndTranslate(String englishFileName) throws IOException, InterruptedException {
        BufferedReader reader;
        String finalTranslate = "";

        reader = new BufferedReader(new FileReader("src/main/resources/files/" + englishFileName));

        String line = reader.readLine();

        while (line != null) {
            if (line.contains("=")) {
                String[] keyValue = line.split("=");
                String translatedKeyValue = keyValue[0] + "=" + translate(keyValue[1]) + "\n";
                System.out.println(translatedKeyValue);
                finalTranslate += translatedKeyValue;
            } else {
                finalTranslate += line + "\n";
            }
            line = reader.readLine();
        }
        reader.close();
        return finalTranslate;
    }

    private static void writeOnFile(String finalResult, String arabicFileName) throws IOException {
        FileWriter myWriter = new FileWriter("src/main/resources/files/" + arabicFileName);
        myWriter.write(finalResult);
        myWriter.close();
    }

    private static String translate(String word) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://text-translator2.p.rapidapi.com/translate"))
                .header("content-type", "application/x-www-form-urlencoded")
                .header("X-RapidAPI-Key", "2dc40e20f4msh0e505f08f3432f3p1fe335jsn700d4ef52b1b")
                .header("X-RapidAPI-Host", "text-translator2.p.rapidapi.com")
                .method("POST", HttpRequest.BodyPublishers.ofString("source_language=en&target_language=ar&text=" + word))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject responseBody = new JSONObject(response.body());
        if (responseBody.has("data")) {
            String data = responseBody.get("data").toString();
            JSONObject dataAsJson = new JSONObject(data);
            return dataAsJson.get("translatedText").toString();
        }
        return word;
    }

}
