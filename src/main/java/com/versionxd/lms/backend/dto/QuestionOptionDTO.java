package com.versionxd.lms.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionOptionDTO {
    private Long id;

    @NotBlank(message = "Option text cannot be blank")
    private String text;

    @JsonProperty("isCorrect")
    private boolean isCorrect;
}