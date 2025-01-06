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
import java.util.Arrays;
import java.util.List;

public class CodeLlamaInvocationService {

    public void invokeCodeLLama() {
        Gson gson = new Gson();

       try( var httpClient = HttpClient.newHttpClient(); ){
          String code = Files.readString(Paths.get("/home/nishit/Documents/JavaWorkspace/TransSearchService/src/main/java/com/nitron/TransSearchService/service/TranslationService.java"));

           var cleanCode = code.replaceAll("\n", "");
           System.out.println(cleanCode);
           var embedPayload = new EmbedRequest("llama3.2", false, cleanCode);
           var embedRequest = HttpRequest.newBuilder()
                   .uri(new URI("http://localhost:11434/api/embed"))
                   .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(embedPayload)))
                   .build();
           var llamaEmbedResponse = httpClient.send(embedRequest, HttpResponse.BodyHandlers.ofString());
           var embedResponse  =  gson.fromJson(llamaEmbedResponse.body(), EmbedResponse.class);
           System.out.println(embedResponse.getEmbeddings().length);
           // var embeddingsFromFile = Files.readString(Paths.get("/home/nishit/Documents/embeddings.txt"));
           //System.out.println(embeddingsFromFile.length());
           //var generatePayload = new GenerateRequest("llama3.2", "Explain this Java code "+cleanCode, false);
           ChatMessage message = new ChatMessage("user", "explain this code "+ Arrays.deepToString(embedResponse.getEmbeddings()));
           var chatPayload = new ChatRequest("codellama", List.of(message), false);
           var request = HttpRequest.newBuilder()
                   .uri(new URI("http://localhost:11434/api/chat"))
                   .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(chatPayload)))
                   .build();
           var llamaResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
           var body = llamaResponse.body();
          var response =  gson.fromJson(body, ChatResponse.class);
           System.out.println(response.getMessage().getContent());
       } catch (RuntimeException | IOException | InterruptedException | URISyntaxException e) {
           e.printStackTrace();
       }
    }
}
