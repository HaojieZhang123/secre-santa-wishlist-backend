package com.haojie.secret_santa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haojie.secret_santa.model.entity.Gift;
import com.haojie.secret_santa.repository.GiftRepository;

@Service
public class GiftService {

    @Autowired
    private GiftRepository giftRepository;

    public List<Gift> findAll() {
        return giftRepository.findAll();
    }

    public Optional<Gift> findById(Integer id) {
        return giftRepository.findById(id);
    }

    public Gift getById(Integer id) {
        Optional<Gift> gift = giftRepository.findById(id);
        if (gift.isEmpty()) {
            return null;
        } else {
            return gift.get();
        }
    }

    public Gift store(Gift gift) {
        return giftRepository.save(gift);
    }

    public Gift update(Gift gift) {
        return giftRepository.save(gift);
    }

    public void deleteById(Integer id) {
        giftRepository.deleteById(id);
    }

    public void delete(Gift gift) {
        giftRepository.delete(gift);
    }
}
