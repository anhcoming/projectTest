package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.constant.*;
import com.viettel.hstd.core.utils.StringUtils;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.ContractImportDTO;
import com.viettel.hstd.dto.vps.VhrFutureOrganizationDTO;
import com.viettel.hstd.entity.hstd.*;
import com.viettel.hstd.entity.vps.EmployeeVhrEntity;
import com.viettel.hstd.entity.vps.PositionEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.*;
import com.viettel.hstd.repository.vps.EmployeeVhrRepository;
import com.viettel.hstd.repository.vps.PositionRepository;
import com.viettel.hstd.repository.vps.VhrFutureOrganizationRepository;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.ContractImportService;
import com.viettel.hstd.service.inf.*;
import com.viettel.hstd.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ContractImportServiceImpl implements ContractImportService {
    @Autowired
    private EmployeeVhrRepository employeeVhrRepository;
    @Autowired
    private ResignSessionRepository resignSessionRepository;
    @Autowired
    private ResignSessionContractRepository resignSessionContractRepository;
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private VhrFutureOrganizationRepository vhrFutureOrganizationRepository;
    @Autowired
    private VhrFutureOrganizationService vhrFutureOrganizationService;
    @Autowired
    private EmployeeMonthlyReviewRepository employeeMonthlyReviewRepository;
    @Autowired
    private LaborContractService laborContractService;
    @Autowired
    private LaborContractRepository laborContractRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private FileService fileService;
    @Autowired
    private ProbationaryContractService probationaryContractService;
    @Autowired
    private ProbationaryContractRepository probationaryContractRepository;
    @Autowired
    private SmsMessageService smsMessageService;

    @Override
    @Transactional
    public ContractImportDTO.ContractImportResponse importContractFromExcel(SSoResponse soResponse, ContractImportDTO.ContractImportRequest request) {
        ContractImportDTO.ContractImportResponse contractImportResponse = new ContractImportDTO.ContractImportResponse();
        if (request.filePath == null) {
            throw new NotFoundException("Kh??ng t??m th???y file b???n import");
        }
        Resource resource = fileService.downloadFileWithRealPath(request.filePath);
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(resource.getInputStream());
            ContractImportDTO.ContractImportResponse responseFile = getContractImportFromFile(workbook, request.category);

            ContractImportDTO.ContractImportResponse validateSSO = validateSSO(responseFile, soResponse, request.category);

            if (!validateSSO.result) {
                return validateSSO;
            }

            List<ContractImportDTO.ContractImportModel> contractImportModels = validateSSO.reads.stream().map(contractImportRead -> contractImportRead.success).collect(Collectors.toList());


            List<EmployeeVhrEntity> employeeVhrEntityList = getAllEmployeeVhr(contractImportModels);


            Set<String> positionNameSet = contractImportModels.stream().map(ContractImportDTO.ContractImportModel::getPositionName).collect(Collectors.toSet());

            List<PositionEntity> positionEntityList = positionRepository.findByPositionNameIn(positionNameSet);
            Map<String, PositionEntity> positionEntityMap = positionEntityList.stream().collect(Collectors.toMap(PositionEntity::getPositionName, Function.identity(), (o1, o2) -> o1));
            Map<String, EmployeeVhrEntity> employeeVhrEntityMap = employeeVhrEntityList.stream().collect(Collectors.toMap(EmployeeVhrEntity::getEmployeeCode, Function.identity()));

            switch (request.category) {
                case LABOR:
                    contractImportResponse.result = this.createLaborContract(soResponse, contractImportModels, request, positionEntityMap, employeeVhrEntityList, employeeVhrEntityMap);
                    break;
                case PROBATION:
                    contractImportResponse.result = this.createProbationContract(soResponse, contractImportModels, request, positionEntityMap, employeeVhrEntityList, employeeVhrEntityMap);
                    break;
                case PROBATION_TO_LABOR:
                    contractImportResponse.result = this.createProbationToLabor(soResponse, contractImportModels, request, positionEntityMap, employeeVhrEntityList, employeeVhrEntityMap);
                    break;
                case CORPORATION_TO_COMPANY:
                    contractImportResponse.result = this.createCorporationToCompany(soResponse, contractImportModels, request, positionEntityMap, employeeVhrEntityList, employeeVhrEntityMap);
                    break;
                case LABOR_OLD:
                    contractImportResponse.result = this.createLaborOld(soResponse, contractImportModels, request, positionEntityMap, employeeVhrEntityList, employeeVhrEntityMap);
                    break;
            }
        } catch (IOException exception) {
            throw new NotFoundException("File t???i l??n kh??ng ????ng ?????nh d???ng excel");
        }

        return contractImportResponse;
    }

    private ContractImportDTO.ContractImportResponse getContractImportFromFile(XSSFWorkbook workbook, ContractImportCategory category) {
        ContractImportDTO.ContractImportResponse response = new ContractImportDTO.ContractImportResponse();

        boolean isError = false;
        List<ContractImportDTO.ContractImportRead> reads = new ArrayList<>();
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();

        String dateFormat = "dd/MM/yyyy";
        int rowNumber = 0;
        System.out.println("Tr???ng th??i k???t qu??? tr?????c khi ?????c d??? li???u " + response.result);
        boolean usingTemplateOld = true;
        boolean isLabor = category.equals(ContractImportCategory.LABOR) || category.equals(ContractImportCategory.CORPORATION_TO_COMPANY) || category.equals(ContractImportCategory.LABOR_OLD);
        boolean isProbation = category.equals(ContractImportCategory.PROBATION) || category.equals(ContractImportCategory.PROBATION_TO_LABOR);
        boolean notContractOld = category.equals(ContractImportCategory.PROBATION) || category.equals(ContractImportCategory.CORPORATION_TO_COMPANY) || category.equals(ContractImportCategory.LABOR_OLD);
        boolean onlyOneLabor = category.equals(ContractImportCategory.CORPORATION_TO_COMPANY) || category.equals(ContractImportCategory.LABOR_OLD);
        String contractName = notContractOld ? "h???p ?????ng" : "h???p ?????ng c??";
        while (rows.hasNext()) {
            log.info("B???t ?????u ?????c d??? li???u {}", rowNumber);
            Row currentRow = rows.next();
            if (rowNumber < 3) {
                if (rowNumber == 1) {
                    Cell contractTypeCell = currentRow.getCell(CellReference.convertColStringToIndex("B"));
                    String contractTypeName = StringUtils.convertCellToString(contractTypeCell);
                    if (contractTypeName != null && contractTypeName.contains("c??")) {
                        usingTemplateOld = true;
                    } else {
                        usingTemplateOld = false;
                    }
                }
                rowNumber++;
                continue;
            }
            Iterator<Cell> cellsInRow = currentRow.iterator();
            ContractImportDTO.ContractImportRead read = new ContractImportDTO.ContractImportRead();
            read.error.line = rowNumber + 1;
            read.error.reason = "";
            // Lo???i h???p ?????ng
//            System.out.println("Tr???ng th??i isError tr?????c khi ?????c d??? li???u contractType " + isError);
            Cell contractTypeCell = currentRow.getCell(CellReference.convertColStringToIndex("B"));
            Cell celleffectiveDate = currentRow.getCell(CellReference.convertColStringToIndex("D"));
            if (contractTypeCell == null || StringUtils.convertCellToString(contractTypeCell) == null) {
                Cell cellEmployeeCode = currentRow.getCell(CellReference.convertColStringToIndex("J"));
                Cell cellContractDuration = currentRow.getCell(CellReference.convertColStringToIndex("BJ"));
                Cell cellEmployeeName = currentRow.getCell(CellReference.convertColStringToIndex("K"));
                if ((cellEmployeeCode == null || StringUtils.convertCellToString(cellEmployeeCode) == null) && (celleffectiveDate == null || StringUtils.convertCellToString(celleffectiveDate) == null) && (cellContractDuration == null || StringUtils.convertCellToString(cellContractDuration) == null) && (cellEmployeeName == null || StringUtils.convertCellToString(cellEmployeeName) == null)) {
                    break;
                } else {
                    isError = true;
                    read.error.reason = getReason(read.error.reason, "Ch??a nh???p lo???i " + contractName);
                }
            } else {
                if (usingTemplateOld) {
                    if (category.equals(ContractImportCategory.PROBATION)) {
                        isError = true;
                        read.error.reason = getReason(read.error.reason, "D??ng sai template ????? nh???p d??? li???u cho h???p ?????ng th??? vi???c");
                    } else if (category.equals(ContractImportCategory.CORPORATION_TO_COMPANY)) {
                        isError = true;
                        read.error.reason = getReason(read.error.reason, "D??ng sai template ????? nh???p d??? li???u cho h???p ?????ng lao ?????ng chuy???n c??ng t??c t??? t???ng c??ng ty sang VCC");
                    } else if (category.equals(ContractImportCategory.LABOR_OLD)) {
                        isError = true;
                        read.error.reason = getReason(read.error.reason, "D??ng sai template ????? nh???p d??? li???u cho h???p ?????ng lao ?????ng c??");
                    } else {
                        if (contractTypeCell.getStringCellValue().equals("H???p ?????ng lao ?????ng")) {
                            read.success.setContractType(ContractType.LABOR_CONTRACT);
                            read.error.contractType = "H???p ?????ng lao ?????ng";
                        } else if (contractTypeCell.getStringCellValue().equals("H???p ?????ng th??? vi???c")) {
                            read.success.setContractType(ContractType.PROBATIONARY_CONTRACT);
                            read.error.contractType = "H???p ?????ng th??? vi???c";
                        }
                    }
                } else {
                    if (contractTypeCell.getStringCellValue().equals("H???p ?????ng lao ?????ng") && onlyOneLabor) {
                        read.success.setContractType(ContractType.LABOR_CONTRACT);
                        read.error.contractType = "H???p ?????ng lao ?????ng";
                    } else if (contractTypeCell.getStringCellValue().equals("H???p ?????ng th??? vi???c") && category.equals(ContractImportCategory.PROBATION)) {
                        read.success.setContractType(ContractType.PROBATIONARY_CONTRACT);
                        read.error.contractType = "H???p ?????ng th??? vi???c";
                    } else {
                        isError = true;
                        read.error.contractType = contractTypeCell.getStringCellValue();
                        read.error.reason = getReason(read.error.reason, "T??n lo???i " + contractName + " kh??ng tr??ng v???i y??u c???u nh???p d??? li???u ");
                    }
                }
            }

            // S??? h???p ?????ng c??
            Cell contractNumberCell = currentRow.getCell(CellReference.convertColStringToIndex("C"));
            if (contractNumberCell == null || StringUtils.convertCellToString(contractNumberCell) == null) {
                isError = true;
                read.error.reason = getReason(read.error.reason, "Ch??a c?? s??? " + contractName);
            } else {
                read.success.setContractNumber(StringUtils.convertCellToString(contractNumberCell));
                read.error.contractNumber = StringUtils.convertCellToString(contractNumberCell);
            }

            // Ng??y b???t ?????u h???p ?????ng c??
            Cell effectiveDateCell = currentRow.getCell(CellReference.convertColStringToIndex("D"));
            if (effectiveDateCell == null || DateUtils.convertCellDate(effectiveDateCell, dateFormat) == null) {
                isError = true;
                read.error.reason = getReason(read.error.reason, "Ch??a c?? ng??y b???t ?????u " + contractName);
            } else {
                read.success.setEffectiveDate(DateUtils.convertDateToLocalDate(DateUtils.convertCellDate(effectiveDateCell, dateFormat)));
                read.error.effectiveDate = DateUtils.convertDateToString(DateUtils.convertCellDate(effectiveDateCell, dateFormat), dateFormat);
            }

            // Th???i h???n h???p ?????ng c??
//            System.out.println("Tr???ng th??i isError tr?????c khi ?????c d??? li???u contractDuration " + isError);
            Cell contractDurationCell = currentRow.getCell(CellReference.convertColStringToIndex("J"));
            if (contractDurationCell == null || StringUtils.convertCellToString(contractDurationCell) == null) {
                isError = true;
                read.error.reason = getReason(read.error.reason, "Ch??a c?? th???i h???n " + contractName);
            } else {
                String durationContract = StringUtils.convertCellToString(contractDurationCell);
                read.error.contractDuration = durationContract;
                if (!StringUtils.isDouble(durationContract)) {
                    isError = true;
                    read.error.reason = getReason(read.error.reason, "Th???i h???n " + contractName + " kh??ng ??? d???ng s???");
                } else {
                    try {
                        int duration = (int) Double.parseDouble(durationContract);
                        ContractDuration contractDuration = ContractDuration.of(duration);
                        read.error.contractDuration = contractDuration.getVietnameseStringValue();
                        read.success.setContractDuration(contractDuration);

                        if (read.success.getContractType().equals(ContractType.LABOR_CONTRACT)) {
                            if (category.equals(ContractImportCategory.LABOR)) {
                                if (contractDuration.equals(ContractDuration.ONE_YEAR) || contractDuration.equals(ContractDuration.TWO_YEAR)) {
                                    read.success.setContractDuration(contractDuration);
                                } else {
                                    isError = true;
                                    String error = (duration == 0) ? "H???p ?????ng c?? ??ang ch???n lo???i v?? th???i h???n" : "H???p ?????ng lao ?????ng c?? kh??ng c?? th???i h???n " + duration + " th??ng";
                                    read.error.reason = getReason(read.error.reason, error);
                                }
                            }
                        } else if (read.success.getContractType().equals(ContractType.PROBATIONARY_CONTRACT)) {
                            if (contractDuration.equals(ContractDuration.TWO_MONTH) || contractDuration.equals(ContractDuration.ONE_MONTH) || contractDuration.equals(ContractDuration.THREE_MONTH) || contractDuration.equals(ContractDuration.SIX_MONTH)) {
                                read.success.setContractDuration(contractDuration);
                            } else {
                                isError = true;
                                String error = !category.equals(ContractImportCategory.PROBATION) ? "H???p ?????ng th??? vi???c c?? kh??ng th??? c?? th???i h???n " + duration + " th??ng" : "H???p ?????ng th??? vi???c kh??ng c?? th???i h???n " + duration + " th??ng";
                                read.error.reason = getReason(read.error.reason, error);
                            }
                        }

                        if (read.success.getEffectiveDate() != null) {
                            read.success.setSignedDate(read.success.getEffectiveDate());
                            read.success.setExpiredDate(this.getExpiredDate(read.success.getEffectiveDate(), read.success.getContractDuration()));
                        }
                    } catch (NumberFormatException exception) {
                        isError = true;
                        read.error.contractDuration = durationContract;
                        read.error.reason = getReason(read.error.reason, "Th???i h???n h???p ?????ng c?? kh??ng ??? d???ng s???");
                    }

                }

            }
            // M?? nh??n vi??n
            Cell employeeCodeCell = currentRow.getCell(CellReference.convertColStringToIndex("K"));
            if (employeeCodeCell == null || StringUtils.convertCellToString(employeeCodeCell) == null) {
                isError = true;
                read.employeeCode = null;
                read.error.reason = getReason(read.error.reason, "Ch??a c?? m?? nh??n vi??n");
            } else {
                String employeeCode = StringUtils.convertCellToString(employeeCodeCell);
                if (employeeCodeCell.getCellType().equals(CellType.NUMERIC)) {
                    employeeCode = (int) Double.parseDouble(employeeCode) + "";
                }
                read.employeeCode = employeeCode;
                read.success.setEmployeeCode(employeeCode);
                read.error.employeeCode = employeeCode;
            }
//            System.out.println("Tr???ng th??i isError sau khi ?????c d??? li???u employeeCode " + isError);

            // T??n nh??n vi??n
//            System.out.println("Tr???ng th??i isError tr?????c khi ?????c d??? li???u employeeName " + isError);
            Cell employeeNameCell = currentRow.getCell(CellReference.convertColStringToIndex("L"));
            if (employeeNameCell == null || StringUtils.convertCellToString(employeeNameCell) == null) {
                isError = true;
                read.error.reason = getReason(read.error.reason, "Ch??a c?? nh??n vi??n");
            } else {
                read.success.setEmployeeName(StringUtils.convertCellToString(employeeNameCell));
                read.error.employeeName = StringUtils.convertCellToString(employeeNameCell);
            }
//            System.out.println("Tr???ng th??i isError tr?????c khi ?????c d??? li???u employeeName " + isError);

            // Qu???c t???ch
//            System.out.println("Tr???ng th??i isError tr?????c khi ?????c d??? li???u nationality " + isError);
            Cell nationalityCell = currentRow.getCell(CellReference.convertColStringToIndex("M"));
            if (nationalityCell == null || StringUtils.convertCellToString(nationalityCell) == null) {
                isError = true;
                read.error.reason = getReason(read.error.reason, "Ch??a c?? qu???c t???ch");
            } else {
                read.success.setNationality(StringUtils.convertCellToString(nationalityCell));
                read.error.nationality = StringUtils.convertCellToString(nationalityCell);
            }
//            System.out.println("Tr???ng th??i isError sau khi ?????c d??? li???u nationality " + isError);

            // Ng??y sinh
//            System.out.println("Tr???ng th??i isError tr?????c khi ?????c d??? li???u birthDate " + isError);
            Cell birthDateCell = currentRow.getCell(CellReference.convertColStringToIndex("N"));
            if (birthDateCell == null || DateUtils.convertCellDate(birthDateCell, dateFormat) == null) {
                isError = true;
                read.error.reason = getReason(read.error.reason, "Ch??a c?? ng??y sinh");
            } else {
                read.success.setBirthDate(DateUtils.convertDateToLocalDate(DateUtils.convertCellDate(birthDateCell, dateFormat)));
                read.error.birthDate = DateUtils.convertDateToString(DateUtils.convertCellDate(birthDateCell, dateFormat), dateFormat);
            }
//            System.out.println("Tr???ng th??i isError sau khi ?????c d??? li???u birthDate " + isError);

            // N??i sinh
//            System.out.println("Tr???ng th??i isError tr?????c khi ?????c d??? li???u placeOfBirth " + isError);
            Cell placeOfBirthCell = currentRow.getCell(CellReference.convertColStringToIndex("P"));
            if (placeOfBirthCell == null || StringUtils.convertCellToString(placeOfBirthCell) == null) {
                isError = true;
                read.error.reason = getReason(read.error.reason, "Ch??a c?? n??i sinh");
            } else {
                read.success.setPlaceOfBirth(StringUtils.convertCellToString(placeOfBirthCell));
                read.error.placeOfBirth = StringUtils.convertCellToString(placeOfBirthCell);
            }
//            System.out.println("Tr???ng th??i isError sau khi ?????c d??? li???u placeOfBirth " + isError);

            // Gi???i t??nh
//            System.out.println("Tr???ng th??i isError tr?????c khi ?????c d??? li???u gender " + isError);
            Cell genderCell = currentRow.getCell(CellReference.convertColStringToIndex("Q"));
            if (genderCell == null || StringUtils.convertCellToString(genderCell) == null) {
                isError = true;
                read.error.reason = getReason(read.error.reason, "Ch??a c?? gi???i t??nh");
            } else {
                read.error.gender = StringUtils.convertCellToString(genderCell);
                read.success.setGender(StringUtils.convertCellToString(genderCell).equals("Nam") ? Gender.MALE : Gender.FEMALE);
            }
//            System.out.println("Tr???ng th??i isError sau khi ?????c d??? li???u gender " + isError);

            // Tr??nh ?????
//            System.out.println("Tr???ng th??i isError tr?????c khi ?????c d??? li???u trainingLevel " + isError);
            Cell trainingLevelCell = currentRow.getCell(CellReference.convertColStringToIndex("R"));
            if (trainingLevelCell == null || StringUtils.convertCellToString(trainingLevelCell) == null) {
                isError = true;
                read.error.reason = getReason(read.error.reason, "Ch??a c?? tr??nh ?????");
            } else {
                read.success.setTrainingLevel(StringUtils.convertCellToString(trainingLevelCell));
                read.error.trainingLevel = StringUtils.convertCellToString(trainingLevelCell);
            }
//            System.out.println("Tr???ng th??i isError sau khi ?????c d??? li???u trainingLevel " + isError);

            // Chuy??n ng??nh
//            System.out.println("Tr???ng th??i isError tr?????c khi ?????c d??? li???u trainingSpeciality " + isError);
            Cell trainingSpecialityCell = currentRow.getCell(CellReference.convertColStringToIndex("S"));
            if (trainingSpecialityCell == null || StringUtils.convertCellToString(trainingLevelCell) == null) {
                isError = true;
                read.error.reason = getReason(read.error.reason, "Ch??a c?? chuy??n ng??nh");
            } else {
                read.success.setTrainingSpeciality(StringUtils.convertCellToString(trainingSpecialityCell));
                read.error.trainingSpeciality = StringUtils.convertCellToString(trainingSpecialityCell);
            }
//            System.out.println("Tr???ng th??i isError sau khi ?????c d??? li???u trainingSpeciality " + isError);

            // S??? CMT/CCCD
//            System.out.println("Tr???ng th??i isError tr?????c khi ?????c d??? li???u personalIdNumber " + isError);
            Cell personalIdNumberCell = currentRow.getCell(CellReference.convertColStringToIndex("T"));
            if (personalIdNumberCell == null || StringUtils.convertCellToString(personalIdNumberCell) == null) {
                isError = true;
                read.error.reason = getReason(read.error.reason, "Ch??a c?? s??? CMT/CCCD");
            } else {
                String personalIdNumber = StringUtils.convertCellToString(personalIdNumberCell);
                if (StringUtils.isOnlyNumber(personalIdNumber)) {
                    read.success.setPersonalIdNumber(StringUtils.convertCellToString(personalIdNumberCell));
                    read.error.personalIdNumber = StringUtils.convertCellToString(personalIdNumberCell);
                } else {
                    isError = true;
                    read.error.personalIdNumber = personalIdNumber;
                    read.error.reason = getReason(read.error.reason, "S??? CMT/CCCD kh??ng ph???i ?????nh d???ng s???");
                }
            }
//            System.out.println("Tr???ng th??i isError sau khi ?????c d??? li???u personalIdNumber " + isError);

            // Ng??y c???p
//            System.out.println("Tr???ng th??i isError tr?????c khi ?????c d??? li???u personalIdIssuedDate " + isError);
            Cell personalIdIssuedDateCell = currentRow.getCell(CellReference.convertColStringToIndex("U"));

            if (personalIdIssuedDateCell == null || DateUtils.convertCellDate(personalIdIssuedDateCell, dateFormat) == null) {
                isError = true;
                read.error.reason = getReason(read.error.reason, "Ch??a c?? ng??y c???p CMT/CCCD");
            } else {
                read.success.setPersonalIdIssuedDate(DateUtils.convertDateToLocalDate(DateUtils.convertCellDate(personalIdIssuedDateCell, dateFormat)));
                read.error.personalIdIssuedDate = DateUtils.convertDateToString(DateUtils.convertCellDate(personalIdIssuedDateCell, dateFormat), dateFormat);
            }
//            System.out.println("Tr???ng th??i isError sau khi ?????c d??? li???u personalIdIssuedDate " + isError);

            // N??i c???p
//            System.out.println("Tr???ng th??i isError tr?????c khi ?????c d??? li???u personalIdIssuedPlace " + isError);
            Cell personalIdIssuedPlaceCell = currentRow.getCell(CellReference.convertColStringToIndex("V"));
            if (personalIdIssuedPlaceCell == null || StringUtils.convertCellToString(personalIdIssuedPlaceCell) == null) {
                isError = true;
                read.error.reason = getReason(read.error.reason, "Ch??a c?? n??i c???p CMT/CCCD");
            } else {
                read.success.setPersonalIdIssuedPlace(StringUtils.convertCellToString(personalIdIssuedPlaceCell));
                read.error.personalIdIssuedPlace = StringUtils.convertCellToString(personalIdIssuedPlaceCell);
            }

//            System.out.println("Tr???ng th??i isError sau khi ?????c d??? li???u personalIdIssuedPlace " + isError);

            // ?????a ch??? th?????ng tr??
            Cell permanentAddressCell = currentRow.getCell(CellReference.convertColStringToIndex("W"));
            if (permanentAddressCell == null || StringUtils.convertCellToString(permanentAddressCell) == null) {
                isError = true;
                read.error.reason = getReason(read.error.reason, "Ch??a c?? ?????a ch??? th?????ng tr??");
            } else {
                read.success.setPermanentAddress(StringUtils.convertCellToString(permanentAddressCell));
                read.error.permanentAddress = StringUtils.convertCellToString(permanentAddressCell);
            }

            // S??? ??i???n tho???i
//            System.out.println("Tr???ng th??i isError tr?????c khi ?????c d??? li???u mobileNumber " + isError);
            Cell mobileNumberCell = currentRow.getCell(CellReference.convertColStringToIndex("X"));
            if (mobileNumberCell == null || StringUtils.convertCellToString(mobileNumberCell) == null) {
                isError = true;
                read.error.reason = getReason(read.error.reason, "Ch??a c?? s??? ??i???n tho???i");
            } else {
                String mobileNumber = StringUtils.convertCellToString(mobileNumberCell);
                if (!StringUtils.isOnlyNumber(mobileNumber)) {
                    isError = true;
                    read.error.mobileNumber = mobileNumber;
                    read.error.reason = getReason(read.error.reason, "S??? ??i???n tho???i kh??ng ph???i d???ng s???");
                } else {
                    read.success.setMobileNumber(StringUtils.convertCellToString(mobileNumberCell));
                    read.error.mobileNumber = StringUtils.convertCellToString(mobileNumberCell);
                }
            }
//            System.out.println("Tr???ng th??i isError sau khi ?????c d??? li???u mobileNumber " + isError);

            // T??n v??? tr??
            Cell positionNameCell = currentRow.getCell(CellReference.convertColStringToIndex("AA"));
            if (positionNameCell == null || StringUtils.convertCellToString(positionNameCell) == null) {
                isError = true;
                read.error.reason = getReason(read.error.reason, "Ch??a c?? t??n v??? tr??");
            } else {
                read.success.setPositionName(StringUtils.convertCellToString(positionNameCell));
                read.error.positionName = StringUtils.convertCellToString(positionNameCell);
            }
            if (read.success.getContractType().equals(ContractType.LABOR_CONTRACT) && isLabor) {
                // H??? s??? l????ng c??
                Cell payRateCell = currentRow.getCell(CellReference.convertColStringToIndex("AE"));
                if (payRateCell != null && StringUtils.convertCellToString(payRateCell) != null) {
                    String payRate = StringUtils.convertCellToString(payRateCell);
                    read.error.payRate = payRate;
                    if (!StringUtils.isDouble(payRate)) {
                        isError = true;
                        String message = category.equals(ContractImportCategory.LABOR) ? "H??? s??? l????ng H?? c?? kh??ng ph???i d???ng s???" : "H??? s??? l????ng kh??ng ph???i d???ng s???";
                        read.error.reason = getReason(read.error.reason, message);
                    } else {
                        read.success.setPayRate(payRate);
                    }
                } else {
                    if (onlyOneLabor) {
                        isError = true;
                        read.error.reason = getReason(read.error.reason, "Ch??a c?? h??? s??? l????ng");
                    }
                }
                // Ng???ch c??
                Cell payGradeCell = currentRow.getCell(CellReference.convertColStringToIndex("AF"));
                if (payGradeCell != null && StringUtils.convertCellToString(payGradeCell) != null) {
                    String payGrade = StringUtils.convertCellToString(payGradeCell);
                    read.success.setPayGrade(payGrade);
                    read.error.payGrade = payGrade;
                } else {
                    if (onlyOneLabor) {
                        isError = true;
                        read.error.reason = getReason(read.error.reason, "Ch??a c?? ng???ch l????ng");
                    }
                }

                // B???c c??
                Cell payRangeCell = currentRow.getCell(CellReference.convertColStringToIndex("AG"));
                if (payRangeCell != null && StringUtils.convertCellToString(payRangeCell) != null) {
                    String payRange = StringUtils.convertCellToString(payRangeCell);
                    read.error.payRange = payRange;
                    if (!StringUtils.isDouble(payRange)) {
                        isError = true;
                        String message = category.equals(ContractImportCategory.LABOR) ? "B???c l????ng H?? c?? kh??ng ph???i s???" : "B???c l????ng kh??ng ph???i s???";
                        read.error.reason = getReason(read.error.reason, message);
                    } else {
                        read.success.setPayRange(payRange);
                        read.error.payRange = payRange;
                    }
                } else {
                    if (onlyOneLabor) {
                        isError = true;
                        read.error.reason = getReason(read.error.reason, "Ch??a c?? b???c l????ng");
                    }
                }
            }
            // L????ng c?? b???n
            Cell negotiateSalaryCell = currentRow.getCell(CellReference.convertColStringToIndex("BY"));
            if (negotiateSalaryCell != null && StringUtils.convertCellToString(negotiateSalaryCell) != null) {
                String salary = StringUtils.convertCellToString(negotiateSalaryCell);
                if (!StringUtils.isDouble(salary)) {
                    isError = true;
                    read.error.reason = getReason(read.error.reason, "L????ng c?? b???n " + contractName + " kh??ng ??? d???ng s???");
                    read.error.basicSalary = salary;
                    read.error.negotiateSalary = salary;
                } else {
                    if (read.success.getContractType().equals(ContractType.LABOR_CONTRACT)) {
                        read.success.setBasicSalary(salary);
                        read.error.basicSalary = StringUtils.numberCurrency(new Locale("vi", "VN"), Float.parseFloat(salary));
                    } else if (read.success.getContractType().equals(ContractType.PROBATIONARY_CONTRACT)) {
                        read.success.setNegotiateSalary((int) Double.parseDouble(salary));
                        read.error.negotiateSalary = StringUtils.numberCurrency(new Locale("vi", "VN"), Float.parseFloat(salary));
                    } else {
                        read.error.basicSalary = salary;
                        read.error.negotiateSalary = salary;
                    }
                }
            } else {
                if (onlyOneLabor) {
                    isError = true;
                    read.error.reason = getReason(read.error.reason, "Ch??a c?? l????ng c?? b???n " + contractName);
                }
            }
            // Ph???n tr??m l????ng
            Cell percentSalaryCell = currentRow.getCell(CellReference.convertColStringToIndex("BZ"));
            if (read.success.getContractType().equals(ContractType.PROBATIONARY_CONTRACT)) {
                if (percentSalaryCell != null && StringUtils.convertCellToString(percentSalaryCell) != null) {
                    String percent = StringUtils.convertCellToString(percentSalaryCell);
                    read.error.percentSalary = percent;
                    if (!StringUtils.isDouble(percent)) {
                        isError = true;
                        read.error.reason = getReason(read.error.reason, "Ph???n tr??m l????ng " + contractName + " kh??ng ph???i l?? s???");
                    } else {
                        float percentFloat = (float) Double.parseDouble(percent);
                        if (percentFloat > 0 && percentFloat <= 100f) {
                            read.success.setPercentSalary(percentFloat);
                        } else {
                            isError = true;
                            read.error.reason = getReason(read.error.reason, "Ph???n tr??m l????ng " + contractName + " kh??ng n???m t??? 0 ?????n 100");
                        }
                    }
                }
            }
            if (read.success.getContractType().equals(ContractType.PROBATIONARY_CONTRACT)) {
                // T??n ng??n h??ng
                Cell bankCell = currentRow.getCell(CellReference.convertColStringToIndex("CC"));
                if (bankCell == null || StringUtils.convertCellToString(bankCell) == null) {
                    if (category.equals(ContractImportCategory.PROBATION)) {
                        isError = true;
                        read.error.reason = getReason(read.error.reason, "Ch??a c?? t??n ng??n h??ng");
                    }
                } else {
                    read.error.bank = StringUtils.convertCellToString(bankCell);
                    read.success.setBank(StringUtils.convertCellToString(bankCell));
                }
//                System.out.println("Tr???ng th??i isError sau khi ?????c d??? li???u bank " + isError);
                // M?? s??? ng??n h??ng
                Cell accountNumberCell = currentRow.getCell(CellReference.convertColStringToIndex("CD"));
                if (accountNumberCell == null || StringUtils.convertCellToString(accountNumberCell) == null) {
                    if (category.equals(ContractImportCategory.PROBATION)) {
                        isError = true;
                        read.error.reason = getReason(read.error.reason, "Ch??a c?? s??? t??i kho???n ng??n h??ng");
                    }
                } else {
                    read.error.accountNumber = StringUtils.convertCellToString(accountNumberCell);
                    read.success.setAccountNumber(StringUtils.convertCellToString(accountNumberCell));
                }
//                System.out.println("Tr???ng th??i isError sau khi ?????c d??? li???u accountNumber " + isError);

                // S??? s??? lao ?????ng
                Cell laborNumberCell = currentRow.getCell(CellReference.convertColStringToIndex("CE"));
                read.error.laborNoteNumber = StringUtils.convertCellToString(laborNumberCell);
                read.success.setLaborNoteNumber(StringUtils.convertCellToString(laborNumberCell));

                // Ng??y c???p s??? lao ?????ng
                Cell laborDateCell = currentRow.getCell(CellReference.convertColStringToIndex("CF"));
                read.success.setLaborNoteDate(DateUtils.convertDateToLocalDate(DateUtils.convertCellDate(laborDateCell, dateFormat)));
                read.error.laborNoteDate = DateUtils.convertDateToString(DateUtils.convertCellDate(laborDateCell, dateFormat), dateFormat);

                // N??i c???p s??? lao ?????ng
                Cell laborPlaceCell = currentRow.getCell(CellReference.convertColStringToIndex("CG"));
                read.success.setLaborNoteAddress(StringUtils.convertCellToString(laborPlaceCell));
                read.error.laborNoteAddress = StringUtils.convertCellToString(laborPlaceCell);
//                System.out.println("Tr???ng th??i isError sau khi ?????c d??? li???u  labor " + isError);
            }
            if (category.equals(ContractImportCategory.LABOR) || category.equals(ContractImportCategory.PROBATION_TO_LABOR)) {
                //Th???i h???n h???p ?????ng m???i
                Cell newContractDuration = currentRow.getCell(CellReference.convertColStringToIndex("CH"));
                if (newContractDuration == null || StringUtils.convertCellToString(newContractDuration) == null) {
                    isError = true;
                    read.error.reason = getReason(read.error.reason, "Ch??a c?? th???i h???n h???p ?????ng m???i");
                } else {
                    String durationContract = StringUtils.convertCellToString(newContractDuration);
                    try {
                        int duration = (int) Double.parseDouble(durationContract);
                        ContractDuration contractDuration = ContractDuration.of(duration);
                        read.error.newContractDuration = contractDuration.getVietnameseStringValue();

                        if (contractDuration.equals(ContractDuration.ONE_YEAR) || contractDuration.equals(ContractDuration.TWO_YEAR) || contractDuration.equals(ContractDuration.PERMANENCE)) {
                            read.success.setNewContractDuration(contractDuration);
                        } else {
                            isError = true;
                            read.error.reason = getReason(read.error.reason, "H???p ?????ng lao ?????ng m???i kh??ng c?? th???i h???n " + duration + " th??ng");
                        }
                    } catch (NumberFormatException exception) {
                        isError = true;
                        read.error.newContractDuration = durationContract;
                        read.error.reason = getReason(read.error.reason, "Th???i h???n h???p ?????ng m???i kh??ng ??? d???ng s???");
                    }

                }
                //S??? h???p ?????ng m???i
                Cell newContractNumber = currentRow.getCell(CellReference.convertColStringToIndex("CI"));
                if (newContractNumber == null || StringUtils.convertCellToString(newContractDuration) == null && usingTemplateOld) {
                    isError = true;
                    read.error.reason = getReason(read.error.reason, "Ch??a c?? s??? h???p ?????ng m???i");
                } else {
                    read.success.setNewContractNumber(StringUtils.convertCellToString(newContractNumber));
                    read.error.newContractNumber = StringUtils.convertCellToString(newContractNumber);
                }

                // H??? s??? l????ng m???i
                Cell newPayRateCell = currentRow.getCell(CellReference.convertColStringToIndex("CJ"));
                if (newPayRateCell == null || StringUtils.convertCellToString(newPayRateCell) == null && usingTemplateOld) {
                    isError = true;
                    read.error.reason = getReason(read.error.reason, "Ch??a c?? h??? s??? l????ng H?? m???i");
                } else {
                    String payRate = StringUtils.convertCellToString(newPayRateCell);
                    read.error.newPayRate = payRate;
                    if (!StringUtils.isDouble(payRate)) {
                        isError = true;
                        read.error.reason = getReason(read.error.reason, "H??? s??? l????ng H?? m???i kh??ng ph???i d???ng s???");
                    } else {
                        read.success.setNewPayRate(payRate);
                    }
                }
                // Ng???ch m???i
                Cell newPayGradeCell = currentRow.getCell(CellReference.convertColStringToIndex("CK"));
                if (newPayGradeCell == null || StringUtils.convertCellToString(newPayGradeCell) == null && usingTemplateOld) {
                    isError = true;
                    read.error.reason = getReason(read.error.reason, "Ch??a c?? ng???ch H?? m???i");
                } else {
                    String payGrade = StringUtils.convertCellToString(newPayGradeCell);
                    read.success.setNewPayGrade(payGrade);
                    read.error.newPayGrade = payGrade;
                }
                // B???c m???i
                Cell newPayRangeCell = currentRow.getCell(CellReference.convertColStringToIndex("CL"));

                if (newPayRangeCell == null || StringUtils.convertCellToString(newPayRangeCell) == null && usingTemplateOld) {
                    isError = true;
                    read.error.reason = getReason(read.error.reason, "Ch??a c?? b???c H?? m???i");
                } else {
                    String payRange = StringUtils.convertCellToString(newPayRangeCell);
                    read.error.newPayRange = payRange;
                    if (!StringUtils.isDouble(payRange)) {
                        isError = true;
                        read.error.reason = getReason(read.error.reason, "B???c H?? m???i kh??ng ph???i s???");
                    } else {
                        read.success.setNewPayRange(payRange);
                    }
                }
                // L????ng c?? b???n m???i
                Cell newNegotiateSalaryCell = currentRow.getCell(CellReference.convertColStringToIndex("CM"));
                if (newNegotiateSalaryCell != null && StringUtils.convertCellToString(newNegotiateSalaryCell) != null && usingTemplateOld) {
                    String salary = StringUtils.convertCellToString(newNegotiateSalaryCell);
                    read.error.newBasicSalary = salary;
                    if (!StringUtils.isDouble(salary)) {
                        isError = true;
                        read.error.reason = getReason(read.error.reason, "L????ng c?? b???n H?? m???i kh??ng ??? d???ng s???");
                    } else {
                        read.success.setNewBasicSalary(salary);
                    }
                } else {
                    isError = true;
                    read.error.reason = getReason(read.error.reason, "Ch??a c?? l????ng c?? b???n H?? m???i");
                }
            }

            reads.add(read);
            rowNumber++;
        }
        response.result = !isError;
        response.reads = reads;
        System.out.println("Tr???ng th??i k???t qu??? sau khi ?????c d??? li???u " + response.result);
        return response;
    }


    private ContractImportDTO.ContractImportResponse validateSSO(ContractImportDTO.ContractImportResponse response, SSoResponse soResponse, ContractImportCategory category) {
        if (response.result && response.reads.isEmpty()) {
            throw new NotFoundException("File b???n nh???p ??ang kh??ng ch???a d??? li???u ho???c d??ng th??? 4, c???t h???p ?????ng lao ?????ng v?? m?? nh??n vi??n ??ang ????? tr???ng");
        }
        System.out.println("Tr???ng th??i k???t qu??? tr?????c khi validateSSo " + response.result);
        AtomicReference<Boolean> isError = new AtomicReference<>(!response.result);
        Long unitId = soResponse.getUnitId();
        List<ContractImportDTO.ContractImportRead> newReads = response.reads.stream().filter(e -> !StringUtils.isBlank(e.employeeCode)).map(item -> {
            List<ContractImportDTO.ContractImportRead> listRead = response.reads.stream().filter(i -> !StringUtils.isBlank(i.employeeCode) && i.employeeCode.equals(item.employeeCode)).collect(Collectors.toList());
            if (listRead.size() > 1) {
                isError.set(true);
                item.error.reason = getReason(item.error.reason, "C?? nhi???u h??n 1 b???n ghi ??ang c?? m?? nh??n vi??n " + item.employeeCode);
            }
            Boolean isBranch = isBranch(soResponse.getUnitName());

            if (category.equals(ContractImportCategory.PROBATION)) {
                if (!isBranch) {
                    if (item.success.getNegotiateSalary() == null) {
                        isError.set(true);
                        item.error.reason = getReason(item.error.reason, "Ch??a c?? l????ng nh??n vi??n H?? th??? vi???c");
                    }
                    if (item.success.getPercentSalary() == null) {
                        isError.set(true);
                        item.error.reason = getReason(item.error.reason, "Ch??a c?? ph???n tr??m l????ng h???p ?????ng th??? vi???c");
                    }
                }
            }

            if (!item.success.getContractType().equals(ContractType.UNKNOWN) && item.success.getContractDuration() != null && item.success.getEffectiveDate() != null) {
                List<ContractEntity> contractEntities = contractRepository.getContractActiveByEmployeeCode(item.employeeCode);
                if (!contractEntities.isEmpty()) {
                    // H???p ?????ng lao ?????ng v?? chuy???n di???n up h???p ?????ng c?? l??n check h???p ?????ng c??
                    if (category.equals(ContractImportCategory.PROBATION_TO_LABOR) || category.equals(ContractImportCategory.LABOR)) {
                        if (item.success.getEffectiveDate() != null && item.success.getContractDuration() != null && item.success.getNewContractDuration() != null) {
                            LocalDate newEffectiveDate = getNewEffectiveDate(item.success.getEffectiveDate(), item.success.getContractDuration());
                            LocalDate newExpiredDate = getExpiredDate(newEffectiveDate, item.success.getNewContractDuration());
                            contractEntities.forEach(ce -> {
                                if (ce.getContractDuration().equals(ContractDuration.PERMANENCE)) {
                                    if (item.success.getNewContractDuration().equals(ContractDuration.PERMANENCE)) {
                                        isError.set(true);
                                        item.error.reason = getReason(item.error.reason, "C?? 1 h???p ?????ng kh??ng th???i h???n k???t th??c t??? ng??y " + DateUtils.convertLocalDateToString(ce.getEffectiveDate(), "dd/MM/yyyy") + " tr??n h??? th???ng");
                                    } else {
                                        if (newEffectiveDate != null && newExpiredDate != null && (ce.getEffectiveDate().isBefore(newExpiredDate))) {
                                            isError.set(true);
                                            item.error.reason = getReason(item.error.reason, "C?? 1 h???p ?????ng kh??ng th???i h???n k???t th??c t??? ng??y " + DateUtils.convertLocalDateToString(ce.getEffectiveDate(), "dd/MM/yyyy") + " tr??n h??? th???ng");
                                        }
                                    }
                                } else {
                                    if (item.success.getNewContractDuration().equals(ContractDuration.PERMANENCE)) {
                                        if (ce.getExpiredDate() != null && newEffectiveDate != null && newEffectiveDate.isBefore(ce.getExpiredDate())) {
                                            isError.set(true);
                                            item.error.reason = getReason(item.error.reason, "???? c?? 1 h???p ?????ng t??? ng??y " + DateUtils.convertLocalDateToString(ce.getEffectiveDate(), "dd/MM/yyyy") + " ?????n  ng??y " + DateUtils.convertLocalDateToString(ce.getExpiredDate(), "dd/MM/yyyy") + " tr??n h??? th???ng");
                                        }
                                    } else {
                                        if (newExpiredDate != null && newEffectiveDate != null &&
                                                !(ce.getEffectiveDate().isAfter(newExpiredDate) || ce.getExpiredDate().isBefore(newEffectiveDate) || ce.getExpiredDate().equals(newEffectiveDate) || ce.getEffectiveDate().equals(newExpiredDate))) {
                                            isError.set(true);
                                            item.error.reason = getReason(item.error.reason, "???? c?? 1 h???p ?????ng t??? ng??y " + DateUtils.convertLocalDateToString(ce.getEffectiveDate(), "dd/MM/yyyy") + " ?????n  ng??y " + DateUtils.convertLocalDateToString(ce.getExpiredDate(), "dd/MM/yyyy") + " tr??n h??? th???ng");
                                        }
                                    }
                                }
                            });
                        }
                    } else {
                        // H???p ?????ng th??? vi???c v?? chuy???n c??ng t??c
                        contractEntities.forEach(ce -> {
                            if (ce.getContractDuration().equals(ContractDuration.PERMANENCE)) {
                                if (item.success.getContractDuration().equals(ContractDuration.PERMANENCE)) {
                                    isError.set(true);
                                    item.error.reason = getReason(item.error.reason, "C?? 1 h???p ?????ng kh??ng th???i h???n k???t th??c t??? ng??y " + DateUtils.convertLocalDateToString(ce.getEffectiveDate(), "dd/MM/yyyy") + " tr??n h??? th???ng");
                                } else {
                                    if (ce.getEffectiveDate() != null && item.success.getExpiredDate() != null && ce.getEffectiveDate().isBefore(item.success.getExpiredDate())) {
                                        isError.set(true);
                                        item.error.reason = getReason(item.error.reason, "C?? 1 h???p ?????ng kh??ng th???i h???n k???t th??c t??? ng??y " + DateUtils.convertLocalDateToString(ce.getEffectiveDate(), "dd/MM/yyyy") + " tr??n h??? th???ng");
                                    }
                                }

                            } else {
                                if (item.success.getContractDuration().equals(ContractDuration.PERMANENCE)) {
                                    if (ce.getExpiredDate() != null && item.success.getEffectiveDate() != null && item.success.getEffectiveDate().isBefore(ce.getExpiredDate())) {
                                        isError.set(true);
                                        item.error.reason = getReason(item.error.reason, "???? c?? 1 h???p ?????ng t??? ng??y " + DateUtils.convertLocalDateToString(ce.getEffectiveDate(), "dd/MM/yyyy") + " ?????n  ng??y " + DateUtils.convertLocalDateToString(ce.getExpiredDate(), "dd/MM/yyyy") + " tr??n h??? th???ng");
                                    }
                                } else {
                                    if (item.success.getExpiredDate() != null && !(ce.getEffectiveDate().isAfter(item.success.getExpiredDate()) || ce.getExpiredDate().isBefore(item.success.getEffectiveDate()))) {
                                        isError.set(true);
                                        item.error.reason = getReason(item.error.reason, "???? c?? 1 h???p ?????ng t??? ng??y " + DateUtils.convertLocalDateToString(ce.getEffectiveDate(), "dd/MM/yyyy") + " ?????n  ng??y " + DateUtils.convertLocalDateToString(ce.getExpiredDate(), "dd/MM/yyyy") + " tr??n h??? th???ng");
                                    }
                                }
                            }

                        });
                    }
                } else {
                    if (category.equals(ContractImportCategory.LABOR)) {
                        if (StringUtils.isBlank(item.success.getBasicSalary())) {
                            isError.set(true);
                            item.error.reason = getReason(item.error.reason, "Ch??a c?? l????ng nh??n vi??n H?? c??");
                        }

                        if (StringUtils.isBlank(item.success.getPayGrade())) {
                            isError.set(true);
                            item.error.reason = getReason(item.error.reason, "Ch??a c?? ng???ch H?? c??");
                        }

                        if (StringUtils.isBlank(item.success.getPayRate())) {
                            isError.set(true);
                            item.error.reason = getReason(item.error.reason, "Ch??a c?? h??? s??? l????ng H?? c??");
                        }

                        if (StringUtils.isBlank(item.success.getPayRange())) {
                            isError.set(true);
                            item.error.reason = getReason(item.error.reason, "Ch??a c?? b???c l????ng H?? c??");
                        }

                        if (StringUtils.isBlank(item.success.getPayRange())) {
                            isError.set(true);
                            item.error.reason = getReason(item.error.reason, "Ch??a c?? b???c l????ng H?? c??");
                        }

                        if (StringUtils.isBlank(item.success.getBasicSalary())) {
                            isError.set(true);
                            item.error.reason = getReason(item.error.reason, "Ch??a c?? l????ng c?? b???n H?? c??");
                        }
                    } else if (category.equals(ContractImportCategory.PROBATION_TO_LABOR)) {
                        if (!isBranch) {
                            if (item.success.getNegotiateSalary() == null) {
                                isError.set(true);
                                item.error.reason = getReason(item.error.reason, "Ch??a c?? l????ng nh??n vi??n H?? th??? vi???c c??");
                            }

                            if (item.success.getPercentSalary() == null) {
                                isError.set(true);
                                item.error.reason = getReason(item.error.reason, "Ch??a c?? ph???n tr??m l????ng h???p ?????ng th??? vi???c c??");
                            }
                        }
                    }
                }
            }
            EmployeeVhrEntity employeeVhrEntity = employeeVhrRepository.findByEmployeeCode(item.employeeCode).orElse(null);
            if (employeeVhrEntity == null) {
                isError.set(true);
                item.error.reason = getReason(item.error.reason, "Kh??ng t???n t???i m?? nh??n vi??n tr??n h??? th???ng SSO");
            } else if (!StringUtils.isBlank(item.success.getEmployeeName()) && !employeeVhrEntity.getFullname().equals(item.success.getEmployeeName())) {
                isError.set(true);
                item.error.reason = getReason(item.error.reason, "Kh??ng tr??ng m?? nh??n vi??n v?? t??n nh??n vi??n tr??n h??? th???ng SSO");
            } else {
                if (!Objects.equals(unitId, VPSConstant.ALL_PROVINCE_UNIT_ID)) {
                    VhrFutureOrganizationDTO.DepartmentUnitResponse departmentUnitResponse = vhrFutureOrganizationService.getDepartmentAndUnitFromOrganization(employeeVhrEntity.getOrganizationId());
                    if (!Objects.equals(unitId, departmentUnitResponse.unitId)) {
                        isError.set(true);
                        item.error.reason = getReason(item.error.reason, "B???n kh??ng c?? quy???n c???p nh???t h???p ?????ng cho nh??n vi??n ??? ????n v??? " + departmentUnitResponse.unitName);
                    }
                }
            }

            if (item.success.getPositionName() != null) {
                PositionEntity positionEntity = positionRepository.findFirstByPositionName(item.success.getPositionName()).orElse(null);
                if (positionEntity == null) {
                    isError.set(true);
                    item.error.reason = getReason(item.error.reason, "Kh??ng t???n t???i ch???c danh tr??n h??? th???ng");
                }
            }

            List<ContractImportDTO.ContractImportRead> listReadMobile = response.reads.stream().filter(i -> i.success.getMobileNumber() != null && i.success.getMobileNumber().equals(item.success.getMobileNumber())).collect(Collectors.toList());
            if (listReadMobile.size() > 1) {
                isError.set(true);
                item.error.reason = getReason(item.error.reason, "C?? nhi???u h??n  1 b???n ghi c?? s??? ??i???n tho???i " + item.success.getMobileNumber());
            }

            List<ContractImportDTO.ContractImportRead> listReadPersonId = response.reads.stream().filter(i -> i.success.getPersonalIdNumber() != null && i.success.getPersonalIdNumber().equals(item.success.getPersonalIdNumber())).collect(Collectors.toList());
            if (listReadPersonId.size() > 1) {
                isError.set(true);
                item.error.reason = getReason(item.error.reason, "C?? nhi???u h??n  1 b???n ghi c?? s??? CMT/CCCD " + item.success.getPersonalIdNumber());
            }

            return item;
        }).collect(Collectors.toList());
        response.result = !isError.get();
        response.reads = newReads;
        System.out.println("Tr???ng th??i k???t qu??? tr?????c khi validateSSo " + response.result);
        return response;
    }


    private Boolean createLaborContract(SSoResponse soResponse, List<ContractImportDTO.ContractImportModel> contractImportModels, ContractImportDTO.ContractImportRequest request, Map<String, PositionEntity> positionEntityMap, List<EmployeeVhrEntity> employeeVhrEntityList, Map<String, EmployeeVhrEntity> employeeVhrEntityMap) {

        List<ContractEntity> oldContractEntityList = createOldContract(contractImportModels, employeeVhrEntityMap, positionEntityMap, ContractType.LABOR_CONTRACT);
        List<ContractEntity> newContractEntityList = createNewContract(contractImportModels, employeeVhrEntityMap, positionEntityMap);
        ResignSessionEntity resignSessionEntity = createResignSession(request.quarter, request.year, DateUtils.getFirstLocalDate(request.quarter, request.year), DateUtils.getLastLocalDate(request.quarter, request.year), ResignType.LABOR, soResponse.getUnitId(), soResponse.getUnitName());

        List<EmployeeMonthlyReviewEntity> employeeMonthlyReviewEntityList = createMonthlyReviews(request.quarter, request.year, employeeVhrEntityList);

        List<ContractEntity> contractOldEntities = contractRepository.saveAll(oldContractEntityList);
        List<ContractEntity> contractNewEntities = contractRepository.saveAll(newContractEntityList);

        List<ResignSessionContractEntity> resignSessionContractEntityList = addOldContractToResignSession(contractOldEntities, contractNewEntities, resignSessionEntity);
        resignSessionRepository.save(resignSessionEntity);
        employeeMonthlyReviewRepository.saveAll(employeeMonthlyReviewEntityList);
        resignSessionContractRepository.saveAll(resignSessionContractEntityList);
        this.generateContractFile(contractOldEntities, true, false, ResignStatus.HR_CREATED_FILE_4_EMPLOYEE_2_SIGN);
        this.generateContractFile(contractNewEntities, true, true, ResignStatus.HR_CREATED_FILE_4_EMPLOYEE_2_SIGN);
        return true;
    }

    private Boolean createProbationContract(SSoResponse soResponse, List<ContractImportDTO.ContractImportModel> contractImportModels, ContractImportDTO.ContractImportRequest request, Map<String, PositionEntity> positionEntityMap, List<EmployeeVhrEntity> employeeVhrEntityList, Map<String, EmployeeVhrEntity> employeeVhrEntityMap) {
        List<ProbationaryContractEntity> probationaryContractEntities = contractImportModels.stream().map(item -> {
            PositionEntity positionEntity = positionEntityMap.get(item.getPositionName());
            if (positionEntity == null) {
                throw new BadRequestException("Kh??ng t???n t???i v??? tr?? " + item.getPositionName() + " tr??n VPS");
            }
            EmployeeVhrEntity employeeVhrEntity = employeeVhrEntityMap.get(item.getEmployeeCode());
            ProbationaryContractEntity probationaryContractEntity = objectMapper.convertValue(item, ProbationaryContractEntity.class);
            if (employeeVhrEntity != null) {
                VhrFutureOrganizationDTO.DepartmentUnitResponse departmentUnitResponse = vhrFutureOrganizationService.getDepartmentAndUnitFromOrganization(employeeVhrEntity.getOrganizationId());

                if (departmentUnitResponse.unitId == null || departmentUnitResponse.departmentId == null) {
                    throw new BadRequestException("Kh??ng th??? t??m th???y ????n v???, ph??ng ban c???a h???p ?????ng c??  m?? nh??n vi??n " + probationaryContractEntity.getEmployeeCode());
                }
                probationaryContractEntity.setUnitId(departmentUnitResponse.unitId);
                probationaryContractEntity.setDepartmentId(departmentUnitResponse.departmentId);

                probationaryContractEntity.setPositionId(positionEntity.getPositionId());
                probationaryContractEntity.setPositionCode(positionEntity.getPositionCode());
                probationaryContractEntity.setPositionName(positionEntity.getPositionName());

                probationaryContractEntity.setEmployeeId(employeeVhrEntity.getEmployeeId());
                probationaryContractEntity.setEmployeeCode(employeeVhrEntity.getEmployeeCode());
                probationaryContractEntity.setEmployeeName(employeeVhrEntity.getFullname());

                probationaryContractEntity.setIsActive(true);
                probationaryContractEntity.setResignStatus(ResignStatus.NOT_IN_RESIGN_SESSION);
            }
            return probationaryContractEntity;
        }).collect(Collectors.toList());

        List<ProbationaryContractEntity> probationaryContractEntityList = probationaryContractRepository.saveAll(probationaryContractEntities);

        List<ProbationaryContractEntity> probationaryContractEntity = probationaryContractEntityList.stream().map(item -> {
            FileDTO.FileResponse fileResponse = probationaryContractService.onlyExportContract(item);
            item.setContractFile(fileResponse.fileName);
            item.setContractFileEncodePath(fileResponse.encodePath);
            item.setNewContractStatus(NewContractStatus.HR_SENT_CONTRACT_EMPLOYEE);
            item.setResignStatus(ResignStatus.NOT_IN_RESIGN_SESSION);
            String result = smsMessageService.sendSmsMessage(item.getMobileNumber(), "Ban co mot hop dong thu viec can ky dien tu. Vui long truy cap phan mem Ho so dien tu de ky hop dong.");
            return item;
        }).collect(Collectors.toList());

        probationaryContractRepository.saveAll(probationaryContractEntity);
        return true;
    }

    private Boolean createProbationToLabor(SSoResponse ssoResponse, List<ContractImportDTO.ContractImportModel> contractImportModels, ContractImportDTO.ContractImportRequest request, Map<String, PositionEntity> positionEntityMap, List<EmployeeVhrEntity> employeeVhrEntityList, Map<String, EmployeeVhrEntity> employeeVhrEntityMap) {
        List<ContractEntity> oldContractEntityList = createOldContract(contractImportModels, employeeVhrEntityMap, positionEntityMap, ContractType.PROBATIONARY_CONTRACT);
        List<ContractEntity> newContractEntityList = createNewContract(contractImportModels, employeeVhrEntityMap, positionEntityMap);
        ResignSessionEntity resignSessionEntity = createResignSession(null, null, request.startDate, request.endDate, ResignType.PROBATIONARY, ssoResponse.getUnitId(), ssoResponse.getUnitName());
        List<ContractEntity> contractOldEntities = contractRepository.saveAll(oldContractEntityList);
        List<ContractEntity> contractNewEntities = contractRepository.saveAll(newContractEntityList);

        List<ResignSessionContractEntity> resignSessionContractEntityList = addOldProbationToResignSession(contractOldEntities, contractNewEntities, resignSessionEntity);

        resignSessionRepository.save(resignSessionEntity);
        resignSessionRepository.save(resignSessionEntity);
        resignSessionContractRepository.saveAll(resignSessionContractEntityList);

        this.generateContractFile(contractOldEntities, false, false, ResignStatus.HR_CREATED_FILE_4_EMPLOYEE_2_SIGN);
        this.generateContractFile(contractNewEntities, true, true, ResignStatus.HR_CREATED_FILE_4_EMPLOYEE_2_SIGN);
        return true;
    }

    private Boolean createCorporationToCompany(SSoResponse soResponse, List<ContractImportDTO.ContractImportModel> contractImportModels, ContractImportDTO.ContractImportRequest request, Map<String, PositionEntity> positionEntityMap, List<EmployeeVhrEntity> employeeVhrEntityList, Map<String, EmployeeVhrEntity> employeeVhrEntityMap) {
        List<ContractEntity> contractEntities = contractImportModels.stream().map(item -> {
            PositionEntity positionEntity = positionEntityMap.get(item.getPositionName());
            if (positionEntity == null) {
                throw new BadRequestException("Kh??ng t???n t???i v??? tr?? " + item.getPositionName() + " tr??n VPS");
            }
            EmployeeVhrEntity employeeVhrEntity = employeeVhrEntityMap.get(item.getEmployeeCode());
            ContractEntity contractEntity = objectMapper.convertValue(item, ContractEntity.class);
            if (employeeVhrEntity != null) {
                VhrFutureOrganizationDTO.DepartmentUnitResponse departmentUnitResponse = vhrFutureOrganizationService.getDepartmentAndUnitFromOrganization(employeeVhrEntity.getOrganizationId());
                if (departmentUnitResponse.unitId == null || departmentUnitResponse.departmentId == null) {
                    throw new BadRequestException("Kh??ng th??? t??m th???y ????n v???, ph??ng ban c???a h???p ?????ng c??  m?? nh??n vi??n " + contractEntity.getEmployeeCode());
                }
                contractEntity.setUnitId(departmentUnitResponse.unitId);
                contractEntity.setDepartmentId(departmentUnitResponse.departmentId);

                contractEntity.setPositionId(positionEntity.getPositionId());
                contractEntity.setPositionCode(positionEntity.getPositionCode());
                contractEntity.setPositionName(positionEntity.getPositionName());

                contractEntity.setEmployeeId(employeeVhrEntity.getEmployeeId());
                contractEntity.setEmployeeCode(employeeVhrEntity.getEmployeeCode());
                contractEntity.setEmployeeName(employeeVhrEntity.getFullname());

                contractEntity.setIsActive(false);
                contractEntity.setNewContractStatus(NewContractStatus.RECEIVED_FROM_VOFFICE);
                contractEntity.setResignStatus(ResignStatus.HR_CREATED_FILE_4_EMPLOYEE_2_SIGN);
            } else {
                throw new NotFoundException("Kh??ng t??m th???y m?? nh??n vi??n " + contractEntity.getEmployeeCode());
            }
            return contractEntity;
        }).collect(Collectors.toList());

        List<ContractEntity> contractEntityList = contractRepository.saveAll(contractEntities);
        this.generateContractFile(contractEntityList, true, true, ResignStatus.HR_CREATED_FILE_4_EMPLOYEE_2_SIGN);
        return true;
    }


    private Boolean createLaborOld(SSoResponse soResponse, List<ContractImportDTO.ContractImportModel> contractImportModels, ContractImportDTO.ContractImportRequest request, Map<String, PositionEntity> positionEntityMap, List<EmployeeVhrEntity> employeeVhrEntityList, Map<String, EmployeeVhrEntity> employeeVhrEntityMap) {
        List<ContractEntity> contractEntities = contractImportModels.stream().map(item -> {
            PositionEntity positionEntity = positionEntityMap.get(item.getPositionName());
            if (positionEntity == null) {
                throw new BadRequestException("Kh??ng t???n t???i v??? tr?? " + item.getPositionName() + " tr??n VPS");
            }
            EmployeeVhrEntity employeeVhrEntity = employeeVhrEntityMap.get(item.getEmployeeCode());
            ContractEntity contractEntity = objectMapper.convertValue(item, ContractEntity.class);
            if (employeeVhrEntity != null) {
                VhrFutureOrganizationDTO.DepartmentUnitResponse departmentUnitResponse = vhrFutureOrganizationService.getDepartmentAndUnitFromOrganization(employeeVhrEntity.getOrganizationId());
                if (departmentUnitResponse.unitId == null || departmentUnitResponse.departmentId == null) {
                    throw new BadRequestException("Kh??ng th??? t??m th???y ????n v???, ph??ng ban c???a h???p ?????ng c??  m?? nh??n vi??n " + contractEntity.getEmployeeCode());
                }
                contractEntity.setUnitId(departmentUnitResponse.unitId);
                contractEntity.setDepartmentId(departmentUnitResponse.departmentId);

                contractEntity.setPositionId(positionEntity.getPositionId());
                contractEntity.setPositionCode(positionEntity.getPositionCode());
                contractEntity.setPositionName(positionEntity.getPositionName());

                contractEntity.setEmployeeId(employeeVhrEntity.getEmployeeId());
                contractEntity.setEmployeeCode(employeeVhrEntity.getEmployeeCode());
                contractEntity.setEmployeeName(employeeVhrEntity.getFullname());

                contractEntity.setIsActive(true);
                contractEntity.setNewContractStatus(NewContractStatus.RECEIVED_FROM_VOFFICE);
                contractEntity.setResignStatus(ResignStatus.NOT_IN_RESIGN_SESSION);
            } else {
                throw new NotFoundException("Kh??ng t??m th???y m?? nh??n vi??n " + contractEntity.getEmployeeCode());
            }
            return contractEntity;
        }).collect(Collectors.toList());

        List<ContractEntity> contractEntityList = contractRepository.saveAll(contractEntities);
        this.generateContractFile(contractEntityList, true, false, ResignStatus.NOT_IN_RESIGN_SESSION);
        return true;
    }

    private List<EmployeeVhrEntity> getAllEmployeeVhr(List<ContractImportDTO.ContractImportModel> contractEntityList) {
        Set<String> importedEmployeeCodeSet = contractEntityList.stream().map(ContractImportDTO.ContractImportModel::getEmployeeCode).collect(Collectors.toSet());

        return employeeVhrRepository.findByEmployeeCodeIn(importedEmployeeCodeSet);
    }

    private List<ContractEntity> createOldContract(List<ContractImportDTO.ContractImportModel> importContractModelList, Map<String, EmployeeVhrEntity> employeeVhrEntityMap, Map<String, PositionEntity> positionEntityMap, ContractType contractType) {
        List<ContractEntity> contractEntityList = new ArrayList<>();

        importContractModelList.forEach(obj -> {
            ContractEntity contractEntity = objectMapper.convertValue(obj, ContractEntity.class);
            PositionEntity positionEntity = positionEntityMap.get(obj.getPositionName());
            EmployeeVhrEntity employeeVhrEntity = employeeVhrEntityMap.get(contractEntity.getEmployeeCode());

            if (employeeVhrEntity != null) {
                VhrFutureOrganizationDTO.DepartmentUnitResponse departmentUnitResponse = vhrFutureOrganizationService.getDepartmentAndUnitFromOrganization(employeeVhrEntity.getOrganizationId());

                if (departmentUnitResponse.unitId == null || departmentUnitResponse.departmentId == null) {
                    throw new BadRequestException("Kh??ng t??m th???y ????n v??? ho???c ph??ng ban v???i m?? nh??n vi??n" + contractEntity.getEmployeeCode());
                }

                ContractEntity contract = contractRepository.getContractEntityExist(obj.getEmployeeCode(), obj.getEffectiveDate(), obj.getContractDuration(), obj.getExpiredDate()).orElse(null);

                if (contract != null) {
                    contractEntity = contract;
                    contractEntity.setResignStatus(ResignStatus.HR_CREATED_FILE_4_EMPLOYEE_2_SIGN);
                    contractEntity.setIsActive(true);
                    if (contract.getContractType().equals(ContractType.LABOR_CONTRACT)) {
                        contractEntity.setNewContractStatus(NewContractStatus.RECEIVED_FROM_VOFFICE);
                    }
                } else {
                    contractEntity.setContractType(contractType);

                    contractEntity.setEffectiveDate(obj.getEffectiveDate());
                    contractEntity.setExpiredDate(obj.getExpiredDate());

                    if (contractEntity.getSignedDate() == null) {
                        contractEntity.setSignedDate(obj.getEffectiveDate());
                    }

                    contractEntity.setContractDuration(obj.getContractDuration());

                    contractEntity.setPositionId(positionEntity.getPositionId());
                    contractEntity.setPositionCode(positionEntity.getPositionCode());
                    contractEntity.setPositionName(positionEntity.getPositionName());

                    contractEntity.setUnitId(departmentUnitResponse.unitId);
                    contractEntity.setDepartmentId(departmentUnitResponse.departmentId);
                }

                contractEntity.setIsActive(true);
                contractEntity.setResignStatus(ResignStatus.HR_CREATED_FILE_4_EMPLOYEE_2_SIGN);
                contractEntity.setNewContractStatus(NewContractStatus.RECEIVED_FROM_VOFFICE);

                contractEntity.setEmployeeId(employeeVhrEntity.getEmployeeId());
                contractEntity.setEmployeeCode(employeeVhrEntity.getEmployeeCode());
                contractEntity.setEmployeeName(employeeVhrEntity.getFullname());

                contractEntityList.add(contractEntity);

            } else {
                throw new NotFoundException("Kh??ng t??m th???y m?? nh??n vi??n " + contractEntity.getEmployeeCode());
            }

        });

        return contractEntityList;
    }

    private List<ContractEntity> createNewContract(List<ContractImportDTO.ContractImportModel> importContractModelList, Map<String, EmployeeVhrEntity> employeeVhrEntityMap, Map<String, PositionEntity> positionEntityMap) {
        List<ContractEntity> contractEntityList = new ArrayList<>();

        importContractModelList.forEach(obj -> {
            ContractEntity contractEntity = objectMapper.convertValue(obj, ContractEntity.class);
            contractEntity.setContractType(ContractType.LABOR_CONTRACT);
            PositionEntity positionEntity = positionEntityMap.get(obj.getPositionName());
            EmployeeVhrEntity employeeVhrEntity = employeeVhrEntityMap.get(contractEntity.getEmployeeCode());

            if (employeeVhrEntity != null) {
                VhrFutureOrganizationDTO.DepartmentUnitResponse departmentUnitResponse = vhrFutureOrganizationService.getDepartmentAndUnitFromOrganization(employeeVhrEntity.getOrganizationId());

                if (departmentUnitResponse.unitId == null || departmentUnitResponse.departmentId == null) {
                    throw new BadRequestException("Kh??ng th??? t??m th???y ????n v???, ph??ng ban c???a h???p ?????ng c??  m?? nh??n vi??n " + contractEntity.getEmployeeCode());
                }
                LocalDate effectiveDate = this.getNewEffectiveDate(obj.getEffectiveDate(), obj.getContractDuration());
                LocalDate expiredDate = this.getExpiredDate(effectiveDate, obj.getNewContractDuration());

                contractEntity.setUnitId(departmentUnitResponse.unitId);
                contractEntity.setDepartmentId(departmentUnitResponse.departmentId);

                contractEntity.setContractDuration(obj.getNewContractDuration());
                contractEntity.setEffectiveDate(effectiveDate);
                contractEntity.setExpiredDate(expiredDate);

                contractEntity.setContractNumber(obj.getNewContractNumber());
                contractEntity.setBasicSalary(obj.getNewBasicSalary());
                contractEntity.setPayGrade(obj.getNewPayGrade());
                contractEntity.setPayRange(obj.getNewPayRange());
                contractEntity.setPayRate(obj.getNewPayRate());

                contractEntity.setPositionId(positionEntity.getPositionId());
                contractEntity.setPositionCode(positionEntity.getPositionCode());
                contractEntity.setPositionName(positionEntity.getPositionName());

                contractEntity.setEmployeeId(employeeVhrEntity.getEmployeeId());
                contractEntity.setEmployeeCode(employeeVhrEntity.getEmployeeCode());
                contractEntity.setEmployeeName(employeeVhrEntity.getFullname());

                contractEntity.setIsActive(false);
                contractEntity.setResignStatus(ResignStatus.HR_CREATED_FILE_4_EMPLOYEE_2_SIGN);
                contractEntity.setNewContractStatus(NewContractStatus.RECEIVED_FROM_VOFFICE);

                contractEntityList.add(contractEntity);

            } else {
                throw new NotFoundException("Kh??ng t??m th???y m?? nh??n vi??n " + contractEntity.getEmployeeCode());
            }

        });

        return contractEntityList;
    }

    private ResignSessionEntity createResignSession(Integer quarter, Integer year, LocalDate startDate, LocalDate endDate, ResignType resignType, Long unitId, String unitName) {
        ResignSessionEntity resignSessionEntity = new ResignSessionEntity();
        List<ResignSessionEntity> resignSessionEntities = null;

        switch (resignType) {
            case LABOR:
                resignSessionEntities = resignSessionRepository.getResignSessionBySessionOfUnit(quarter, year, unitId, resignType);
                resignSessionEntity.setName("?????t t??i k?? h???p ?????ng lao ?????ng qu?? " + quarter + " n??m " + year + " " + unitName);
                resignSessionEntity.setYear(year);
                resignSessionEntity.setQuarter(quarter);
                resignSessionEntity.setStartDate(startDate);
                resignSessionEntity.setEndDate(endDate);
                break;
            case PROBATIONARY:
                resignSessionEntities = resignSessionRepository.getResignSessionBySessionOfUnit(startDate, endDate, unitId, resignType);
                resignSessionEntity.setName("?????t t??i k?? h???p ?????ng th??? vi???c " + "t??? ng??y " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " ?????n ng??y " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " " + unitName);
                resignSessionEntity.setStartDate(startDate);
                resignSessionEntity.setEndDate(endDate);
                break;
        }

        if (!resignSessionEntities.isEmpty()) {
            resignSessionEntity = resignSessionEntities.get(0);
        } else {
            resignSessionEntity.setUnitId(unitId);
            resignSessionEntity.setStatus(ResignVofficeStatus.RECEIVED);
            resignSessionEntity.setResignType(resignType);
        }
        resignSessionEntity.setTranscode(HSDTConstant.BM09DocumentPrefix + "generated " + unitName);
        resignSessionEntity.setResignStatus(ResignStatus.UPDATED_TEMP_CONTRACT_2_OFFICAL);
        return resignSessionEntity;
    }

    private List<ResignSessionContractEntity> addOldContractToResignSession(List<ContractEntity> oldContractList, List<ContractEntity> newContractList, ResignSessionEntity resignSessionEntity) {
        List<ResignSessionContractEntity> resignSessionContractEntityList = new ArrayList<>();

        Map<String, ContractEntity> newContractEmployeeCodeMap = newContractList.stream().collect(Collectors.toMap(ContractEntity::getEmployeeCode, Function.identity()));

        oldContractList.forEach(contractEntity -> {
            ResignSessionContractEntity resignSessionContractEntity = new ResignSessionContractEntity();

            resignSessionContractEntity.setContractEntity(contractEntity);
            resignSessionContractEntity.setResignSessionEntity(resignSessionEntity);
            resignSessionContractEntity.setReviewDate(LocalDate.now());
            resignSessionContractEntity.setInterviewScore(55f);
            resignSessionContractEntity.setAttitude(Attitude.PASS);
            resignSessionContractEntity.setPassStatus(ResignPassStatus.PASS);

            resignSessionContractEntity.setWorkingProgressNote("OK");
            resignSessionContractEntity.setInterviewNote("OK");
            resignSessionContractEntity.setResignNote("Ok");

            resignSessionContractEntity.setResignType(ResignType.LABOR);
            resignSessionContractEntity.setReportScore(20f);
            resignSessionContractEntity.setSpecialityScore(20f);
            resignSessionContractEntity.setAttitudeScore(8f);
            resignSessionContractEntity.setNewContractId(newContractEmployeeCodeMap.get(contractEntity.getEmployeeCode()).getContractId());

            resignSessionContractEntity.setNewContractEffectiveDate(newContractEmployeeCodeMap.get(contractEntity.getEmployeeCode()).getEffectiveDate());
            resignSessionContractEntity.setNewContractExpiredDate(newContractEmployeeCodeMap.get(contractEntity.getEmployeeCode()).getExpiredDate());
            resignSessionContractEntity.setContractDuration(newContractEmployeeCodeMap.get(contractEntity.getEmployeeCode()).getContractDuration());

            resignSessionContractEntity.setResignStatus(ResignStatus.HR_CREATED_FILE_4_EMPLOYEE_2_SIGN);
            resignSessionContractEntity.setInterviewComment("OK");

            resignSessionContractEntityList.add(resignSessionContractEntity);
        });

        return resignSessionContractEntityList;
    }

    private List<ResignSessionContractEntity> addOldProbationToResignSession(List<ContractEntity> oldContractList, List<ContractEntity> newContractList, ResignSessionEntity resignSessionEntity) {
        List<ResignSessionContractEntity> resignSessionContractEntityList = new ArrayList<>();

        Map<String, ContractEntity> newContractEmployeeCodeMap = newContractList.stream().collect(Collectors.toMap(ContractEntity::getEmployeeCode, Function.identity()));
        oldContractList.forEach(contractEntity -> {
            ResignSessionContractEntity resignSessionContractEntity = new ResignSessionContractEntity();
            resignSessionContractEntity.setContractEntity(contractEntity);
            resignSessionContractEntity.setResignSessionEntity(resignSessionEntity);
            resignSessionContractEntity.setResignType(ResignType.PROBATIONARY);

            resignSessionContractEntity.setNewContractId(newContractEmployeeCodeMap.get(contractEntity.getEmployeeCode()).getContractId());

            resignSessionContractEntity.setReportScore(20f);
            resignSessionContractEntity.setSpecialityScore(20f);
            resignSessionContractEntity.setAttitudeScore(10f);
            resignSessionContractEntity.setInterviewScore(50f);
            resignSessionContractEntity.setPassStatus(ResignPassStatus.PASS);
            resignSessionContractEntity.setResignNote("OK");

            resignSessionContractEntity.setNewContractEffectiveDate(newContractEmployeeCodeMap.get(contractEntity.getEmployeeCode()).getEffectiveDate());
            resignSessionContractEntity.setNewContractExpiredDate(newContractEmployeeCodeMap.get(contractEntity.getEmployeeCode()).getExpiredDate());
            resignSessionContractEntity.setContractDuration(newContractEmployeeCodeMap.get(contractEntity.getEmployeeCode()).getContractDuration());
            resignSessionContractEntity.setResignStatus(ResignStatus.HR_CREATED_FILE_4_EMPLOYEE_2_SIGN);

            resignSessionContractEntityList.add(resignSessionContractEntity);
        });
        return resignSessionContractEntityList;
    }

    private List<EmployeeMonthlyReviewEntity> createMonthlyReviews(Integer quarter, Integer year, List<EmployeeVhrEntity> employeeVhrEntityList) {
        List<EmployeeMonthlyReviewEntity> allEmployeesMonthlyReviewEntityList = new ArrayList<>();
        LocalDate lastLocalDate = DateUtils.getLastLocalDate(quarter, year);
        LocalDate now = LocalDate.now();
        if (now.getMonth().getValue() < lastLocalDate.getMonth().getValue()) {
            lastLocalDate = LocalDate.of(year, now.getMonth(), 1);
        }
        LocalDate localDate = lastLocalDate;

        employeeVhrEntityList.forEach(employeeVhrEntity -> {
            List<EmployeeMonthlyReviewEntity> employeeMonthlyReviewEntityList = new ArrayList<>();

            for (int i = 0; i < 16; i++) {
                EmployeeMonthlyReviewEntity employeeMonthlyReviewEntity = new EmployeeMonthlyReviewEntity();
                LocalDate month = localDate.withDayOfMonth(1).minusMonths(i);
                List<EmployeeMonthlyReviewEntity> employeeMonthlyReviewEntities = employeeMonthlyReviewRepository
                        .getEmployeeMonthlyReviewEntitiesByEmployeeIdAndMonth(employeeVhrEntity.getEmployeeId(), month);
                if (employeeMonthlyReviewEntities.isEmpty()) {
                    employeeMonthlyReviewEntity.setEmployeeId(employeeVhrEntity.getEmployeeId());
                    employeeMonthlyReviewEntity.setMonth(month);
                    employeeMonthlyReviewEntity.setGrade(KpiGrade.B);
                    employeeMonthlyReviewEntityList.add(employeeMonthlyReviewEntity);
                }
            }

            allEmployeesMonthlyReviewEntityList.addAll(employeeMonthlyReviewEntityList);
        });

        return allEmployeesMonthlyReviewEntityList;
    }

    private List<ContractEntity> generateContractFile(List<ContractEntity> contractEntities, boolean isLabor, boolean hasMessage, ResignStatus status) {
        List<ContractEntity> contractEntityList = contractEntities.stream().map(item -> {
            FileDTO.FileResponse fileResponse = null;
            if (isLabor) {
                fileResponse = laborContractService.createOnlyTempContract(item);
            } else {
                ProbationaryContractEntity probationaryContractEntity = objectMapper.convertValue(item, ProbationaryContractEntity.class);
                fileResponse = probationaryContractService.onlyExportContract(probationaryContractEntity);
            }
            if ((item.getSignedFileEncodePath() != null && item.getTransCode() != null) || item.getResignStatus().getValue() > ResignStatus.HR_CREATED_FILE_4_EMPLOYEE_2_SIGN.getValue()) {
                return item;
            }
            item.setContractFile(fileResponse.fileName);
            item.setContractFileEncodePath(fileResponse.encodePath);
            item.setResignStatus(status);
            if (hasMessage) {
                String message = isLabor ? "Ban co mot hop dong lao dong can ky dien tu. Vui long truy cap phan mem Ho so dien tu de ky hop dong." : "Ban co mot hop dong thu viec can ky dien tu. Vui long truy cap phan mem Ho so dien tu de ky hop dong.";
                String result = smsMessageService.sendSmsMessage(item.getMobileNumber(), message);
            }
            return item;
        }).collect(Collectors.toList());
        return contractRepository.saveAll(contractEntityList);
    }

    @Transactional
    @Override
    public Boolean deleteContractImport(String employeeCode) {
        List<ContractEntity> contractEntities = contractRepository.findByEmployeeCode(employeeCode);
        if (contractEntities.isEmpty()) {
            throw new NotFoundException("Ch??a c?? h???p ?????ng v???i m?? nh??n vi??n " + employeeCode);
        }
        List<ResignSessionContractEntity> resignSessionContractEntities = resignSessionContractRepository.getByEmployeeCode(employeeCode);
        List<Long> resignSessionContractIds = resignSessionContractEntities.stream().map(ResignSessionContractEntity::getResignSessionContractId).collect(Collectors.toList());
        List<ResignSessionEntity> resignSessionEntities = resignSessionRepository.findByListIds(resignSessionContractIds);

        resignSessionContractRepository.softDeleteAll(resignSessionContractIds);
        resignSessionRepository.deleteAll(resignSessionEntities);
        contractRepository.deleteAll(contractEntities);
        return true;

    }

    private String getReason(String reason, String error) {
        return StringUtils.isBlank(reason) ? error : reason + ", " + error;
    }

    private LocalDate getExpiredDate(LocalDate effectiveDate, ContractDuration contractDuration) {
        if (contractDuration.equals(ContractDuration.PERMANENCE)) {
            return null;
        }
        return effectiveDate.plusMonths(contractDuration.getValue()).minusDays(1);
    }

    private LocalDate getNewEffectiveDate(LocalDate effectiveDate, ContractDuration contractDuration) {
        if (contractDuration.equals(ContractDuration.PERMANENCE)) {
            return null;
        }
        return effectiveDate.plusMonths(contractDuration.getValue());
    }

    private Boolean isBranch(String unitName) {
        boolean result = false;
        if (unitName != null && unitName.toLowerCase().contains("chi nh??nh")) {
            result = true;
        }
        return result;
    }

}
