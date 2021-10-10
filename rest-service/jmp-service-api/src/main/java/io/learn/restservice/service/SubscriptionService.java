package io.learn.restservice.service;

import java.util.List;

import io.learn.restservice.dto.domain.Subscription;

public interface SubscriptionService {

    Subscription createSubscription(Subscription subscription);

    Subscription updateSubscription(Subscription subscription);

    void deleteSubscription(Long id);

    Subscription getSubscription(Long id);

    List<Subscription> getAllSubscriptions();
}
