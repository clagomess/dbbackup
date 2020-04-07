package br.dbbackup.core;

import lombok.extern.slf4j.Slf4j;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
public class ResourceTest {
    @Test
    public void getFromJavaResources() throws Throwable {
        String result = Resource.getString("sql/h2_info.sql");
        Assert.assertThat(result, CoreMatchers.containsString("INFORMATION_SCHEMA.CONSTRAINTS"));
    }

    @Test
    public void getFromTestResources() throws Throwable {
        String result = Resource.getString("samples/sample_002.sql");
        Assert.assertThat(result, CoreMatchers.containsString("tbl_dbbackup"));
    }
}
