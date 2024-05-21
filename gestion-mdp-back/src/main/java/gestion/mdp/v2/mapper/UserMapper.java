package gestion.mdp.v2.mapper;

import gestion.mdp.v2.entity.AppUser;
import gestion.mdp.v2.dto.UserDto;

public record UserMapper() {
    public static UserDto mapToUserDto(AppUser appUser){
        return new UserDto(appUser.getId(), appUser.getEmail(), appUser.getPassword(), appUser.getAppRole(), appUser.getAppManagers());
    }

    public static AppUser mapToUser(UserDto userDto){
        return new AppUser(userDto.getId(), userDto.getEmail(), userDto.getPassword(), userDto.getAppRole(), userDto.getAppManagers());
    }
}
