/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.acme.routes;

import javax.enterprise.context.ApplicationScoped;

import org.acme.domain.Item.Item;
import org.acme.domain.Member;
import org.acme.domain.Order;
import org.acme.domain.Orderform;
import org.acme.routes.beans.OrderProcessingBean;
import org.apache.camel.BeanInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@ApplicationScoped //-> @Component in Spring
public class Routes extends RouteBuilder {

    private static Logger logger = LoggerFactory.getLogger(Routes.class);

    @BeanInject
    private OrderProcessingBean orderProcessingBean;

    @Override
    public void configure() throws Exception {
        // produces messages to kafka
        from("kafka:{{kafka.topic.name}}?brokers=localhost:9092&keyDeserializer=org.apache.kafka.common.serialization.IntegerDeserializer")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        //** exchange header에서 KEY, OFFSET, PARTITION, TIMESTAMP, TOPIC 정보 확인 가능 **//
                        //System.out.a(exchange.getIn().getHeaders());

                        /// ** take the Employee object from the exchange and create the parameter map ** ///
                        Integer empId = exchange.getIn().getHeader("kafka.KEY", Integer.class);
                        String empName = exchange.getIn().getBody(String.class);

                        Map<String, Object> employeeMap = Map.of(
                                "EmpId", empId, "EmpName", empName
                        );

                        exchange.getIn().setBody(employeeMap);
                    }
                })
                .to("sql:INSERT INTO Employee(EmpId, EmpName) VALUES (:#EmpId, :#EmpName)");


        from("kafka:orders?brokers=localhost:9092&valueDeserializer=org.acme.json.OrderformDeserializer")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Message message = exchange.getIn();
                        System.out.println("[message header] -- " + message.getHeaders());
                        System.out.println("[message body] -- " + message.getBody());

                        Orderform orderform = message.getBody(Orderform.class);

                        // Message Transformation
                        Long memberId = orderform.getMemberId();
                        Long itemId = orderform.getItemId();
                        Long orderCount = orderform.getOrderCount();

                        Map<String, Object> orderFormMap = Map.of(
                                "memberId", memberId, "itemId", itemId, "orderCount", orderCount
                        );
                        message.setHeaders(orderFormMap);

                        System.out.println("[message header(changed)] -- " + message.getHeaders());
                        System.out.println("[message body] -- " + message.getBody());
                    }
                })
                // Search Member Info. (주문자 정보를 DB에서 select)
                .toD("jpa://" + Member.class.getName() + "?query=select b from " + Member.class.getName() + " b where b.id = ${header.memberId}")
                .to("bean:orderProcessingBean?method=checkMember")
                // Search Item Info (주문상품 정보를 DB에서 select)
                .toD("jpa://" + Item.class.getName() + "?query=select b from " + Item.class.getName() + " b where b.id = ${header.itemId}")
                .to("bean:orderProcessingBean?method=checkItem")
                // Make Order (주문 회원, 배송정보, 주문상품 정보로 실제 주문 엔티티를 생성)
                .to("bean:orderProcessingBean?method=makeOrder")

                // JPA persist (주문 정보를 DB에 저장, 연관 table들이 같이 update됨)
                .to("jpa://" + Order.class.getName() + "?usePersist=true")
                .choice()
                .when(simple("${body} is 'org.acme.domain.Order'"))
                    .to("jpa://" + Order.class.getName() + "?usePersist=true")
                .otherwise() // Exception이 발생한 경우, message body가 Order type이 아니므로 slack으로 메시지 송신
                    .to("slack:@hslee09?webhookUrl=https://hooks.slack.com/services/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");

                /*
                // JPA persist (주문 정보를 DB에 저장, 연관 table들이 같이 update됨)
                .to("jpa://" + Order.class.getName() + "?usePersist=true")
                .onException(NotEnoughStockException.class)
                    .transform(simple("exception"))
                    .to("slack:@hslee09?webhookUrl=https://hooks.slack.com/services/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");
                */
    }
}
