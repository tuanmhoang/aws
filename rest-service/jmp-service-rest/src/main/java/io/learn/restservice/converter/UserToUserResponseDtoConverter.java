package io.learn.restservice.converter;

import io.learn.restservice.dto.UserResponseDto;
import io.learn.restservice.dto.domain.User;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserResponseDtoConverter implements Converter<User, UserResponseDto> {

    @Override
    public UserResponseDto convert(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        BeanUtils.copyProperties(user, userResponseDto);
        userResponseDto.setBirthday(user.getBirthday().toString());
        return userResponseDto;
    }
}
