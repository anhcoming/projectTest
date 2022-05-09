package com.viettel.hstd.util;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.List;

public class FileUtils {
    public static String getMD5(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            // replay MD5, SHA-512
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return DatatypeConverter.printHexBinary(hash);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String getNameFileMD5(String input, String extension) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            // replay MD5, SHA-512
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return DatatypeConverter.printHexBinary(hash) + extension;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String getFileExtension(String name) {
        String extension;
        try {
            extension = name.substring(name.lastIndexOf("."));
        } catch (Exception e) {
            extension = "";
        }
        return extension;
    }

    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    private static void createDirectoryNotExist(String realPath) throws IOException {
        Path path = Paths.get(realPath);
        File outFile = path.toFile();
        Path directory = Paths.get(outFile.getParent());
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
    }

    public static File mergeFileWord(List<File> files, String realPath) throws IOException, XmlException {
        Path path = Paths.get(realPath);
        File outFile = path.toFile();
        createDirectoryNotExist(realPath);
        OutputStream os = new FileOutputStream(outFile);
        InputStream inputStream = new FileInputStream(files.get(0));
        XWPFDocument doc = new XWPFDocument(inputStream);
        CTBody ctBody = doc.getDocument().getBody();
        for (int i = 1; i < files.size(); i++) {
            InputStream fileInputStream = new FileInputStream(files.get(i));
            XWPFDocument document = new XWPFDocument(fileInputStream);
            CTBody ctBodyI = document.getDocument().getBody();
            appendBody(ctBody, ctBodyI);
            if (i != files.size()) {
                XWPFParagraph paragraph = doc.createParagraph();
                paragraph.setPageBreak(true);
            }
        }
        doc.write(os);
        os.close();
        return outFile;
    }

    private static void appendBody(CTBody src, CTBody append) throws XmlException {
        XmlOptions optionsOuter = new XmlOptions();
        optionsOuter.setSaveOuter();
        String appendString = append.xmlText(optionsOuter);
        String srcString = src.xmlText();
        String prefix = srcString.substring(0, srcString.indexOf(">") + 1);
        String mainPart = srcString.substring(srcString.indexOf(">") + 1, srcString.lastIndexOf("<"));
        String suffix = srcString.substring(srcString.lastIndexOf("<"));
        String addPart = appendString.substring(appendString.indexOf(">") + 1, appendString.lastIndexOf("<"));
        CTBody makeBody;
        try {
            makeBody = CTBody.Factory.parse(prefix + mainPart + addPart + suffix);
        } catch (XmlException e) {
            e.printStackTrace();
            throw new XmlException("Ghép nội dung lỗi", e);
        }
        src.set(makeBody);
    }

    public static File mergeFileWord2(List<File> files, String realPath) throws IOException {
        Path path = Paths.get(realPath);
        File outFile = path.toFile();
        createDirectoryNotExist(realPath);
        OutputStream os = new FileOutputStream(outFile);
        XWPFDocument mergedDocuments = new XWPFDocument();
        CTDocument1 mergedCTDocument = mergedDocuments.getDocument();
        mergedCTDocument.unsetBody();
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            InputStream fileInputStream = new FileInputStream(file);
            XWPFDocument xwpfDocument = new XWPFDocument(fileInputStream);
            CTDocument1 srcCTDocument = xwpfDocument.getDocument();
            if (srcCTDocument != null) {
                CTBody srcCTBody = srcCTDocument.getBody();
                if (srcCTBody != null) {
                    CTBody mergedCTBody = mergedCTDocument.addNewBody();
                    mergedCTBody.set(srcCTBody);
                    if (i != files.size() - 1) {
                        XWPFParagraph xwpfParagraph = mergedDocuments.createParagraph();
                        xwpfParagraph.setPageBreak(true);
                    }
                }
            }
        }
        mergedDocuments.write(os);
        os.close();
        return outFile;
    }

    public static File mergeFileWord3(List<File> files, String realPath) throws IOException {
        Path path = Paths.get(realPath);
        File outFile = path.toFile();
        createDirectoryNotExist(realPath);
        OutputStream os = new FileOutputStream(outFile);
        XWPFDocument mergedDocuments = new XWPFDocument();
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            InputStream fileInputStream = new FileInputStream(file);
            XWPFDocument xwpfDocument = new XWPFDocument(fileInputStream);
            copyLayout(xwpfDocument, mergedDocuments);
            copyElement(xwpfDocument, mergedDocuments);
            if (i != files.size() - 1) {
                XWPFParagraph xwpfParagraph = mergedDocuments.createParagraph();
                xwpfParagraph.setPageBreak(true);
            }
        }
        mergedDocuments.write(os);
        os.close();
        return outFile;
    }

    private static void copyLayout(XWPFDocument srcDoc, XWPFDocument destDoc) {
        CTPageMar pgMar = srcDoc.getDocument().getBody().getSectPr().getPgMar();

        BigInteger bottom = pgMar.getBottom();
        BigInteger footer = pgMar.getFooter();
        BigInteger gutter = pgMar.getGutter();
        BigInteger header = pgMar.getHeader();
        BigInteger left = pgMar.getLeft();
        BigInteger right = pgMar.getRight();
        BigInteger top = pgMar.getTop();

        CTPageMar addNewPgMar = destDoc.getDocument().getBody().addNewSectPr().addNewPgMar();

        addNewPgMar.setBottom(bottom);
        addNewPgMar.setFooter(footer);
        addNewPgMar.setGutter(gutter);
        addNewPgMar.setHeader(header);
        addNewPgMar.setLeft(left);
        addNewPgMar.setRight(right);
        addNewPgMar.setTop(top);

        CTPageSz pgSzSrc = srcDoc.getDocument().getBody().getSectPr().getPgSz();

        BigInteger code = pgSzSrc.getCode();
        BigInteger h = pgSzSrc.getH();

        org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation.Enum orient = pgSzSrc.getOrient();
        BigInteger w = pgSzSrc.getW();

        CTPageSz addNewPgSz = destDoc.getDocument().getBody().addNewSectPr().addNewPgSz();

        addNewPgSz.setCode(code);
        addNewPgSz.setH(h);
        addNewPgSz.setOrient(orient);
        addNewPgSz.setW(w);
    }

    private static void copyStyle(XWPFDocument srcDoc, XWPFDocument destDoc, XWPFStyle style) {
        if (destDoc == null || style == null) return;

        if (destDoc.getStyles() == null) {
            destDoc.createStyles();
        }

        List<XWPFStyle> usedStyleList = srcDoc.getStyles().getUsedStyleList(style);
        for (XWPFStyle xwpfStyle : usedStyleList) {
            destDoc.getStyles().addStyle(xwpfStyle);
        }
    }

    private static void copyElement(XWPFDocument srcDoc, XWPFDocument destDoc) {
        for (IBodyElement bodyElement : srcDoc.getBodyElements()) {
            BodyElementType elementType = bodyElement.getElementType();
            if (elementType.equals(BodyElementType.PARAGRAPH)) {
                XWPFParagraph pr = (XWPFParagraph) bodyElement;
                copyStyle(srcDoc, destDoc, srcDoc.getStyles().getStyle(pr.getStyleID()));
                destDoc.createParagraph();

                int pos = destDoc.getParagraphs().size() - 1;

                destDoc.setParagraph(pr, pos);
            } else if (elementType.equals(BodyElementType.TABLE)) {
                XWPFTable table = (XWPFTable) bodyElement;
                copyStyle(srcDoc, destDoc, srcDoc.getStyles().getStyle(table.getStyleID()));
                destDoc.createTable();
                int pos = destDoc.getTables().size() - 1;
                destDoc.setTable(pos, table);
            }
        }
    }

}

