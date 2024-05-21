package gestion.mdp.v2.mapper;

import gestion.mdp.v2.dto.RoleDto;
import gestion.mdp.v2.entity.AppRole;

public record RoleMapper() {
    public static RoleDto mapToUserDto(AppRole appRole){
        return new RoleDto(appRole.getId(), appRole.getRoleName());
    }
    public static AppRole mapToUser(RoleDto roleDto){
        return new AppRole(roleDto.getId(), roleDto.getRoleName());
    }
}
