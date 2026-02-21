package org.ssu.belous.dto.request;

import jakarta.validation.constraints.NotNull;

public record AuthorizeRequestDto(
        @NotNull
        String username,

        @NotNull
        String password) {
}
