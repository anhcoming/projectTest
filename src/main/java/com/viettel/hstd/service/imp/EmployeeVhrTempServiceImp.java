package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.constant.AttachmentDocumentStatus;
import com.viettel.hstd.constant.ContractType;
import com.viettel.hstd.constant.NewContractStatus;
import com.viettel.hstd.constant.VPSConstant;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.constant.EmployeeStatusConstant;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.MapUtils;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.core.utils.StringUtils;
import com.viettel.hstd.dto.hstd.*;
import com.viettel.hstd.dto.hstd.EmployeeVhrTempDTO.*;
import com.viettel.hstd.entity.hstd.*;
import com.viettel.hstd.entity.vps.PositionEntity;
import com.viettel.hstd.entity.vps.VhrFutureOrganizationEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.filter.HSTDFilter;
import com.viettel.hstd.repository.hstd.*;
import com.viettel.hstd.repository.vps.EmployeeVhrRepository;
import com.viettel.hstd.repository.vps.PositionRepository;
import com.viettel.hstd.repository.vps.VhrFutureOrganizationRepository;
import com.viettel.hstd.security.AuthenticationFacade;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.DocumentTypeService;
import com.viettel.hstd.service.inf.EmployeeVhrTempService;
import com.viettel.hstd.service.inf.VhrFutureOrganizationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeVhrTempServiceImp extends BaseService implements EmployeeVhrTempService {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    Message message;
    @Autowired
    MapUtils mapUtils;
    @Autowired
    private EmployeeVhrTempRepository employeeVhrTempRepository;
    @Autowired
    private RecruitProfileAttachmentRepository recruitProfileAttachmentRepository;
    @Autowired
    private DocumentTypeService documentTypeService;

    @Autowired
    private RecruiteeAccountRepository recruiteeAccountRepository;
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private TerminateContractRepository terminateContractRepository;

    @Autowired
    private VhrFutureOrganizationService organizationService;

    @Autowired
    private VhrFutureOrganizationRepository organizationRepository;

    @Autowired
    private EmployeeVhrRepository employeeVhrRepository;

    @Autowired
    private InterviewSessionRepository interviewSessionRepository;

    @Autowired
    private InterviewSessionCvRepository interviewSessionCvRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private ProvinceAreaRepository provinceAreaRepository;

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Autowired
    private HSTDFilter hstdFilter;

    @Override
    public Page<EmployeeVhrTempResponse> findPage(SearchDTO searchRequest) {
        hstdFilter.unitDepartmentFilter(searchRequest, "organizationId");
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<EmployeeVhrTempEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<EmployeeVhrTempEntity> list;
        if (searchRequest.pagedFlag) {
            list = employeeVhrTempRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = employeeVhrTempRepository.findAll(p);
        }

        return list.map(obj -> this.objectMapper.convertValue(obj, EmployeeVhrTempResponse.class));
    }

    @Override
    public EmployeeVhrTempResponse findOneById(Long id) {
        EmployeeVhrTempEntity entity = employeeVhrTempRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        EmployeeVhrTempResponse response = objectMapper.convertValue(entity, EmployeeVhrTempResponse.class);
        return response;
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        EmployeeVhrTempEntity employeeVhrTempEntity = employeeVhrTempRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        List<RecruiteeAccountEntity> recruiteeAccountEntities = recruiteeAccountRepository.findByInterviewCvId(employeeVhrTempEntity.getInterviewSessionCvId());
        if (!recruiteeAccountEntities.isEmpty()) {
            List<Long> recruiteeAccountEntityIds = recruiteeAccountEntities.stream().map(RecruiteeAccountEntity::getRecruiteeAccountId).collect(Collectors.toList());
            recruiteeAccountRepository.softDeleteAll(recruiteeAccountEntityIds);
        }
        employeeVhrTempRepository.softDelete(id);
        super.addLog("EMPLOYEE_PROFILE", "DELETE", id.toString());
        return true;
    }

    @Override
    public EmployeeVhrTempResponse create(EmployeeVhrTempRequest request) {
        EmployeeVhrTempEntity entity = objectMapper.convertValue(request, EmployeeVhrTempEntity.class);
        entity = employeeVhrTempRepository.save(entity);
        super.addLog("EMPLOYEE_PROFILE", "CREATE", new Gson().toJson(request));
        return objectMapper.convertValue(entity, EmployeeVhrTempResponse.class);
    }

    @Override
    public EmployeeVhrTempResponse update(Long id, EmployeeVhrTempRequest request) {
        EmployeeVhrTempEntity entity = employeeVhrTempRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        EmployeeVhrTempEntity newE = objectMapper.convertValue(request, EmployeeVhrTempEntity.class);
        mapUtils.customMap(newE, entity);
        entity.setEmployeeVhrTempId(id);
        entity = employeeVhrTempRepository.save(entity);
        super.addLog("EMPLOYEE_PROFILE", "UPDATE", new Gson().toJson(request));
        SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
        //<editor-fold desc="Them tai lieu dinh kem">
        updateAttachment(request, sSoResponse);
        //</editor-fold>
        return objectMapper.convertValue(entity, EmployeeVhrTempResponse.class);
    }

    @Override
    public EmployeeVhrTempResponse findByInterviewCvId(Long interviewCvId) {
        EmployeeVhrTempEntity entity = employeeVhrTempRepository.findByInterviewSessionCvId(interviewCvId);
        if (entity != null && entity.getEmployeeVhrTempId() > 0) {
            return objectMapper.convertValue(entity, EmployeeVhrTempResponse.class);
        }
        return null;
    }

    @Override
    public int lock(EmployeeVhrTempLocking data) {
        if (data == null || data.ids == null || data.ids.size() == 0) {
            return 0;
        }
        int rows = employeeVhrTempRepository.lock(true, data.ids);
        super.addLog("EMPLOYEE_PROFILE", "LOCK", data.ids.toString());
        return rows;
    }

    @Override
    public int unlock(EmployeeVhrTempLocking data) {
        if (data == null || data.ids == null || data.ids.size() == 0) {
            return 0;
        }
        int rows = employeeVhrTempRepository.lock(false, data.ids);
        super.addLog("EMPLOYEE_PROFILE", "UNLOCK", data.ids.toString());
        return rows;
    }

    @Override
    public int approval(Long id) {
        int rows = employeeVhrTempRepository.approval(EmployeeStatusConstant.approval, id);
        super.addLog("EMPLOYEE_PROFILE", "APPROVAL", id.toString());
        return rows;
    }

    @Override
    public int reject(Long id) {
        int rows = employeeVhrTempRepository.approval(EmployeeStatusConstant.reject, id);
        super.addLog("EMPLOYEE_PROFILE", "REJECT", id.toString());
        return rows;
    }


    private Boolean addDocumentAttachment(ContractType contractType, Long interviewCvId) {
        boolean hasRecord = false;
        ArrayList<RecruiteeAccountEntity> lstAccount = recruiteeAccountRepository.findByInterviewCvId(interviewCvId);
        if (contractType != null && contractType != ContractType.UNKNOWN) {
            ArrayList<DocumentTypeEntity> lstDocument = documentTypeRepository.findByType(contractType);
            for (DocumentTypeEntity item : lstDocument) {
                RecruitProfileAttachmentEntity entity = new RecruitProfileAttachmentEntity();
                entity.setInterviewSessionCvId(interviewCvId);
                entity.setDocumentTypeId(item.getDocumentTypeId());
                entity.setStatus(AttachmentDocumentStatus.PENDING);
                if (lstAccount != null && lstAccount.size() > 0) {
                    entity.setAccountId(lstAccount.get(0).getRecruiteeAccountId());
                }
                recruitProfileAttachmentRepository.save(entity);
            }
            hasRecord = true;
        }
        return hasRecord;
    }

    @Override
    public EmployeeVhrTempResponse getProfile() {
        SSoResponse ssoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
        if (ssoResponse.getId() != null && ssoResponse.getId() > 0) {
            EmployeeVhrTempEntity employeeVhrTempEntity = employeeVhrTempRepository.findByInterviewSessionCvId(ssoResponse.getInterviewSessionCvId());
            if (employeeVhrTempEntity != null) {
                EmployeeVhrTempResponse employeeVhrTempResponse = objectMapper.convertValue(employeeVhrTempEntity, EmployeeVhrTempResponse.class);
                //<editor-fold desc="Gan du lieu ho so (giay to)">
                EmployeeVhrAttachmentResponse employeeVhrAttachmentResponse = getAttachment(employeeVhrTempEntity.getEmployeeVhrTempId());
                employeeVhrTempResponse.listAttachment = employeeVhrAttachmentResponse.documentTypes;
                employeeVhrTempResponse.status = employeeVhrAttachmentResponse.status;

                VhrFutureOrganizationEntity organizationEntity = organizationRepository.findById(Long.parseLong(employeeVhrTempEntity.getOrganizationId())).orElse(null);

                assert organizationEntity != null;
                if (organizationEntity.getOrgLevelManage() >= 2) {
                    String unitIdString = StringUtils.dividedPathIntoArray(organizationEntity.getPath(), VPSConstant.UNIT_ORGANIZATION_LEVEL);

                    assert unitIdString != null;
                    employeeVhrTempResponse.unitId = Long.parseLong(unitIdString);
                    employeeVhrTempResponse.unitName = organizationEntity.getOrgNameLevel2();

                    if (organizationEntity.getOrgLevelManage() >= 3) {
                        String departmentIdString = StringUtils.dividedPathIntoArray(organizationEntity.getPath(), VPSConstant.DEPARTMENT_ORGANIZATION_LEVEL);

                        assert departmentIdString != null;
                        employeeVhrTempResponse.departmentId = Long.parseLong(departmentIdString);
                        employeeVhrTempResponse.departmentName = organizationEntity.getOrgNameLevel3();
                    }
                }

                //</editor-fold>
                if (employeeVhrTempResponse.employeeCode != null && employeeVhrTempResponse.employeeCode.length() > 0) {
                    ArrayList<ContractEntity> lstContract = contractRepository.findByEmployeeCode(employeeVhrTempResponse.employeeCode);
                    employeeVhrTempResponse.lstContract = lstContract.stream().map(obj -> {
                        ContractDTO.ContractResponse response = this.objectMapper.convertValue(obj, ContractDTO.ContractResponse.class);
                        response.unitId = employeeVhrTempResponse.unitId;
                        response.unitName = employeeVhrTempResponse.unitName;
                        response.departmentId = employeeVhrTempResponse.departmentId;
                        response.departmentName = employeeVhrTempResponse.departmentName;
                        return response;
                    }).collect(Collectors.toList());
                    ArrayList<TerminateContractEntity> lstTerminate = terminateContractRepository.findByEmployeeCode(employeeVhrTempResponse.employeeCode);
                    employeeVhrTempResponse.lstTeminate = lstTerminate.stream().map(obj -> this.objectMapper.convertValue(obj, TerminateContractDTO.TerminateContractResponse.class)).collect(Collectors.toList());
                }


                return employeeVhrTempResponse;
            }
        }

        return null;
    }

    @Override
    @Transactional
    public boolean sendRequestToVHRToReceivedEmployeeCode(SendVhrCodeRequest request) {
        // TODO: Về sau sẽ còn giai đoạn chờ VHR, xong mới nhận đc mã
        EmployeeVhrTempEntity employeeVhrTempEntity = employeeVhrTempRepository.findById(request.employeeVhrTempId).orElseThrow(() -> new NotFoundException("Không tìm thấy nhân viên đc cấp mã"));

        if (!employeeVhrTempEntity.getNewContractStatus().equals(NewContractStatus.HAVENT_SENT_REQUEST_TO_VHR)) {
            throw new BadRequestException("Hồ sơ nhân viên không ở trạng thái có thể xin cấp mã VHR mà đang ở trạng thái " + employeeVhrTempEntity.getNewContractStatus().getVietnameseStringValue());
        }

        if (employeeVhrTempEntity.getIsLock()) {
            throw new BadRequestException("Hợp đồng nhân viên này đang bị khóa, Làm ơn hãy kiểm tra lại");
        }

        if (StringUtils.isBlank(employeeVhrTempEntity.getCurrentAddress()) || employeeVhrTempEntity.getCurrentProvinceId() == null || employeeVhrTempEntity.getCurrentDistrictId() == null || employeeVhrTempEntity.getCurrentWardId() == null) {
            throw new NotFoundException("Hồ sơ thiếu thông tin nơi ở hiện tại. Làm ơn hãy kiểm tra lại");
        }

        if (StringUtils.isBlank(employeeVhrTempEntity.getOrigin()) || employeeVhrTempEntity.getOriginProvinceId() == null || employeeVhrTempEntity.getOriginDistrictId() == null || employeeVhrTempEntity.getOriginWardId() == null) {
            throw new NotFoundException("Hồ sơ thiếu thông tin hộ khẩu thường trú. Làm ơn hãy kiểm tra lại");
        }

        if (StringUtils.isBlank(employeeVhrTempEntity.getBirthPlace())) {
            throw new NotFoundException("Hồ sơ thiếu thông tin nơi sinh. Làm ơn hãy kiểm tra lại");
        }

        if (employeeVhrTempEntity.getPersonalIdIssuedDate() == null) {
            throw new NotFoundException("Hồ sơ thiếu thông tin ngày cấp CMTND/CCCD. Làm ơn hãy kiểm tra lại");
        }

        if (employeeVhrTempEntity.getPersonalIdIssuedPlace() == null) {
            throw new NotFoundException("Hồ sơ thiếu thông tin nơi cấp CMTND/CCCD. Làm ơn hãy kiểm tra lại");
        }

        // TODO: Remove this fake employee Code
        Long fakeEmployeeCode = employeeVhrTempRepository.generateEmployeeVhrTempCode();

        employeeVhrTempEntity.setEmployeeId(fakeEmployeeCode);
        employeeVhrTempEntity.setEmployeeCode(fakeEmployeeCode + "");

        InterviewSessionCvEntity interviewSessionCvEntity = interviewSessionCvRepository.findById(employeeVhrTempEntity.getInterviewSessionCvId()).orElseThrow(() -> new NotFoundException("Không tìm thấy đợt phỏng vấn ứng với ứng viên: " + employeeVhrTempEntity.getFullname()));
        RecruiteeAccountEntity recruiteeAccountEntity = recruiteeAccountRepository.getByInterviewSessionCvId(employeeVhrTempEntity.getInterviewSessionCvId()).orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản của ứng viên/ Xin vui lòng gửi email cho ứng viên trước để tạo tài khoản đăng nhập hệ thống"));
        PositionEntity positionEntity = positionRepository.findById(employeeVhrTempEntity.getPositionId()).orElseThrow(() -> new NotFoundException("Không tìm thấy vị trí tuyển dụng"));

        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setCreatedByHsdtService(true);
        contractEntity.setContractType(employeeVhrTempEntity.getContractType());
        contractEntity.setEffectiveDate(interviewSessionCvEntity.getStartingDate());

        contractEntity.setCurrentAddress(employeeVhrTempEntity.getCurrentAddress());

        contractEntity.setEmployeeId(fakeEmployeeCode);
        contractEntity.setEmployeeCode(fakeEmployeeCode + "");
        contractEntity.setEmployeeName(employeeVhrTempEntity.getFullname());

        contractEntity.setBirthDate(employeeVhrTempEntity.getUserBirthday());
        contractEntity.setGender(employeeVhrTempEntity.getGender());
        contractEntity.setMobileNumber(employeeVhrTempEntity.getMobileNumber());

        contractEntity.setPermanentAddress(this.getOriginAddress(employeeVhrTempEntity));
        contractEntity.setCurrentAddress(this.getCurrentAddress(employeeVhrTempEntity));

        contractEntity.setNationality("Việt Nam");
//        contractEntity.setEmail(employeeVhrTempEntity.getEmail());
        contractEntity.setTrainingSpeciality(employeeVhrTempEntity.getTrainingSpeciality());

        contractEntity.setPlaceOfBirth(employeeVhrTempEntity.getBirthPlace());

        contractEntity.setPersonalIdNumber(employeeVhrTempEntity.getPersonalIdNumber());
        contractEntity.setPersonalIdIssuedDate(employeeVhrTempEntity.getPersonalIdIssuedDate());
        contractEntity.setPersonalIdIssuedPlace(employeeVhrTempEntity.getPersonalIdIssuedPlace());

        contractEntity.setPositionId(positionEntity.getPositionId());
        contractEntity.setPositionCode(positionEntity.getPositionCode());
        contractEntity.setPositionName(positionEntity.getPositionName());

        employeeVhrTempEntity.setAccountId(recruiteeAccountEntity.getRecruiteeAccountId());
        contractEntity.setAccountId(recruiteeAccountEntity.getRecruiteeAccountId());

        contractEntity.setUnitId(interviewSessionCvEntity.getInterviewSessionEntity().getUnitId());
        contractEntity.setDepartmentId(interviewSessionCvEntity.getInterviewSessionEntity().getDepartmentId());
        contractEntity.setNewContractStatus(NewContractStatus.RECEIVED_CODE_FROM_VHR);

        employeeVhrTempRepository.save(employeeVhrTempEntity);
        contractRepository.save(contractEntity);

        return true;
    }

    @Override
    public EmployeeVhrAttachmentResponse getAttachment(Long employeeVhrTempId) {
        EmployeeVhrTempEntity employeeVhrTempEntity = employeeVhrTempRepository.findById(employeeVhrTempId).orElseThrow(() -> new NotFoundException("Không tìm thấy nhân viên ứng tuyển với employeeVhrTempId = " + employeeVhrTempId));
        if (employeeVhrTempEntity.getContractType() != ContractType.UNKNOWN) {
            EmployeeVhrAttachmentResponse employeeVhrAttachmentResponse = new EmployeeVhrAttachmentResponse();
            employeeVhrAttachmentResponse.contractType = employeeVhrTempEntity.getContractType();
            employeeVhrAttachmentResponse.status = employeeVhrTempEntity.getStatus();
            List<RecruitProfileAttachmentEntity> recruitProfileAttachmentEntities = recruitProfileAttachmentRepository.findByInterviewSessionCvId(employeeVhrTempEntity.getInterviewSessionCvId());
            List<DocumentTypeEntity> documentTypeEntities = documentTypeRepository.findByTypeAndIsActive(employeeVhrTempEntity.getContractType(), true);
            employeeVhrAttachmentResponse.documentTypes = documentTypeEntities.stream().map(item -> {
                DocumentTypeDTO.DocumentTypeResponse documentAttachment = objectMapper.convertValue(item, DocumentTypeDTO.DocumentTypeResponse.class);
                List<RecruitProfileAttachmentEntity> recruitProfileAttachmentEntityList = recruitProfileAttachmentEntities.stream().
                        filter(recruitProfileAttachmentEntity -> recruitProfileAttachmentEntity.getDocumentTypeId().equals(item.getDocumentTypeId())).
                        collect(Collectors.toList());
                documentAttachment.recruitProfileAttachment = recruitProfileAttachmentEntityList.isEmpty() ?
                        new RecruitProfileAttachmentDTO.RecruitProfileAttachmentResponse() :
                        objectMapper.convertValue(recruitProfileAttachmentEntityList.get(0), RecruitProfileAttachmentDTO.RecruitProfileAttachmentResponse.class);
                return documentAttachment;
            }).collect(Collectors.toList());

            return employeeVhrAttachmentResponse;
        }
        return new EmployeeVhrAttachmentResponse();
    }

    private String getCurrentAddress(EmployeeVhrTempEntity employeeVhrTempEntity) {
        ProvinceAreaEntity province = provinceAreaRepository.findById(employeeVhrTempEntity.getCurrentProvinceId().longValue()).orElseThrow(() -> new NotFoundException("Địa chỉ hộ khẩu thường trú tỉnh/thành phố trong hồ sơ không tồn tại"));
        ProvinceAreaEntity district = provinceAreaRepository.findById(employeeVhrTempEntity.getCurrentDistrictId().longValue()).orElseThrow(() -> new NotFoundException("Địa chỉ hộ khẩu thường trú quận/huyện trong hồ sơ không tồn tại"));

        ProvinceAreaEntity ward = provinceAreaRepository.findById(employeeVhrTempEntity.getCurrentWardId().longValue()).orElseThrow(() -> new NotFoundException("Địa chỉ hộ khẩu thường trú xã/phường trong hồ sơ không tồn tại"));

        return employeeVhrTempEntity.getCurrentAddress() + " , " + ward.getName() + " , " + district.getName() + " , " + province.getName();
    }

    private String getOriginAddress(EmployeeVhrTempEntity employeeVhrTempEntity) {
        ProvinceAreaEntity province = provinceAreaRepository.findById(employeeVhrTempEntity.getOriginProvinceId().longValue()).orElseThrow(() -> new NotFoundException("Địa chỉ nơi ở tỉnh/thành phố trong hồ sơ không tồn tại"));
        ProvinceAreaEntity district = provinceAreaRepository.findById(employeeVhrTempEntity.getOriginDistrictId().longValue()).orElseThrow(() -> new NotFoundException("Địa chỉ nơi ở quận/huyện trong hồ sơ không tồn tại"));

        ProvinceAreaEntity ward = provinceAreaRepository.findById(employeeVhrTempEntity.getOriginWardId().longValue()).orElseThrow(() -> new NotFoundException("Địa chỉ nơi ở xã/phường trong hồ sơ không tồn tại"));

        return employeeVhrTempEntity.getOrigin() + " , " + ward.getName() + " , " + district.getName() + " , " + province.getName();
    }

    private boolean updateAttachment(EmployeeVhrTempRequest employeeVhrTempRequest, SSoResponse sSoResponse) {
        List<DocumentTypeDTO.DocumentTypeRequest> lisAttachment = employeeVhrTempRequest.listAttachment;
        if (!lisAttachment.isEmpty()) {
            List<RecruitProfileAttachmentDTO.RecruitProfileAttachmentRequest> recruitProfileAttachmentRequests = lisAttachment.stream().filter(e -> e.documentTypeId.equals(e.recruitProfileAttachment.documentTypeId)).map(item -> item.recruitProfileAttachment).collect(Collectors.toList());
            List<Long> recruitProfileAttachmentIds = recruitProfileAttachmentRequests.stream().map(item -> item.recProfileAttachmentId).collect(Collectors.toList());
            List<RecruitProfileAttachmentEntity> recruitProfileAttachmentEntities = recruitProfileAttachmentRepository.getByIds(recruitProfileAttachmentIds);
            if (!recruitProfileAttachmentEntities.isEmpty()) {
                List<RecruitProfileAttachmentEntity> newAttachment = recruitProfileAttachmentEntities.stream().map(item -> {
                    RecruitProfileAttachmentDTO.RecruitProfileAttachmentRequest request = recruitProfileAttachmentRequests.stream().filter(i -> i.recProfileAttachmentId.equals(item.getRecProfileAttachmentId())).findFirst().orElse(null);
                    if (request != null) {
                        if (!StringUtils.isBlank(request.filePath)) {
                            item.setFilePath(request.filePath);
                            item.setStatus(AttachmentDocumentStatus.PENDING);
                            item.setCreatedBy(sSoResponse.getSysUserId());
                            item.setUpdatedBy(sSoResponse.getSysUserId());
                            if (!StringUtils.isBlank(request.fileName)) {
                                item.setFileName(request.fileName);
                            }
                        }
                    }
                    return item;
                }).collect(Collectors.toList());
                recruitProfileAttachmentRepository.saveAll(newAttachment);
            }
        }
        return true;
    }
}
