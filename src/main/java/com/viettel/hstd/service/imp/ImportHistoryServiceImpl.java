package com.viettel.hstd.service.imp;

import com.viettel.hstd.constant.ImportConstant;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.dto.hstd.ImportHistoryDTO;
import com.viettel.hstd.repository.hstd.ImportHistoryRepository;
import com.viettel.hstd.service.inf.ImportHistoryService;
import com.viettel.hstd.service.mapper.ImportHistoryConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportHistoryServiceImpl implements ImportHistoryService {

    private final ImportHistoryRepository importHistoryRepository;

    private final ImportHistoryConverter importHistoryConverter;

    @Override
    @Transactional
    public void save(ImportHistoryDTO.Request request) {
        importHistoryRepository.save(importHistoryConverter.requestToEntity(request));
    }

    @Override
    public ImportConstant.ImportStatus checkStatus(Long id) {
        return importHistoryRepository.findStatusById(id);
    }

    @Override
    public Page<ImportHistoryDTO.Response> search(ImportHistoryDTO.SearchCriteria searchCriteria) {
        List<SearchDTO.OrderDTO> orders = searchCriteria.getSortList();
        List<Sort.Order> queryOrderList = new ArrayList<>();
        for (SearchDTO.OrderDTO order : orders) {
            Sort.Order queryOrder = new Sort.Order(
                    order.direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, order.property);
            queryOrderList.add(queryOrder);
        }

        return importHistoryRepository.search(searchCriteria.getEmployeeId(), searchCriteria.getImportStatus(),
                searchCriteria.getImportCode(),searchCriteria.getFromImportDate(), searchCriteria.getToImportDate(),
                PageRequest.of(searchCriteria.getPage(), searchCriteria.getSize(), Sort.by(queryOrderList)));


    }
}
