package com.security.demo.model;


import com.security.demo.audits.Auditable;
import com.security.demo.enums.BusinessCategory;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Entity
@Table(name="company")
public class Company extends Auditable implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="rcNumber",unique = true)
    @NotBlank(message = "Rc Number cannot be empty")
    private String rcNumber;

    @Column(name="companyName",unique = true)
    @NotBlank(message = "Company name cannot be empty")
    private String companyName;

    @Enumerated(EnumType.STRING)
    @Column(name="category")
    private BusinessCategory businessCategory;
}
