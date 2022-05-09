package com.viettel.hstd.controller;

import com.itextpdf.text.DocumentException;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.UploadDTO;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.FileService;
import com.viettel.hstd.util.VOConfig;
import com.viettel.voffice.ws_autosign.service.FileAttachTranfer;
import com.viettel.voffice.ws_autosign.service.FileAttachTranferList;
import com.viettel.voffice.ws_autosign.service.Vo2AutoSignSystemImpl;
import com.viettel.voffice.ws_autosign.service.Vo2AutoSignSystemImplService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Parameter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
public class FileController {
    @Autowired
    FileService fileService;

    @Autowired
    Message message;
    @Autowired
    private VOConfig voConfig;

    @PostMapping(path = "/upload-file")
    public BaseResponse<List<UploadDTO.UploadFileResponse>> uploadFile(@RequestParam(value = "files") MultipartFile[] files) {
        if (files == null || files.length == 0) throw new BadRequestException(message.getMessage("file.invalid"));
        List<UploadDTO.UploadFileResponse> urlFiles = fileService.storeFiles(files);
        return new BaseResponse.ResponseBuilder<List<UploadDTO.UploadFileResponse>>().success(urlFiles);
    }

    @GetMapping("/download-file")
    public BaseResponse<FileDTO.FileResponse> downloadFile(@Parameter(hidden = true) @AuthenticationPrincipal SSoResponse SSoResponse, @RequestParam String path, HttpServletRequest request) throws IOException, DocumentException {
        Resource resource;

        resource = fileService.downloadFileWithEncodePath(path);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            System.out.println("Could not determine file type.");
            ;
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        byte[] data = Base64.getEncoder().encode(Files.readAllBytes(resource.getFile().toPath()));
        FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
        fileDTO.filePath = path;
        fileDTO.encodePath = path;
        fileDTO.fileName = path;
        fileDTO.size = resource.contentLength();
        fileDTO.type = contentType;
        fileDTO.data = new String(data, StandardCharsets.US_ASCII);
        return new BaseResponse.ResponseBuilder<FileDTO.FileResponse>().success(fileDTO);
    }

    @PostMapping("/download-multifile")
    public BaseResponse<List<FileDTO.FileResponse>> downloadMultiFile(@RequestBody List<String> listPath, HttpServletRequest request) throws IOException, DocumentException {
        List<FileDTO.FileResponse> fileResponseList = new ArrayList<>();

        listPath.forEach(path -> {
            Resource resource;

            resource = fileService.downloadFileWithEncodePath(path);

            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                System.out.println("Could not determine file type.");
                ;
            }
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            byte[] data = new byte[0];
            try {
                data = Base64.getEncoder().encode(Files.readAllBytes(resource.getFile().toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
            fileDTO.fileName = path;
            try {
                fileDTO.size = resource.contentLength();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileDTO.type = contentType;
            fileDTO.data = new String(data, StandardCharsets.US_ASCII);
            fileResponseList.add(fileDTO);

        });

        return new BaseResponse.ResponseBuilder<List<FileDTO.FileResponse>>().success(fileResponseList);
    }

    @GetMapping("/download")
    public ResponseEntity<?> download(@Parameter(hidden = true) @AuthenticationPrincipal SSoResponse SSoResponse, @RequestParam String path, HttpServletRequest request) throws IOException, DocumentException {
        Resource resource;

        resource = fileService.downloadFileWithEncodePath(path);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            System.out.println("Could not determine file type.");
            ;
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
    }

    @DeleteMapping("/remove")
    public BaseResponse<Boolean> remove(@RequestParam String path) {

        return new BaseResponse.ResponseBuilder<Boolean>().success(fileService.removeFile(path));
    }

    @PostMapping("/remove-multi")
    public BaseResponse<Boolean> removeMulti(@RequestBody List<String> listPath) {

        return new BaseResponse.ResponseBuilder<Boolean>().success(fileService.removeMultiFile(listPath));
    }

    @GetMapping("/download-export-file")
    public BaseResponse<FileDTO.FileResponse> downloadExportFile(@RequestParam String path) {
        return new BaseResponse.ResponseBuilder<FileDTO.FileResponse>().success(fileService.downloadExportFile(path));
    }

    @GetMapping("/download-file-vo")
    public BaseResponse<FileDTO.FileResponse> downloadFileFromVo(@Parameter(hidden = true) @AuthenticationPrincipal @RequestParam String transCode, @RequestParam String path, HttpServletRequest request) {
        return new BaseResponse.ResponseBuilder<FileDTO.FileResponse>().success(fileService.downloadFileFromVO(request, path, transCode));
    }

}
