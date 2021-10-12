package io.learn.restservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRequestDto {

    private Long id;
    private String name;
    private String surname;
    private String birthday;
}
