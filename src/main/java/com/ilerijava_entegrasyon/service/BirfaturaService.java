package com.ilerijava_entegrasyon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ilerijava_entegrasyon.config.ApiConfig;
// import com.ilerijava_entegrasyon.dto.InvoiceDto; // Eski DTO importu kaldırıldı
import com.ilerijava_entegrasyon.dto.SendDocumentRequestDto; // Yeni DTO importu eklendi
import com.ilerijava_entegrasyon.dto.SendDocumentResponseDto; // Yeni import

import java.io.IOException;
// import java.net.Authenticator; // Artık kullanılmıyor
// import java.net.PasswordAuthentication; // Artık kullanılmıyor
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
// import java.util.Base64; // Artık kullanılmıyor

public class BirfaturaService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public BirfaturaService() {        
        this.httpClient = HttpClient.newBuilder()                
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Verilen fatura DTO'sunu Birfatura API'sine gönderir.
     *
     * @param requestDto Gönderilecek belge bilgileri (SendDocumentRequestDto).
     * @return API'den dönen yanıt (SendDocumentResponseDto olarak).
     * @throws IOException          HTTP isteği sırasında hata oluşursa.
     * @throws InterruptedException HTTP isteği kesintiye uğrarsa.
     */
    public SendDocumentResponseDto sendInvoice(SendDocumentRequestDto requestDto) throws IOException, InterruptedException {
        if (requestDto == null) {
            throw new IllegalArgumentException("SendDocumentRequestDto null olamaz.");
        }
        String requestBody = objectMapper.writeValueAsString(requestDto);
        URI apiUri = URI.create(ApiConfig.BASE_URL + ApiConfig.SEND_DOCUMENT_ENDPOINT);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(apiUri)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("X-Api-Key", ApiConfig.API_KEY)
                .header("X-Secret-Key", ApiConfig.SECRET_KEY)
                .header("X-Integration-Key", ApiConfig.INTEGRATION_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .timeout(Duration.ofSeconds(30))
                .build();

        System.out.println("Birfatura API'sine gönderiliyor: " + apiUri);
        System.out.println("Kullanılan Headerlar: X-Api-Key, X-Secret-Key, X-Integration-Key");

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        System.out.println("SendDocument API Yanıt Kodu: " + response.statusCode());
        System.out.println("SendDocument API Yanıt Body: " + response.body());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            // JSON yanıtını DTO'ya dönüştür
            return objectMapper.readValue(response.body(), SendDocumentResponseDto.class);
        } else {
            throw new IOException("SendDocument API isteği başarısız oldu. Status Kodu: " + response.statusCode() + ", Yanıt: " + response.body());
        }
    }

    /**
     * Verilen UUID'ye ait belgeyi PDF olarak indirir.
     *
     * @param uuid İndirilecek belgenin UUID'si.
     * @return PDF dosyasının içeriğini tutan byte dizisi.
     * @throws IOException          HTTP isteği sırasında hata oluşursa.
     * @throws InterruptedException HTTP isteği kesintiye uğrarsa.
     */
    public byte[] downloadPdfByUuid(String uuid) throws IOException, InterruptedException {
        if (uuid == null || uuid.isBlank()) {
            throw new IllegalArgumentException("UUID boş olamaz.");
        }

        URI apiUri = URI.create(ApiConfig.BASE_URL + ApiConfig.PREVIEW_DOCUMENT_PDF_ENDPOINT);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(apiUri)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/pdf") 
                .header("X-Api-Key", ApiConfig.API_KEY)
                .header("X-Secret-Key", ApiConfig.SECRET_KEY)
                .header("X-Integration-Key", ApiConfig.INTEGRATION_KEY)
                .timeout(Duration.ofSeconds(60)) 
                .build();

        System.out.println("Birfatura API'sine PDF indirme isteği gönderiliyor: " + apiUri + " (UUID: " + uuid + ")");

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        System.out.println("PreviewDocumentReturnPDF API Yanıt Kodu: " + response.statusCode());

        if (response.statusCode() == 200) {
            System.out.println("PDF başarıyla indirildi (bytes: " + response.body().length + ")");
            return response.body();
        } else {
            String errorBody = new String(response.body(), StandardCharsets.UTF_8);
            System.err.println("PreviewDocumentReturnPDF API Yanıt Body (Hata): " + errorBody);
            throw new IOException("PreviewDocumentReturnPDF API isteği başarısız oldu. Status Kodu: " + response.statusCode() + ", Yanıt: " + errorBody);
        }
    }
} 