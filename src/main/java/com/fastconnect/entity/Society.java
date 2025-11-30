package com.fastconnect.entity;

import com.fastconnect.enums.SocietyCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "society")
public class Society {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "society_seq")
    @SequenceGenerator(
            name = "society_seq",
            sequenceName = "society_sequence",
            allocationSize = 50
    )
    @Column(name = "society_id") 
    private Long societyId;

    @NotBlank(message = "Society name is required")
    @Size(min = 10, max = 100)
    @Column(name = "society_name", nullable = false,unique = true,length = 100) 
    private String societyName;

    @URL
    @Column(columnDefinition = "TEXT")
    private String logo;

    @URL
    @Column(name = "cover_pic", columnDefinition = "TEXT") 
    private String coverPic;

    @Size(max = 500)
    @Column(columnDefinition = "TEXT", length = 500)
    private String description;

    @NotNull(message = "Enter a category")
    @Enumerated(EnumType.STRING)
    private SocietyCategory category;

    @Column(nullable = false)
    private Boolean verified;

    @OneToMany(mappedBy = "society", cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<SocietyMembership> societyMemberships ;

    @OneToMany(mappedBy = "society", cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<SocietyFollowers> followers;

    @PrePersist
    public void prePersist() {
        this.verified = false;

        if(this.category == null)
        {
            this.category = SocietyCategory.OTHERS;
        }
    }
}