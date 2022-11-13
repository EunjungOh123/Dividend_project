package com.project.dividend.service;

import com.project.dividend.dto.CompanyDto;
import com.project.dividend.entity.CompanyEntity;
import com.project.dividend.entity.DividendEntity;
import com.project.dividend.exception.impl.NoCompanyException;
import com.project.dividend.model.Company;
import com.project.dividend.model.ScrapedResult;
import com.project.dividend.repository.CompanyRepository;
import com.project.dividend.repository.DividendRepository;
import com.project.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService{

    private final Trie trie;
    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Override
    public Company save(String ticker) {
        boolean result = companyRepository.existsByTicker(ticker);
        if(result) {
            throw new RuntimeException("Already exist ticker -> "+ticker);
        }
        return storeCompanyAndDividend(ticker);
    }

    @Override
    public Page <CompanyDto> getAllCompany(Pageable pageable) {
        Page<CompanyDto> company = companyRepository.findAll(pageable).map(CompanyDto :: fromEntity);
        return company;
    }

    @Override
    public void addAutocompleteKeyword(String keyword) {
        trie.put(keyword, null);
    }

    @Override
    public List<String> autocomplete(String keyword) {
        return (List<String>) trie.prefixMap(keyword).keySet()
                .stream().collect(Collectors.toList());
    }

    @Override
    public void deleteAutocompleteKeyword(String keyword) {
        trie.remove(keyword);
    }

    @Override
    public List<String> getCompanyNameByKeyword(String keyword) {
        Pageable limit = PageRequest.of(0, 10);
        Page<CompanyEntity> companyEntities = companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities
                .stream().map(e -> e.getName()).collect(Collectors.toList());
    }

    @Override
    public String deleteCompany(String ticker) {
        CompanyEntity company = companyRepository.findByTicker(ticker)
                .orElseThrow(()-> new NoCompanyException());
        dividendRepository.deleteAllByCompanyId(company.getId());
        companyRepository.delete(company);

        deleteAutocompleteKeyword(company.getName());

        return company.getName();
    }


    private Company storeCompanyAndDividend(String ticker) {
        Company company = yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if(ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("Failed to scrap ticker -> "+ticker);
        }

        ScrapedResult scrapedResult = yahooFinanceScraper.scrap(company);
        CompanyEntity companyEntity = companyRepository.save(new CompanyEntity(company));

        List<DividendEntity> dividendEntityList = scrapedResult.getDividends().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .collect(Collectors.toList());

        dividendRepository.saveAll(dividendEntityList);

        return company;
    }
}
