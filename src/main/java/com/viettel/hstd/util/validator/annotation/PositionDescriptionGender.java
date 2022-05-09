package com.viettel.hstd.util.validator.annotation;

import com.viettel.hstd.util.validator.constraint.PositionDescriptionGenderValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PositionDescriptionGenderValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PositionDescriptionGender {
    String message() default "Giới tính chỉ nhận các giá trị Nam, Nữ, Nam/ Nữ";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
