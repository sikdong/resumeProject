package com.example.resume.openAI.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class OpenAIService {
    @Value("${openai.api-key}")
    private String apiKey;
    private final String apiUrl = "https://api.openai.com/v1/chat/completions";

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getResumeKeyword(String resumeText) throws IOException {
        String script = "다음 이력서에서 핵심 키워드를 추출해줘:\n\n" + resumeText +
                "\n\n 응답은 중요도 순으로 정렬해서 키워드 3개만 ,로 구분해서 줘";
        //FIXME
        script ="다음 이력서에서 핵심 키워드를 추출해줘:\n\n백엔드 개발자, Java, Spring Boot 경험 있음\n\n응답은 키워드 3개만 ,로 구분해서 줘";
        // 🔹 메시지 구성 (ChatGPT API 형식)
        Map<String, Object> message = Map.of(
                "role", "user",
                "content", script
        );

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "너는 HR 전문가야."),
                message
        ));
        requestBody.put("temperature", 0.7);

        // 🔹 HTTP 요청 전송
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(
                        objectMapper.writeValueAsString(requestBody),
                        MediaType.parse("application/json")
                ))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("OpenAI API 호출 실패: " + response);
            }

            // 🔹 응답 파싱
            Map<String, Object> result = objectMapper.readValue(response.body().string(), Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) result.get("choices");
            Map<String, Object> messageResult = (Map<String, Object>) choices.get(0).get("message");
            return (String) messageResult.get("content");
        }
    }

    @Async
    public CompletableFuture<String> extractKeywordsAsync(String resumeText) {
        // OpenAI 요청 로직 (동기 방식 그대로 복사)
        try {
            // ChatGPT 호출해서 JSON 키워드 추출
            String result = callOpenAiApi(resumeText); // 기존 메서드로 분리해도 좋음
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    // 이 부분은 위에서 만든 callOpenAiApi와 동일
    private String callOpenAiApi(String resumeText) throws IOException {
        // OkHttp 사용해서 POST 요청 보내고 응답 파싱
        // (너가 앞에서 만든 OpenAI 호출 코드 넣으면 됨)
        return "";
    }
}
