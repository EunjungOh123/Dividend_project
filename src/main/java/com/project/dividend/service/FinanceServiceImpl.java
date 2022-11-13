package com.project.dividend.service;

import com.project.dividend.entity.CompanyEntity;
import com.project.dividend.exception.impl.NoCompanyException;
import com.project.dividend.model.Company;
import com.project.dividend.model.Dividend;
import com.project.dividend.model.ScrapedResult;
import com.project.dividend.repository.CompanyRepository;
import com.project.dividend.repository.DividendRepository;
import com.project.dividend.type.CacheKey;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceServiceImpl implements FinanceService{

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Override
    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        log.info("search company -> "+companyName);

        CompanyEntity company = companyRepository.findByName(companyName)
                .orElseThrow(() -> new NoCompanyException());

        List<Dividend> dividendList = dividendRepository.findAllByCompanyId(company.getId())
                .stream().map(e -> new Dividend(e.getDate(), e.getDividend())).collect(Collectors.toList());

        ScrapedResult result = new ScrapedResult(new Company(company.getTicker(), companyName), dividendList);

        return result;
    }
}
