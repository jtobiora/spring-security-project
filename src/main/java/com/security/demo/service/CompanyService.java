package com.security.demo.service;


import com.security.demo.dto.request.CompanyRequest;
import com.security.demo.enums.BusinessCategory;
import com.security.demo.exceptions.AppException;
import com.security.demo.exceptions.DataIntegrityException;
import com.security.demo.exceptions.ErrorDetails;
import com.security.demo.model.Company;
import com.security.demo.repo.CompanyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepo repo;

    public Company saveCompany(Company company){
        return repo.save(company);
    }

    public Company generate(CompanyRequest request) throws AppException,DataIntegrityException {
        if(request == null){
            throw new AppException("Request cannot be empty");
        }

        Company c = getCompanyByRCAndName(request.getRcNumber(),request.getCompanyName());
        if(c != null) {
            if(c.getCompanyName().equalsIgnoreCase(request.getCompanyName())){
                throw new DataIntegrityException(request.getCompanyName());
            }else if(c.getRcNumber().equalsIgnoreCase(request.getRcNumber())){
                throw new DataIntegrityException(request.getRcNumber());
            }
        }
        Company company = new Company();
        company.setCompanyName(request.getCompanyName());
        BusinessCategory businessCategory = BusinessCategory.find(request.getBusinessCategory());
        company.setBusinessCategory(businessCategory);
        company.setRcNumber(request.getRcNumber());

        return saveCompany(company);
    }

    public Company getCompany(Long id){
        return repo.getOne(id);
    }

    public List<Company> getAllCompanies(){
        return repo.findAll();
    }

    public Company getCompanyByRCAndName(String rc, String name){
        return repo.findCompanyByRcNumberAndCompanyName(rc,name);
    }
}
