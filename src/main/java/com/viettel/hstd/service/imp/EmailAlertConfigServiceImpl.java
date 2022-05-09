package com.viettel.hstd.service.imp;

import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.dto.hstd.EmailAlertConfigDTO;
import com.viettel.hstd.dto.hstd.EmailAlertRecipientDTO;
import com.viettel.hstd.dto.hstd.RecruitmentProgressDTO;
import com.viettel.hstd.entity.hstd.EmailAlertConfigEntity;
import com.viettel.hstd.entity.hstd.EmailAlertRecipientEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.repository.hstd.EmailAlertConfigRepository;
import com.viettel.hstd.repository.hstd.EmailAlertRecipientRepository;
import com.viettel.hstd.service.inf.EmailAlertConfigService;
import com.viettel.hstd.service.mapper.EmailAlertConfigConverter;
import com.viettel.hstd.service.mapper.EmailAlertRecipientConverter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailAlertConfigServiceImpl implements EmailAlertConfigService {

    private final EmailAlertConfigRepository emailAlertConfigRepository;

    private final EmailAlertRecipientRepository emailAlertRecipientRepository;

    private final EmailAlertConfigConverter emailAlertConfigConverter;

    private final EmailAlertRecipientConverter emailAlertRecipientConverter;

    private final Message message;


    @Override
    @Transactional
    public void save(EmailAlertConfigDTO.Request request) {
        EmailAlertConfigEntity emailAlertConfigEntity = emailAlertConfigConverter.requestToEntity(request);
        //check unique position and organization
        if (emailAlertConfigRepository.existsByPositionIdAndOrganizationId(request.getPositionId(), request.getOrganizationId()))
            throw new BadRequestException(message.getMessage("email.alert.config.existed"));
        saveEntity(request, emailAlertConfigEntity);

    }

    @Override
    @Transactional
    public void update(Long id, EmailAlertConfigDTO.Request request) {
        emailAlertRecipientRepository.deleteAllByEmailAlertConfigId(id);

        EmailAlertConfigEntity emailAlertConfigEntity = emailAlertConfigConverter.requestToEntity(request);
        emailAlertConfigEntity.setId(id);
        saveEntity(request, emailAlertConfigEntity);

    }

    private void saveEntity(EmailAlertConfigDTO.Request request, EmailAlertConfigEntity emailAlertConfigEntity) {
        emailAlertConfigRepository.save(emailAlertConfigEntity);

        List<EmailAlertRecipientEntity> emailAlertRecipientEntities = new ArrayList<>();
        List<EmailAlertRecipientDTO.Request> recipients = request.getRecipients();
        if (recipients != null && !recipients.isEmpty()) {
            for (EmailAlertRecipientDTO.Request recipient : recipients) {
                emailAlertRecipientEntities.add(emailAlertRecipientConverter.requestToEntity(recipient, emailAlertConfigEntity.getId()));
            }
        }
        emailAlertRecipientRepository.saveAll(emailAlertRecipientEntities);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmailAlertConfigDTO.Response> search(EmailAlertConfigDTO.SearchCriteria searchCriteria) {
        List<SearchDTO.OrderDTO> orders = searchCriteria.getSortList();
        List<Sort.Order> queryOrderList = new ArrayList<>();
        if (orders != null && !orders.isEmpty()) {
            for (SearchDTO.OrderDTO order : orders) {
                Sort.Order queryOrder = new Sort.Order(
                        order.direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, order.property);
                queryOrderList.add(queryOrder);
            }
        }

        Page<EmailAlertConfigDTO.Projection> page = emailAlertConfigRepository.search(searchCriteria.getPositionId(),
                searchCriteria.getOrganizationId(), searchCriteria.getEmployeeId(),
                PageRequest.of(searchCriteria.getPage(), searchCriteria.getSize(), Sort.by(queryOrderList)));
        return page.map(emailAlertConfigConverter::projectionToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public EmailAlertConfigDTO.SingleResponse findById(Long id) {
        EmailAlertConfigEntity emailAlertConfigEntity =
                emailAlertConfigRepository.findById(id)
                        .orElseThrow(() -> new BadRequestException(message.getMessage("message.not_found")));
        EmailAlertConfigDTO.SingleResponse response = emailAlertConfigConverter.entityToSingleResponse(emailAlertConfigEntity);
        response.setListEmployee(emailAlertRecipientConverter
                .entitiesToResponses(emailAlertRecipientRepository
                        .findByEmailAlertConfigId(emailAlertConfigEntity.getId())));
        return response;


    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        emailAlertConfigRepository.deleteById(id);
        emailAlertRecipientRepository.deleteAllByEmailAlertConfigId(id);
    }

    @Override
    public String createEmailContent(String template, List<RecruitmentProgressDTO.EmailResponse> contents, String name, String date) {
        Map<String, String> valueMap = new ConcurrentHashMap<>();
        valueMap.put("date", StringUtils.defaultIfEmpty(date,""));
        valueMap.put("name", StringUtils.defaultIfEmpty(name,""));
        valueMap.put("table", createTable(contents));
        StrSubstitutor strSubstitutor = new StrSubstitutor(valueMap);
        return strSubstitutor.replace(template);
    }

    private String createTable(List<RecruitmentProgressDTO.EmailResponse> contents) {
        StringBuilder result = new StringBuilder("<table class=\"tftable\" border=\"1\" cellpadding=\"10\"><tr><th>STT</th><th>Chức danh</th><th>Đơn vị</th><th>Định biên</th><th>Hiện có</th><th>Nhân sự đã tuyển</th><th>Thời hạn hoàn thành</th><th>Tỷ lệ hoàn thành</th><th>Nhân sự phụ trách</th><th>Ghi chú</th></tr>");
        int numberOrder = 1;
        for (RecruitmentProgressDTO.EmailResponse row : contents) {
            result.append(MessageFormat.format("<tr>\n" +
                            "<td style=\"text-align: center;\">{0}</td>\n" +
                            "<td style=\"text-align: center;\">{1}</td>\n" +
                            "<td style=\"text-align: center;\">{2}</td>\n" +
                            "<td style=\"text-align: center;\">{3}</td>\n" +
                            "<td style=\"text-align: center;\">{4}</td>\n" +
                            "<td style=\"text-align: center;\">{5}</td>\n" +
                            "<td style=\"text-align: center;\">{6}</td>\n" +
                            "<td style=\"text-align: center;\">{7}</td>\n" +
                            "<td style=\"text-align: center;\">{8}</td>\n" +
                            "<td style=\"text-align: center;\">{9}</td>\n" +
                            "</tr>", numberOrder++, row.getPositionName(), row.getOrganizationName(),
                    row.getHrPlan(), row.getCurrentEmp(), row.getRecruited(), row.getDeadline(), row.getCompletionRate(),
                    row.getEmployeeName(), StringUtils.defaultIfEmpty(row.getDescription(), StringUtils.EMPTY)));
        }
        result.append("</table>");
        return result.toString();

    }

}
