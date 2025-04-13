# Birfatura API Integration Example

This project is a simple Java (Spring Boot based) command-line application demonstrating how to send an e-Invoice using the Birfatura/eDonustur V2 e-Document API.

## Purpose

The primary goal of this project is to show how to send a request to the `/api/OutEBelgeV2/SendDocument` endpoint, how to prepare the necessary UBL XML data (via an example), and how to process the response returned from the API.

## Features

*   **Sample Request Creation:** Creates a sample `SendDocumentRequestDto` object for testing purposes.
*   **Dynamic UUID:** Generates a unique UUID for the invoice on each run.
*   **Sample UBL XML:** Includes a static UBL XML template and uses it for submission.
*   **Data Preparation:** Packages the UBL XML into a zip file and encodes it in Base64 format.
*   **API Call:** Sends the prepared request to the Birfatura API via `BirfaturaService`.
*   **Response Handling:** Processes the response (`SendDocumentResponseDto`) from the API, printing the success status, message, and invoice number (if available) to the console.
*   **Lombok & Jackson:** Uses Lombok for Data Transfer Objects (DTOs) and Jackson for JSON processing.
*   **Swagger (OpenAPI):** Includes the Swagger UI dependency (However, the project in its current state does not serve a REST API, it only consumes one).

## Requirements

*   **Java Development Kit (JDK):** Version 17 or higher.
*   **Apache Maven:** To compile the project and manage dependencies.
*   **Birfatura/eDonustur Account:** You need a test or production account and an API Key to use the API.

## Setup

1.  Clone the project:
    ```bash
    git clone <repository_url>
    ```
2.  Navigate to the project directory:
    ```bash
    cd birfatura-api-entegrasyonu
    ```
3.  Install Maven dependencies:
    ```bash
    mvn install
    ```

## Configuration

Before running, you **must** perform the following configurations:

1.  **API Credentials and URL:** Open the `src/main/java/com/ilerijava_entegrasyon/config/ApiConfig.java` file and update the following constants with your Birfatura/eDonustur information:
    *   `API_KEY`: Your provided API Key.
    *   `SECRET_KEY`: Your provided Secret Key.
    *   `INTEGRATION_KEY`: Your provided Integration Key (if applicable).
    *   `BASE_URL`: The base URL for the API (Defaults to the test environment `https://test1.edonustur.com`; you may need to change this for the production environment).
    ```java
    public static final String API_KEY = "YOUR_API_KEY"; // Enter your API key here
    public static final String SECRET_KEY = "YOUR_SECRET_KEY"; // Enter your secret key here
    public static final String INTEGRATION_KEY = "YOUR_INTEGRATION_KEY"; // Use if necessary
    public static final String BASE_URL = "https://test1.edonustur.com"; // Example for test URL, change for production
    ```
2.  **Sample UBL XML Content:** Update the XML content within the `sampleUblXmlTemplate` variable in the `createSampleSendDocumentRequest` method inside the `src/main/java/com/ilerijava_entegrasyon/BirfaturaApiEntegrasyonApplication.java` file **according to your scenario**.
    *   **Sender Information (`AccountingSupplierParty`):** Replace VKN/TCKN, Title/Name Surname, Address, Tax Office, etc., with your information.
    *   **Recipient Information (`AccountingCustomerParty`):** Replace VKN/TCKN, Title/Name Surname, Address, Tax Office, etc., with the **actual recipient's information**.
    *   **Invoice Lines (`InvoiceLine`):** Replace or duplicate the sample invoice line with your product/service details.
    *   Edit other fields (Date, Time, Currency Code, Notes, etc.) as needed.
3.  **Recipient Tag (`receiverTag`):** In the same method, change the value in the `request.setReceiverTag(...)` line to the **correct mailbox label (PK) or other identifier** for the recipient you are sending the invoice to.
    ```java
    request.setReceiverTag("urn:mail:ACTUAL_RECIPIENT_TAG"); // Enter the recipient's PK here
    ```

## Running the Application

After completing the configurations, you can run the application either from your IDE by running the `main` method in `BirfaturaApiEntegrasyonApplication.java` or from the command line using Maven:

```bash
mvn spring-boot:run
```

The application will run, prepare the sample UBL XML, send it to the API, and print the result to the console.

## Important Notes

*   This project serves as a **simple demonstration**. In a real application, UBL XML should be generated dynamically, error handling should be more comprehensive, and sensitive information like API keys should be stored more securely (e.g., in configuration files or environment variables).


 
