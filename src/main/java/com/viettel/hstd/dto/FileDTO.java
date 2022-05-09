package com.viettel.hstd.dto;

import java.util.HashMap;
import java.util.Map;

public class FileDTO {
    public static class FileResponse{
        public String filePath;
        public String encodePath;
        public String fileName;
        public String type;
        public Long size;
        public String data;
        public String downloadFileExtension = ".docx";
        public Map<String, String> additionalDataMap = new HashMap<>();
    }
}
