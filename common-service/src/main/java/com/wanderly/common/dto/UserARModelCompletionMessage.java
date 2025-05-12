package com.wanderly.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserARModelCompletionMessage {
    private UUID userId;
    private UUID modelId;
    private String cityName;
}
