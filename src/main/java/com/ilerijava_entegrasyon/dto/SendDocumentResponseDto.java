package com.ilerijava_entegrasyon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * SendDocument API yanıtını temsil eden ana DTO.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Bilmediğimiz alanları yoksay
public class SendDocumentResponseDto {

    @JsonProperty("Success")
    private boolean success;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("Code")
    private String code;

    @JsonProperty("Result")
    private ResultDto result;
} 