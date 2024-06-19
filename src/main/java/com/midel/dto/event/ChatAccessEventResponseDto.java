package com.midel.dto.event;

import com.midel.dto.chat.ChatResponseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChatAccessEventResponseDto extends EventResponseDto {

    private ChatResponseDto sharedChat;

}
