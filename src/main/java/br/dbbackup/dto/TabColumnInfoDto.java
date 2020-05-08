package br.dbbackup.dto;

import lombok.Data;

@Data
public class TabColumnInfoDto {
    private String type;
    private boolean nullable;
    private Long precision;
    private Long scale;

    public TabColumnInfoDto(String type, boolean nullable, Long precision, Long scale){
        this.type = type;
        this.nullable = nullable;
        this.precision = precision;
        this.scale = scale;
    }
}
