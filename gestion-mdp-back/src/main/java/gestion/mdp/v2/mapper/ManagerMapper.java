package gestion.mdp.v2.mapper;

import gestion.mdp.v2.dto.ManagerDto;
import gestion.mdp.v2.entity.AppManager;

public record ManagerMapper() {
    public static ManagerDto mapToManagerDto(AppManager appManager){
        return new ManagerDto(appManager.getId(), appManager.getUsernameApp(), appManager.getPasswordApp(), appManager.getAppUser());
    }

    public static AppManager mapToManager(ManagerDto managerDto){
        return new AppManager(managerDto.getId(), managerDto.getUsernameApp(), managerDto.getPasswordApp(), managerDto.getAppUser());
    }
}
