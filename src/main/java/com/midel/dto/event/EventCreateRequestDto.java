package com.midel.dto.event;

import com.midel.entity.enums.EventVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class EventCreateRequestDto {

    @NotNull(message = "Title must be specified.")
    @NotBlank(message = "Title must be not empty string.")
    @Schema(description = "Title of the event", example = "New Year Party")
    private String title;

    @NotNull(message = "Description must be specified.")
    @Schema(description = "Description of the event", example = "Celebrate the New Year with friends and family")
    private String description;

    @NotNull(message = "Event visibility must be specified.")
    @Schema(description = "Visibility of the event", example = "SPECIFIC_CHAT")
    private EventVisibility visibility;


    @Schema(description = "Expiration date of the event", example = "2024-06-17T01:41:19.200Z")
    @Future(message = "Expiration date must be in the future.")
    private LocalDateTime expirationAt;

    @Schema(description = "Identifier of the allowed chat associated with the event. This field is applicable when the event visibility is set to SPECIFIC_CHAT.", example = "68240812-7a87-4a7e-8d20-17c6dce7fbff")
    private UUID allowedChatId;

}
