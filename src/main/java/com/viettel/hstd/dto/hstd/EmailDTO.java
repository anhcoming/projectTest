package com.viettel.hstd.dto.hstd;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

public class EmailDTO {
    @NotNull
    public String content;
    //    @NotNull
//    public Long cvId;
    @NotNull
    @NotBlank
    public String name;
    public String emailSend;
    public ArrayList<String> filePath;
    public Long emailConfigId;
    public Long emailTemplateId;

    public Long interviewSessionCvId;
    public Long employeeId;
}
