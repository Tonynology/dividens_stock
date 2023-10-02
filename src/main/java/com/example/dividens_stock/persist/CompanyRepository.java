package com.example.dividens_stock.persist;

import com.example.dividens_stock.persist.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    boolean existsByTicker(String ticker);

    Optional<CompanyEntity> findByName(String name);
    // 바로 CompanyEntity로 받지않고 Optional (Wapping) 으로 받은 이유는
    // 1. nullpoint exception 방지해주는 효과
    // 2. 값이 없는경우에 대한 처리도 좀더 코드적으로 깔끔하게 정리해 주는 장점이 있다.

    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String s, Pageable pageable);

    Optional<CompanyEntity> findByTicker(String ticker);

}
