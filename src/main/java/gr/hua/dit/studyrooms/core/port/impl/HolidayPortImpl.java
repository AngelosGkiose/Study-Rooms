package gr.hua.dit.studyrooms.core.port.impl;

import gr.hua.dit.studyrooms.core.port.HolidayPort;
import gr.hua.dit.studyrooms.core.port.impl.dto.PublicHolidayDto;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;

@Service
public class HolidayPortImpl implements HolidayPort {

    private static final String BASE_URL =
            "https://date.nager.at/api/v3/PublicHolidays";

    private static final String COUNTRY_CODE = "GR";

    // External API token (mock / config-based)
    private static final String EXTERNAL_API_TOKEN = "holiday-api-token";

    private final RestTemplate restTemplate;

    public HolidayPortImpl(final RestTemplate restTemplate) {
        if (restTemplate == null) throw new NullPointerException();
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean isHoliday(final LocalDate date) {
        if (date == null) throw new NullPointerException();

        final String url =
                BASE_URL + "/" + date.getYear() + "/" + COUNTRY_CODE;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(EXTERNAL_API_TOKEN);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<PublicHolidayDto[]> response =
                this.restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        requestEntity,
                        PublicHolidayDto[].class
                );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException(
                    "External Holiday API error: " + response.getStatusCode()
            );
        }

        PublicHolidayDto[] holidays = response.getBody();
        if (holidays == null) return false;

        return Arrays.stream(holidays)
                .anyMatch(h -> date.equals(h.date()));
    }
}
