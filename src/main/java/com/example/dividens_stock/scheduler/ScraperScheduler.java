package com.example.dividens_stock.scheduler;

import com.example.dividens_stock.model.Company;
import com.example.dividens_stock.model.ScrapedResult;
import com.example.dividens_stock.model.constants.CacheKey;
import com.example.dividens_stock.persist.CompanyRepository;
import com.example.dividens_stock.persist.DividendRepository;
import com.example.dividens_stock.persist.entity.CompanyEntity;
import com.example.dividens_stock.persist.entity.DividendEntity;
import com.example.dividens_stock.scraper.Scraper;
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
    private final DividendRepository dividendRepository;

    private final Scraper yahooFinanceScraper;

    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    // 일정 주기마다 수행
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        log.info("scraping scheduler is started");
        // 저장된 회사 목록을 조회
        List<CompanyEntity> companies = this.companyRepository.findAll();

        // 회사마다 배당금 정보를 새로 스크래핑
        for (var company : companies) {
            log.info("scraping scheduler is started -> " + company.getName());
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(
                    new Company(company.getTicker(), company.getName()));


            // 스프래핑한 배당금 정보 중 데이터베이스에 없는 겂은 저장
            scrapedResult.getDividends().stream()
                    // 디비든 모델을 디비든 엔티티로 매핑
                    .map(e -> new DividendEntity(company.getId(), e))
                    // 엘리먼트를 하나씩 디비든 레파지토리에 삽입
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if (!exists) {
                            this.dividendRepository.save(e);
                        }
                    });

            // 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지.
            try {
                Thread.sleep(3000); // 3 seconds
                // thread 5 status
                // New, Ready, Running, Blocked/Waiting, Exit
            } catch (InterruptedException e) {  // 인터럽트를 받는 스레드가 blocking 될 수 있는 메소드를 실행할 때 발생
                Thread.currentThread().interrupt(); // 적절한 스레드 처리.
            }
        }

    }
}
