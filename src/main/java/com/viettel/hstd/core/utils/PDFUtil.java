package com.viettel.hstd.core.utils;

import com.spire.doc.Document;
import com.viettel.hstd.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class PDFUtil {

    public void mergePdf(List<String> pdfPathList, String destFilePath) {
        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
        pdfPathList.forEach(pdfMergerUtility::addSource);
        pdfMergerUtility.setDestinationFileName(destFilePath);

        try {
            pdfMergerUtility.mergeDocuments();
        } catch (IOException | COSVisitorException e) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            log.error(stackTrace);
        }
    }

    public void mergeDocx(List<String> docxPathList, String destFilePath) {
        if (docxPathList.size() == 0) {
            throw new NotFoundException("Đường dẫn tới file docx để kết hợp rỗng");
        }

        Document document = new Document(docxPathList.get(0));


    }

}
