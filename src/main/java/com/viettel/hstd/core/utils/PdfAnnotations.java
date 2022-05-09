package com.viettel.hstd.core.utils;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.viettel.hstd.constant.FileTemplateConstant;
import com.viettel.hstd.constant.HSDTConstant;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.util.DateUtils;
import com.viettel.hstd.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;

import static com.viettel.hstd.constant.HSDTConstant.*;

@Slf4j
public class PdfAnnotations {
    private static int currentPage;

    public static class TextPositionAndPageNum {
        TextPosition textPosition;
        int pageNum = 1;
    }

    public static TextPosition findPosition(String pdfFile, final String key) throws IOException {
        currentPage = 0;
        List<TextPosition> findPositions = findPositions(pdfFile, key);
        if (findPositions.size() > 0) {
            return findPositions.get(0);
        }
        return null;
    }

    public static List<TextPosition> findPositions(String pdfFile, final String key) throws IOException {
        PDDocument document = PDDocument.load(pdfFile);
        final StringBuffer extractedText = new StringBuffer();
        final List<TextPosition> positions = new ArrayList<>();
        PDFTextStripper textStripper = new PDFTextStripper() {
            @Override
            protected void processTextPosition(TextPosition text) {
                extractedText.append(text.getCharacter());
                if (extractedText.toString().endsWith(key)) {
                    positions.add(text);
                }
            }
        };
        List lstPage = document.getDocumentCatalog().getAllPages();
        for (int pageNum = 0; pageNum < document.getDocumentCatalog().getAllPages().size(); pageNum++) {
            PDPage page = (PDPage) lstPage.get(pageNum);
            textStripper.processStream(page, page.findResources(), page.getContents().getStream());
            if (currentPage == 0 && positions.size() > 0) {
                currentPage = pageNum + 1;
            }
            extractedText.setLength(0);
        }
        document.close();
        return Collections.unmodifiableList(positions);
    }


    public static TextPosition findPosition(String pdfFile, final String key, String name) throws IOException {
        List<TextPosition> findPositions = findPositions(pdfFile, key);
        if (findPositions.size() > 0) {
            return (name.equals("collaborator") || name.equalsIgnoreCase("freelance")
                    || name.equalsIgnoreCase("serviceContract")
                    || name.equalsIgnoreCase("NoSeveranceAllowance")) ? findPositions.get(findPositions.size() - 1) : findPositions.get(0);
        }
        return null;
    }


    public static void addNoteIntoFile(File file, String fileName)
            throws Exception {
        if (file != null) {
            PdfReader reader = null;
            PdfStamper stamper = null;
            String inputFile = file.getPath();
            String outputFile = inputFile + "_temp";
            try {
                // reader = new PdfReader(file.getAbsolutePath());
                reader = new PdfReader(inputFile);
                stamper = new PdfStamper(reader, new FileOutputStream(
                        outputFile));
                int pageNum = reader.getNumberOfPages();
                float pageHeight = reader
                        .getPageSize(reader.getNumberOfPages()).getHeight();
                PdfDictionary page;
                page = reader.getPageN(pageNum);
                if (page != null) {
                    if (fileName.contains("signature")) {
                        PdfContentByte under = stamper.getUnderContent(pageNum);
                        Rectangle rect = null;
                        TextPosition pDvpd = findPosition(inputFile, "PHÒNG TỔ CHỨC LAO ĐỘNG");
                        if (pDvpd != null) {
                            rect = new Rectangle(pDvpd.getX() - 40, pDvpd.getY(), pDvpd.getX() - 20, pDvpd.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "1");
                        }

                        TextPosition txt = findPosition(inputFile, "TỔNG CÔNG TY VCC");
                        if (txt != null) {
                            rect = new Rectangle(txt.getX() - 5, txt.getY(), txt.getX() - 30, txt.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "2");
                        }
                    } else if (fileName.contains("collaborator")) {
                        PdfContentByte under = stamper.getUnderContent(pageNum);
                        Rectangle rect = null;
                        TextPosition pDvpd = findPosition(inputFile, "BÊN A", fileName);
                        if (pDvpd != null) {
                            rect = new Rectangle(pDvpd.getX() - 20, pDvpd.getY(), pDvpd.getX() - 20, pDvpd.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "1");
                        }

                        TextPosition txt = findPosition(inputFile, "BÊN B", fileName);
                        if (txt != null) {
                            rect = new Rectangle(txt.getX() - 30, txt.getY(), txt.getX() - 30, txt.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "2");
                        }
                    } else if (fileName.contains("freelance")) {
                        PdfContentByte under = stamper.getUnderContent(pageNum);
                        Rectangle rect = null;
                        TextPosition pDvpd = findPosition(inputFile, "BÊN GIAO KHOÁN", "freelance");
                        if (pDvpd != null) {
                            rect = new Rectangle(pDvpd.getX() - 5, pDvpd.getY(), pDvpd.getX() - 5, pDvpd.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "1");
                        }

                        TextPosition txt = findPosition(inputFile, "BÊN NHẬN KHOÁN", "freelance");
                        if (txt != null) {
                            rect = new Rectangle(txt.getX() - 5, txt.getY(), txt.getX() - 5, txt.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "2");
                        }
                    } else if (fileName.contains("serviceContract_PL01")) {
                        PdfContentByte under = stamper.getUnderContent(pageNum);
                        Rectangle rect = null;
                        TextPosition pDvpd = findPosition(inputFile, "BÊN B");
                        if (pDvpd != null) {
                            rect = new Rectangle(pDvpd.getX() + 10, pDvpd.getY(), pDvpd.getX() + 10, pDvpd.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "3");
                        }

                        TextPosition txt = findPosition(inputFile, "BÊN A");
                        if (txt != null) {
                            rect = new Rectangle(txt.getX() - 30, txt.getY(), txt.getX() - 30, txt.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "4");
                        }
                    } else if (fileName.contains("serviceContract_PL02")) {
                        PdfContentByte under = stamper.getUnderContent(pageNum);
                        Rectangle rect = null;
                        TextPosition pDvpd = findPosition(inputFile, "BÊN GIAO KHOÁN");
                        if (pDvpd != null) {
                            rect = new Rectangle(pDvpd.getX() - 20, pDvpd.getY(), pDvpd.getX() - 20, pDvpd.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "5");
                        }

                        TextPosition txt = findPosition(inputFile, "BÊN NHẬN KHOÁN");
                        if (txt != null) {
                            rect = new Rectangle(txt.getX() - 30, txt.getY(), txt.getX() - 30, txt.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "6");
                        }
                    } else if (fileName.equalsIgnoreCase("serviceContract")) {
                        PdfContentByte under = stamper.getUnderContent(pageNum);
                        Rectangle rect = null;
                        TextPosition pDvpd = findPosition(inputFile, "BÊN B", fileName);
                        if (pDvpd != null) {
                            rect = new Rectangle(pDvpd.getX() + 25, pDvpd.getY(), pDvpd.getX() + 25, pDvpd.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "1");
                        }

                        TextPosition txt = findPosition(inputFile, "BÊN A", fileName);
                        if (txt != null) {
                            rect = new Rectangle(txt.getX() + 30, txt.getY(), txt.getX() + 30, txt.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "2");
                        }
                    } else if (fileName.contains("debtOfUnit")) {
                        PdfContentByte under = stamper.getUnderContent(pageNum);
                        Rectangle rect = null;
                        TextPosition pDvpd = findPosition(inputFile, "GIÁM ĐỐC ĐƠN VỊ");
                        if (pDvpd != null) {
                            rect = new Rectangle(pDvpd.getX() - 20, pDvpd.getY(), pDvpd.getX() - 20, pDvpd.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "5");
                        }

                        pDvpd = findPosition(inputFile, "TỔ CHỨC LAO ĐỘNG");
                        if (pDvpd != null) {
                            rect = new Rectangle(pDvpd.getX() - 20, pDvpd.getY(), pDvpd.getX() - 30, pDvpd.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "6");
                        }
                        //<editor-fold desc="Ky xac nhan">
                        pDvpd = findPosition(inputFile, "Ký xác nhận");
                        if (pDvpd != null) {
                            Rectangle nRect;
                            rect = new Rectangle(pDvpd.getX() + 5, pDvpd.getY(), pDvpd.getX() + 30, pDvpd.getY() - 10);
                            nRect = new Rectangle(rect.getLeft() - 20, pageHeight - rect.getTop() - 45,
                                    rect.getLeft() - 41, pageHeight - rect.getTop() - 25);
                            insertAnnotaion(stamper, under, nRect, "1");

                            rect = new Rectangle(pDvpd.getX() + 25, pDvpd.getY(), pDvpd.getX() + 30, pDvpd.getY() + 55);
                            nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "2");

                            rect = new Rectangle(pDvpd.getX() + 25, pDvpd.getY(), pDvpd.getX() + 30, pDvpd.getY() + 125);
                            nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "3");

                            rect = new Rectangle(pDvpd.getX() + 25, pDvpd.getY(), pDvpd.getX() + 30, pDvpd.getY() + 165);
                            nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "4");
                        }
                        //</editor-fold>
                    } else if (fileName.contains("debtOfCompany")) {
                        PdfContentByte pageFirst = stamper.getUnderContent(1);
                        PdfContentByte pageSecond = stamper.getUnderContent(pageNum);
                        Rectangle rect = null;
                        TextPosition pDvpd = findPosition(inputFile, "CHỈ HUY ĐƠN VỊ");
                        if (pDvpd != null) {
                            rect = new Rectangle(pDvpd.getX() + 5, pDvpd.getY(), pDvpd.getX() - 20, pDvpd.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, pageSecond, nRect, "5");
                        }

                        pDvpd = findPosition(inputFile, "TỔ CHỨC LAO ĐỘNG");
                        if (pDvpd != null) {
                            rect = new Rectangle(pDvpd.getX() - 20, pDvpd.getY(), pDvpd.getX() - 30, pDvpd.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, pageSecond, nRect, "6");
                        }
                        //<editor-fold desc="Ky xac nhan">
                        pDvpd = findPosition(inputFile, "Ký xác nhận");
                        if (pDvpd != null) {
                            Rectangle nRect;
                            rect = new Rectangle(pDvpd.getX() + 5, pDvpd.getY(), pDvpd.getX() + 30, pDvpd.getY() + 10);
                            nRect = new Rectangle(rect.getLeft() - 20, pageHeight - rect.getTop() - 45,
                                    rect.getLeft() - 41, pageHeight - rect.getTop() - 25);
                            insertAnnotaion(stamper, pageFirst, nRect, "1");

                            rect = new Rectangle(pDvpd.getX() + 25, pDvpd.getY(), pDvpd.getX() + 30, pDvpd.getY() + 150);
                            nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, pageFirst, nRect, "2");

                            rect = new Rectangle(pDvpd.getX() + 25, pDvpd.getY(), pDvpd.getX() + 30, pDvpd.getY() + 300);
                            nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, pageFirst, nRect, "3");

                            rect = new Rectangle(pDvpd.getX() + 5, pDvpd.getY(), pDvpd.getX() + 30, pDvpd.getY() - 120);
                            nRect = new Rectangle(rect.getLeft() - 20, pageHeight - rect.getTop() - 45,
                                    rect.getLeft() - 41, pageHeight - rect.getTop() - 25);
                            insertAnnotaion(stamper, pageSecond, nRect, "4");
                        }
                        //</editor-fold>
                    } else if (fileName.equalsIgnoreCase(SIGNAL_BM09)) {
                        PdfContentByte under = stamper.getUnderContent(pageNum);
                        Rectangle rect = null;
                        TextPosition pDvpd = findPosition(inputFile, "NGƯỜI LẬP", fileName);
                        if (pDvpd != null) {
                            rect = new Rectangle(pDvpd.getX() - 5, pDvpd.getY(), pDvpd.getX() + 25, pDvpd.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "1");
                        }

                        TextPosition txt = findPosition(inputFile, "CHỈ HUY ĐƠN VỊ", fileName);
                        if (txt != null) {
                            rect = new Rectangle(txt.getX() + 5, txt.getY(), txt.getX() + 30, txt.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "2");
                        }
                    } else if (fileName.equalsIgnoreCase(SIGNAL_BM09_TCT)) {
                        PdfContentByte under = stamper.getUnderContent(pageNum);
                        PdfContentByte under1 = stamper.getUnderContent(1);

                        Rectangle rect = null;
                        TextPosition ptgd = findPosition(inputFile, "PHÓ TỔNG GIÁM ĐỐC", fileName);
                        if (ptgd != null) {
                            rect = new Rectangle(ptgd.getX() + 5, ptgd.getY(), ptgd.getX() + 30, ptgd.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "2");
                        }

                        TextPosition ptcld = findPosition(inputFile, "P.TỔ CHỨC LAO", fileName);
                        if (ptcld != null) {
                            rect = new Rectangle(ptcld.getX() + 5, ptcld.getY(), ptcld.getX() + 30, ptcld.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "3");
                        }

                        TextPosition tptcld = findPosition(inputFile, "TP.TỔ CHỨC NHÂN SỰ", fileName);
                        if (tptcld != null) {
                            rect = new Rectangle(tptcld.getX() + 5, tptcld.getY(), tptcld.getX() + 30, tptcld.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "3");
                        }
                    } else if (fileName.equalsIgnoreCase(SIGNAL_BM03)) {
                        PdfContentByte under = stamper.getUnderContent(pageNum);
                        Rectangle rect = null;
                        TextPosition pDvpd = findPosition(inputFile, "NGƯỜI LẬP", fileName);
                        if (pDvpd != null) {
                            rect = new Rectangle(pDvpd.getX() - 5, pDvpd.getY(), pDvpd.getX() + 25, pDvpd.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "1");
                        }

                        TextPosition txt = findPosition(inputFile, "CHỈ HUY ĐƠN VỊ", fileName);
                        if (txt != null) {
                            rect = new Rectangle(txt.getX() + 5, txt.getY(), txt.getX() + 30, txt.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "2");
                        }
                    } else if (fileName.equalsIgnoreCase(SIGNAL_BM03_TCT)) {
                        PdfContentByte under = stamper.getUnderContent(pageNum);
                        PdfContentByte under1 = stamper.getUnderContent(2);
                        Rectangle rect = null;

                        TextPosition ptgd = findPosition(inputFile, "PHÓ TỔNG GIÁM ĐỐC", fileName);
                        if (ptgd != null) {
                            rect = new Rectangle(ptgd.getX() + 5, ptgd.getY(), ptgd.getX() + 30, ptgd.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "2");
                        }

                        TextPosition ptcld = findPosition(inputFile, "P.TỔ CHỨC LAO", fileName);
                        if (ptcld != null) {
                            rect = new Rectangle(ptcld.getX() + 5, ptcld.getY(), ptcld.getX() + 30, ptcld.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "3");
                        }

                        TextPosition tptcld = findPosition(inputFile, "TP.TỔ CHỨC NHÂN SỰ", fileName);
                        if (tptcld != null) {
                            rect = new Rectangle(tptcld.getX() + 5, tptcld.getY(), tptcld.getX() + 30, tptcld.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "3");
                        }
                    } else if (fileName.equalsIgnoreCase("SeveranceAllowance")) {
                        PdfContentByte under = stamper.getUnderContent(pageNum);
                        Rectangle rect = null;
                        TextPosition txt = findPosition(inputFile, "TỔNG GIÁM ĐỐC");
                        if (txt != null) {
                            rect = new Rectangle(txt.getX() + 95, txt.getY(), txt.getX() - 20, txt.getY() - 100);
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "2");
                        }
                    } else if (fileName.contains("ResignationLetter")) {
                        PdfContentByte under = stamper.getUnderContent(pageNum);
                        Rectangle rect = null;
                        TextPosition txt = findPosition(inputFile, "THỦ TRƯỞNG ĐƠN VỊ");
                        if (txt != null) {
                            rect = new Rectangle(txt.getX() - 10, txt.getY(), txt.getX() - 20, txt.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, fileName.equalsIgnoreCase("ResignationLetter") ? "6" : "5");
                        }
                        txt = findPosition(inputFile, "P. TỔ CHỨC LAO ĐỘNG");
                        if (txt != null) {
                            rect = new Rectangle(txt.getX() - 10, txt.getY(), txt.getX() - 20, txt.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, fileName.equalsIgnoreCase("ResignationLetter") ? "5" : "6");
                        }
                    } else if (fileName.equalsIgnoreCase("NoSeveranceAllowance")) {
                        PdfContentByte under = stamper.getUnderContent(pageNum);
                        Rectangle rect = null;
                        TextPosition txt = findPosition(inputFile, "TỔNG GIÁM ĐỐC", "NoSeveranceAllowance");
                        if (txt != null) {
                            rect = new Rectangle(txt.getX() + 5, txt.getY(), txt.getX() - 20, txt.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "2");
                        }
                    } else if (fileName.equalsIgnoreCase("Agreement")) {
                        PdfContentByte under = stamper.getUnderContent(pageNum);
                        Rectangle rect = null;
                        TextPosition txt = findPosition(inputFile, "NGƯỜI SỬ DỤNG LAO ĐỘNG");
                        if (txt != null) {
                            rect = new Rectangle(txt.getX() - 25, txt.getY(), txt.getX() - 20, txt.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "5");
                        }
                    } else if (fileName.contains(FileTemplateConstant.SIGNAL_LABOR_ADD_NOTE)) {
                        PdfContentByte under = stamper.getUnderContent(pageNum);
                        Rectangle rect = null;
                        TextPosition pDvpd = findPosition(inputFile, "(Đại diện Người sử dụng lao động)", fileName);
                        if (pDvpd != null) {
                            rect = new Rectangle(pDvpd.getX() - 35, pDvpd.getY(), pDvpd.getX() - 35, pDvpd.getY() + 10);
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "2");
                        }
                    } else if (fileName.contains(FileTemplateConstant.SIGNAL_PROBATION_ADD_NOTE)) {
                        PdfContentByte under = stamper.getUnderContent(pageNum);
                        Rectangle rect = null;
                        TextPosition pDvpd = findPosition(inputFile, "(Đại diện Người sử dụng lao động)", fileName);
                        if (pDvpd != null) {
                            rect = new Rectangle(pDvpd.getX() - 35, pDvpd.getY(), pDvpd.getX() - 35, pDvpd.getY() + 10);
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "2");
                        }
                    } else if (fileName.equals("SevAllowanceMulti")) {
                        PdfContentByte under = stamper.getUnderContent(pageNum);
                        List<TextPosition> positions = findPositions(inputFile, "TỔNG GIÁM ĐỐC");
                        Rectangle rect = null;
                        if (positions != null) {
                            for (TextPosition position : positions) {
                                rect = new Rectangle(position.getX() + 5, position.getY(), position.getX() - 20, position.getY());
                                Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                        rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                                insertAnnotaion(stamper, under, nRect, "2");
                            }
                        }
                    }
                }

            } finally {
                if (stamper != null) {
                    stamper.close();
                }
                if (reader != null) {
                    reader.close();
                }
                // rename backup file
                (new File(inputFile)).delete();
                File oldFile = new File(inputFile);
                new File(outputFile).renameTo(oldFile);
            }
        }
    }

    public static void addNoteIntoDebtFile(File file, String fileName)
            throws Exception {
        if (file != null) {
            PdfReader reader = null;
            PdfStamper stamper = null;
            String inputFile = file.getPath();
            String outputFile = inputFile + "_temp";
            try {
                // reader = new PdfReader(file.getAbsolutePath());
                reader = new PdfReader(inputFile);
                stamper = new PdfStamper(reader, new FileOutputStream(
                        outputFile));
                int pageNum = reader.getNumberOfPages();
                float pageHeight = reader
                        .getPageSize(reader.getNumberOfPages()).getHeight();
                PdfDictionary page;
                page = reader.getPageN(pageNum);
                if (page != null) {
                    if (fileName.contains("debtOfUnit")) {
                        PdfContentByte under = stamper.getUnderContent(pageNum);
                        Rectangle rect = null;
                        TextPosition pDvpd = findPosition(inputFile, "GIÁM ĐỐC ĐƠN VỊ");
                        if (pDvpd != null) {
                            rect = new Rectangle(pDvpd.getX() - 20, pDvpd.getY(), pDvpd.getX() - 20, pDvpd.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "1");
                        }

                    } else if (fileName.contains("debtOfCompany")) {
                        PdfContentByte under = stamper.getUnderContent(pageNum);
                        Rectangle rect = null;
                        TextPosition pDvpd = findPosition(inputFile, "CHỈ HUY ĐƠN VỊ");
                        if (pDvpd != null) {
                            rect = new Rectangle(pDvpd.getX() + 5, pDvpd.getY(), pDvpd.getX() - 20, pDvpd.getY());
                            Rectangle nRect = new Rectangle(rect.getLeft() - 43, pageHeight - rect.getTop() - 65,
                                    rect.getLeft() - 61, pageHeight - rect.getTop() - 45);
                            insertAnnotaion(stamper, under, nRect, "1");
                        }

                    }
                }

            } finally {
                if (stamper != null) {
                    stamper.close();
                }
                if (reader != null) {
                    reader.close();
                }
                // rename backup file
                (new File(inputFile)).delete();
                File oldFile = new File(inputFile);
                new File(outputFile).renameTo(oldFile);
            }
        }
    }

    private static void insertAnnotaion(PdfStamper stamper, PdfContentByte under, Rectangle nRect, String note) {
        PdfAnnotation annotation = PdfAnnotation.createSquareCircle(
                stamper.getWriter(), nRect, note, false);
        annotation.setTitle("Note");
        annotation.setColor(BaseColor.YELLOW);
        annotation.setFlags(PdfAnnotation.FLAGS_PRINT);
        under.addAnnotation(annotation, true);
    }

    public static void addImagePdf(String src, String img, String key, String documentType, LocalDateTime dateSign) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(src);
        String outputFile = src + "_temp";
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputFile));
        if (documentType != null && (documentType.equals("Agreement")
                || documentType.equals("")
                || documentType.equals("debtOfUnit")
                || documentType.equals("debtOfCompany"))) {
            resizeImage(img, 170, 70);
        } else if (documentType != null && (documentType.equals("ResignationLetter") || documentType.equals("ResignationLetter1"))) {
            resizeImage(img, 100, 60);
        } else if (documentType != null && documentType.equals(HSDTConstant.LABOR_CONTRACT_DOCUMENT_TYPE)) {
            resizeImage(img, 170, 70);
        } else if (documentType != null && documentType.equals(HSDTConstant.PROBATIONARY_CONTRACT_DOCUMENT_TYPE)) {
            resizeImage(img, 170, 70);
        } else {
            resizeImage(img, 170, 100);
        }

        Image image = Image.getInstance(img);
        int pageNum = reader.getNumberOfPages();
//        PdfImage stream = new PdfImage(image, "", null);
//        stream.put(new PdfName("ITXT_SpecialId"), new PdfName("123456789"));
//        PdfIndirectObject ref = stamper.getWriter().addToBody(stream);

        TextPosition textPosition = findPosition(src, key);
//        image.setDirectReference(ref.getIndirectReference());
//        if (key.equalsIgnoreCase("NGƯỜI LÀM ĐƠN")) {
//            if (documentType != null && (documentType.equals("Agreement")
//                    || documentType.equals("ResignationLetter")
//                    || documentType.equals("ResignationLetter1")
//                    || documentType.equals("debtOfUnit")
//                    || documentType.equals("debtOfCompany"))) {
//                image.setAbsolutePosition(textPosition.getX() - 100, textPosition.getTextPos().getYPosition() - 80);
//            } else {
//                image.setAbsolutePosition(textPosition.getX() - 110, textPosition.getTextPos().getYPosition() - 145);
//            }
//        } else {
//            log.info("Adding signature image above key {}", key);
//            List<Float> absolutePosition = getPositionForImage(image, key, textPosition);
//            if (!absolutePosition.isEmpty()) {
//                image.setAbsolutePosition(absolutePosition.get(0), absolutePosition.get(1));
//            }
////            image.setAbsolutePosition((textPosition.getX() - 110), (textPosition.getTextPos().getYPosition() - 110));
//        }

        log.info("Adding signature image above key {}", key);
        List<Float> absolutePosition = getPositionForImage(image, key, textPosition);
        if (!absolutePosition.isEmpty()) {
            image.setAbsolutePosition(absolutePosition.get(0), absolutePosition.get(1));
            PdfContentByte over = stamper.getOverContent(currentPage > 0 ? currentPage : pageNum);
            over.addImage(image);
            String confirmSign = messageDateSign(dateSign);
            // List<Float> absoluateConfirmSign = getPositionAddText(confirmSign, key, textPosition, 10 + image.getHeight());
            // if (!absoluateConfirmSign.isEmpty()) {
            //     addText(over, confirmSign, absoluateConfirmSign.get(0), absoluateConfirmSign.get(1));
            // }
            addText(over, confirmSign, textPosition.getX() - 135, textPosition.getTextPos().getYPosition() - 160);
        }

        stamper.close();
        reader.close();
        (new File(src)).delete();
        File oldFile = new File(src);
        new File(outputFile).renameTo(oldFile);
    }

    private static void resizeImage(String imagePath, int targetWidth, int targetHeight) throws IOException {
        File file = new File(imagePath);
        BufferedImage originalImage = ImageIO.read(file);
        int w = originalImage.getWidth();
        int h = originalImage.getHeight();
        float dimension = ((float) w / h);
        java.awt.Image resultingImage;
        if ((targetHeight >= h && targetWidth < w) || (((int) (targetWidth / dimension)) < targetHeight)) {
            int finalTargetHeight = (int) (targetWidth / dimension);
            resultingImage = originalImage.getScaledInstance(targetWidth, finalTargetHeight, java.awt.Image.SCALE_SMOOTH);
            BufferedImage outputImage = new BufferedImage(targetWidth, finalTargetHeight, BufferedImage.TYPE_INT_ARGB);
//            BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
            outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
            File f = new File(imagePath);
            String ext = FilenameUtils.getExtension(imagePath);
            ImageIO.write(outputImage, ext, f);
//            Graphics2D graphics2D = outputImage.createGraphics();
//            graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
//            graphics2D.dispose();
        } else {
            int finalTargetWidth = (int) (targetHeight * dimension);
            resultingImage = originalImage.getScaledInstance(finalTargetWidth, targetHeight, java.awt.Image.SCALE_SMOOTH);
            BufferedImage outputImage = new BufferedImage(finalTargetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
//            BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
            outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
            File f = new File(imagePath);
            String ext = FilenameUtils.getExtension(imagePath);
            ImageIO.write(outputImage, ext, f);
        }
    }

    private static String messageDateSign(LocalDateTime date) {
        if (date == null) {
            date = LocalDateTime.now();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        String now = date.format(formatter);
        return "Bên B đã ký vào lúc " + now;
    }

    private static void addText(PdfContentByte contentByte, String text, float x, float y) {
        log.info("Thêm ngày ký vào hợp đồng: " + text);
        FontFactory.registerDirectories();
        Font font = FontFactory.getFont("Times New Roman", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        if (font.getBaseFont() != null) {
            BaseFont bf = font.getBaseFont();
            contentByte.saveState();
            contentByte.beginText();
            contentByte.moveText(x, y);
            contentByte.setFontAndSize(bf, 12);
            contentByte.showText(text);
            contentByte.endText();
            contentByte.restoreState();
        } else {
            throw new BadRequestException("Không tìm thấy font Times New Roman trên server, không thêm được ngày ký");
        }
    }

    public static File mergePDFs(List<File> files, String outputFile) throws IOException, DocumentException {
        Path path = Paths.get(outputFile);
        File outFile = path.toFile();
        Path directory = Paths.get(outFile.getParent());
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        Document document = new Document();
        PdfCopy copy = new PdfCopy(document, new FileOutputStream(outputFile));
        document.open();
        for (File file : files) {
            PdfReader pdfReader = new PdfReader(file.getCanonicalPath());
            copy.addDocument(pdfReader);
            pdfReader.close();
        }
        document.close();
        return outFile;
    }

    public static List<Float> getPositionForImage(Image img, String key, TextPosition keyPosition) {
        if (StringUtils.isBlank(key)) {
            return new ArrayList<>();
        }
        float centerKeyX = keyPosition.getX() - (key.length() / 2) * keyPosition.getWidth();
        float keyY = keyPosition.getTextPos().getYPosition();
        Float absoluteX = centerKeyX - img.getWidth() / 2;
        Float absoluteY = keyY - 20 - img.getHeight();
        return Arrays.asList(absoluteX, absoluteY);
    }

    public static List<Float> getPositionAddText(String text, String key, TextPosition keyPosition, float height) {
        if (StringUtils.isBlank(key)) {
            return new ArrayList<>();
        }
        float centerKeyX = keyPosition.getX() - (key.length() / 2) * keyPosition.getWidth();
        float keyY = keyPosition.getTextPos().getYPosition();
        Float absoluteX = centerKeyX - (text.length() / 2) * keyPosition.getWidth();
        Float absoluteY = keyY - 20 - height;
        return Arrays.asList(absoluteX, absoluteY);

    }
}
