/*
 * Copyright 2014 Harlan Noonkester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tiogasolutions.lib.joda.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * User: harlan
 * Date: 7/21/12
 * Time: 10:36 PM
 */
public final class JodaMoneyDeserializer extends StdScalarDeserializer<Money> {
    private final CurrencyUnit currency;

    public JodaMoneyDeserializer() {
        super(Money.class);
        this.currency = CurrencyUnit.USD;
    }

    public JodaMoneyDeserializer(CurrencyUnit currency) {
        super(Money.class);
        this.currency = currency;
    }

    @Override
    public Money deserialize(JsonParser jp, DeserializationContext context) throws IOException, JsonProcessingException {
        String amount = jp.readValueAs(String.class);
        return Money.of(currency, new BigDecimal(amount));
    }

}

