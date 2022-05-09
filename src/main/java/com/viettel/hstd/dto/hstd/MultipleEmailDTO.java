package com.viettel.hstd.dto.hstd;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;

public class MultipleEmailDTO {
    public ArrayList<String> filePath;
    public Long emailConfigId;
    public Long emailTemplateId;
    public ArrayList<Long> interviewSessionCvId;
    public String content;
}
