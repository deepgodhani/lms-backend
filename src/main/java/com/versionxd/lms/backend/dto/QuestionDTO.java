package com.versionxd.lms.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

import java.util.ArrayList;
import java.util.List;


@Data
public class QuestionDTO {
    private Long id;

    @NotBlank(message = "Question text cannot be blank")
    private String text;

    @Valid
    @NotEmpty(message = "A question must have at least one option")
    @Size(min = 2, message = "A question must have at least two options")
    private List<QuestionOptionDTO> options = new ArrayList<>();;
}