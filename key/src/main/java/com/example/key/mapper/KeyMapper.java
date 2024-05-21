package com.example.key.mapper;


import com.example.key.dto.KeyDto;
import com.example.key.entity.AppKey;

public record KeyMapper() {
    public static KeyDto mapToKeyDto(AppKey appKey){
        return new KeyDto(appKey.getId(), appKey.getEncryptionKey(), appKey.getManagerId());
    }
    public static AppKey mapToKey(KeyDto keyDto){
        return new AppKey(keyDto.getId(), keyDto.getEncryptionKey(), keyDto.getManagerId());
    }
}
