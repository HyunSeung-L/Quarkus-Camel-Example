package org.acme.kafka;

import io.smallrye.reactive.messaging.kafka.Record;
import org.acme.domain.Employee;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped//-> @Component in Spring
public class EmployeeProducer {

    @Inject //-> @Autowired in Spring
    @Channel("employees-out") // messaging channel
    Emitter<Record<Integer, String>> emitter;

    public void sendEmployeeToKafka(Employee employee) {
        emitter.send(Record.of(employee.getEmpId(), employee.getEmpName()));
    }
}

