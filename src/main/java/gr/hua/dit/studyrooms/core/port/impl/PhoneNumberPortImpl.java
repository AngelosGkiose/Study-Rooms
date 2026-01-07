package gr.hua.dit.studyrooms.core.port.impl;

import gr.hua.dit.studyrooms.config.RestApiClientConfig;
import gr.hua.dit.studyrooms.core.port.PhoneNumberPort;
import gr.hua.dit.studyrooms.core.port.impl.dto.PhoneNumberValidationResult;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Default implementation of {@link PhoneNumberPort}.
 * It uses an external secured REST API.
 */
@Service
public class PhoneNumberPortImpl implements PhoneNumberPort {

    private static final String AUTH_TOKEN = "external-api-token";

    private final RestTemplate restTemplate;

    public PhoneNumberPortImpl(final RestTemplate restTemplate) {
        if (restTemplate == null) throw new NullPointerException();
        this.restTemplate = restTemplate;
    }

    @Override
    public PhoneNumberValidationResult validate(final String rawPhoneNumber) {
        if (rawPhoneNumber == null) throw new NullPointerException();
        if (rawPhoneNumber.isBlank()) throw new IllegalArgumentException();

        // --------------------------------------------------
        // Build secured HTTP request
        // --------------------------------------------------

        final String baseUrl = RestApiClientConfig.BASE_URL;
        final String url =
                baseUrl + "/api/v1/phone-numbers/" + rawPhoneNumber + "/validations";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AUTH_TOKEN);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<PhoneNumberValidationResult> response =
                this.restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        PhoneNumberValidationResult.class
                );

        // --------------------------------------------------

        if (response.getStatusCode().is2xxSuccessful()) {
            PhoneNumberValidationResult body = response.getBody();
            if (body == null) throw new NullPointerException();
            return body;
        }

        throw new RuntimeException(
                "External service responded with " + response.getStatusCode()
        );
    }
}
