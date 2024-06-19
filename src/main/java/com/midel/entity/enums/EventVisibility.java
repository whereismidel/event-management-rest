package com.midel.entity.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Visibility of the event")
public enum EventVisibility {
    @Schema(description = "Visible to everyone")
    PUBLIC,

    @Schema(description = "Visible to friends only")
    FRIENDS_ONLY,

    @Schema(description = "Visible to selected individuals")
    SELECTED_INDIVIDUALS,

    @Schema(description = "Visible to specific people in a specific chat")
    SPECIFIC_CHAT
}
