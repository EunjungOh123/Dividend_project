package com.project.dividend.dto;

import com.project.dividend.entity.CompanyEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyDto {

    private Long id;

    private String ticker;

    private String name;

    public static CompanyDto fromEntity(CompanyEntity company) {
        return CompanyDto.builder()
                .id(company.getId())
                .ticker(company.getTicker())
                .name(company.getName())
                .build();
    }

}
