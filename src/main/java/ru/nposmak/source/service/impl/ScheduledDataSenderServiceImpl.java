package ru.nposmak.source.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.nposmak.source.dto.DataDto;
import ru.nposmak.source.service.DataReaderService;
import ru.nposmak.source.service.ScheduledDataSenderService;

import java.util.Objects;


@Slf4j
@Data
@Service
public class ScheduledDataSenderServiceImpl implements ScheduledDataSenderService {

    @Value("${producer.api.url}")
    private String url;
//    @Value("${task.delay}")
//    private String delay;
    private final DataReaderService dataReaderService;
    private final RestTemplate restTemplate;
    record ResponseDto(String message){}



    @Scheduled(cron = "${task.delay}")
    @Override
    public void sendData() {
        DataDto dto = dataReaderService.readCsvData();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/json");
        headers.add("Accept", "application/json");
        HttpEntity<DataDto> httpEntity = new HttpEntity<>(dto, headers);
        log.info("Отправка данных на {}", url);
        try {
            ResponseEntity<ResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    new ParameterizedTypeReference<>() {}
            );
            if(HttpStatus.OK.equals(response.getStatusCode())){
                log.info("Данные отправленны, ответ: {}", Objects.requireNonNull(response.getBody()).message);
            }
        } catch (RestClientException ex) {
            log.warn("Ошибка при отправке данных {}", ex.getLocalizedMessage());
        }


    }
}
