package com.ilerijava_entegrasyon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * SendDocument yanıtındaki "Result" nesnesini temsil eden DTO.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultDto {

    @JsonProperty("UUID")
    private String uuid;

    @JsonProperty("invoiceNo")
    private String invoiceNo;

} 