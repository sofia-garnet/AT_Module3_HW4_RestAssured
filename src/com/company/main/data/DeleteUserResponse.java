package com.company.main.data;

import lombok.*;

@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeleteUserResponse {
    private int code;
    private String type;
    private String message;
}
