package org.acme.kafka;

import org.acme.domain.Orderform;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped//-> @Component in Spring
public class OrderformProducer {

    @Inject //-> @Autowired in Spring
    @Channel("orderform-out") // messaging channel
    Emitter<Orderform> emitter;

    public void sendOrderformToKafka(Orderform orderForm) {
        emitter.send(orderForm);
    }
}
