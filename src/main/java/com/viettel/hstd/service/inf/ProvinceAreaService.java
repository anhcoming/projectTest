package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.hstd.ProvinceAreaDTO;
import com.viettel.hstd.entity.hstd.ProvinceAreaEntity;
import com.viettel.hstd.security.sso.SSoResponse;

import java.util.List;
import java.util.Optional;

public interface ProvinceAreaService extends
        CRUDService<ProvinceAreaDTO.ProvinceAreaRequest,
                ProvinceAreaDTO.ProvinceAreaResponse, Long> {

    List<ProvinceAreaDTO.ProvinceAreaTree> getTree();

    List<ProvinceAreaDTO.ProvinceAreaTreeShort> getTreeShort(SSoResponse sSoResponse);
    List<ProvinceAreaEntity> getChildren(Long addressId);
    Optional<ProvinceAreaEntity> findById(Long id);
    ProvinceAreaEntity getParentByLevel(Long addressId, Long level);
}
