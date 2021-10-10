package io.learn.restservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubscriptionResponseDto {

    private Long id;
    private Long userId;
    private String startDate;
}
