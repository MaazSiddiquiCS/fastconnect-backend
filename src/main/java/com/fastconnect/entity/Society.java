package com.fastconnect.entity;

import com.fastconnect.enums.SocietyCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@Entity
@Table(name = "society")
public class Society {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long society_id;

    @NotBlank(message = "Society name is required")
    @Size(min = 10, max = 100)
    @Column(nullable = false,unique = true,length = 100)
    private String society_name;

    @URL
    @Column(columnDefinition = "TEXT")
    private String logo;

    @URL
    @Column(columnDefinition = "TEXT")
    private String cover_pic;

    @Size(max = 255)
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Enter a category")
    @Enumerated(EnumType.STRING)
    private SocietyCategory category;

    @Column(nullable = false)
    private Boolean verified;

    @PrePersist
    public void prePersist() {
        verified = false;

        if(category == null)
        {
            category = SocietyCategory.OTHERS;
        }
    }
}
