package gestion.mdp.v2.service;

import gestion.mdp.v2.dto.ManagerDto;

import java.security.Principal;
import java.util.List;

public interface ManagerService {
    ManagerDto createManager(ManagerDto managerDto, Principal principal);
    ManagerDto getManagerById(Long managerId);
    ManagerDto getManagerByIdDecrypt(Long managerId);
    List<ManagerDto> getAllManager();
    List<ManagerDto> getManagerByUserId(Principal principal);
    ManagerDto updateManager(Long managerId, ManagerDto managerDto);
    void deleteManager(Long managerId);
}
