package com.project.dividend.service;

import com.project.dividend.model.ScrapedResult;

public interface FinanceService {
    ScrapedResult getDividendByCompanyName(String companyName);
}
