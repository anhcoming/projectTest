package com.viettel.hstd.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.constant.Operation;
import com.viettel.hstd.constant.ResignStatus;
import com.viettel.hstd.constant.SearchType;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.constant.ConstantConfig;
import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.dto.SearchDTO.*;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.ContractDTO.*;
import com.viettel.hstd.dto.hstd.ContractImportDTO;
import com.viettel.hstd.dto.hstd.SysConfigDTO;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.ContractService;
import com.viettel.hstd.service.inf.SysConfigService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController()
@RequestMapping("contract")
@Tag(name = "Contract")
@Slf4j
public class ContractController {

    @Autowired
    private ContractService contractService;

    @Autowired
    private SysConfigService sysConfigService;

    @GetMapping
//    @PreAuthorize("hasPermission('VIEW CONTRACT_MANAGEMENT', '')")
    public BaseResponse<List<ContractResponse>> findAll() {
        return SearchUtils.getResponseFromPage(contractService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
//    @PreAuthorize("hasPermission('VIEW CONTRACT_MANAGEMENT', '')")
    public BaseResponse<List<ContractResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(contractService.findPage(searchDTO));
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasPermission('VIEW CONTRACT_MANAGEMENT', '')")
    public BaseResponse<ContractResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<ContractResponse>()
                .success(contractService.findOneById(id));
    }

    @Autowired
    ObjectMapper objectMapper;

    @PostMapping("about-to-expired/search")
//    @PreAuthorize("hasPermission('VIEW CONTRACT_MANAGEMENT', '')")
    public BaseResponse<List<ContractResponse>> searchExpiredContract(@RequestBody SearchDTO searchDTO) {
        try {
            List<SearchCriteria> oldCriteria = searchDTO.criteriaList;
            List<SearchCriteria> newCriteria = new ArrayList<>();

            SysConfigDTO.SysConfigResponse sysConfigResponse = sysConfigService.findOneByKey(ConstantConfig.EXPIRED_CONTRACT_LENGTH_CONFIG_CODE);
            Long numOfExpiredMonth = Long.parseLong(sysConfigResponse.configValue);

            SearchCriteria numMonthCriteria = new SearchCriteria();
            numMonthCriteria.setField("expiredDate");
            numMonthCriteria.setOperation(Operation.LESS_EQUAL);
            numMonthCriteria.setValue(objectMapper.writeValueAsString(LocalDate.now().plusMonths(numOfExpiredMonth)).replace("\"", ""));
            numMonthCriteria.setType(SearchType.DATE);

            SearchCriteria resignStatusCriteria = new SearchCriteria();
            resignStatusCriteria.setField("resignStatus");
            resignStatusCriteria.setOperation(Operation.LESS_EQUAL);
            resignStatusCriteria.setValue(2);
            resignStatusCriteria.setType(SearchType.NUMBER);

            newCriteria.add(numMonthCriteria);
            newCriteria.add(resignStatusCriteria);
            newCriteria.addAll(oldCriteria);
            searchDTO.criteriaList = newCriteria;

        } catch (JsonProcessingException e) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            log.error(stackTrace);
        }

        return SearchUtils.getResponseFromPage(contractService.findPage(searchDTO));

    }

    @PostMapping("update-resign-status")
    public BaseResponse<Boolean> search(@RequestBody UpdateResignStatusRequest request) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(contractService.updateContractResignStatus(request));
    }

}
