package org.acme.kafka.resource;

import org.acme.kafka.EmployeeProducer;
import org.acme.domain.Employee;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EmployeeResource {

    @Inject//-> @Autowired in Spring
    EmployeeProducer producer;

    @POST
    public Response send(Employee employee) {
        producer.sendEmployeeToKafka(employee);
        // Return an 202 - Accepted response.
        return Response.accepted().build();
    }
}