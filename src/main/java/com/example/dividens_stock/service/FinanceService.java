package com.example.dividens_stock.service;

import com.example.dividens_stock.exception.impl.NoCompanyException;
import com.example.dividens_stock.model.Company;
import com.example.dividens_stock.model.Dividend;
import com.example.dividens_stock.model.ScrapedResult;
import com.example.dividens_stock.model.constants.CacheKey;
import com.example.dividens_stock.persist.CompanyRepository;
import com.example.dividens_stock.persist.DividendRepository;
import com.example.dividens_stock.persist.entity.CompanyEntity;
import com.example.dividens_stock.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    // 캐싱이 필요한 데이터 (메소드) 인지 판단하는 기준.
    // 요청이 자주 들어오는가?
    // 자주 변경되는 데이터 인가?
    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {

        // 1. 회사명을 기준으로 회사 정보를 조회.
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new NoCompanyException());
        log.info("조회한 회사 정보: " + company);

        // orElseThrow() = 값이 정상적으로 있다면, Optional이 벗겨인 알맹이 (여기선 CompanyEntity)를 뱉어낸다.
        // 그러므로 여기의 반환값은 Optional일 필요가 없다.

        // 2. 조회된 회사 ID로 배당금 정보를 조회.
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());

        // 3. 결과 조합 후 반환.
//        List<Dividend> dividends = new ArrayList<>();
//        for (var entity : dividendEntities) {
//            dividends.add(Dividend.builder()
//                    .date(entity.getDate())
//                    .dividend(entity.getDividend())
//                    .build());
//        }

        List<Dividend> dividends = dividendEntities.stream()
                .map(e -> new Dividend(e.getDate(), e.getDividend()))
                .collect(Collectors.toList());


        return new ScrapedResult(new Company(company.getTicker(), company.getName()), dividends);
    }
}
