package com.example.key.repository;

import com.example.key.entity.AppKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeysRepository extends JpaRepository<AppKey, Long> {
    AppKey findByManagerId(Long id);
}
