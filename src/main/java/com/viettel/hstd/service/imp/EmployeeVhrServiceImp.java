package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.constant.ContractType;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.core.utils.StringUtils;
import com.viettel.hstd.dto.hstd.*;
import com.viettel.hstd.dto.vps.EmployeeVhrDTO.*;
import com.viettel.hstd.dto.vps.VhrFutureOrganizationDTO;
import com.viettel.hstd.entity.hstd.*;
import com.viettel.hstd.entity.vps.EmployeeVhrEntity;
import com.viettel.hstd.entity.vps.EmployeeVhrEntityFull;
import com.viettel.hstd.entity.vps.VhrFutureOrganizationEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.*;
import com.viettel.hstd.repository.vps.EmployeeVhrRepository;
import com.viettel.hstd.repository.vps.EmployeeVhrRepositoryFull;
import com.viettel.hstd.repository.vps.PositionRepository;
import com.viettel.hstd.repository.vps.VhrFutureOrganizationRepository;
import com.viettel.hstd.security.AuthenticationFacade;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.EmployeeVhrService;
import com.viettel.hstd.service.inf.EmployeeVhrTempService;
import com.viettel.hstd.service.inf.VhrFutureOrganizationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeVhrServiceImp implements EmployeeVhrService {

    final
    ObjectMapper objectMapper;

    final
    ModelMapper modelMapper;

    final
    Message message;
    private final EmployeeVhrRepository employeeVhrRepository;
    private final EmployeeVhrRepositoryFull employeeVhrRepositoryFull;
    private final DocumentTypeRepository documentTypeRepository;
    private final ContractRepository contractRepository;
    private final TerminateContractRepository terminateContractRepository;
    private final RecruitProfileAttachmentRepository recruitProfileAttachmentRepository;
    private final VhrFutureOrganizationRepository vhrFutureOrganizationRepository;
    private final VhrFutureOrganizationService organizationService;
    private final EmployeeVhrTempRepository employeeVhrTempRepository;
    private final AuthenticationFacade authenticationFacade;
    private final PositionRepository positionRepository;

    @Autowired
    private EmployeeVhrTempService employeeVhrTempService;

    public EmployeeVhrServiceImp(PositionRepository positionRepository, EmployeeVhrRepository employeeVhrRepository, ObjectMapper objectMapper, ModelMapper modelMapper, Message message, EmployeeVhrRepositoryFull employeeVhrRepositoryFull, DocumentTypeRepository documentTypeRepository, ContractRepository contractRepository, AuthenticationFacade authenticationFacade, TerminateContractRepository terminateContractRepository, RecruitProfileAttachmentRepository recruitProfileAttachmentRepository, VhrFutureOrganizationRepository vhrFutureOrganizationRepository, VhrFutureOrganizationService organizationService, EmployeeVhrTempRepository employeeVhrTempRepository) {
        this.positionRepository = positionRepository;
        this.employeeVhrRepository = employeeVhrRepository;
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
        this.message = message;
        this.employeeVhrRepositoryFull = employeeVhrRepositoryFull;
        this.documentTypeRepository = documentTypeRepository;
        this.contractRepository = contractRepository;
        this.authenticationFacade = authenticationFacade;
        this.terminateContractRepository = terminateContractRepository;
        this.recruitProfileAttachmentRepository = recruitProfileAttachmentRepository;
        this.vhrFutureOrganizationRepository = vhrFutureOrganizationRepository;
        this.organizationService = organizationService;
        this.employeeVhrTempRepository = employeeVhrTempRepository;
    }

    @Override
    public Page<EmployeeVhrResponse> findPage(SearchDTO searchRequest) {
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<EmployeeVhrEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<EmployeeVhrEntity> list;
        if (searchRequest.pagedFlag) {
            list = employeeVhrRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = employeeVhrRepository.findAll(p);
        }

        return list.map(obj ->
                this.objectMapper.convertValue(obj, EmployeeVhrResponse.class)
        );
    }

    @Override
    public EmployeeVhrResponse findOneById(Long id) {
        EmployeeVhrEntity employeeVhrEntity = employeeVhrRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return objectMapper.convertValue(employeeVhrEntity, EmployeeVhrResponse.class);
    }

    @Override
    public EmployeeVhrResponse findOneByIdCombineDb(Long id) {
        EmployeeVhrEntity employeeVhrEntity = employeeVhrRepository
                .findById(id)
                .orElse(null);
        if (employeeVhrEntity == null) {
            employeeVhrEntity = findEmployeeInTemp(id);
        }
        EmployeeVhrResponse employeeVhrResponse = objectMapper.convertValue(employeeVhrEntity, EmployeeVhrResponse.class);
        return employeeVhrResponse;
    }

    private EmployeeVhrEntity findEmployeeInTemp(Long employeeId) {
        EmployeeVhrTempEntity employeeVhrTempEntity = employeeVhrTempRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhân viên"));
        EmployeeVhrEntity employeeVhrEntity = objectMapper.convertValue(employeeVhrTempEntity, EmployeeVhrEntity.class);
        employeeVhrEntity.setDateOfBirth(employeeVhrTempEntity.getUserBirthday());
        return employeeVhrEntity;
    }


    @Override
    public Boolean delete(Long s) {
        return null;
    }

    @Override
    public EmployeeVhrTempDTO.EmployeeVhrTempResponse getProfile() {
        SSoResponse ssoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
        if (ssoResponse.getStaffCode() != null && ssoResponse.getStaffCode().trim().length() > 0) {
            EmployeeVhrEntityFull entity = employeeVhrRepositoryFull.findFirstByEmployeeCode(ssoResponse.getStaffCode())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy thông tin nhân viên với mã nhân viên: " + ssoResponse.getStaffCode()));
            List<ContractEntity> contractEntityList = contractRepository.getCurrentContract(entity.getEmployeeCode(), LocalDate.now());
            ContractEntity currentContract = new ContractEntity();
            if (contractEntityList.size() > 0) {
                currentContract = contractEntityList.get(0);
            }
            if (entity != null) {
                //<editor-fold desc="Gan du lieu qua thong tin nhan vien">
                EmployeeVhrTempDTO.EmployeeVhrTempResponse employeeVhrTempResponse = new EmployeeVhrTempDTO.EmployeeVhrTempResponse();
                employeeVhrTempResponse.employeeId = entity.getEmptypeId();
                employeeVhrTempResponse.employeeCode = entity.getEmployeeCode();
                employeeVhrTempResponse.employeeVhrTempId = null;
                employeeVhrTempResponse.email = entity.getEmail();
                employeeVhrTempResponse.birthPlace = entity.getPlaceOfBirth();
                employeeVhrTempResponse.contractDecisionNumber = entity.getContractDecisionNumber();
//                employeeVhrTempResponse.contractType = StringUtils.convertToInt(entity.getLabourContractTypeId());
                employeeVhrTempResponse.contractType = currentContract.getContractType();
                employeeVhrTempResponse.currentAddress = entity.getCurrentAddress();
                employeeVhrTempResponse.educationName = entity.getEducationSubjectName();
                employeeVhrTempResponse.ethnicId = StringUtils.convertToLong(entity.getEthnicId());
                employeeVhrTempResponse.fullname = entity.getFullname();
                employeeVhrTempResponse.gender = entity.getGender();
                employeeVhrTempResponse.insuranceNumber = entity.getInsuranceFactor();
                employeeVhrTempResponse.joinCompanyDate = entity.getJoinCompanyDate();
                employeeVhrTempResponse.labourContractTypeId = StringUtils.convertToInt(entity.getLabourContractTypeId());
                employeeVhrTempResponse.organizationCode = entity.getOrganizationCode();
                employeeVhrTempResponse.mobileNumber = entity.getMobileNumber();
                employeeVhrTempResponse.organizationId = entity.getOrganizationId();
                employeeVhrTempResponse.organizationName = entity.getOrganizationName();
                employeeVhrTempResponse.partyAdmissionDate = StringUtils.convertToLocalDate(entity.getPartyAdmissionDate());
                employeeVhrTempResponse.partyAdmissionPlace = entity.getPartyDdmissionPlace();
                employeeVhrTempResponse.partyOfficialAdmissionDate = StringUtils.convertToLocalDate(entity.getPartyOfficialAdmissionDate());
                employeeVhrTempResponse.personalIdIssuedDate = StringUtils.convertToLocalDate(entity.getPersonalIdIssuedDate());
                employeeVhrTempResponse.permanentAddress = entity.getPermanentAddress();
                employeeVhrTempResponse.personalIdIssuedPlace = entity.getPersonalIdIssuedPlace();
                employeeVhrTempResponse.personalIdNumber = entity.getPersonalIdNumber();
                employeeVhrTempResponse.positionId = entity.getPositionId();


                employeeVhrTempResponse.religionId = StringUtils.convertToLong(entity.getReligionId());
                employeeVhrTempResponse.taxNumber = entity.getTaxNumber();
                employeeVhrTempResponse.origin = entity.getOrgAddress();
                employeeVhrTempResponse.signedDate = StringUtils.convertToLocalDate(entity.getSignedDate());
                employeeVhrTempResponse.userBirthday = entity.getDateOfBirth();
                employeeVhrTempResponse.trainingLevel = entity.getTrainingLevel();
                employeeVhrTempResponse.trainingSpeciality = entity.getTrainingSpeciality();

                VhrFutureOrganizationDTO.DepartmentUnitResponse departmentUnitResponse = organizationService.getDepartmentAndUnitFromOrganization(ssoResponse.getOrganizationId());
                employeeVhrTempResponse.departmentId = departmentUnitResponse.departmentId;
                employeeVhrTempResponse.departmentName = departmentUnitResponse.departmentName;
                employeeVhrTempResponse.unitId = departmentUnitResponse.unitId;
                employeeVhrTempResponse.unitName = departmentUnitResponse.unitName;
                //</editor-fold>
                //<editor-fold desc="Gan du lieu ho so">
                if (employeeVhrTempResponse.contractType != null && employeeVhrTempResponse.contractType != ContractType.UNKNOWN) {
                    EmployeeVhrTempDTO.EmployeeVhrAttachmentResponse employeeVhrAttachmentResponse = employeeVhrTempService.getAttachment(employeeVhrTempResponse.employeeVhrTempId);
                    employeeVhrTempResponse.listAttachment = employeeVhrAttachmentResponse.documentTypes;
                    employeeVhrTempResponse.status = employeeVhrAttachmentResponse.status;
                }
                //</editor-fold>
                if (employeeVhrTempResponse.organizationId != null && employeeVhrTempResponse.organizationId > 0) {
                    Optional<VhrFutureOrganizationEntity> organization = vhrFutureOrganizationRepository.findById(employeeVhrTempResponse.organizationId);
                    if (organization.isPresent() && organization.get().getOrgLevelManage() != null && organization.get().getOrgLevelManage() > 3) {
                        VhrFutureOrganizationEntity department = vhrFutureOrganizationRepository.findById(organization.get().getOrgParentId()).orElse(null);
                        if (department != null) {
                            employeeVhrTempResponse.organizationId = department.getOrgParentId();
                        }
                    }
                }
                if (employeeVhrTempResponse.employeeCode != null && employeeVhrTempResponse.employeeCode.length() > 0) {
                    ArrayList<ContractEntity> lstContract = contractRepository.findByEmployeeCode(employeeVhrTempResponse.employeeCode);
                    employeeVhrTempResponse.lstContract = lstContract.stream().map(obj ->
                            this.objectMapper.convertValue(obj, ContractDTO.ContractResponse.class)).collect(Collectors.toList());
                    ArrayList<TerminateContractEntity> lstTerminate = terminateContractRepository.findByEmployeeCode(employeeVhrTempResponse.employeeCode);
                    employeeVhrTempResponse.lstTeminate = lstTerminate.stream().map(obj ->
                            this.objectMapper.convertValue(obj, TerminateContractDTO.TerminateContractResponse.class)).collect(Collectors.toList());
                }

                return employeeVhrTempResponse;
            }
        }
        return null;
    }

    @Override
    public Page<EmployeeFullResponse> findFullPage(SearchDTO searchRequest) {
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<EmployeeVhrEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<EmployeeVhrEntity> list;
        if (searchRequest.pagedFlag) {
            list = employeeVhrRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = employeeVhrRepository.findAll(p);
        }

        return list.map(obj ->
                this.objectMapper.convertValue(obj, EmployeeFullResponse.class)
        );
    }


    @Override
    public Integer countByPositionIdAndOrganizationId(Long positionId, Long organizationId) {
        return employeeVhrRepositoryFull.countByPosIdAndPath(positionId, organizationId);
    }
}
