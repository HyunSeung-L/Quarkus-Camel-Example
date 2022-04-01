package org.acme.json;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import org.acme.domain.Orderform;

public class OrderformDeserializer extends ObjectMapperDeserializer<Orderform> {
    public OrderformDeserializer() {
        super(Orderform.class);
    }
}
