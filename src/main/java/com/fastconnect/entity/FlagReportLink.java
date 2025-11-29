package com.fastconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "flag_report_link"
)
public class FlagReportLink {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "flag_report_link_seq")
    @SequenceGenerator(
            name = "flag_report_link_seq",
            sequenceName = "flag_report_link_sequence",
            allocationSize = 50
    )
    @Column(name = "link_id") 
    private Long linkId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "flag_id",
            nullable = false
    )
    private Flag flag;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;
}