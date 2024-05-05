package com.security.demo.dto.request;

import com.security.demo.enums.BusinessCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyRequest {
    @NotBlank(message = "Company name cannot be empty")
    private String companyName;
    @NotBlank(message = "Rc Number cannot be empty")
    private String rcNumber;
    private String businessCategory;
}
