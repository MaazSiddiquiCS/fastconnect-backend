package com.fastconnect.dto;

import com.fastconnect.enums.SocietyCategory;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocietyRequest {

    @NotBlank(message = "Society name is required")
    @Size(min = 10, max = 100, message = "Name must be between 10 and 100 characters")
    private String societyName;

    @URL(message = "Logo must be a valid URL")
    private String logo;

    @URL(message = "Cover picture must be a valid URL")
    private String coverPic;

    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    @NotNull(message = "Category is required")
    private SocietyCategory category;
}