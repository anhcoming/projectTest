package com.viettel.hstd.annotation;

import com.viettel.hstd.dto.hstd.ResignSessionDTO.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ResignRequestValidator implements
                ConstraintValidator<RequestAnnotation, ResignSessionRequest> {

    @Override
    public void initialize(RequestAnnotation constraintAnnotation) {

    }

    @Override
    public boolean isValid(ResignSessionRequest value, ConstraintValidatorContext context) {
        // validation logic goes here

        return false;
    }
}