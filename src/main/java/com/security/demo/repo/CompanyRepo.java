package com.security.demo.repo;

import com.security.demo.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepo extends JpaRepository<Company,Long>{

    @Query("select c from Company c where c.rcNumber = ?1 or c.companyName = ?2")
    Company findCompanyByRcNumberAndCompanyName(String rcNumber,String name);
}
