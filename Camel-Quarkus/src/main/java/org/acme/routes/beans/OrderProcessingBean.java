package org.acme.routes.beans;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.acme.domain.*;
import org.acme.domain.Item.Item;
import org.apache.camel.Message;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Map;

@ApplicationScoped
@Named("orderProcessingBean")
@RegisterForReflection
public class OrderProcessingBean {

    private static Logger logger = LoggerFactory.getLogger(OrderProcessingBean.class);
    private Member member;
    private Delivery delivery;
    private Item item;

    public void checkMember(ArrayList<Member> memberList) {
        if (ObjectUtils.isEmpty(memberList)) {
            logger.error("member가 존재하지 않습니다.");
        } else if (memberList.size() > 1) {
            logger.error("하나의 member_id key에 두 명 이상의 member가 조회되었습니다.");
        } else {
            Member member = memberList.get(0);
            this.member = member;
            this.delivery = new Delivery(member.getAddress(), DeliveryStatus.READY);
        }
    }

    public void checkItem(ArrayList<Item> itemList) {
        if (ObjectUtils.isEmpty(itemList)) {
            logger.error("item이 존재하지 않습니다.");
        } else if (itemList.size() > 1) {
            logger.error("하나의 item_id에 두 개 이상의 item이 조회되었습니다.");
        } else {
            Item item = itemList.get(0);
            this.item = item;
        }
    }

    public void makeOrder(Message message) {
        try {
            Map<String, Object> orderFormMap = message.getHeaders();
            Long orderCount = (Long) orderFormMap.get("orderCount");

            OrderItem orderItem = OrderItem.createOrderItem(this.item, this.item.getPrice(), orderCount.intValue());
            Order order = Order.createOrder(this.member, this.delivery, orderItem);

            message.setBody(order);
        } catch (RuntimeException e) {
            message.setBody("exception");
        }
    }
}
