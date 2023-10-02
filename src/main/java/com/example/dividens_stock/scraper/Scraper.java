package com.example.dividens_stock.scraper;

import com.example.dividens_stock.model.Company;
import com.example.dividens_stock.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}
