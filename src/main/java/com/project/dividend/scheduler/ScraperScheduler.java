package com.project.dividend.scheduler;

import com.project.dividend.entity.CompanyEntity;
import com.project.dividend.entity.DividendEntity;
import com.project.dividend.model.Company;
import com.project.dividend.model.ScrapedResult;
import com.project.dividend.repository.CompanyRepository;
import com.project.dividend.repository.DividendRepository;
import com.project.dividend.scraper.Scraper;
import com.project.dividend.type.CacheKey;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@EnableCaching
@AllArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final Scraper yahooFinanceScraper;
    private final DividendRepository dividendRepository;

    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {

        log.info("scraper scheduler is started!");

        // 저장된 회사 목록 조회
        List<CompanyEntity> companies = companyRepository.findAll();
        // 회사마다 배당금 정보 새로 스크래핑
        for(CompanyEntity company : companies) {
            log.info("scraper scheduler is started! -> "+company.getName());
            ScrapedResult result
            = yahooFinanceScraper.scrap(Company.fromEntity(company));

            // 스크래핑한 배당금 정보 중 데이터베이스에 없는 값은 저장
            result.getDividends().stream().map(e -> new DividendEntity(company.getId(), e))
                    .forEach(e -> {
                        boolean exist = dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if(!exist) {
                            dividendRepository.save(e);
                        }
                    });
            // 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
            try {
                Thread.sleep(3000); // 3 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
