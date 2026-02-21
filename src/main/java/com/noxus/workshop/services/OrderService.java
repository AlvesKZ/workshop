package com.noxus.workshop.services;

import com.noxus.workshop.entities.Order;
import com.noxus.workshop.repositories.OrderRepository;
import com.noxus.workshop.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    public List<Order> findAll() {
        return repository.findAll();
    }

    public Order findById(long id) {
        Optional<Order> obj = repository.findById(id);
        return obj.get();
    }
}
