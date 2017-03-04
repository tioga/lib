package org.tiogasolutions.lib.jaxrs.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.exceptions.ApiException;

@Test
public class TiogaExceptionInfoTest {

    public void testTranslation() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        ApiException ex = ApiException.notFound("This really sucks");
        TiogaExceptionInfo oldInfo = new TiogaExceptionInfo(ex);

        Assert.assertEquals(oldInfo.get_response().getCode(), 404);
        Assert.assertEquals(oldInfo.get_response().getMessage(), "Not Found");
    }
}