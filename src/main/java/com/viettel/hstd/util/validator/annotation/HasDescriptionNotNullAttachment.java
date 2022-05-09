package com.viettel.hstd.util.validator.annotation;

import com.viettel.hstd.util.validator.constraint.PositionDescriptionAttachmentValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PositionDescriptionAttachmentValidator.class)
@Target( { ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HasDescriptionNotNullAttachment {
    String message() default "File đính kèm không được để trống khi có MTCV";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
