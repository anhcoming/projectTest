package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.constant.ConstantConfig;
import com.viettel.hstd.core.constant.ScheduleConstant;
import com.viettel.hstd.constant.VPSConstant;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.MapUtils;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.EmployeeInterviewSessionDTO.*;
import com.viettel.hstd.dto.hstd.InterviewSessionDTO.*;
import com.viettel.hstd.dto.vps.EmployeeVhrDTO.*;
import com.viettel.hstd.dto.vps.VhrFutureOrganizationDTO.*;
import com.viettel.hstd.entity.hstd.*;
import com.viettel.hstd.entity.vps.EmployeeVhrEntity;
import com.viettel.hstd.entity.vps.PositionEntity;
import com.viettel.hstd.entity.vps.VhrFutureOrganizationEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.filter.HSTDFilter;
import com.viettel.hstd.repository.hstd.*;

import com.viettel.hstd.repository.vps.EmployeeVhrRepository;
import com.viettel.hstd.repository.vps.PositionRepository;
import com.viettel.hstd.repository.vps.VhrFutureOrganizationRepository;
//import com.viettel.hstd.schedule.UpdateInterviewStateJob;
import com.viettel.hstd.security.AuthenticationFacade;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.InterviewSessionService;
import com.viettel.hstd.service.inf.VhrFutureOrganizationService;
import com.viettel.hstd.util.HTMLtoExcel;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
//import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InterviewSessionImp extends BaseService implements InterviewSessionService {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    Message message;

    @Autowired
    private InterviewSessionRepository interviewSessionRepository;

    @Autowired
    private EmployeeVhrRepository employeeVhrRepository;
    @Autowired
    private EmployeeInterviewSessionRepository employeeInterviewSessionRepository;

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    private InterviewSessionCvRepository interviewSessionCvRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private VhrFutureOrganizationRepository vhrFutureOrganizationRepository;

    @Autowired
    CvRepository cvRepository;

    @Autowired
    HSTDFilter hstdFilter;

    @Autowired
    VhrFutureOrganizationService organizationService;

    @Autowired
    private RecruiteeAccountRepository recruiteeAccountRepository;

    @Override
    public Page<InterviewSessionResponse> findPage(SearchDTO searchRequest) {
        hstdFilter.unitDepartmentFilter(searchRequest, "unitId");

        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<InterviewSessionEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<InterviewSessionEntity> list;
        if (searchRequest.pagedFlag) {
            list = interviewSessionRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = interviewSessionRepository.findAll(p);
        }

        Page<InterviewSessionResponse> responsePage = new PageImpl<>(convertListEntity2ListResponse(list.getContent()), pageable, list.getTotalElements());
        return responsePage;
    }


    @Override
    public InterviewSessionResponse findOneById(Long id) {
        InterviewSessionEntity interviewSessionEntity = interviewSessionRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));

        return convertEntity2Response(interviewSessionEntity);
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        InterviewSessionEntity interviewSessionEntity = interviewSessionRepository.findById(id).orElse(null);
        if (interviewSessionEntity == null) {
            throw new NotFoundException(message.getMessage("message.not_found"));
        }

        if (interviewSessionEntity.getIsLock()) {
            throw new BadRequestException("Đã có ít nhất 1 ứng viên được ký hợp đồng, Làm ơn kiểm tra lại !");
        }

        if(interviewSessionRepository.isHasContract(id)){
            throw  new BadRequestException("Đã có ít nhất 1 ứng viên trúng tuyển và được cấp tài khoản VHR, Làm ơn kiểm tra lại !");
        }

        //<editor-fold desc="Xoa thong tin lien quan">
        ArrayList<InterviewSessionCvEntity> lstCv = interviewSessionCvRepository.findByInterviewSessionId(id);

        if (lstCv != null && lstCv.size() > 0) {
            List<Long> seveIdList = lstCv
                    .stream().map(InterviewSessionCvEntity::getInterviewSessionCvId)
                    .collect(Collectors.toList());
            interviewSessionCvRepository.softDeleteAll(seveIdList);
            recruiteeAccountRepository.softDeleteAll(seveIdList);
        }

        ArrayList<EmployeeInterviewSessionEntity> lstEmployee = employeeInterviewSessionRepository.findByInterviewSessionIdIn(id);
        if (lstEmployee != null && lstEmployee.size() > 0) {
            List<Long> seveIdList = lstEmployee
                    .stream().map(EmployeeInterviewSessionEntity::getEmployeeInterviewSessionId)
                    .collect(Collectors.toList());
            employeeInterviewSessionRepository.softDeleteAll(seveIdList);
        }
        //</editor-fold>
        interviewSessionRepository.softDelete(id);
        addLog("INTERVIEW_SESSION", "DELETE", id.toString());
        return true;
    }

    @Override
    public InterviewSessionResponse create(InterviewSessionRequest request) {
        SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();

        if (request.positionId != null) fillPosition(request);
        if (request.leaderId != null) fillLeader(request);
        InterviewSessionEntity interview = objectMapper.convertValue(request, InterviewSessionEntity.class);
        if (this.checkExisted(interview.getPositionId(), interview.getStartDate(),
                interview.getEndDate())) {
            throw new BadRequestException("Đã tồn tại đợt phỏng vấn với cùng vị trí, thời gian bắt đầu và kết thúc");
        }

        interview.setUnitId(request.unitId);

        interview = interviewSessionRepository.save(interview);
//        createInterviewSchedule(interview.getInterviewSessionId());

        //<editor-fold desc="Them moi nhan vien">
        if (request.lstEmployee != null && request.lstEmployee.size() > 0) {
            for (EmployeeInterviewSessionRequest item : request.lstEmployee) {
                if (item.employeeId != null && item.employeeId > 0) {
                    EmployeeInterviewSessionEntity entity = new EmployeeInterviewSessionEntity();
                    entity.setInterviewSessionEntity(interview);
                    entity.setEmployeeId(item.employeeId);
                    entity.setEmployeeName(item.fullname);
                    employeeInterviewSessionRepository.save(entity);
                }
            }
        }
        //</editor-fold>
        addLog("INTERVIEW_SESSION", "CREATE", new Gson().toJson(request));
        return objectMapper.convertValue(interview, InterviewSessionResponse.class);
    }

    @Autowired
    MapUtils mapUtils;

    @Override
    @Transactional
    public InterviewSessionResponse update(Long id, InterviewSessionRequest request) {
        InterviewSessionEntity interviewSessionEntity = interviewSessionRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));

        if (!request.leaderId.equals(interviewSessionEntity.getLeaderId()) && request.leaderId != null) {
            fillLeader(request);
        }

        if (!request.positionId.equals(interviewSessionEntity.getPositionId()) && request.positionId != null) {
            fillPosition(request);
        }

        boolean isStartDateChange = false;
        if (!request.startDate.equals(interviewSessionEntity.getStartDate()) && request.startDate != null) {
            isStartDateChange = true;
        }

        //<editor-fold desc="gan du lieu cho dot phong van">
        interviewSessionEntity.setEndDate(request.endDate);
        interviewSessionEntity.setName(request.name);
        interviewSessionEntity.setStartDate(request.startDate);
        interviewSessionEntity.setInterviewLocation(request.interviewLocation);
        interviewSessionEntity.setInterviewSessionId(id);
        interviewSessionEntity.setPositionId(request.positionId);
        interviewSessionEntity.setPositionCode(request.positionCode);
        interviewSessionEntity.setPositionName(request.positionName);
        interviewSessionEntity.setLeaderId(request.leaderId);
        interviewSessionEntity.setLeaderName(request.leaderName);
        interviewSessionEntity.setLeaderEmail(request.leaderEmail);
        Long oldDomainDataId = interviewSessionEntity.getUnitId();
        interviewSessionEntity.setDescription(request.description);
        interviewSessionEntity.setUnitId(request.unitId);

        if (request.departmentId == null) {
            interviewSessionEntity.setDepartmentId(null);
        }

        if (request.departmentId != null && !request.departmentId.equals(interviewSessionEntity.getDepartmentId())) {
            if (vhrFutureOrganizationRepository.checkDepartmentWithUnit(request.unitId, request.departmentId)) {
                interviewSessionEntity.setDepartmentId(request.departmentId);
            } else {
                throw new NotFoundException("Không tìm thấy phòng ban trong đơn vị");
            }
        }

        //</editor-fold>
        if (request.lstEmployee == null) {
            request.lstEmployee = new ArrayList<>();
        }
        //<editor-fold desc="Cap nhat nhan vien">
        List<EmployeeInterviewSessionEntity> lstEmployee = (
                interviewSessionEntity.getEmployeeInterviewSessionEntities().stream().filter(x ->
                                request.lstEmployee.stream().anyMatch(y -> y.employeeId.equals(x.getEmployeeId())))
                        .collect(Collectors.toList()));
        if (request.lstEmployee.size() > 0) {
            int size = lstEmployee.size();
            for (EmployeeInterviewSessionRequest emp : request.lstEmployee) {
                if (emp.employeeId != null && emp.employeeId > 0) {
                    if (lstEmployee
                            .stream().noneMatch(x -> x.getEmployeeId().equals(emp.employeeId))
                            || size == 0) {
                        EmployeeInterviewSessionEntity entity = new EmployeeInterviewSessionEntity();
                        entity.setEmployeeId(emp.employeeId);
                        entity.setEmployeeName(emp.fullname);
                        entity.setInterviewSessionEntity(interviewSessionEntity);
                        lstEmployee.add(entity);
                    }
                }
            }
        } else {
            interviewSessionEntity.getEmployeeInterviewSessionEntities().clear();
        }
        interviewSessionEntity.getEmployeeInterviewSessionEntities().clear();
        interviewSessionEntity.getEmployeeInterviewSessionEntities().addAll(lstEmployee);
        //</editor-fold>
        //End cap nhat thanh vien
        interviewSessionEntity = interviewSessionRepository.save(interviewSessionEntity);

        // Create schedule to change interview state in cv
        if (isStartDateChange) {
//            createInterviewSchedule(interviewSessionEntity.getInterviewSessionId());
        }

        addLog("INTERVIEW_SESSION", "UPDATE", new Gson().toJson(request));
        return objectMapper.convertValue(interviewSessionEntity, InterviewSessionResponse.class);
    }

    private InterviewSessionResponse convertEntity2Response(InterviewSessionEntity interviewSessionEntity) {
        List<Long> employeeIds = new ArrayList<>();
        interviewSessionEntity.getEmployeeInterviewSessionEntities().forEach(obj -> {
            employeeIds.add(obj.getEmployeeId());
        });
        Map<Long, EmployeeVhrEntity> employeeVhrEntityMap = employeeVhrRepository
                .findByEmployeeIdIn(employeeIds).stream().collect(Collectors.toMap(EmployeeVhrEntity::getEmployeeId, Function.identity()));
        return convertEntity2ResponseBase(interviewSessionEntity, employeeVhrEntityMap);
    }

    private InterviewSessionResponse convertEntity2ResponseBase(InterviewSessionEntity interviewSessionEntity, Map<Long, EmployeeVhrEntity> employeeVhrEntityMap) {
        InterviewSessionResponse response = objectMapper.convertValue(interviewSessionEntity, InterviewSessionResponse.class);
        if (interviewSessionEntity.getEmployeeInterviewSessionEntities() != null
                && interviewSessionEntity.getEmployeeInterviewSessionEntities().size() > 0) {
            ArrayList<Long> lstEmpIds = new ArrayList<>();
            for (EmployeeInterviewSessionEntity emp : interviewSessionEntity.getEmployeeInterviewSessionEntities()) {
                lstEmpIds.add(emp.getEmployeeId());
            }
            List<EmployeeVhrEntity> lstEmployee = new ArrayList<>();
            lstEmpIds.forEach(obj -> lstEmployee.add(employeeVhrEntityMap.get(obj)));
            response.lstEmployee = lstEmployee.stream().map(obj -> {
                EmployeeVhrResponse employeeVhrResponse = new EmployeeVhrResponse();
                employeeVhrResponse.employeeId = obj.getEmployeeId();
                employeeVhrResponse.fullname = obj.getFullname();
                return employeeVhrResponse;
            }).collect(Collectors.toList());
        }
        response.totalCandidate = interviewSessionEntity.getInterviewSessionCvEntities().size();

        VhrFutureOrganizationEntity unitEntity = vhrFutureOrganizationRepository.findById(interviewSessionEntity.getUnitId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn vị"));
        response.unitId = unitEntity.getOrganizationId();
        response.unitName = unitEntity.getName();

        if (interviewSessionEntity.getDepartmentId() != null) {
            VhrFutureOrganizationEntity departmentEntity = vhrFutureOrganizationRepository.findById(interviewSessionEntity.getDepartmentId())
                    .orElseThrow(() -> {
                        log.error("Cant found department with id {}", interviewSessionEntity.getDepartmentId());
                        return new NotFoundException("Không tìm thấy phòng ban");
                    });
            response.departmentId = departmentEntity.getOrganizationId();
            response.departmentName = departmentEntity.getName();
        }


        return response;
    }

    private List<InterviewSessionResponse> convertListEntity2ListResponse(List<InterviewSessionEntity> interviewSessionEntityList) {
        List<InterviewSessionResponse> interviewSessionResponseList = new ArrayList<>();

//        List<Long> employeeIds = new ArrayList<>();
//        interviewSessionEntityList.forEach(obj -> {
//            obj.getEmployeeInterviewSessionEntities().forEach(obj2 -> {
//                employeeIds.add(obj2.getEmployeeId());
//            });
//        });
        Map<Long, EmployeeVhrEntity> employeeVhrEntityMap = new HashMap<>();
        interviewSessionEntityList.forEach(obj -> {
            obj.getEmployeeInterviewSessionEntities().forEach(obj2 -> {
                EmployeeVhrEntity employeeVhrEntity = new EmployeeVhrEntity();
                employeeVhrEntity.setEmployeeId(obj2.getEmployeeId());
                employeeVhrEntity.setFullname(obj2.getEmployeeName());

                employeeVhrEntityMap.put(obj2.getEmployeeId(), employeeVhrEntity);
            });
        });

        interviewSessionEntityList.forEach(obj -> {
            interviewSessionResponseList.add(convertEntity2ResponseBase(obj, employeeVhrEntityMap));
        });

        return interviewSessionResponseList;
    }

    @Override
    public Boolean checkExisted(Long positionId, LocalDateTime startDate, LocalDateTime endDate) {
        ArrayList<InterviewSessionEntity> lstInterview = interviewSessionRepository.existsByPositionId(positionId, startDate, endDate);
        return lstInterview.size() > 0;
    }

    @Override
    public void updateCvThatInterviewed(Long interviewSessionId) {
        InterviewSessionEntity entity = interviewSessionRepository
                .findById(interviewSessionId)
                .orElseThrow(() -> new NotFoundException("Không tồn tại đợt phỏng vấn"));

        List<CvEntity> cvList = entity.getInterviewSessionCvEntities()
                .stream()
                .map(InterviewSessionCvEntity::getCvEntity)
                .collect(Collectors.toList());
        cvList.forEach(obj -> obj.setInterviewState(true));
        cvRepository.saveAll(cvList);
    }

    private void fillPosition(InterviewSessionRequest request) {
        Long positionId = request.getPositionId();

        PositionEntity positionEntity = positionRepository.findById(positionId).orElse(new PositionEntity());

        request.setPositionCode(positionEntity.getPositionCode());
        request.setPositionName(positionEntity.getPositionName());
    }

    private void fillLeader(InterviewSessionRequest request) {
        Long leaderId = request.getLeaderId();

        EmployeeVhrEntity employeeEntity = employeeVhrRepository.findById(leaderId).orElse(new EmployeeVhrEntity());

        request.setLeaderName(employeeEntity.getFullname());
        request.setLeaderEmail(employeeEntity.getEmail());
    }

//    private void createInterviewSchedule(Long interviewSessionId) {
//        InterviewSessionEntity entity = interviewSessionRepository
//            .findById(interviewSessionId)
//            .orElseThrow(() -> new NotFoundException("Không tồn tại đợt phỏng vấn"));
//
//        JobDataMap jobDataMap = new JobDataMap();
//        jobDataMap.put("interviewSessionId", interviewSessionId);
//
//        JobKey jobKey = new JobKey(interviewSessionId.toString(), ScheduleConstant.INTERVIEW_SESSION_GROUP_KEY);
//        JobDetail jobDetail = JobBuilder.newJob(UpdateInterviewStateJob.class)
//            .withIdentity(jobKey)
//            .withDescription("Tạo lịch cập nhật CV với id" + entity.getInterviewSessionId() + " tại thời điểm  " + entity.getStartDate())
//            .usingJobData(jobDataMap)
//            .build();
//
//        Trigger trigger = TriggerBuilder.newTrigger()
//            .forJob(jobDetail)
//            .withIdentity(interviewSessionId.toString())
//            .withDescription("Trigger")
//            .startAt(Date.from(entity.getStartDate().atZone(ZoneId.systemDefault()).toInstant()))
//            .withSchedule(SimpleScheduleBuilder.simpleSchedule()
//                .withRepeatCount(0)
//                .withIntervalInMinutes(1))
//            .build();
//
//        try {
//            if (scheduler.checkExists(jobKey)) {
//                scheduler.deleteJob(jobKey);
//            }
//            scheduler.scheduleJob(jobDetail, trigger);
//
//        } catch (SchedulerException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public FileDTO.FileResponse exportExcel(InterviewSessionResponse request) {
        InterviewSessionEntity interview = interviewSessionRepository
                .findById(request.interviewSessionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đợt phỏng vấn"));
        if (interview.getInterviewSessionCvEntities() == null) {
            interview.setInterviewSessionCvEntities(new ArrayList<>());
        }
        try {
            Resource resource = new ClassPathResource("template/DanhSachPhongVan.xlsx");
            InputStream inp = resource.getInputStream();
            Workbook workbook = new XSSFWorkbook(inp);
            Sheet sheet = workbook.getSheetAt(0);
            CellStyle borderCellStyle = workbook.createCellStyle();
            borderCellStyle.setBorderTop(BorderStyle.MEDIUM);
            borderCellStyle.setBorderBottom(BorderStyle.MEDIUM);
            borderCellStyle.setBorderLeft(BorderStyle.MEDIUM);
            borderCellStyle.setBorderRight(BorderStyle.MEDIUM);
            LocalDate now = LocalDate.now();
            Cell tempContentCell = sheet.getRow(4).getCell(0);
            tempContentCell.setCellValue("Vị trí phỏng vấn: " + interview.getPositionName());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            tempContentCell = sheet.getRow(5).getCell(0);
            if (interview.getEndDate() != null && interview.getStartDate().toLocalDate().compareTo(interview.getEndDate().toLocalDate()) == 0) {
                String date = formatter.format(interview.getStartDate());
                formatter = DateTimeFormatter.ofPattern("HH:mm");

                tempContentCell.setCellValue("Thời gian: " + formatter.format(interview.getStartDate()) + " - " + formatter.format(interview.getEndDate()) + " - Ngày " + date);
            } else {
                String cellValue = "Thời gian: " + formatter.format(interview.getStartDate());
                tempContentCell.setCellValue(cellValue);
            }

            DateTimeFormatter ldtFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            int size = interview.getInterviewSessionCvEntities().size();
            List<InterviewSessionCvEntity> lstInterviewCv = interview.getInterviewSessionCvEntities();
            Font font = sheet.getWorkbook().createFont();
            font.setFontName("Times New Roman");
            font.setFontHeightInPoints((short) 14);
            CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
            cellStyle.setFont(font);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setWrapText(true);
            formatter = DateTimeFormatter.ofPattern("HH:mm");

            for (int i = 0; i < size; i++) {

                CvEntity cvEntity = lstInterviewCv.get(i).getCvEntity();
                Row rows = sheet.createRow(i + 9);
                Cell cell = rows.createCell(0);
                cell.setCellValue(i + 1);
                cell.setCellStyle(cellStyle);
                //Ho va ten
                cell = rows.createCell(1);
                cell.setCellValue(cvEntity != null && cvEntity.getFullName() != null ? cvEntity.getFullName() : "");
                cell.setCellStyle(cellStyle);
                //Nam sinh
                cell = rows.createCell(2);
                cell.setCellValue(cvEntity != null && cvEntity.getUserBirthday() != null ? (cvEntity.getUserBirthday().getYear() + "") : "");
                cell.setCellStyle(cellStyle);
                //Dien thoai
                cell = rows.createCell(3);
                cell.setCellValue(cvEntity != null ? cvEntity.getPhoneNumber() : "");
                cell.setCellStyle(cellStyle);
                //Email
                cell = rows.createCell(4);
                cell.setCellValue(cvEntity != null ? cvEntity.getEmail() : "");
                cell.setCellStyle(cellStyle);
                //trinh do
                cell = rows.createCell(5);
                cell.setCellValue(cvEntity != null && cvEntity.getTechnicalExpertiseProfession() != null ? cvEntity.getTechnicalExpertiseProfession() : "");
                cell.setCellStyle(cellStyle);
                //chuyen mon
                cell = rows.createCell(6);
                cell.setCellValue(cvEntity.getMajor() != null ? cvEntity.getMajor() : "");
                cell.setCellStyle(cellStyle);
                //noi dao tao
                cell = rows.createCell(7);
                cell.setCellValue(cvEntity.getSchool() != null ? cvEntity.getSchool() : "");
                cell.setCellStyle(cellStyle);
                //kinh nghiem
                cell = rows.createCell(8);
                cell.setCellValue(HTMLtoExcel.fromHtmlToCellValue(cvEntity.getSummaryWorkingExperience(), workbook));
                cell.setCellStyle(cellStyle);
                //Thoi gian phong van
                cell = rows.createCell(9);
                cell.setCellValue(lstInterviewCv.get(i).getInterviewDate() != null ? ldtFormatter.format(interview.getStartDate()) : "");
                cell.setCellStyle(cellStyle);
                //ghi chu
                cell = rows.createCell(10);
                cell.setCellValue("");
                cell.setCellStyle(cellStyle);
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            workbook.write(byteArrayOutputStream);
            byteArrayOutputStream.close();
            workbook.close();
            byte[] data = Base64.getEncoder().encode(byteArrayOutputStream.toByteArray());
            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
            fileDTO.fileName = "DanhSachPhongVan.xlsx";
            fileDTO.size = (long) data.length;
            fileDTO.type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            fileDTO.data = new String(data, StandardCharsets.US_ASCII);

            return fileDTO;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String fmt(float d) {
        if (d == (long) d)
            return String.format("%d", (long) d);
        else
            return String.format("%s", d);
    }
}
