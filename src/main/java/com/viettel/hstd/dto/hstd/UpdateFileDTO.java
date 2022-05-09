package com.viettel.hstd.dto.hstd;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UpdateFileDTO {
    @NotNull
    public Long id;
    @NotNull
    @NotBlank
    public String filePath;
    @NotNull
    public Integer type;
}
