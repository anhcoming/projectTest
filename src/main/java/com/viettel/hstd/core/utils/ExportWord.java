package com.viettel.hstd.core.utils;


import com.spire.doc.*;
import com.spire.doc.reporting.MailMergeDataTable;
import com.viettel.hstd.core.constant.ExportFileExtension;
import com.viettel.hstd.dto.UploadDTO;
import com.viettel.hstd.util.DateUtils;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

//import org.apache.poi.xwpf.converter.pdf.PdfConverter;
//import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import org.springframework.core.io.ClassPathResource;

public class ExportWord<T> {
    /**
     * Xuat file dua theo template
     *
     * @param template  file dau vao
     * @param basePath  duong dan xuat file
     * @param fileName  ten file xuat ra
     * @param inputData Du lieu import cho file word
     * @param extension dinh dang file muon xuat
     */
    public String export(
        String template, String
            basePath,
        String fileName,
        HashMap<String, String> inputData, Integer
                    extension,
        HashMap<String, List<T>> dataTables, String signal) {
        Document document = new Document();
        try {
            File fonts = new ClassPathResource("fonts/font-times-new-roman.ttf").getFile();
            document.setEmbedFontsInFile(true);
            PrivateFontPath fontPath = new PrivateFontPath();
            fontPath.setFontName("Times New Roman");
            fontPath.setFontPath(fonts.getAbsolutePath());
            document.loadFromFile(template, FileFormat.Docx);
            int size = inputData.values().size();
            String[] arrKey = new String[size];
            String[] arrValue = new String[size];
            String[] fieldNames = inputData.keySet().toArray(arrKey);
            String[] fieldValues = inputData.values().toArray(arrValue);
            document.getMailMerge().execute(fieldNames, fieldValues);
            //<editor-fold desc="Mail merge for table">
            if (dataTables != null) {
                for (String item : dataTables.keySet()) {
                    MailMergeDataTable table = new MailMergeDataTable(item, dataTables.get(item));
                    document.getMailMerge().executeGroup(table);
                }
            }
            //</editor-fold>
            document.isUpdateFields(true);
            String pattern = "HHmmss";
            Date today = Calendar.getInstance().getTime();
            DateFormat df = new SimpleDateFormat(pattern);
            String todayAsString = df.format(today);
            String docPath = basePath + File.separator + fileName;
            String uploadFolder = initUploadFolder(basePath);
            if (extension == ExportFileExtension.DOC) {
                docPath = uploadFolder + todayAsString + fileName + ".doc";
                document.saveToFile(docPath, FileFormat.Doc);
            } else if (extension == ExportFileExtension.DOCX) {
                docPath = uploadFolder + todayAsString + fileName + ".docx";
                document.saveToFile(docPath, FileFormat.Docx);
            } else {
                docPath = uploadFolder + todayAsString + fileName + ".docx";
                document.saveToFile(docPath, FileFormat.Docx);
                String pdfFileName = uploadFolder + todayAsString + fileName + ".pdf";
                convertToPDF(docPath, pdfFileName, signal, uploadFolder);
                //<editor-fold desc="Xoa file tam sau khi da convert ra pdf">
//                File file = new File(docPath);
//                if (file.exists() && file.isFile()) {
//                    file.delete();
//                }
                //</editor-fold>
                docPath = pdfFileName;
            }
            return docPath;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            document.close();
        }
        return null;
    }

    public String exportV2(
            String template, String
            basePath,
            String fileName,
            HashMap<String, String> inputData, Integer
                    extension,
            HashMap<String, List<T>> dataTables, String signal) {
        Document document = new Document();
        try {
            File fonts = new ClassPathResource("fonts/font-times-new-roman.ttf").getFile();
            document.setEmbedFontsInFile(true);
            PrivateFontPath fontPath = new PrivateFontPath();
            fontPath.setFontName("Times New Roman");
            fontPath.setFontPath(fonts.getAbsolutePath());
            document.loadFromFile(template);
            int size = inputData.values().size();
            String[] arrKey = new String[size];
            String[] arrValue = new String[size];
            String[] fieldNames = inputData.keySet().toArray(arrKey);
            String[] fieldValues = inputData.values().toArray(arrValue);
            document.getMailMerge().execute(fieldNames, fieldValues);
            //<editor-fold desc="Mail merge for table">
            if (dataTables != null) {
                for (String item : dataTables.keySet()) {
                    MailMergeDataTable table = new MailMergeDataTable(item, dataTables.get(item));
                    document.getMailMerge().executeGroup(table);
                }
            }
            //</editor-fold>
            document.isUpdateFields(true);
            String pattern = "HHmmss";
            Date today = Calendar.getInstance().getTime();
            DateFormat df = new SimpleDateFormat(pattern);
            String todayAsString = df.format(today);
            String docPath = basePath + File.separator + fileName;
            String uploadFolder = initUploadFolder(basePath);
            if (extension == ExportFileExtension.DOC) {
                docPath = uploadFolder + todayAsString + fileName + ".doc";
                document.saveToFile(docPath, FileFormat.Doc);
            } else if (extension == ExportFileExtension.DOCX) {
                docPath = uploadFolder + todayAsString + fileName + ".docx";
                document.saveToFile(docPath, FileFormat.Docx);
            } else {
                docPath = uploadFolder + todayAsString + fileName + ".docx";
                document.saveToFile(docPath, FileFormat.Docx);

                docPath = uploadFolder + todayAsString + fileName + ".pdf";
                document.saveToFile(docPath, FileFormat.PDF);

                File outputFile = new File(docPath);
                PdfAnnotations.addNoteIntoFile(outputFile, signal);
            }
            return docPath;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            document.close();
        }
        return null;
    }

    private static void convertToPDF(String docPath, String pdfPath, String signal, String basePath) {
//        <editor - fold desc = "convert docx to pdf using xdocreport" >
        try {
            //taking input from docx file
            InputStream doc = new FileInputStream(new File(docPath));
            //process for creating pdf started
            XWPFDocument document = new XWPFDocument(doc);
            PdfOptions options = PdfOptions.create();
            OutputStream out = new FileOutputStream(new File(pdfPath));
            PdfConverter.getInstance().convert(document, out, options);
            document.close();
            out.close();
            doc.close();
            File outputFile = new File(pdfPath);
            PdfAnnotations.addNoteIntoFile(outputFile, signal);
            if (signal.equals("serviceContract")) {
                String pattern = "HHmmss";
                Date today = Calendar.getInstance().getTime();
                DateFormat df = new SimpleDateFormat(pattern);
                String todayAsString = df.format(today);
                File phuLuc01 = new ClassPathResource("template/HopDong_DichVu_PhuLuc01.pdf").getFile();
                File phuLuc02 = new ClassPathResource("template/HopDong_DichVu_PhuLuc02.pdf").getFile();
                OutputStream outputPhuLuc01 =
                        new FileOutputStream(basePath + "HopDong_DichVu_PhuLuc01" + todayAsString + ".pdf");
                java.nio.file.Files.copy(phuLuc01.toPath(), outputPhuLuc01);
                outputPhuLuc01.close();
                OutputStream outputPhuLuc02 =
                        new FileOutputStream(basePath + "HopDong_DichVu_PhuLuc02" + todayAsString + ".pdf");
                java.nio.file.Files.copy(phuLuc02.toPath(), outputPhuLuc02);
                outputPhuLuc02.close();
                phuLuc01 = new File(basePath + "HopDong_DichVu_PhuLuc01" + todayAsString + ".pdf");
                phuLuc02 = new File(basePath + "HopDong_DichVu_PhuLuc02" + todayAsString + ".pdf");
                PdfAnnotations.addNoteIntoFile(phuLuc01, signal + "_PL01");
                PdfAnnotations.addNoteIntoFile(phuLuc02, signal + "_PL02");
                PDFMergerUtility PDFmerger = new PDFMergerUtility();
                PDFmerger.setDestinationFileName(pdfPath);
                PDFmerger.addSource(outputFile);
                PDFmerger.addSource(phuLuc01);
                PDFmerger.addSource(phuLuc02);
                PDFmerger.mergeDocuments();
                phuLuc01.delete();
                phuLuc02.delete();
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String initUploadFolder(String pathStore) {
        String dateString = DateUtils.getNowTime("yyyy-MM-dd");
        String[] dateParts = dateString.split("-");
        String year = dateParts[0];
        String month = dateParts[1];
        String day = dateParts[2];
        String UPLOADED_FOLDER = pathStore + "/" + year + "/" + month + "/" + day + "/";

        final Path fileStorageLocation = Paths.get(UPLOADED_FOLDER)
                .toAbsolutePath().normalize();
        try {
            if (!Files.exists(fileStorageLocation))
                Files.createDirectories(fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("file.directory_create");
        }
        return UPLOADED_FOLDER;
    }


}
