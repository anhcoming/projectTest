package com.viettel.hstd.service.imp;

import com.viettel.hstd.service.inf.FileService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

@SpringBootTest
@Transactional
class FileServiceImpTest {

    private String encodePath = "2021-08-27-113251HopDongThuViec.pdf";
    private String decodePath = "../uploads/2021/08/27/113251HopDongThuViec.pdf";
    private String fileName = "HopDongThuViec.pdf";

    @Autowired
    private FileService fileService;

    @Test
    void decodePath() {
        Assertions.assertEquals(fileService.decodePath(encodePath), decodePath);
    }

    @Test
    void encodePath() {
        Assertions.assertEquals(fileService.encodePath(decodePath), encodePath);
    }

    @Test
    void getFileNameFromEncodePath() {
        Assertions.assertEquals(fileService.getFileNameFromEncodePath(encodePath), fileName);
    }

    @Test
    void getFileNameFromDecodePath() {
        Assertions.assertEquals(fileService.getFileNameFromEncodePath(encodePath), fileName);
    }

    @Test
    void getUploadFolder() {
        String uploadFolder = fileService.getUploadFolder();
        Assertions.assertNotNull(uploadFolder);
    }

    @Test
    void getFilePrefix() {
        String filePrefix = fileService.getFilePrefix();
        Assertions.assertNotNull(filePrefix);
    }
}