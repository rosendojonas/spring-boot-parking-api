package com.jonasrosendo.demoparkingapi.web.mappers;

import com.jonasrosendo.demoparkingapi.entities.User;
import com.jonasrosendo.demoparkingapi.web.dtos.user.UserCreateDTO;
import com.jonasrosendo.demoparkingapi.web.vos.user.UserResponseVO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User toUser(UserCreateDTO userCreateDto) {
        User user = new User();
        user.setUsername(userCreateDto.getUsername());
        user.setPassword(userCreateDto.getPassword());
        return user;
    }

    public static UserResponseVO toUserResponseVO(User user) {
        UserResponseVO userResponseVO = new UserResponseVO();
        userResponseVO.setId(user.getId());
        userResponseVO.setUsername(user.getUsername());
        userResponseVO.setRole(user.getRole().name().substring("ROLE_".length()));
        return userResponseVO;
    }

    public static List<UserResponseVO> toUserResponseVOList(List<User> users) {
        return users.stream().map(user -> toUserResponseVO(user)).toList();
    }
}
