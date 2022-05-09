package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.viettel.hstd.constant.AcceptJobStatus;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.MapUtils;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.CvDTO.*;
import com.viettel.hstd.entity.hstd.*;
import com.viettel.hstd.entity.vps.PositionEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.filter.HSTDFilter;
import com.viettel.hstd.repository.hstd.*;
import com.viettel.hstd.repository.vps.PositionRepository;
import com.viettel.hstd.security.AuthenticationFacade;
import com.viettel.hstd.security.JwtTokenUtils;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.CvService;
import com.viettel.hstd.service.inf.VhrFutureOrganizationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CvServiceImp extends BaseService implements CvService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    Message message;

    @Autowired
    CvRepository cvRepository;

    @Autowired
    CvRepositoryWithoutSWE cvRepositoryWithoutSWE;
    //    @Autowired
//    private PositionRepository positionRepository;
    @Autowired
    JwtTokenUtils jwtTokenUtils;
    @Autowired
    private InterviewSessionCvRepository interviewSessionCvRepository;
    @Autowired
    private RecruiteeAccountRepository recruiteeAccountRepository;
    @Autowired
    MapUtils mapUtils;

    @Autowired
    private EmployeeVhrTempRepository employeeVhrTempRepository;

    @Autowired
    VhrFutureOrganizationService organizationService;

    @Autowired
    HSTDFilter hstdFilter;

    @Override
//    @PreAuthorize("hasPermission('', 'VIEW CANDIDATE')")
    public Page<CvResponse> findPage(SearchDTO searchRequest) {
        hstdFilter.unitDepartmentFilter(searchRequest, "unitId");

        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<CvEntityWithoutSWE> specs = SearchUtils.getSpecifications(searchRequest);

        Page<CvEntityWithoutSWE> list;
        if (searchRequest.pagedFlag) {
            list = cvRepositoryWithoutSWE.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = cvRepositoryWithoutSWE.findAll(p);
        }

        return list.map(obj -> convertEntity2Response(addSWEToCvEntity(obj)));
    }

    private CvEntity addSWEToCvEntity(CvEntityWithoutSWE cvEntityWithoutSWE) {
        String summaryWorkingExperience = cvRepository.getSummaryWorkingExperience(cvEntityWithoutSWE.getCvId());
        CvEntity cvEntity = objectMapper.convertValue(cvEntityWithoutSWE, CvEntity.class);
        cvEntity.setInterviewSessionCvEntities(cvEntityWithoutSWE.getInterviewSessionCvEntities());
        cvEntity.setSummaryWorkingExperience(summaryWorkingExperience);
        return cvEntity;
    }

    private CvResponse convertEntity2Response(CvEntity entity) {
        Long numberOfInterview = 0l;
        LocalDateTime latestInterviewDate = null;
        Boolean isLock = false;

        for (int i = 0; i < entity.getInterviewSessionCvEntities().size(); i++) {
            numberOfInterview += 1;
            LocalDateTime interViewDate = entity.getInterviewSessionCvEntities().get(i).getInterviewSessionEntity().getStartDate();
            if (latestInterviewDate == null) {
                latestInterviewDate = interViewDate;
            } else {
                latestInterviewDate = interViewDate.isAfter(latestInterviewDate) ? interViewDate : latestInterviewDate;
            }

            InterviewSessionCvEntity interviewSessionCvEntity = entity.getInterviewSessionCvEntities().get(i);
            AcceptJobStatus isWork = interviewSessionCvEntity.getIsWork();
            EmployeeVhrTempEntity employeeVhrTempEntity = employeeVhrTempRepository.findByInterviewSessionCvId(interviewSessionCvEntity.getInterviewSessionCvId());

            if (employeeVhrTempEntity != null) {
                isLock = employeeVhrTempEntity.getIsLock();
            }

            if (isWork != null && isWork.equals(AcceptJobStatus.DECLINE)) {
                isLock = true;
            }
        }


        CvResponse response = objectMapper.convertValue(entity, CvResponse.class);
        response.isLock = isLock;
        response.numberOfInterview = numberOfInterview;
        response.interviewState = numberOfInterview > 0;
        response.latestInterviewDate = latestInterviewDate;
        return response;
    }

    @Override
//    @PreAuthorize("hasPermission('', 'VIEW CANDIDATE')")
    public CvResponse findOneById(Long id) {
        CvEntity candidateEntity = cvRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return objectMapper.convertValue(candidateEntity, CvResponse.class);
    }

    @Override
    public Boolean delete(Long id) {
        if (!cvRepository.existsById(id))
            throw new NotFoundException(message.getMessage("message.not_found"));
        //<editor-fold desc="Xoa cac doi tuong lien quan">
        ArrayList<InterviewSessionCvEntity> list = interviewSessionCvRepository.findByCvId(id);
        if (list != null && list.size() > 0) {
            //Validate xem da tao ho so nhan su hay chua?
            boolean canDelete = list.stream().anyMatch(x -> x.getIsWork() != null && x.getIsWork().equals(AcceptJobStatus.ACCEPT));
            if (canDelete) {
               throw new BadRequestException("Ứng viên đã tạo hợp đồng nhân sự không thể xóa");
            }
            List<Long> seveIdList = list
                    .stream().map(InterviewSessionCvEntity::getInterviewSessionCvId)
                    .collect(Collectors.toList());
//            if (seveIdList != null && seveIdList.size() > 0) {
//                EmployeeVhrTempEntity employeeVhrEntity = employeeProfileRepository.findByInterviewSessionCvIdIn(seveIdList);
//                if (employeeVhrEntity != null && employeeVhrEntity.getEmployeeVhrTempId() > 0) {
//                    return false;
//                }
//            }
            interviewSessionCvRepository.softDeleteAll(seveIdList);

            ArrayList<RecruiteeAccountEntity> lstAccount = recruiteeAccountRepository
                    .findByInterviewCvIdIn(seveIdList);
            if (lstAccount != null && lstAccount.size() > 0) {
                List<Long> accountIds = lstAccount
                        .stream().map(RecruiteeAccountEntity::getRecruiteeAccountId)
                        .collect(Collectors.toList());
                recruiteeAccountRepository.softDeleteAll(accountIds);
            }
        }
        //</editor-fold>
        cvRepository.softDelete(id);
        addLog("CV", "DELETE", id.toString());
        return true;
    }

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Override
    public CvResponse create(CvRequest request) {
        SSoResponse ssoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();

        if (cvRepository.existsByEmail(request.email)) {
            throw new NotFoundException("Email đã tồn tại");
        }
        if (cvRepository.existsByPhoneNumber(request.phoneNumber)) {
            throw new NotFoundException("Số điện thoại đã tồn tại");
        }

        CvEntity candidateEntity = objectMapper.convertValue(request, CvEntity.class);
        if (candidateEntity.getPositionId() != null) {
            PositionEntity positionEntity = positionRepository.findById(candidateEntity.getPositionId())
                    .orElse(new PositionEntity());
            candidateEntity.setPositionCode(positionEntity.getPositionCode());
            candidateEntity.setApplyPosition(positionEntity.getPositionName());
        }
        candidateEntity.setUnitId(ssoResponse.getUnitId());
        candidateEntity.setUnitName(ssoResponse.getUnitName());
        candidateEntity = cvRepository.save(candidateEntity);
        addLog("CV", "CREATE", new Gson().toJson(request));
        return objectMapper.convertValue(candidateEntity, CvResponse.class);
    }

    @Override
    public CvResponse update(Long id, CvRequest request) {
        CvEntity candidateEntity = cvRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        if (!request.email.equals(candidateEntity.getEmail()) && cvRepository.existsByEmail(request.email)) {
            throw new NotFoundException("Email đã tồn tại");
        }
        if (!request.phoneNumber.equals(candidateEntity.getPhoneNumber()) && cvRepository.existsByPhoneNumber(request.phoneNumber)) {
            throw new NotFoundException("Số điện thoại đã tồn tại");
        }
//        if (!request.personalIdNumber.equals(candidateEntity.getPersonalIdNumber()) && cvRepository.existsByPersonalIdNumber(request.personalIdNumber)) {
//            throw new NotFoundException("Số cmt đã tồn tại");
//        }
        CvEntity newE = objectMapper.convertValue(request, CvEntity.class);
        mapUtils.customMap(newE, candidateEntity);
        candidateEntity.setCvId(id);

        if (candidateEntity.getPositionId() != null && !request.positionId.equals(candidateEntity.getPositionId())) {
            PositionEntity positionEntity = positionRepository.findById(candidateEntity.getPositionId())
                    .orElse(new PositionEntity());
            candidateEntity.setPositionCode(positionEntity.getPositionCode());
            candidateEntity.setApplyPosition(positionEntity.getPositionName());
        }

        candidateEntity = cvRepository.save(candidateEntity);
        addLog("CV", "UPDATE", new Gson().toJson(request));
        return objectMapper.convertValue(candidateEntity, CvResponse.class);
    }

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

//    @Override
//    public FileDTO.FileResponse importExcel(MultipartFile file) {
//        if (!TYPE.equals(file.getContentType())) {
//            return new FileDTO.FileResponse();
//        }
//        try {
//            Workbook workbook = new XSSFWorkbook(file.getInputStream());
//
//            Sheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rows = sheet.iterator();
//            List<ImportExcelModel<CvEntity>> lstImportExcel = new ArrayList<>();
//            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//            HashMap<String, String> hmPosititonCode = new HashMap<>();
//            int rowNumber = 0;
//            while (rows.hasNext()) {
//                Row currentRow = rows.next();
//                if (rowNumber < 1) {
//                    rowNumber++;
//                    continue;
//                }
//                Iterator<Cell> cellsInRow = currentRow.iterator();
//                CvEntity cvEntity = new CvEntity();
//                int cellIndex = 0;
//                ArrayList<String> lstErrors = new ArrayList<>();
//                //<editor-fold desc="Doc du lieu tung dong trong file excel">
//                while (cellsInRow.hasNext()) {
//                    Cell currentCell = cellsInRow.next();
//                    currentCell.setCellType(Cell.CELL_TYPE_STRING);
//                    switch (cellIndex) {
//                        case 0:
//                            //STT
//                            break;
//                        case 1:
//                            //Ho ten
//                            if (currentCell.getStringCellValue() == null ||
//                                    currentCell.getStringCellValue().length() == 0) {
//                                lstErrors.add("Họ và tên không được để trống");
//                            } else {
//                                cvEntity.setFullName(currentCell.getStringCellValue());
//                            }
//                            if (cvEntity.getFullName().length() > 200) {
//                                lstErrors.add("Họ và tên không được nhập nhiều hơn 200 ký tự");
//                            }
//                            break;
//                        case 2:
//                            //Gioi tinh
//                            if (currentCell.getStringCellValue().equalsIgnoreCase("nam")
//                                    || currentCell.getStringCellValue().equalsIgnoreCase("1")) {
//                                cvEntity.setGender(true);
//                            } else {
//                                cvEntity.setGender(false);
//                            }
//                            break;
//                        case 3:
//                            //Ngay sinh
//                            if (currentCell.getStringCellValue() == null ||
//                                    currentCell.getStringCellValue().length() == 0) {
//                                lstErrors.add("Ngày sinh không được để trống");
//                            } else {
//                                cvEntity.setUserBirthday(LocalDate.parse(currentCell.getStringCellValue(), format));
//                            }
//                            if (cvEntity.getUserBirthday() == null) {
//                                lstErrors.add("Ngày sinh không đúng định dạng (dd/mm/yyyy)");
//                            }
//                            break;
//                        case 4:
//                            //Email
//                            String email = currentCell.getStringCellValue();
//                            if (currentCell.getStringCellValue() == null ||
//                                    currentCell.getStringCellValue().length() == 0) {
//                                lstErrors.add("Email không được để trống");
//                            } else {
//                                cvEntity.setEmail(email);
//                                if (cvEntity.getEmail().length() > 200) {
//                                    lstErrors.add("Email không được nhập nhiều hơn 200 ký tự");
//                                }
//                            }
//                            break;
//                        case 5:
//                            //SDT
//                            if (currentCell.getStringCellValue() == null ||
//                                    currentCell.getStringCellValue().length() == 0) {
//                                lstErrors.add("Số điện thoại không được để trống");
//                            } else {
//                                cvEntity.setPhoneNumber(currentCell.getStringCellValue());
//                                if (cvEntity.getPhoneNumber().length() > 50) {
//                                    lstErrors.add("Số điện thoại không được nhập nhiều hơn 50 ký tự");
//                                }
//                            }
//
//                            break;
//                        case 6:
//                            //Dia chi
//                            if (currentCell.getStringCellValue() == null ||
//                                    currentCell.getStringCellValue().length() == 0) {
//                                lstErrors.add("Địa chỉ không được để trống");
//                            } else {
//                                cvEntity.setAddress(currentCell.getStringCellValue());
//                                if (cvEntity.getAddress().length() > 50) {
//                                    lstErrors.add("Địa chỉ không được nhập nhiều hơn 50 ký tự");
//                                }
//                            }
//                            break;
//                        case 7:
//                            //Vi tri ung tuyen
//                            if (currentCell.getStringCellValue() == null ||
//                                    currentCell.getStringCellValue().length() == 0) {
//                                lstErrors.add("Vị trí ứng tuyển không được để trống");
//                            } else {
//                                cvEntity.setPositionCode(currentCell.getStringCellValue());
//                                if (cvEntity.getPositionCode().length() > 50) {
//                                    lstErrors.add("Vị trí ứng tuyển không được nhập nhiều hơn 50 ký tự");
//                                } else {
//                                    hmPosititonCode.put(cvEntity.getPositionCode(), cvEntity.getPositionCode());
//                                }
//                            }
//
//                            break;
//                        case 8:
//                            //Ten vi tri ung tuyen
//                            if ((currentCell.getStringCellValue() == null ||
//                                    currentCell.getStringCellValue().length() == 0) && cvEntity.getPositionCode() == null) {
//                                lstErrors.add("Vị trí ứng tuyển không được để trống");
//                            } else {
//                                cvEntity.setApplyPosition(currentCell.getStringCellValue());
//                                if (cvEntity.getApplyPosition().length() > 255) {
//                                    lstErrors.add("Vị trí ứng tuyển không được nhập nhiều hơn 255 ký tự");
//                                }
//                            }
//                            break;
//                        case 9:
//                            //Trinh do
//                            cvEntity.setTechnicalExpertiseProfession(currentCell.getStringCellValue());
//                            break;
//                        case 10:
//                            //Truong dao tao
//                            cvEntity.setSchool(currentCell.getStringCellValue());
//                            break;
//                        case 11:
//                            //Chuyen nganh
//                            cvEntity.setMajor(currentCell.getStringCellValue());
//                            break;
//                        case 12:
//                            //So nam kinh nghiem
//                            cvEntity.setYearsExperience(Integer.parseInt(currentCell.getStringCellValue()));
//                            break;
//                        case 13:
//                            //Ngay ung tuyen
//                            if (currentCell.getStringCellValue() == null ||
//                                    currentCell.getStringCellValue().length() == 0) {
//                                lstErrors.add("Ngày ứng tuyển không được để trống");
//                            }
//                            cvEntity.setApplyDate(LocalDate.parse(currentCell.getStringCellValue(), format));
//                            if (cvEntity.getApplyDate() == null) {
//                                lstErrors.add("Ngày ứng tuyển không đúng định dạng (dd/mm/yyyy)");
//                            }
//                            break;
//                    }
//                    cellIndex++;
//                }
//                //</editor-fold>
//                ImportExcelModel<CvEntity> model = new ImportExcelModel<>();
//                model.setData(cvEntity);
//                model.setErrors(lstErrors);
//                lstImportExcel.add(model);
//            }
            //Neu pass qua tat ca cac loi
//            if (!lstImportExcel.stream().anyMatch(x -> x.getErrors().size() > 0)) {
//                Collection<String> values = hmPosititonCode.values();
//                ArrayList<String> listOfValues
//                        = new ArrayList<>(values);
//                //Danh sach vi tri tu vps theo ma vi tri
//                ArrayList<PositionEntity> lstPosition = positionRepository.findByPositionCodeIn(listOfValues);
//                for (ImportExcelModel<CvEntity> item : lstImportExcel) {
//                    PositionEntity position = lstPosition.stream().filter(x -> x.getPositionCode().equalsIgnoreCase(item.getData().getPositionCode())).findFirst().get();
//                    if (position != null) {
//                        item.getData().setPositionId(position.getPositionId());
//                    }
//                    cvRepository.save(item.getData());
//                }
//            } else {
//                Resource resource = new ClassPathResource("template/ExportCvError.xlsx");
//                InputStream inp = resource.getInputStream();
//                Workbook errorWorkbook = WorkbookFactory.create(inp);
//                Sheet errorSheet = workbook.getSheetAt(0);
//                CellStyle borderCellStyle = workbook.createCellStyle();
//                borderCellStyle.setBorderTop(BorderStyle.MEDIUM);
//                borderCellStyle.setBorderBottom(BorderStyle.MEDIUM);
//                borderCellStyle.setBorderLeft(BorderStyle.MEDIUM);
//                borderCellStyle.setBorderRight(BorderStyle.MEDIUM);
//                int index = 0;
//                for (ImportExcelModel<CvEntity> item : lstImportExcel) {
//                    index++;
//                    Row errorRows = errorSheet.createRow(index + 1);
//                    initCellValue(errorRows, 0, index);
//                    initCellValue(errorRows, 1, item.getData().getFullName());
//                    initCellValue(errorRows, 2, item.getData().getGender());
//                    initCellValue(errorRows, 3, item.getData().getUserBirthday());
//                    initCellValue(errorRows, 4, item.getData().getEmail());
//                    initCellValue(errorRows, 5, item.getData().getPhoneNumber());
//                    initCellValue(errorRows, 6, item.getData().getAddress());
//                    initCellValue(errorRows, 7, item.getData().getPositionCode());
//                    initCellValue(errorRows, 8, item.getData().getApplyPosition());
//                    initCellValue(errorRows, 9, item.getData().getTechnicalExpertiseProfession());
//                    initCellValue(errorRows, 10, item.getData().getSchool());
//                    initCellValue(errorRows, 11, item.getData().getMajor());
//                    initCellValue(errorRows, 12, item.getData().getYearsExperience());
//                    initCellValue(errorRows, 13, item.getData().getApplyDate());
//                    initCellValue(errorRows, 14, item.getErrors());
//                }
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                errorWorkbook.write(byteArrayOutputStream);
//                byteArrayOutputStream.close();
//                errorWorkbook.close();
//                byte[] data = Base64.encodeBase64(byteArrayOutputStream.toByteArray());
//                FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
//                fileDTO.fileName = "ImportCv_Error.xlsx";
//                fileDTO.size = (long) data.length;
//                fileDTO.type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
//                fileDTO.data = new String(data, StandardCharsets.US_ASCII);
//                return fileDTO;
//            }
//
//        } catch (IOException e) {
//            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
//        } catch (InvalidFormatException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private void initCellValue(Row row, int index, Object value) {
//        Cell tempCell = row.getCell(index);
//
//        if (value instanceof Integer) {
//            tempCell.setCellValue((Integer) value);
//        } else if (value instanceof Long) {
//            tempCell.setCellValue((Long) value);
//        } else if (value instanceof LocalDate) {
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//            LocalDate localDate = (LocalDate) value;
//            String formattedString = localDate.format(formatter);
//            tempCell.setCellValue(formattedString);
//        } else if (value instanceof ArrayList) {
//            ArrayList<String> errors = (ArrayList<String>) value;
//            String strError = "";
//            for (String item : errors) {
//                strError += item + "\n";
//            }
//            tempCell.setCellValue(strError);
//        } else {
//            tempCell.setCellValue(value.toString());
//        }
//    }

    @Override
    public List<CVExcelResponse> importExcel(List<CvRequest> request) {
        SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();

        List<CVExcelResponse> responses = new ArrayList<>();
        String firstReason;

        List<CvEntity> cvEntityList = request.stream().map(excelRequest -> objectMapper.convertValue(excelRequest, CvEntity.class)).collect(Collectors.toList());

        responses = cvEntityList.stream().map(entity -> {
                    entity.setUnitId(sSoResponse.getUnitId());
                    entity.setUnitName(sSoResponse.getUnitName());

                    CVExcelResponse response = objectMapper.convertValue(entity, CVExcelResponse.class);
                    if (response.phoneNumber != null) {
                        if (cvRepository.existsByPhoneNumber(response.phoneNumber)) {
                            response.reason = "Số điện thoại " + response.phoneNumber + " đã tồn tại";
                        }
                    } else {
                        response.reason = "Không nhân diện được số điện thoại";
                    }

                    if (response.email != null) {
                        if (cvRepository.existsByEmail(response.email)) {
                            response.reason = "Email " + response.email + "  đã tồn tại";
                        }
                    } else {
                        response.reason = "Không nhân diện được email";
                    }

                    if (response.positionCode != null) {
                        PositionEntity positionEntity = positionRepository.findFirstByPositionCode(response.positionCode)
                                .orElse(null);
                        if (positionEntity != null) {
                            entity.setPositionId(positionEntity.getPositionId());
                            entity.setPositionCode(positionEntity.getPositionCode());
                            entity.setApplyPosition(positionEntity.getPositionName());

                            response.positionCode = positionEntity.getPositionCode();
                            response.applyPosition = positionEntity.getPositionName();
                            response.positionId = positionEntity.getPositionId();
                        }

                    } else {
                        response.reason = "Không nhận diện được mã vị trí";
                    }

                    return response;
                }

        ).collect(Collectors.toList());

        firstReason = responses
                .stream()
                .parallel()
                .filter(obj -> obj.reason != null)
                .findAny()
                .orElse(new CVExcelResponse()).reason;

        if (firstReason == null) {
            cvRepository.saveAll(cvEntityList);
        } else {
            throw new BadRequestException(firstReason);
        }

        return responses;
    }

    @Override
    public Boolean updateInterviewState(Long id, Boolean interviewState) {
        CvEntity entity = cvRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy ứng viên"));
        entity.setInterviewState(interviewState);

        return true;
    }


}
