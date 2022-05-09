package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.EmailConfigDTO;
import com.viettel.hstd.dto.vps.EmployeeVhrDTO.*;
import com.viettel.hstd.service.inf.EmployeeVhrService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("employee")
@Tag(name = "Employee")
public class EmployeeController {

    @Autowired
    private EmployeeVhrService employeeVhrService;

    @GetMapping
    public BaseResponse<List<EmployeeVhrResponse>> findAll() {
        return SearchUtils.getResponseFromPage(employeeVhrService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
    public BaseResponse<List<EmployeeVhrResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(employeeVhrService.findPage(searchDTO));
    }

    @PostMapping("/full/search")
    public BaseResponse<List<EmployeeFullResponse>> searchFull(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(employeeVhrService.findFullPage(searchDTO));
    }

    @GetMapping("/{id}")
    public BaseResponse<EmployeeVhrResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<EmployeeVhrResponse>()
                .success(employeeVhrService.findOneById(id));
    }

    @GetMapping("/combine-db/{id}")
    public BaseResponse<EmployeeVhrResponse> findOneByIdCombineDb(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<EmployeeVhrResponse>()
                .success(employeeVhrService.findOneByIdCombineDb(id));
    }

    @GetMapping("/count-by-pos-and-org")
    public BaseResponse<Integer> countByPosAndOrg(@RequestParam Long positionId, @RequestParam Long organizationId) {
        return new BaseResponse
                .ResponseBuilder<Integer>()
                .success(employeeVhrService.countByPositionIdAndOrganizationId(positionId, organizationId));
    }
}
