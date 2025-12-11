package com.fastconnect.dto;

import com.fastconnect.enums.SocietyCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocietyResponse {

    private Long societyId;
    private String societyName;
    private String logo;
    private String coverPic;
    private String description;
    private SocietyCategory category;
    private Boolean verified;

    // Add counts for quick view
    private int memberCount;
    private int followerCount;
}