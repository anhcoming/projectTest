package com.viettel.hstd.core.utils;

import com.viettel.hstd.service.inf.FileService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class PDFUtilTest {

//    @Test
//    void mergePdf() {
//        String filePath = "../uploads/2021/09/02/224845BM09TCT.pdf";
//        String output = "../uploads/2021/09/02/test.pdf";
//
//        PDFUtil pdfUtil = new PDFUtil();
//        List<String> pathList = new ArrayList<>();
//        pathList.add(filePath);
//        pdfUtil.mergePdf(pathList, output);
//
//        java.io.File file = new File(output);
//        Assertions.assertTrue(file.exists());
//
//    }

    @Test
    @DisplayName("Test disable accent")
    void testDisableAccent() {
        String str = "Hoàng Sa là đảo của Việt Nam";

        Assertions.assertEquals(StringUtils.unAccent(str), "Hoang Sa la dao cua Viet Nam");
    }
}