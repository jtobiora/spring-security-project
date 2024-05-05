package com.security.demo.controller;

import com.security.demo.dto.request.CompanyRequest;
import com.security.demo.dto.response.ApiResponse;
import com.security.demo.dto.response.UserSummary;
import com.security.demo.exceptions.DataIntegrityException;
import com.security.demo.model.Company;
import com.security.demo.security.CurrentUser;
import com.security.demo.security.UserPrincipal;
import com.security.demo.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/companies")
public class CompanyController {
    @Autowired
    private CompanyService companyService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity createCompany(@RequestBody CompanyRequest request) throws DataIntegrityException{

        Company company = companyService.generate(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{companyId}")
                .buildAndExpand(company.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Company Created Successfully"));
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<Company> getCompanyById(@PathVariable Long companyId,@CurrentUser UserPrincipal currentUser){
        return ResponseEntity.ok(companyService.getCompany(companyId));
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Company>> getAllCompanies(){
        return ResponseEntity.ok(companyService.getAllCompanies());
    }
}


