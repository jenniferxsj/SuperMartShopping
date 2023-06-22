package com.example.superdupermart.dto.question;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCreationRequest {

    @NotBlank(message = "description cannot be blank")
    String description;
    boolean isActive;
}
