package ru.nposmak.source.service.impl;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.nposmak.source.dto.DataDto;
import ru.nposmak.source.dto.PressureDto;
import ru.nposmak.source.dto.VolumeDto;
import ru.nposmak.source.service.DataReaderService;


import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


@Data
@Slf4j
@Service
public class DataReaderServiceImpl implements DataReaderService {

    @Value("${data.path}")
    private String filePath;
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public DataDto readCsvData() {
        DataDto dto = new DataDto();
        List<PressureDto> pressureDtos = new ArrayList<>();
        List<VolumeDto> volumeDtos = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))){
            String[] nextRow;
            while((nextRow = reader.readNext()) != null) {
                if(nextRow.length < 6) {
                    log.warn("В строке не хватает данных");
                    continue;
                }
                pressureDtos.add(createPressureDto(nextRow[0], nextRow[1], nextRow[2]));
                volumeDtos.add(createVolumeDto(nextRow[3], nextRow[4], nextRow[5]));
            }
            dto.setPressureDto(pressureDtos);
            dto.setVolumeDto(volumeDtos);
        } catch (IOException | CsvValidationException ex) {
            log.error("Ошибка при чтении файла {}", ex.getLocalizedMessage());
        }
        return dto;
    }

    private PressureDto createPressureDto(String orig, String measureTimestamp, String value) {
        return PressureDto.builder()
                .orig(orig)
                .measureTimestamp(parseDate(measureTimestamp))
                .value(parseValue(value)).build();
    }

    private VolumeDto createVolumeDto(String orig, String measureTimestamp, String value) {
        return VolumeDto.builder()
                .orig(orig)
                .measureTimestamp(parseDate(measureTimestamp))
                .value(parseValue(value))
                .build();
    }


    private LocalDateTime parseDate(String measureTimestamp) {
        try {
            return LocalDateTime.parse(measureTimestamp, DTF);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    private Double parseValue(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NullPointerException | NumberFormatException ex) {
            return null;
        }
    }





}
