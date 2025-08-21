package com.challenge.pinapp.util;

import com.challenge.pinapp.entity.Client;

import java.util.List;

public class MetricCalculator {
    private MetricCalculator(){}

    public static Double averageAge(List<Client> clients) {
        return clients.stream().mapToInt(Client::getAge).average().orElse(0.0);
    }

    public static Double varianceOfAge(List<Client> clients, Double averageAgeClients) {
        return clients.stream().mapToDouble(client -> Math.pow(client.getAge() - averageAgeClients, 2))
                .average()
                .orElse(0);
    }
}
