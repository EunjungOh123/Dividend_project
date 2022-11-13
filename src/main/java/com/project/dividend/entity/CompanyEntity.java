package com.project.dividend.entity;

import com.project.dividend.model.Company;
import lombok.*;

import javax.persistence.*;

@Entity(name = "COMPANY")
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String ticker;
    private String name;

    public CompanyEntity (Company company) {
        this.ticker = company.getTicker();
        this.name = company.getName();
    }
}
