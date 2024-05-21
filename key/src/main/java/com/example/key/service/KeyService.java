package com.example.key.service;

import com.example.key.dto.KeyDto;
import com.example.key.entity.AppKey;

import java.util.Optional;

public interface KeyService {
    KeyDto createKey(KeyDto keyDto);
    KeyDto getKeyById(Long id);
    KeyDto getKeyByManagerId(Long id);
    void deleteKey(Long keyId);
}
