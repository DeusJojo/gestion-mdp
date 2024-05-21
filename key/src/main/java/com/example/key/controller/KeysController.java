package com.example.key.controller;

import com.example.key.dto.KeyDto;
import com.example.key.entity.AppKey;
import com.example.key.service.KeyService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/key")
public class KeysController {
    private KeyService keyService;

    @PostMapping("/create")
    public ResponseEntity<KeyDto> createKey(@RequestBody KeyDto keyDto){
        KeyDto savedAppKey = keyService.createKey(keyDto);
        return new ResponseEntity<>(savedAppKey, HttpStatus.CREATED);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<KeyDto> getKeyById(@RequestBody @PathVariable("id") Long keyId){
        KeyDto savedAppKey = keyService.getKeyById(keyId);
        return new ResponseEntity<>(savedAppKey, HttpStatus.CREATED);
    }

    @GetMapping("/getByManagerId/{id}")
    public ResponseEntity<KeyDto> getKeyByManagerId(@RequestBody @PathVariable("id") Long managerId){
        KeyDto appKey = keyService.getKeyByManagerId(managerId);
        return new ResponseEntity<>(appKey, HttpStatus.CREATED);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteKey(@PathVariable("id") Long managerId){
        KeyDto appKey = keyService.getKeyByManagerId(managerId);
        keyService.deleteKey(appKey.getId());
        Map<String, String> response = new HashMap<>();
        response.put("message", "La clé a été supprimée avec succès.");
        return ResponseEntity.ok(response);
    }
}
