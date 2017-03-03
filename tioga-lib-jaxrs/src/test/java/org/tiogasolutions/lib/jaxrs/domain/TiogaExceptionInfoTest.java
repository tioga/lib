package org.tiogasolutions.lib.jaxrs.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.exceptions.ApiException;

@Test
public class TiogaExceptionInfoTest {

    public void testTranslation() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        Exception ex = ApiException.notFound("This really sucks");
        TiogaExceptionInfo oldInfo = new TiogaExceptionInfo(404, ex);

        Assert.assertEquals(oldInfo.getStatus(), 404);
        Assert.assertEquals(oldInfo.getMessage(), "This really sucks");
    }

}