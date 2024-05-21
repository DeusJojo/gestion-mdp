package com.example.key.service.impl;

import com.example.key.dto.KeyDto;
import com.example.key.entity.AppKey;
import com.example.key.exception.RessourceNotFoundException;
import com.example.key.mapper.KeyMapper;
import com.example.key.repository.KeysRepository;
import com.example.key.service.KeyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KeyServiceImpl implements KeyService {
    private KeysRepository keysRepository;
    @Override
    public KeyDto createKey(KeyDto keyDto) {
        AppKey appKey = KeyMapper.mapToKey(keyDto);
        AppKey savedAppKey = keysRepository.save(appKey);
        return KeyMapper.mapToKeyDto(savedAppKey);
    }

    @Override
    public KeyDto getKeyById(Long id) {
        AppKey appKey = keysRepository.findById(id)
                .orElseThrow(() ->
                        new RessourceNotFoundException(("La clé avec l'id " + id + " n'existe pas.")));
        return KeyMapper.mapToKeyDto(appKey);
    }

    @Override
    public KeyDto getKeyByManagerId(Long id) {
        AppKey appKey = keysRepository.findByManagerId(id);
        return KeyMapper.mapToKeyDto(appKey);
    }

    @Override
    public void deleteKey(Long keyId) {
        keysRepository.deleteById(keyId);
    }
}
