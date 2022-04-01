package org.acme.kafka;

import org.acme.domain.Orderform;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;



@ApplicationScoped
public class OrderformConsumer {

    private final Logger logger = Logger.getLogger(OrderformConsumer.class);

    @Incoming("orderform-in") // messaging channel
    public void receive(Orderform orderform) {
        logger.infof("memberId: %s, itemId: %s", orderform.getMemberId(), orderform.getItemId());
    }
}