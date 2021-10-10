package io.learn.restservice.converter;

import java.time.LocalDate;

import io.learn.restservice.dto.SubscriptionRequestDto;
import io.learn.restservice.dto.domain.Subscription;
import io.learn.restservice.dto.domain.User;
import io.learn.restservice.exception.UserNotFoundException;
import io.learn.restservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionRequestDtoToSubscriptionConverter implements Converter<SubscriptionRequestDto, Subscription> {

    private final UserRepository userRepository;

    @Autowired
    public SubscriptionRequestDtoToSubscriptionConverter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Subscription convert(SubscriptionRequestDto subscriptionRequestDto) {
        Subscription subscription = new Subscription();
        subscription.setId(subscriptionRequestDto.getId());
        subscription.setStartDate(LocalDate.parse(subscriptionRequestDto.getStartDate()));
        User user = userRepository.findById(subscriptionRequestDto.getUserId())
            .orElseThrow(() ->
                new UserNotFoundException(String.format("User with id '%s' is not found", subscriptionRequestDto.getUserId())));
        subscription.setUser(user);
        return subscription;
    }
}
