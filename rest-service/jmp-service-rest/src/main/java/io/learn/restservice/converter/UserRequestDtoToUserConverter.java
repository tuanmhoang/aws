package io.learn.restservice.converter;

import java.time.LocalDate;

import io.learn.restservice.dto.UserRequestDto;
import io.learn.restservice.dto.domain.User;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserRequestDtoToUserConverter implements Converter<UserRequestDto, User> {

    @Override
    public User convert(UserRequestDto userRequestDto) {
        User user = new User();
        BeanUtils.copyProperties(userRequestDto, user);
        user.setBirthday(LocalDate.parse(userRequestDto.getBirthday()));
        return user;
    }
}
