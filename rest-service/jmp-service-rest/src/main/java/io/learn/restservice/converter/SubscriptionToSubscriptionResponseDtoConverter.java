package io.learn.restservice.converter;

import io.learn.restservice.dto.SubscriptionResponseDto;
import io.learn.restservice.dto.domain.Subscription;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionToSubscriptionResponseDtoConverter implements Converter<Subscription, SubscriptionResponseDto> {

    @Override
    public SubscriptionResponseDto convert(Subscription subscription) {
        SubscriptionResponseDto subscriptionResponseDto = new SubscriptionResponseDto();
        subscriptionResponseDto.setId(subscription.getId());
        subscriptionResponseDto.setUserId(subscription.getUser().getId());
        subscriptionResponseDto.setStartDate(subscription.getStartDate().toString());
        return subscriptionResponseDto;
    }
}
