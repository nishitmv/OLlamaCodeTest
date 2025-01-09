package com.nitron;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CodeLlamaInvocationService {

    public void invokeCodeLLama() {
        Gson gson = new Gson();

        try (var httpClient = HttpClient.newHttpClient();) {
            String code1 = Files.readString(Paths.get("/home/nishit/Documents/JavaWorkspace/TransSearchService/src/main/java/com/nitron/TransSearchService/service/TranslationService.java"));
            String code2 = Files.readString(Paths.get("/home/nishit/Documents/JavaWorkspace/TransSearchService/src/main/java/com/nitron/TransSearchService/repository/TranslationsRepository.java"));
            String code3 = Files.readString(Paths.get("/home/nishit/Documents/JavaWorkspace/TransSearchService/src/main/java/com/nitron/TransSearchService/controller/TranslationsController.java"));

            var cleanCode1 = code1.replaceAll("\n", "");
            System.out.println(cleanCode1);
            var cleanCode2 = code2.replaceAll("\n", "");
            System.out.println(cleanCode2);
            var cleanCode3 = code3.replaceAll("\n", "");
            System.out.println(cleanCode3);
            String cleanCode = cleanCode1.concat(cleanCode2).concat(cleanCode3);
            var embeddings = getEmbedRequest(cleanCode);
            var chatPayload = getChatRequest(cleanCode);
            var request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:11434/api/chat"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(chatPayload)))
                    .build();
            var llamaResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            var body = llamaResponse.body();
            var response = gson.fromJson(body, ChatResponse.class);
            System.out.println(response.getMessage().getContent());
        } catch (RuntimeException | IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private  ChatRequest getChatRequest(String cleanCode) {
        ChatMessage message = new ChatMessage("user", "which method throws UnsupportedOperationException " + cleanCode);
        return new ChatRequest("codellama", List.of(message), false);
    }

    private  double[][] getEmbedRequest(String cleanCode) {
        try (var httpClient = HttpClient.newHttpClient();) {
            Gson gson = new Gson();
            var embedPayload = new EmbedRequest("llama3.2", false, cleanCode);
            var embedRequest = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:11434/api/embed"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(embedPayload)))
                    .build();
            var llamaEmbedResponse = httpClient.send(embedRequest, HttpResponse.BodyHandlers.ofString());
            var embedResponse = gson.fromJson(llamaEmbedResponse.body(), EmbedResponse.class);
            System.out.println(embedResponse.getEmbeddings().length);
            return embedResponse.getEmbeddings();
            // var embeddingsFromFile = Files.readString(Paths.get("/home/nishit/Documents/embeddings.txt"));
            //System.out.println(embeddingsFromFile.length());
        } catch (InterruptedException | IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
