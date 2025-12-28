package com.mariosmant.webapp.mediahub.forms.service.domain.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.mariosmant.webapp.mediahub.common.spring.validation.annotations.ValidCountryPostalCode;
import com.mariosmant.webapp.mediahub.common.spring.validation.contract.PostalCodeValidationData;
import com.mariosmant.webapp.mediahub.common.spring.validation.annotations.CountryCode;
import com.mariosmant.webapp.mediahub.common.spring.validation.annotations.Money;
import com.mariosmant.webapp.mediahub.common.spring.validation.annotations.NoHtml;
import com.mariosmant.webapp.mediahub.common.utils.BigDecimalUtils;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@ValidCountryPostalCode
public class FormSubmitRequest implements PostalCodeValidationData {
    // Person related.
    @NotBlank @Size(max = 64) @NoHtml
    private String username;

    @NotBlank @Email @Size(max = 190) @NoHtml
    private String email;

    @NotBlank @Size(max = 80)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Invalid name")
    @NoHtml
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 2)
    @NoHtml
    @CountryCode
    private String country;


    @NotBlank @Size(max = 80)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Invalid name")
    @NoHtml
    private String lastName;

    @NotBlank @Size(max = 120) @NoHtml
    private String address;

    // E.164 standard.
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
    @Size(max = 20)
    @NoHtml
    private String phone;

    // E.164 standard.
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid mobile phone number")
    @Size(max = 20)
    @NoHtml
    private String mobilePhone;

    // E.164 standard.
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid fax number")
    @Size(max = 20)
    @NoHtml
    private String fax;

    @NotBlank @Size(max = 80) @NoHtml
    private String postalCode;

    @NotNull @Money
    private BigDecimal payAmount;
    @NotNull @PastOrPresent
    private LocalDateTime paymentDate;
    @NotNull @PositiveOrZero
    private Integer numberOfClaims;

    @NotNull @PositiveOrZero
    private JsonNode formData;

    private boolean enabled;

    // Optional: enforce scale automatically
    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = BigDecimalUtils.scale(payAmount, 2, RoundingMode.HALF_UP);
    }


    @Override
    public String postalCodeValidationCountry() {
        return getCountry();
    }

    @Override
    public String postalCodeValidationPostalCode() {
        return getPostalCode();
    }



}
