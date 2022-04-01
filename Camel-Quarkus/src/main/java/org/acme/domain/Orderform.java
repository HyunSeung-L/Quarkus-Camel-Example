package org.acme.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
public class Orderform {
    private Long memberId;
    private Long itemId;
    private Long orderCount;
}
