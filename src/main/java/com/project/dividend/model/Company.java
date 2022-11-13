package com.project.dividend.model;

import com.project.dividend.entity.CompanyEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Company {

    private String ticker;
    private String name;

    public static Company fromEntity (CompanyEntity company) {
        return Company.builder()
                .ticker(company.getTicker())
                .name(company.getName())
                .build();
    }
}
