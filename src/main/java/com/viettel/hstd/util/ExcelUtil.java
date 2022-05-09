package com.viettel.hstd.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.poiji.option.PoijiOptions;
import com.viettel.hstd.core.utils.CustomMapper;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.service.inf.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ExcelUtil {
    private final int numberOfField = 19;
    private final String prefix = "{";
    private final String suffix = "}";
    private final String groupKeyStart = "groupKeyStart_";
    private final String groupKeyEnd = "groupKeyEnd_";
    private final int groupKeyMaximumColumn = 100;

    @Autowired
    private FileService fileService;

    private Cell findCellContain(Sheet sheet, String cellContent) {
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell.getCellTypeEnum().equals(CellType.STRING) && cell.getStringCellValue().contains(cellContent)) {
//                    if (cell.getRichStringCellValue().getString().trim().contains(cellContent)) {
                        return cell;
//                    }
                }
            }
        }
        return null;
    }

    private List<Cell> findGroupCell(Sheet sheet) {
        List<Cell> cellList = new ArrayList<>();

        for (Row row : sheet) {
            for (Cell cell : row) {
                String stringValue = returnStringValue(cell);
                if (stringValue.contains(groupKeyStart)) {
                    log.info("Row {} column {}", cell.getRowIndex(), cell.getColumnIndex());
                    cellList.add(cell);
                    boolean isGroupEnd = false;
                    int index = 1;
                    while (!isGroupEnd && index < groupKeyMaximumColumn) {
                        Cell keyCell = row.getCell(cell.getColumnIndex() + index);
                        isGroupEnd = returnStringValue(keyCell).contains(groupKeyEnd);
                        cellList.add(keyCell);
                        index += 1;
                    }
                }
            }
        }

        return cellList;
    }


    public <T, R> void exportExcel(String input, String output, R metadata, List<T> objectList) {
        try {
            // Blank workbook
            Resource resource = new ClassPathResource(input);
            ;
//            FileInputStream fileInputStream = new FileInputStream(input);

            XSSFWorkbook workbook = new XSSFWorkbook(resource.getInputStream());

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                final Sheet sheet = workbook.getSheetAt(i);

                List<Map<String, String>> mapList = objectList
                        .stream()
                        .map(CustomMapper::convert)
                        .collect(Collectors.toList());

                List<Cell> groupKeyCellSet = findGroupCell(sheet);
                if (groupKeyCellSet.size() == 0) continue;

                Cell firstKeyCell = groupKeyCellSet.get(0);
                sheet.shiftRows(firstKeyCell.getRowIndex() + 1, sheet.getLastRowNum(), objectList.size());
//            pushbackMergeCell(sheet, objectList, firstKeyCell);

                writeGroupContent(sheet, mapList, groupKeyCellSet);

                // Remove keyrow
                Row currentRow = groupKeyCellSet.get(0).getRow();
                sheet.shiftRows(currentRow.getRowNum() + 1, groupKeyCellSet.get(0).getRowIndex() + objectList.size(), -1);

                // Fill metadata
                Map<String, String> metadataMap = CustomMapper.convert(metadata);
                List<Cell> metadataKeyCellSet = metadataMap.keySet()
                        .stream()
                        .map(obj -> findCellContain(sheet, prefix + obj + suffix))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                writeMetadataContent(sheet, metadataMap, metadataKeyCellSet);
            }


            FileOutputStream fileOutputStream = new FileOutputStream(output);
            workbook.write(fileOutputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class CellComparator implements Comparator<Cell> {

        @Override
        public int compare(Cell firstCell, Cell secondCell) {
            return Integer.compare(firstCell.getColumnIndex(), secondCell.getColumnIndex());
        }

    }

    private <T> void pushbackMergeCell(Sheet sheet, List<T> objectList, Cell firstKeyCell) {

        Cell finalFirstKeyCell = firstKeyCell;
        List<Integer> certainMergeCellIndex = new ArrayList<>();
        List<CellRangeAddress> cellRangeAddressList = sheet.getMergedRegions()
                .stream()
                .filter(mergeResion -> mergeResion.getFirstRow() > finalFirstKeyCell.getRowIndex())
                .collect(Collectors.toList());

        for (int i = 0; i < sheet.getMergedRegions().size(); i++) {
            for (int j = 0; j < cellRangeAddressList.size(); j++) {
                if (sheet.getMergedRegions().get(i).equals(cellRangeAddressList.get(j))) {
                    certainMergeCellIndex.add(i);
                }
            }
        }

        cellRangeAddressList.forEach(oldCRA -> {
            CellRangeAddress newCRA = oldCRA.copy();
            newCRA.setFirstRow(oldCRA.getFirstRow() + objectList.size());
            newCRA.setLastRow(oldCRA.getLastRow() + objectList.size());
            sheet.addMergedRegion(newCRA);
            Cell oldMergeCell = sheet.getRow(oldCRA.getFirstRow()).getCell(oldCRA.getFirstColumn());
            Cell newMergeCell = null;
            if (sheet.getRow(newCRA.getFirstRow()) != null) {
                newMergeCell = sheet.getRow(newCRA.getFirstRow()).createCell(newCRA.getFirstColumn());
            } else {
                newMergeCell = sheet.createRow(newCRA.getFirstRow()).createCell(newCRA.getFirstColumn());
            }
            newMergeCell.setCellValue(oldMergeCell.getStringCellValue());
            newMergeCell.setCellStyle(oldMergeCell.getCellStyle());

            oldMergeCell.setCellValue("");
        });

            sheet.removeMergedRegions(certainMergeCellIndex);
    }

    private <T> void writeGroupContent(Sheet sheet, List<Map<String, String>> mapList, List<Cell> keyCellSet) {
        Cell firstKeyCell = keyCellSet.get(0);

        for (int i = 0; i < mapList.size(); i++) {
            Row row = sheet.createRow(firstKeyCell.getRowIndex() + i + 1);
            row.setRowStyle(firstKeyCell.getRow().getRowStyle());
            for (int j = 0; j < keyCellSet.size(); j++) {
                Cell newCell = row.createCell(keyCellSet.get(j).getColumnIndex());
                newCell.setCellStyle(keyCellSet.get(j).getCellStyle());
                String key = returnStringValue(keyCellSet.get(j)).replace(prefix, "").replace(suffix, "");
                if (j == 0 || j == mapList.size()) key = key.replace(groupKeyStart, "").replace(groupKeyEnd, "");

                String value = mapList.get(i).get(key);
                newCell.setCellValue(value != null ? value : "");
            }
        }

    }

    private <T> void writeMetadataContent(Sheet sheet, Map<String, String> map, List<Cell> keyCellSet) {
        List<String> mapKeyList = new ArrayList<>(map.keySet());

        for (int i = 0; i < keyCellSet.size(); i++) {
            Cell currentCell = keyCellSet.get(i);
            String oldValue = currentCell.getStringCellValue();
            for (int j = 0; j < mapKeyList.size(); j++) {
                if (currentCell.getStringCellValue().contains(prefix + mapKeyList.get(j) + suffix)) {
                    oldValue = oldValue.replace(prefix + mapKeyList.get(j) + suffix, map.get(mapKeyList.get(j)));
                }
            }

            currentCell.setCellValue(oldValue);
        }

    }

    private String returnStringValue(Cell cell) {
        CellType cellType = cell.getCellType();

        switch (cellType) {
            case NUMERIC:
                double doubleVal = cell.getNumericCellValue();
                return String.valueOf(doubleVal);
            case STRING:
                return cell.getStringCellValue();
            case ERROR:
                return String.valueOf(cell.getErrorCellValue());
            case BLANK:
                return "";
            case FORMULA:
                return cell.getCellFormula();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
        }
        return "error decoding string value of the cell";
    }

    public <T> List<T> getListExcelRow(Class<T> type,String fileEncodeUrl, PoijiExcelType excelType,
                                       int sheetNumber, int headerStart, String listDelimiter) throws IOException {
        Resource resource = fileService.downloadFileWithEncodePath(fileEncodeUrl);

        //read file
        PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings()
                .headerStart(headerStart)
                .sheetIndex(sheetNumber)
                .addListDelimiter(listDelimiter)
                .ignoreWhitespaces(true)
                .trimCellValue(true)
                .preferNullOverDefault(true)
                .build();
        return Poiji.fromExcel(resource.getInputStream(),excelType, type , options);
    }

    public PoijiExcelType getPoijiExcelType(String fileName){
        String extension = fileName.substring(fileName.lastIndexOf(".")+1);
        if(extension.toUpperCase().equals(PoijiExcelType.XLSX.name())) return PoijiExcelType.XLSX;
        else if(extension.toUpperCase().equals(PoijiExcelType.XLS.name())) return PoijiExcelType.XLS;
        throw new BadRequestException("Unsupported type");
    }
}
