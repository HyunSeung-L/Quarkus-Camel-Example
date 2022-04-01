package org.acme.kafka;

import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped //-> @Component in Spring
public class EmployeeConsumer {

    private final Logger logger = Logger.getLogger(EmployeeConsumer.class);

    @Incoming("employees-in") // messaging channel
    public void receive(Record<Integer, String> record) {
        logger.infof("Got an employee: %s - %s", record.key(), record.value());
    }
}