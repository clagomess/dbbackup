SELECT SATC.TABLE_NAME as "table_name", SATC.COLUMN_NAME as "column_name", SATC.DATA_TYPE as "data_type"
FROM SYS.ALL_TAB_COLUMNS SATC
JOIN SYS.ALL_TABLES SAT ON SAT.OWNER = SATC.OWNER AND SAT.TABLE_NAME = SATC.TABLE_NAME
LEFT JOIN SYS.ALL_MVIEWS SAM ON SAM.OWNER = SATC.OWNER AND SAM.MVIEW_NAME = SATC.TABLE_NAME
WHERE SAM.MVIEW_NAME IS NULL AND SATC.OWNER = :TABLE_SCHEMA
AND (:TABLE_NAME IS NULL OR SATC.TABLE_NAME IN (:TABLE_NAME_AR))
ORDER BY SATC.TABLE_NAME, SATC.COLUMN_ID