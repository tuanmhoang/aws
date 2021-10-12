package io.learn.restservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubscriptionRequestDto {

    private Long id;
    private Long userId;
    private String startDate;
}
