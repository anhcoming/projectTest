package com.viettel.hstd.util.validator.constraint;

import com.viettel.hstd.util.validator.annotation.PositionDescriptionGender;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class PositionDescriptionGenderValidator implements ConstraintValidator<PositionDescriptionGender, String> {

    private static final List<String> GENDER_VALUES = Arrays.asList("Nam", "Nữ", "Nam/ Nữ");
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return GENDER_VALUES.contains(s);
    }
}
