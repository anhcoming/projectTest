package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.viettel.hstd.constant.AttachmentDocumentStatus;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.constant.ProfileStatusConstant;
import com.viettel.hstd.core.utils.MapUtils;
import com.viettel.hstd.dto.hstd.DocumentTypeDTO;
import com.viettel.hstd.dto.hstd.RecruitProfileAttachmentDTO.*;
import com.viettel.hstd.entity.hstd.EmployeeVhrTempEntity;
import com.viettel.hstd.entity.hstd.RecruitProfileAttachmentEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.EmployeeVhrTempRepository;
import com.viettel.hstd.repository.hstd.RecruitProfileAttachmentRepository;
import com.viettel.hstd.service.inf.DocumentTypeService;
import com.viettel.hstd.service.inf.RecruitProfileAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecruitProfileAttachmentServiceImp extends BaseService implements RecruitProfileAttachmentService {
    @Autowired
    private RecruitProfileAttachmentRepository recruitProfileAttachmentRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    Message message;
    @Autowired
    MapUtils mapUtils;

    @Autowired
    private EmployeeVhrTempRepository employeeVhrTempRepository;

    @Override
    public RecruitProfileAttachmentResponse findOneById(Long id) {
        RecruitProfileAttachmentEntity entity = recruitProfileAttachmentRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        RecruitProfileAttachmentResponse response = objectMapper.convertValue(entity, RecruitProfileAttachmentResponse.class);
        return response;
    }

    @Override
    public Boolean delete(Long id) {
        RecruitProfileAttachmentEntity entity = recruitProfileAttachmentRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));

        if (entity != null) {
            entity.setFileName("");
            entity.setFilePath("");
            entity = recruitProfileAttachmentRepository.save(entity);
            addLog("RECRUIT_PROFILE_ATTACHMENT", "DELETE", id.toString());
        }
        return false;

    }

    @Override
    public RecruitProfileAttachmentResponse update(Long id, RecruitProfileAttachmentRequest request) {
        RecruitProfileAttachmentEntity entity = recruitProfileAttachmentRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        entity.setFilePath(request.filePath);
        entity.setFileName(request.fileName);
        entity.setRecProfileAttachmentId(id);
        entity = recruitProfileAttachmentRepository.save(entity);
        addLog("RECRUIT_PROFILE_ATTACHMENT", "UPDATE", new Gson().toJson(request));
        if (entity.getInterviewSessionCvId() != null && entity.getInterviewSessionCvId() > 0) {
            EmployeeVhrTempEntity employee = employeeVhrTempRepository.findByInterviewSessionCvId(entity.getInterviewSessionCvId());
            if (employee != null && employee.getStatus() == AttachmentDocumentStatus.PENDING) {
                employee.setStatus(AttachmentDocumentStatus.PENDING);
                employeeVhrTempRepository.save(employee);
            }
        }
        return objectMapper.convertValue(entity, RecruitProfileAttachmentResponse.class);
    }

    /*
    Lay danh sach ho so nhan vien can upload len he thong
     */
    @Override
    public List<RecruitProfileAttachmentResponse> findByUserId(Long userId) {
        return recruitProfileAttachmentRepository.findByAccountId(userId).stream()
                .map((obj) ->
                        this.objectMapper.convertValue(obj, RecruitProfileAttachmentResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public RecruitProfileAttachmentResponse approval(Long recProfileAttachmentId) {
        recruitProfileAttachmentRepository.approval(AttachmentDocumentStatus.APPROVAL, recProfileAttachmentId);
        return this.findOneById(recProfileAttachmentId);
    }

    @Override
    public RecruitProfileAttachmentResponse reject(Long recProfileAttachmentId) {
        recruitProfileAttachmentRepository.approval(AttachmentDocumentStatus.REJECT, recProfileAttachmentId);
        return this.findOneById(recProfileAttachmentId);
    }
}
