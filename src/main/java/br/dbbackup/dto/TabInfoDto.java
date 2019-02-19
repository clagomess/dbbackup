package br.dbbackup.dto;

import lombok.Data;

@Data
public class TabInfoDto {
    private String table;
    private Long qtdRows = 0L;
    private String pkName;
    private String lastPkValue;
    private Long qtdColumn;
    private Long lob;
}
