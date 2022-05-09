package com.viettel.hstd.service.inf;

import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.UploadDTO;
import org.apache.xmlbeans.XmlException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FileService {
    UploadDTO.UploadFileResponse storeFile(MultipartFile file);

    List<UploadDTO.UploadFileResponse> storeFiles(MultipartFile[] files);

    Resource downloadFileWithEncodePath(String fileName);
    List<Resource> downloadMultiFile(List<String> listFiles);
    Boolean removeFile(String fileName);
    Boolean removeMultiFile(List<String> fileName);

    String createFileText(String data);
    String updateFileText(String oldPath, String data);
    String readFileText(String path);

    FileDTO.FileResponse downloadExportFile(String fileName);

    String decodePath(String encodePath);
    String encodePath(String realPath);
    String getFileNameFromEncodePath(String encodePath);
    String getFileNameFromEncodePathWithPrefix(String encodePath);
    String getFileNameFromDecodePath(String decodePath);
    String getFileNameFromDecodePathWithPrefix(String decodePath);
    boolean createFolder(String encodePath);

    Resource downloadFileWithRealPath(String encodePath);

    public String getUploadFolder();
    public String getFilePrefix();

    String createFilePathReal(String fileName);

    FileDTO.FileResponse downloadFileFromVO(HttpServletRequest request, String filePath, String transCode);
}
