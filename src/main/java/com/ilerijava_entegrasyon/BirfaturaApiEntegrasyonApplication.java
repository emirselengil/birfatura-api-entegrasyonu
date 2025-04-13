package com.ilerijava_entegrasyon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilerijava_entegrasyon.dto.SendDocumentRequestDto;
import com.ilerijava_entegrasyon.dto.SendDocumentResponseDto;
import com.ilerijava_entegrasyon.service.BirfaturaService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.UUID;

public class BirfaturaApiEntegrasyonApplication {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static void main(String[] args) {
		System.out.println("Birfatura API Entegrasyon Uygulaması Başlatıldı.");

		
		BirfaturaService birfaturaService = new BirfaturaService();

		SendDocumentRequestDto sampleRequest = createSampleSendDocumentRequest();

		if (sampleRequest == null) {
			System.err.println("Örnek istek oluşturulamadı.");
			return;
		}

		try {
			System.out.println("\nÖrnek belge gönderiliyor (XML zip'lenip Base64 kodlanarak)... ");
			SendDocumentResponseDto sendResponse = birfaturaService.sendInvoice(sampleRequest);

			if (sendResponse != null && sendResponse.isSuccess()) {
                System.out.println("\nBelge gönderme API çağrısı başarılı.");
                System.out.println("Yanıt Mesajı: " + sendResponse.getMessage());
                // Fatura Numarasını kontrol et ve yazdır (varsa)
                if (sendResponse.getResult() != null && sendResponse.getResult().getInvoiceNo() != null && !sendResponse.getResult().getInvoiceNo().isBlank()) {
                    System.out.println("Alınan Fatura No: " + sendResponse.getResult().getInvoiceNo());
                }
                

			} else {				
				 String errorDetails = "null";
				 if (sendResponse != null) {
					 try {
						 errorDetails = objectMapper.writeValueAsString(sendResponse); // Yanıtı JSON olarak yazdır
					 } catch (JsonProcessingException jsonEx) {
						 errorDetails = sendResponse.toString() + " (JSON dönüşüm hatası: " + jsonEx.getMessage() + ")";
					 }
				 }
				 System.err.println("\nBelge gönderimi başarılı olmadı veya geçersiz yanıt alındı. Yanıt: " + errorDetails);
			}

		} catch (Exception e) {
			System.err.println("\nBelge gönderme sırasında hata oluştu:");
			e.printStackTrace();
		}

		System.out.println("\nBirfatura API Entegrasyon Uygulaması Tamamlandı.");
	}

	/**
	 * Test için örnek bir SendDocumentRequestDto nesnesi oluşturur.
	 * Örnek EFATURA-XML-ORNEK.txt içeriğini kullanır, ancak HER SEFERİNDE YENİ UUID üretir.
	 * Gerçek senaryoda UBL XML içeriği dinamik olarak oluşturulmalı veya dosyadan okunmalıdır.
	 */
	private static SendDocumentRequestDto createSampleSendDocumentRequest() {
		SendDocumentRequestDto request = new SendDocumentRequestDto();

		// Alıcı Etiketi
		request.setReceiverTag("urn:mail:ykartal@gmail.com");

		// Sistem Tipi
		request.setSystemTypeCodes("EFATURA");

		// Belge Numarası Otomatik mi?
		request.setDocumentNoAuto(true);

		// YENİ: Her istek için benzersiz bir UUID oluştur
		String newUuid = UUID.randomUUID().toString();
		System.out.println("Oluşturulan Yeni Belge UUID: " + newUuid);

		// Belge İçeriği (Base64 Zip(UBL XML))
		// Örnek XML (Alıcı TCKN ile güncellendi):
		String staticUuidToReplace = "88f924da-b4f8-4c22-8db2-8cf9057a8b8d";
		String sampleUblXmlTemplate = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" +
				"<Invoice xmlns:ubltr=\"urn:oasis:names:specification:ubl:schema:xsd:TurkishCustomizationExtensionComponents\" xmlns:qdt=\"urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2\" xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ccts=\"urn:un:unece:uncefact:documentation:2\" xmlns:ext=\"urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2\" xmlns:xades=\"http://uri.etsi.org/01903/v1.3.2#\" xmlns:udt=\"urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2\" xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2\">\r\n" +
				"  <ext:UBLExtensions>\r\n" +
				"    <ext:UBLExtension>\r\n" +
				"      <ext:ExtensionContent>\r\n" +
				"        \r\n" +
				"      </ext:ExtensionContent>\r\n" +
				"    </ext:UBLExtension>\r\n" +
				"  </ext:UBLExtensions>\r\n" +
				"  <cbc:UBLVersionID>2.1</cbc:UBLVersionID>\r\n" +
				"  <cbc:CustomizationID>TR1.2</cbc:CustomizationID>\r\n" +
				"  <cbc:ProfileID>TICARIFATURA</cbc:ProfileID>\r\n" +
				"  <cbc:ID>ARS2024000000001</cbc:ID>\r\n" +
				"  <cbc:CopyIndicator>false</cbc:CopyIndicator>\r\n" +
				"  <cbc:UUID>" + staticUuidToReplace + "</cbc:UUID>\r\n" +
				"  <cbc:IssueDate>2024-12-19</cbc:IssueDate>\r\n" +
				"  <cbc:IssueTime>17:38:28.3297096+03:00</cbc:IssueTime>\r\n" +
				"  <cbc:InvoiceTypeCode>SATIS</cbc:InvoiceTypeCode>\r\n" +
				"  <cbc:Note>Yalnız BirTürkLirasıYirmiKuruş&lt;br/&gt;</cbc:Note>\r\n" +
				"  <cbc:Note>&lt;br&gt;E-Fatura izni kapsamında elektronik ortamda iletilmiştir. &lt;br/&gt;Ödeme Yöntemi: Kredi Kartı İle Ödendi - Sipariş No:_Kopya - Kargo Kampanya Kodu:3321733760237447 - Kargo Şirketi:Aras - Teslimat Bilgileri: TEST EFİRMA Beytepe Mahallesi, Çankaya/Ankara Çankaya Ankara </cbc:Note>\r\n" +
				"  <cbc:DocumentCurrencyCode>TRY</cbc:DocumentCurrencyCode>\r\n" +
				"  <cbc:LineCountNumeric>1</cbc:LineCountNumeric>\r\n" +
				"  <cac:OrderReference>\r\n" +
				"    <cbc:ID>_Kopya</cbc:ID>\r\n" +
				"    <cbc:IssueDate>2024-12-19</cbc:IssueDate>\r\n" +
				"  </cac:OrderReference>\r\n" +
				"  <cac:AdditionalDocumentReference>\r\n" +
				"    <cbc:ID>88f924da-b4f8-4c22-8db2-8cf9057a8b8d</cbc:ID>\r\n" +
				"    <cbc:IssueDate>2024-12-19</cbc:IssueDate>\r\n" +
				"    <cbc:DocumentTypeCode>CUST_INV_ID</cbc:DocumentTypeCode>\r\n" +
				"  </cac:AdditionalDocumentReference>\r\n" +
				"  <cac:AdditionalDocumentReference>\r\n" +
				"    <cbc:ID>0100</cbc:ID>\r\n" +
				"    <cbc:IssueDate>2024-12-19</cbc:IssueDate>\r\n" +
				"    <cbc:DocumentTypeCode>OUTPUT_TYPE</cbc:DocumentTypeCode>\r\n" +
				"  </cac:AdditionalDocumentReference>\r\n" +
				"  <cac:AdditionalDocumentReference>\r\n" +
				"    <cbc:ID>99</cbc:ID>\r\n" +
				"    <cbc:IssueDate>2024-12-19</cbc:IssueDate>\r\n" +
				"    <cbc:DocumentTypeCode>TRANSPORT_TYPE</cbc:DocumentTypeCode>\r\n" +
				"  </cac:AdditionalDocumentReference>\r\n" +
				"  <cac:AdditionalDocumentReference>\r\n" +
				"    <cbc:ID>ELEKTRONIK</cbc:ID>\r\n" +
				"    <cbc:IssueDate>2024-12-19</cbc:IssueDate>\r\n" +
				"    <cbc:DocumentTypeCode>EREPSENDT</cbc:DocumentTypeCode>\r\n" +
				"  </cac:AdditionalDocumentReference>\r\n" +
				"  <cac:AdditionalDocumentReference>\r\n" +
				"    <cbc:ID>0</cbc:ID>\r\n" +
				"    <cbc:IssueDate>2024-12-19</cbc:IssueDate>\r\n" +
				"    <cbc:DocumentTypeCode>SendingType</cbc:DocumentTypeCode>\r\n" +
				"    <cbc:DocumentType>KAGIT</cbc:DocumentType>\r\n" +
				"  </cac:AdditionalDocumentReference>\r\n" +
				"  <cac:AdditionalDocumentReference>\r\n" +
				"    <cbc:ID>FIT2024000000001</cbc:ID>\r\n" +
				"    <cbc:IssueDate>2024-12-19</cbc:IssueDate>\r\n" +
				"    <cbc:DocumentType>XSLT</cbc:DocumentType>\r\n" +
				"  </cac:AdditionalDocumentReference>\r\n" +
				"  <cac:AdditionalDocumentReference>\r\n" +
				"    <cbc:ID>urn:mail:defaultpk@deneme.com</cbc:ID>\r\n" +
				"    <cbc:IssueDate>2024-12-19</cbc:IssueDate>\r\n" +
				"    <cbc:DocumentTypeCode>recvpk</cbc:DocumentTypeCode>\r\n" +
				"  </cac:AdditionalDocumentReference>\r\n" +
				"  <cac:Signature>\r\n" +
				"    <cbc:ID schemeID=\"VKN_TCKN\">1234567801</cbc:ID>\r\n" +
				"    <cac:SignatoryParty>\r\n" +
				"      <cac:PartyIdentification>\r\n" +
				"        <cbc:ID schemeID=\"VKN\">1234567801</cbc:ID>\r\n" +
				"      </cac:PartyIdentification>\r\n" +
				"      <cac:PostalAddress>\r\n" +
				"        <cbc:StreetName>Kuşkavağı, Belediye Cd. No:78, 07070 Konyaaltı/Antalya</cbc:StreetName>\r\n" +
				"        <cbc:CitySubdivisionName>Konyaaltı</cbc:CitySubdivisionName>\r\n" +
				"        <cbc:CityName>Antalya</cbc:CityName>\r\n" +
				"        <cac:Country>\r\n" +
				"          <cbc:Name>TÜRKİYE</cbc:Name>\r\n" +
				"        </cac:Country>\r\n" +
				"      </cac:PostalAddress>\r\n" +
				"    </cac:SignatoryParty>\r\n" +
				"    <cac:DigitalSignatureAttachment>\r\n" +
				"      <cac:ExternalReference>\r\n" +
				"        <cbc:URI>#Signature</cbc:URI>\r\n" +
				"      </cac:ExternalReference>\r\n" +
				"    </cac:DigitalSignatureAttachment>\r\n" +
				"  </cac:Signature>\r\n" +
				"  <cac:AccountingSupplierParty>\r\n" +
				"    <cac:Party>\r\n" +
				"      <cac:PartyIdentification>\r\n" +
				"        <cbc:ID schemeID=\"VKN\">1234567801</cbc:ID>\r\n" +
				"      </cac:PartyIdentification>\r\n" +
				"      <cac:PartyName>\r\n" +
				"        <cbc:Name>E-Dönüştür Demo Hesabı</cbc:Name>\r\n" +
				"      </cac:PartyName>\r\n" +
				"      <cac:PostalAddress>\r\n" +
				"        <cbc:StreetName>Ankara mahallesi Ankara sok No:7</cbc:StreetName>\r\n" +
				"        <cbc:CitySubdivisionName />\r\n" +
				"        <cbc:CityName>Eskişehir</cbc:CityName>\r\n" +
				"        <cac:Country>\r\n" +
				"          <cbc:Name>Türkiye</cbc:Name>\r\n" +
				"        </cac:Country>\r\n" +
				"      </cac:PostalAddress>\r\n" +
				"      <cac:PartyTaxScheme>\r\n" +
				"        <cac:TaxScheme>\r\n" +
				"          <cbc:Name>ANKARA</cbc:Name>\r\n" +
				"        </cac:TaxScheme>\r\n" +
				"      </cac:PartyTaxScheme>\r\n" +
				"      <cac:Contact>\r\n" +
				"        <cbc:Telephone>03121111111</cbc:Telephone>\r\n" +
				"        <cbc:ElectronicMail>iletisim@edonustur.com</cbc:ElectronicMail>\r\n" +
				"      </cac:Contact>\r\n" +
				"    </cac:Party>\r\n" +
				"  </cac:AccountingSupplierParty>\r\n" +
				"  <cac:AccountingCustomerParty>\r\n" +
				"    <cac:Party>\r\n" +
				"      <cac:PartyIdentification>\r\n" +
				"        <cbc:ID schemeID=\"VKN\">1234567805</cbc:ID>\r\n" +
				"      </cac:PartyIdentification>\r\n" +
				"      <cac:PartyName>\r\n" +
				"        <cbc:Name>HepsiBurada</cbc:Name>\r\n" +
				"      </cac:PartyName>\r\n" +
				"      <cac:PostalAddress>\r\n" +
				"        <cbc:StreetName>Ankara mahallesi Ankara sok No:7</cbc:StreetName>\r\n" +
				"        <cbc:CitySubdivisionName />\r\n" +
				"        <cbc:CityName>Eskişehir</cbc:CityName>\r\n" +
				"        <cac:Country>\r\n" +
				"          <cbc:Name>Türkiye</cbc:Name>\r\n" +
				"        </cac:Country>\r\n" +
				"      </cac:PostalAddress>\r\n" +
				"      <cac:PartyTaxScheme>\r\n" +
				"        <cac:TaxScheme>\r\n" +
				"          <cbc:Name>ANKARA</cbc:Name>\r\n" +
				"        </cac:TaxScheme>\r\n" +
				"      </cac:PartyTaxScheme>\r\n" +
				"      <cac:Contact>\r\n" +
				"        <cbc:Telephone>03121111111</cbc:Telephone>\r\n" +
				"        <cbc:ElectronicMail>hepsi@burada.com</cbc:ElectronicMail>\r\n" +
				"      </cac:Contact>\r\n" +
				"    </cac:Party>\r\n" +
				"  </cac:AccountingCustomerParty>\r\n" +
				"  <cac:Delivery>\r\n" +
				"    <cac:DeliveryAddress>\r\n" +
				"      <cbc:StreetName>Talatpaşa Cad.</cbc:StreetName>\r\n" +
				"      <cbc:CitySubdivisionName>Ümraniye</cbc:CitySubdivisionName>\r\n" +
				"      <cbc:CityName>İstanbul</cbc:CityName>\r\n" +
				"      <cac:Country>\r\n" +
				"        <cbc:Name>Türkiye</cbc:Name>\r\n" +
				"      </cac:Country>\r\n" +
				"    </cac:DeliveryAddress>\r\n" +
				"    <cac:CarrierParty>\r\n" +
				"      <cac:PartyIdentification>\r\n" +
				"        <cbc:ID schemeID=\"VKN\">1234567890</cbc:ID>\r\n" +
				"      </cac:PartyIdentification>\r\n" +
				"      <cac:PartyName>\r\n" +
				"        <cbc:Name>Aras Kargo</cbc:Name>\r\n" +
				"      </cac:PartyName>\r\n" +
				"      <cac:PostalAddress>\r\n" +
				"        <cbc:ID />\r\n" +
				"        <cbc:StreetName>Rüzgarlıbahçe Mah. Yavuz Sultan Selim Cad.</cbc:StreetName>\r\n" +
				"        <cbc:BuildingName>Aras Plaza</cbc:BuildingName>\r\n" +
				"        <cbc:BuildingNumber>2</cbc:BuildingNumber>\r\n" +
				"        <cbc:CitySubdivisionName>Beykoz</cbc:CitySubdivisionName>\r\n" +
				"        <cbc:CityName>İstanbul</cbc:CityName>\r\n" +
				"        <cbc:PostalZone>34000</cbc:PostalZone>\r\n" +
				"        <cac:Country>\r\n" +
				"          <cbc:Name>Türkiye</cbc:Name>\r\n" +
				"        </cac:Country>\r\n" +
				"      </cac:PostalAddress>\r\n" +
				"    </cac:CarrierParty>\r\n" +
				"    <cac:DeliveryParty>\r\n" +
				"      <cac:PartyIdentification>\r\n" +
				"        <cbc:ID />\r\n" +
				"      </cac:PartyIdentification>\r\n" +
				"      <cac:PartyName>\r\n" +
				"        <cbc:Name>Teslimat yapılacak isim</cbc:Name>\r\n" +
				"      </cac:PartyName>\r\n" +
				"      <cac:PostalAddress>\r\n" +
				"        <cbc:ID />\r\n" +
				"        <cbc:StreetName>Talatpaşa Cad. Park Sok.</cbc:StreetName>\r\n" +
				"        <cbc:BuildingNumber>35-1</cbc:BuildingNumber>\r\n" +
				"        <cbc:CitySubdivisionName>Ümraniye</cbc:CitySubdivisionName>\r\n" +
				"        <cbc:CityName>İstanbul</cbc:CityName>\r\n" +
				"        <cbc:PostalZone>34000</cbc:PostalZone>\r\n" +
				"        <cac:Country>\r\n" +
				"          <cbc:Name>Türkiye</cbc:Name>\r\n" +
				"        </cac:Country>\r\n" +
				"      </cac:PostalAddress>\r\n" +
				"      <cac:Person>\r\n" +
				"        <cbc:FirstName>Teslim alacak kişi isim</cbc:FirstName>\r\n" +
				"        <cbc:FamilyName>Teslim alacak kişi soyisim</cbc:FamilyName>\r\n" +
				"      </cac:Person>\r\n" +
				"    </cac:DeliveryParty>\r\n" +
				"  </cac:Delivery>\r\n" +
				"  <cac:TaxTotal>\r\n" +
				"    <cbc:TaxAmount currencyID=\"TRY\">0.20</cbc:TaxAmount>\r\n" +
				"    <cac:TaxSubtotal>\r\n" +
				"      <cbc:TaxableAmount currencyID=\"TRY\">1.00</cbc:TaxableAmount>\r\n" +
				"      <cbc:TaxAmount currencyID=\"TRY\">0.20</cbc:TaxAmount>\r\n" +
				"      <cbc:Percent>20</cbc:Percent>\r\n" +
				"      <cac:TaxCategory>\r\n" +
				"        <cac:TaxScheme>\r\n" +
				"          <cbc:Name>KDV</cbc:Name>\r\n" +
				"          <cbc:TaxTypeCode>0015</cbc:TaxTypeCode>\r\n" +
				"        </cac:TaxScheme>\r\n" +
				"      </cac:TaxCategory>\r\n" +
				"    </cac:TaxSubtotal>\r\n" +
				"  </cac:TaxTotal>\r\n" +
				"  <cac:LegalMonetaryTotal>\r\n" +
				"    <cbc:LineExtensionAmount currencyID=\"TRY\">1.00</cbc:LineExtensionAmount>\r\n" +
				"    <cbc:TaxExclusiveAmount currencyID=\"TRY\">1.00</cbc:TaxExclusiveAmount>\r\n" +
				"    <cbc:TaxInclusiveAmount currencyID=\"TRY\">1.20</cbc:TaxInclusiveAmount>\r\n" +
				"    <cbc:AllowanceTotalAmount currencyID=\"TRY\">0.00</cbc:AllowanceTotalAmount>\r\n" +
				"    <cbc:PayableAmount currencyID=\"TRY\">1.20</cbc:PayableAmount>\r\n" +
				"  </cac:LegalMonetaryTotal>\r\n" +
				"  <cac:InvoiceLine>\r\n" +
				"    <cbc:ID>1</cbc:ID>\r\n" +
				"    <cbc:InvoicedQuantity unitCode=\"NIU\">1.0000</cbc:InvoicedQuantity>\r\n" +
				"    <cbc:LineExtensionAmount currencyID=\"TRY\">1.00</cbc:LineExtensionAmount>\r\n" +
				"    <cac:TaxTotal>\r\n" +
				"      <cbc:TaxAmount currencyID=\"TRY\">0.20</cbc:TaxAmount>\r\n" +
				"      <cac:TaxSubtotal>\r\n" +
				"        <cbc:TaxableAmount currencyID=\"TRY\">1.00</cbc:TaxableAmount>\r\n" +
				"        <cbc:TaxAmount currencyID=\"TRY\">0.20</cbc:TaxAmount>\r\n" +
				"        <cbc:Percent>20</cbc:Percent>\r\n" +
				"        <cac:TaxCategory>\r\n" +
				"          <cac:TaxScheme>\r\n" +
				"            <cbc:Name>KDV</cbc:Name>\r\n" +
				"            <cbc:TaxTypeCode>0015</cbc:TaxTypeCode>\r\n" +
				"          </cac:TaxScheme>\r\n" +
				"        </cac:TaxCategory>\r\n" +
				"      </cac:TaxSubtotal>\r\n" +
				"    </cac:TaxTotal>\r\n" +
				"    <cac:Item>\r\n" +
				"      <cbc:Description />\r\n" +
				"      <cbc:Name>Cep Telefonu Aksesuarı</cbc:Name>\r\n" +
				"      <cac:BuyersItemIdentification>\r\n" +
				"        <cbc:ID />\r\n" +
				"      </cac:BuyersItemIdentification>\r\n" +
				"      <cac:SellersItemIdentification>\r\n" +
				"        <cbc:ID>56898T10Stani1805 </cbc:ID>\r\n" +
				"      </cac:SellersItemIdentification>\r\n" +
				"      <cac:AdditionalItemIdentification>\r\n" +
				"        <cbc:ID />\r\n" +
				"      </cac:AdditionalItemIdentification>\r\n" +
				"    </cac:Item>\r\n" +
				"    <cac:Price>\r\n" +
				"      <cbc:PriceAmount currencyID=\"TRY\">1.000000</cbc:PriceAmount>\r\n" +
				"    </cac:Price>\r\n" +
				"  </cac:InvoiceLine>\r\n" +
				"</Invoice>";

		// XML Şablonundaki statik UUID'yi yeni oluşturulanla değiştir
		String finalUblXml = sampleUblXmlTemplate.replace(staticUuidToReplace, newUuid);

		try {
			// Güncellenmiş UBL XML'i bir zip dosyası içine paketle
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try (ZipOutputStream zos = new ZipOutputStream(baos)) {
				ZipEntry entry = new ZipEntry("invoice.xml");
				zos.putNextEntry(entry);
				zos.write(finalUblXml.getBytes(StandardCharsets.UTF_8));
				zos.closeEntry();
			}
			byte[] zipBytes = baos.toByteArray();

			// Zip dosyasının byte'larını Base64'e çevir
			String base64EncodedZip = Base64.getEncoder().encodeToString(zipBytes);
			request.setDocumentBytes(base64EncodedZip);

		} catch (IOException e) {
			System.err.println("Örnek UBL XML zip'lenirken veya Base64'e çevrilirken hata: " + e.getMessage());
			return null;
		}

		return request;
	}
}
