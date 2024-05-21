package gestion.mdp.v2.controller;

import gestion.mdp.v2.dto.ManagerDto;
import gestion.mdp.v2.service.ManagerService;
import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/manager")
public class ManagerController {

    private ManagerService managerService;

    @PostMapping("create")
    public ResponseEntity<ManagerDto> createManager(@RequestBody ManagerDto managerDto, Principal principal){
        ManagerDto savedManager = managerService.createManager(managerDto, principal);
        return new ResponseEntity<>(savedManager, HttpStatus.CREATED);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<ManagerDto> getManagerById(@PathVariable("id") Long managerId){
        ManagerDto managerDto = managerService.getManagerById(managerId);
        return ResponseEntity.ok(managerDto);
    }

    @GetMapping("/getByIdDecrypt/{id}")
    public ResponseEntity<ManagerDto> getManagerByIdDecrypt(@PathVariable("id") Long managerId){
        ManagerDto managerDto = managerService.getManagerByIdDecrypt(managerId);
        return ResponseEntity.ok(managerDto);
    }

    @GetMapping("/getByUser")
    public ResponseEntity<List<ManagerDto>> getAllManagerByUserId(Principal principal){
        List<ManagerDto> managerDto = managerService.getManagerByUserId(principal);
        return ResponseEntity.ok(managerDto);
    }

    @GetMapping("/getAll")
    @RolesAllowed("ADMIN")
    public ResponseEntity<List<ManagerDto>> getAllManager(){
        List<ManagerDto> managerDtoList = managerService.getAllManager();
        return ResponseEntity.ok(managerDtoList);
    }

    @PutMapping("/modify/{id}")
    public ResponseEntity<ManagerDto> updateManager(@PathVariable("id") Long managerId,
                                              @RequestBody ManagerDto updateManager){
        ManagerDto managerDto = managerService.updateManager(managerId, updateManager);
        return ResponseEntity.ok(managerDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteManager(@PathVariable("id") Long managerId){
        managerService.deleteManager(managerId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Le mot de passe a été supprimé avec succès.");
        return ResponseEntity.ok(response);
    }
}
