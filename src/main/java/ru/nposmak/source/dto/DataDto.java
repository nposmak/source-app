package ru.nposmak.source.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class DataDto implements Serializable {

    private List<PressureDto> pressureDto;
    private List<VolumeDto> volumeDto;

}
