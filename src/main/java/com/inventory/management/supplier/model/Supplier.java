package com.inventory.management.supplier.model;

import com.inventory.management.common.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "suppliers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class Supplier extends BaseEntity {

    @NotBlank(message = "Company name is required")
    @Column(nullable = false)
    private String companyName;

    @NotBlank(message = "Contact person is required")
    @Column(nullable = false)
    private String contactPerson;

    @NotBlank(message = "Email is required")
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Pattern(regexp = "^[0-9+\\-\\s()]{7,15}$",
            message = "Phone must be 7-15 digits only")
    @Column(length = 10)
    private String phone;


    @Column(length = 300)
    private String address;

    @Column(nullable = false)
    private Boolean active = true;
}