/*
 * Copyright 2013 FasterXML.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */

package tools.jackson.databind.ext.javatime.deser;

import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.ext.javatime.DateTimeTestBase;
import tools.jackson.databind.ext.javatime.MockObjectConfiguration;
import tools.jackson.databind.type.LogicalType;

import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PeriodDeserTest extends DateTimeTestBase
{
    private final ObjectMapper MAPPER = newMapper();
    private final TypeReference<Map<String, Period>> MAP_TYPE_REF = new TypeReference<Map<String, Period>>() { };

    @Test
    public void testDeserialization01() throws Exception
    {
        Period period = Period.of(1, 6, 15);
        Period value = MAPPER.readValue(VPackUtils.toVPack('"' + period.toString() + '"'), Period.class);
        assertEquals(period, value, "The value is not correct.");
    }

    @Test
    public void testDeserialization02() throws Exception
    {
        Period period = Period.of(0, 0, 21);
        Period value = MAPPER.readValue(VPackUtils.toVPack('"' + period.toString() + '"'), Period.class);
        assertEquals(period, value, "The value is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        Period period = Period.of(5, 1, 12);

        final ObjectMapper mapper = mapperBuilder()
                .addMixIn(TemporalAmount.class, MockObjectConfiguration.class)
                .build();
        TemporalAmount value = mapper.readValue(
                VPackUtils.toVPack("[\"" + Period.class.getName() + "\",\"" + period.toString() + "\"]"), TemporalAmount.class
                );

        assertNotNull(value, "The value should not be null.");
        assertInstanceOf(Period.class, value, "The value should be a Period.");
        assertEquals(period, value, "The value is not correct.");
    }

       /*
    /**********************************************************
    /* Tests for empty string handling
    /**********************************************************
     */

    @Test
    public void testLenientDeserializeFromEmptyString() throws Exception {

        String key = "period";
        ObjectMapper mapper = newMapper();
        ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String valueFromNullStr = VPackUtils.toJson(mapper.writeValueAsBytes(asMap(key, null)));
        Map<String, Period> actualMapFromNullStr = objectReader.readValue(VPackUtils.toVPack(valueFromNullStr));
        Period actualDateFromNullStr = actualMapFromNullStr.get(key);
        assertNull(actualDateFromNullStr);

        String valueFromEmptyStr = VPackUtils.toJson(mapper.writeValueAsBytes(asMap(key, "")));
        Map<String, Period> actualMapFromEmptyStr = objectReader.readValue(VPackUtils.toVPack(valueFromEmptyStr));
        Period actualDateFromEmptyStr = actualMapFromEmptyStr.get(key);
        assertEquals(null, actualDateFromEmptyStr, "empty string failed to deserialize to null with lenient setting");
    }

    @Test
    public void testStrictDeserializeFromEmptyString() throws Exception {

        final String key = "period";
        final ObjectMapper mapper = mapperBuilder()
                .withCoercionConfig(LogicalType.DateTime,
                        cfg -> cfg.setCoercion(CoercionInputShape.EmptyString, CoercionAction.Fail))
                .build();
        final ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String valueFromNullStr = VPackUtils.toJson(mapper.writeValueAsBytes(asMap(key, null)));
        Map<String, Period> actualMapFromNullStr = objectReader.readValue(VPackUtils.toVPack(valueFromNullStr));
        assertNull(actualMapFromNullStr.get(key));

        String valueFromEmptyStr = VPackUtils.toJson(mapper.writeValueAsBytes(asMap("date", "")));
        try {
            objectReader.readValue(VPackUtils.toVPack(valueFromEmptyStr));
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot coerce empty String");
        }
    }
}
