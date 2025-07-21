package com.example.resume.openAI.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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

    public String preprocessResumeText(String resumeText) {
        if (resumeText == null || resumeText.trim().isEmpty()) {
            return "";
        }

        String processed = resumeText
            // Base64 디코딩된 PDF 텍스트에서 자주 나타나는 불필요한 내용 제거
            .replaceAll("\\[\\d+\\]", "") // [1], [2] 같은 참조 번호 제거
            .replaceAll("\\{.*?\\}", "") // {font-family: ...} 같은 스타일 정보 제거
            .replaceAll("\\s+", " ") // 여러 개의 공백을 하나로 통일
            .replaceAll("\\n\\s*\\n", "\n") // 빈 줄 제거
            .replaceAll("(?i)(page|쪽)\\s*\\d+\\s*(of|/)?\\s*\\d*", "") // 페이지 번호 제거
            .replaceAll("(?i)(http|https)://\\S+", "") // URL 제거
            .replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "") // 제어 문자 제거
            .trim();

        // 핵심 정보만 포함된 섹션 식별 (예: 경력, 기술 스택 등)
        String[] sections = processed.split("\n");
        StringBuilder relevant = new StringBuilder();
        
        for (String section : sections) {
            if (isRelevantSection(section)) {
                relevant.append(section).append("\n");
            }
        }

        return relevant.toString();
    }

    private boolean isRelevantSection(String section) {
        // 핵심 키워드를 포함한 섹션만 선택
        String[] relevantKeywords = {
            "경력", "기술", "스킬", "프로젝트", "자격", "학력",
            "experience", "skill", "project", "qualification", "education",
            "개발", "프로그래밍", "역량", "성과", "업무"
        };

        section = section.toLowerCase();
        for (String keyword : relevantKeywords) {
            if (section.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        
        return false;
    }

    public String getResumeKeyword(String resumeText) throws IOException {
        // 전처리된 텍스트 사용
        String processedText = preprocessResumeText(resumeText);
        
        // 텍스트 길이 제한 (추가 안전장치)
        String truncatedText = processedText.length() > 5000 ? 
            processedText.substring(0, 5000) : processedText;

        /*String script = "다음 이력서에서 핵심 키워드를 추출해줘:\n\n" + truncatedText +
                "\n\n 응답은 중요도 순으로 정렬해서 키워드 3개만 ,로 구분해서 줘";

        // API 키 유효성 검사
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("OpenAI API 키가 설정되지 않았습니다.");
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "너는 HR 전문가야."),
                Map.of("role", "user", "content", script)
        ));
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 150);

        String jsonBody = objectMapper.writeValueAsString(requestBody);
        
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(MediaType.parse("application/json"), jsonBody))
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : null;
            
            if (!response.isSuccessful()) {
                throw new IOException(String.format("OpenAI API 호출 실패 (코드: %d): %s", 
                    response.code(), responseBody));
            }

            if (responseBody == null) {
                throw new IOException("OpenAI API 응답이 비어있습니다.");
            }

            Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) result.get("choices");
            
            if (choices == null || choices.isEmpty()) {
                throw new IOException("OpenAI API 응답에 choices가 없습니다.");
            }

            Map<String, Object> messageResult = (Map<String, Object>) choices.get(0).get("message");
            return (String) messageResult.get("content");
        }*/
        return "";
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