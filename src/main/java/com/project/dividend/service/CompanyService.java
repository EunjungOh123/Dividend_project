package com.project.dividend.service;

import com.project.dividend.dto.CompanyDto;
import com.project.dividend.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompanyService {
    Company save (String ticker);

    Page<CompanyDto> getAllCompany (Pageable pageable);

    void addAutocompleteKeyword(String keyword);

    List<String> autocomplete(String keyword);

    void deleteAutocompleteKeyword(String keyword);
    List<String> getCompanyNameByKeyword(String keyword);

    String deleteCompany(String ticker);
}
