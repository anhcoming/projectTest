package com.viettel.hstd.util.validator.constraint;

import com.viettel.hstd.dto.hstd.PositionDescriptionDTO;
import com.viettel.hstd.util.validator.annotation.HasDescriptionNotNullAttachment;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class PositionDescriptionAttachmentValidator implements
        ConstraintValidator<HasDescriptionNotNullAttachment, PositionDescriptionDTO.PositionDescriptionExcelRow> {

    @Override
    public boolean isValid(PositionDescriptionDTO.PositionDescriptionExcelRow row, ConstraintValidatorContext constraintValidatorContext) {
        return !Objects.nonNull(row.getHasDescription()) || (!Objects.isNull(row.getFileAttachments()) && !row.getFileAttachments().isEmpty());
    }
}
