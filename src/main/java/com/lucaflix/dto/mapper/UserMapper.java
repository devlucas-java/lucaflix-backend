package com.lucaflix.dto.mapper;

import com.lucaflix.dto.response.user.UserDTO;
import com.lucaflix.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toUserDTO (User user);
}