package com.viettel.hstd.service.imp;

import com.viettel.hstd.constant.ImportConstant;
import com.viettel.hstd.dto.hstd.ImportHistorySettingDTO;
import com.viettel.hstd.entity.hstd.ImportHistorySettingEntity;
import com.viettel.hstd.repository.hstd.ImportHistorySettingRepository;
import com.viettel.hstd.service.inf.ImportHistorySettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportHistorySettingServiceImpl implements ImportHistorySettingService {

    private final ImportHistorySettingRepository importHistorySettingRepository;

    private final PlatformTransactionManager transactionManager;

    @PostConstruct
    public void initValue() {
        TransactionTemplate tmpl = new TransactionTemplate(transactionManager);
        tmpl.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                initPositionDescriptionSetting();
                initRecruitmentProgressSetting();
            }
        });
    }

    public void initPositionDescriptionSetting() {
        boolean existByCode = importHistorySettingRepository.existsByImportCode(ImportConstant.ImportCode.POSITION_DESCRIPTION);
        if (!existByCode) {
            List<ImportHistorySettingEntity> importHistorySettingEntities = new ArrayList<>();
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("unitCode").importTitle("Mã đơn vị").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("unitName").importTitle("Tên đơn vị").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("departmentCode").importTitle("Mã phòng ban").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("departmentName").importTitle("Tên phòng ban").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("groupCode").importTitle("Mã tổ,nhóm").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("groupName").importTitle("Tên tổ nhóm").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("positionName").importTitle("Tên chức danh").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("genderName").importTitle("Giới tính").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("academicRequirement").importTitle("Trình độ").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("majorRequirement").importTitle("Ngành đào tạo").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("experienceRequirement").importTitle("Kinh nghiệm").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("healthRequirement").importTitle("Sức khỏe").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("englishRequirement").importTitle("Tiếng anh (TOEIC)").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("positionDescription").importTitle("Nhiệm vụ chính").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("note").importTitle("Ghi chú").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("areaCode").importTitle("Khối").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("specializationCode").importTitle("Ngành").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("jobCode").importTitle("Nghề").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("positionCode").importTitle("Mã chức danh").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("hasDescription").importTitle("MTCV").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("fileAttachments").importTitle("File đính kèm").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                    .importField("employeeRecipients").importTitle("Nhân sự phụ trách").build());
            importHistorySettingRepository.saveAll(importHistorySettingEntities);
        }
    }

    public void initRecruitmentProgressSetting() {
        boolean existByCode = importHistorySettingRepository.existsByImportCode(ImportConstant.ImportCode.RECRUITMENT_PLAN);
        if (!existByCode) {
            List<ImportHistorySettingEntity> importHistorySettingEntities = new ArrayList<>();
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.RECRUITMENT_PLAN)
                    .importField("organizationCode").importTitle("Mã đơn vị").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.RECRUITMENT_PLAN)
                    .importField("organizationName").importTitle("Tên đơn vị").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.RECRUITMENT_PLAN)
                    .importField("positionCode").importTitle("Mã chức danh").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.RECRUITMENT_PLAN)
                    .importField("positionName").importTitle("Tên chức danh").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.RECRUITMENT_PLAN)
                    .importField("hrPlan").importTitle("Định biên").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.RECRUITMENT_PLAN)
                    .importField("deadline").importTitle("Thời hạn hoàn thành").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.RECRUITMENT_PLAN)
                    .importField("listEmployees").importTitle("Nhân sự phụ trách truyển").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.RECRUITMENT_PLAN)
                    .importField("listRecipients").importTitle("Nhân sự quản lý").build());
            importHistorySettingEntities.add(ImportHistorySettingEntity.builder().importCode(ImportConstant.ImportCode.RECRUITMENT_PLAN)
                    .importField("description").importTitle("Ghi chú").build());
            importHistorySettingRepository.saveAll(importHistorySettingEntities);
        }
    }

    @Override
    public List<ImportHistorySettingDTO.Response> search(ImportHistorySettingDTO.SearchCriteria searchCriteria) {
        return importHistorySettingRepository.search(searchCriteria.getImportCode());
    }
}
