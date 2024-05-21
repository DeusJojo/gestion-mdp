package com.example.key.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KeyDto {
    private Long id;
    private String encryptionKey;
    private Long managerId;
}
