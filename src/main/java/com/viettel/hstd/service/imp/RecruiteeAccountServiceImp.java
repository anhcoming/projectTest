package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.constant.ConstantConfig;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.AuthenticateUtils;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.core.utils.StringUtils;
import com.viettel.hstd.dto.hstd.RecruiteeAccountDTO.*;
import com.viettel.hstd.entity.hstd.CvEntity;
import com.viettel.hstd.entity.hstd.InterviewSessionCvEntity;
import com.viettel.hstd.entity.hstd.RecruiteeAccountEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.InterviewSessionCvRepository;
import com.viettel.hstd.repository.hstd.RecruiteeAccountRepository;
import com.viettel.hstd.service.inf.RecruiteeAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecruiteeAccountServiceImp extends BaseService implements RecruiteeAccountService {
    @Autowired
    private RecruiteeAccountRepository recruiteeAccountRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Message message;

    @Override
    public Page<RecruiteeAccountResponse> findPage(SearchDTO searchRequest) {
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<RecruiteeAccountEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<RecruiteeAccountEntity> list;
        if (searchRequest.pagedFlag) {
            list = recruiteeAccountRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = recruiteeAccountRepository.findAll(p);
        }

        return list.map((obj) ->
                this.objectMapper.convertValue(obj, RecruiteeAccountResponse.class)
        );
    }

    @Override
    public RecruiteeAccountResponse findOneById(Long aLong) {
        RecruiteeAccountEntity e = recruiteeAccountRepository
                .findById(aLong)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.province_area.not_found")));
        return objectMapper.convertValue(e, RecruiteeAccountResponse.class);
    }

    @Override
    public Boolean delete(Long id) {
        if (!recruiteeAccountRepository.existsById(id))
            throw new NotFoundException(message.getMessage("message.not_found"));
        recruiteeAccountRepository.softDelete(id);
        addLog("RECRUITEE_ACCOUNT", "DELETE", id.toString());
        return true;
    }

    @Override
    public RecruiteeAccountResponse validate(String account, String password) {
        ArrayList<RecruiteeAccountEntity> lstAccount = recruiteeAccountRepository.findByLoginNameContainingIgnoreCase(account);
        if (lstAccount != null && lstAccount.size() > 0) {
            RecruiteeAccountEntity entity = lstAccount.get(0);
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (passwordEncoder.matches(password, entity.getPassword())) {
                return objectMapper.convertValue(entity, RecruiteeAccountResponse.class);
            }
        }
        return null;
    }

    @Autowired
    InterviewSessionCvRepository interviewSessionCvRepository;

    @Override
    public RecruiteeAccountResponse createAccount(Long interviewCvId) {
        InterviewSessionCvEntity interviewCv = interviewSessionCvRepository.findById(interviewCvId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy kết quả phỏng vấn với id " + interviewCvId ));
        return objectMapper.convertValue(createAccount(interviewCv), RecruiteeAccountResponse.class);
    }

    private RecruiteeAccountEntity createAccount(InterviewSessionCvEntity interviewCv) {
        RecruiteeAccountEntity recruiteeAccountEntity = new RecruiteeAccountEntity();
        String account = "";
        if (interviewCv.getRecruiteeAccountEntities() != null && interviewCv.getRecruiteeAccountEntities().size() > 0) {
            recruiteeAccountEntity = interviewCv.getRecruiteeAccountEntities().get(0);
            recruiteeAccountEntity.setRecruiteeAccountId(recruiteeAccountEntity.getRecruiteeAccountId());
            recruiteeAccountEntity.setInterviewSessionCvEntity(recruiteeAccountEntity.getInterviewSessionCvEntity());
            recruiteeAccountEntity.setLoginName(recruiteeAccountEntity.getLoginName());
            account = recruiteeAccountEntity.getLoginName();
        } else {
            account = StringUtils.generateAccountName(interviewCv.getCvEntity().getFullName(), ConstantConfig.defaultAccountPrefix);
            List<RecruiteeAccountEntity> lstAccount = recruiteeAccountRepository.findByLoginNameContainingIgnoreCase(account);
            if (lstAccount != null && lstAccount.size() > 0) {
                account += lstAccount.size();
            }
        }

        String password = StringUtils.generateRandomPassword(8);
        recruiteeAccountEntity.setInterviewSessionCvEntity(interviewCv);
        recruiteeAccountEntity.setLoginName(account);
        recruiteeAccountEntity.setPassword(AuthenticateUtils.encryptText(password));
        recruiteeAccountEntity = recruiteeAccountRepository.save(recruiteeAccountEntity);
        return recruiteeAccountEntity;
    }
}
