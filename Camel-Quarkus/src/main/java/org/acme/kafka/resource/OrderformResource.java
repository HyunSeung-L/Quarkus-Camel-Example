package org.acme.kafka.resource;

import org.acme.domain.Orderform;
import org.acme.kafka.OrderformProducer;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/order")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrderformResource {

    @Inject//-> @Autowired in Spring
    OrderformProducer producer;

    @POST
    public Response send(Orderform orderform) {
        producer.sendOrderformToKafka(orderform);
        // Return an 202 - Accepted response.
        return Response.accepted().build();
    }
}