package com.ilerijava_entegrasyon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Birfatura API'sinin /api/OutEBelgeV2/SendDocument endpoint'ine gönderilecek
 * istek gövdesini temsil eden DTO.
 */
@Data
public class SendDocumentRequestDto {

    /**
     * Alıcının etiket bilgisi.
     */
    @JsonProperty("receiverTag")
    private String receiverTag;

    /**
     * Gönderilecek belgenin UBL XML içeriğinin Base64 ile kodlanmış hali.
     */
    @JsonProperty("documentBytes")
    private String documentBytes;

    /**
     * Belge numarasının otomatik olarak atanıp atanmayacağını belirtir.
     */
    @JsonProperty("isDocumentNoAuto")
    private boolean isDocumentNoAuto = true;

    /**
     * Oluşturulacak belgenin sistem tipini belirtir.
     */
    @JsonProperty("systemTypeCodes")
    private String systemTypeCodes;
} 