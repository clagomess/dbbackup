package br.dbbackup.core;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Format {
    public static String dateTime(Timestamp timestamp){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(timestamp);
    }

    public static String date(Timestamp timestamp){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(timestamp);
    }

    public static String time(Timestamp timestamp){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(timestamp);
    }
}
