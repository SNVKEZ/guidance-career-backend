package org.ssu.belous.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseDto {
    private ResponseStatus code;
    private String message;
    private LocalDateTime dateTime;
    private boolean showData;

    public static ResponseDtoBuilder success() {
        return builder()
                .code(ResponseStatus.SUCCESS)
                .dateTime(LocalDateTime.now());
    }

    public static ResponseDtoBuilder error() {
        return builder()
                .code(ResponseStatus.ERROR)
                .dateTime(LocalDateTime.now());
    }
}
