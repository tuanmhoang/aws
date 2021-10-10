package io.learn.restservice.service.impl;

import java.util.List;

import io.learn.restservice.dto.domain.Subscription;
import io.learn.restservice.exception.SubscriptionNotFoundException;
import io.learn.restservice.exception.UserNotFoundException;
import io.learn.restservice.repository.SubscriptionRepository;
import io.learn.restservice.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Subscription createSubscription(Subscription subscription) {
        subscription.setId(null);
        return subscriptionRepository.save(subscription);
    }

    @Override
    public Subscription updateSubscription(Subscription subscription) {
        if (subscriptionRepository.existsById(subscription.getId())) {
            return subscriptionRepository.save(subscription);
        } else {
            throw new UserNotFoundException(String.format("Subscription with id '%s' is not found", subscription.getId()));
        }
    }

    @Override
    public void deleteSubscription(Long id) {
        if (subscriptionRepository.existsById(id)) {
            subscriptionRepository.deleteById(id);
        }
    }

    @Override
    public Subscription getSubscription(Long id) {
        return subscriptionRepository.findById(id)
            .orElseThrow(() -> new SubscriptionNotFoundException(String.format("Subscription with id '%s' is not found", id)));
    }

    @Override
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }
}
