package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.MapUtils;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.ProvinceAreaDTO.*;
import com.viettel.hstd.entity.hstd.ProvinceAreaEntity;
import com.viettel.hstd.entity.hstd.view.ProvinceAreaShortEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.ProvinceAreaRepository;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.ProvinceAreaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.viettel.hstd.core.constant.ProvinceAreaConstant.KV1_ADDRESS_ID;
import static com.viettel.hstd.core.constant.ProvinceAreaConstant.KV2_ADDRESS_ID;
import static com.viettel.hstd.core.constant.ProvinceAreaConstant.KV3_ADDRESS_ID;

@Service
public class ProvinceAreaServiceImp extends BaseService implements ProvinceAreaService {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ProvinceAreaRepository provinceAreaRepository;

    @Autowired
    MapUtils mapUtils;

    @Autowired
    Message message;

    @Value("${app.sso.domain-code}")
    private String appDomainCode;

    @Override
    public Page<ProvinceAreaResponse> findPage(SearchDTO searchRequest) {
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<ProvinceAreaEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<ProvinceAreaEntity> list;
        if (searchRequest.pagedFlag) {
            list = provinceAreaRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = provinceAreaRepository.findAll(p);
        }

        return list.map((obj) ->
                this.objectMapper.convertValue(obj, ProvinceAreaResponse.class)
        );
    }

    @Override
    public ProvinceAreaResponse findOneById(Long aLong) {
        ProvinceAreaEntity e = provinceAreaRepository
                .findById(aLong)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.province_area.not_found")));
        return objectMapper.convertValue(e, ProvinceAreaResponse.class);
    }

    @Override
    public Boolean delete(Long aLong) {
        findOneById(aLong);
        Integer result = provinceAreaRepository.softDelete(aLong);
        return result != null;
    }

    @Override
    public ProvinceAreaResponse create(ProvinceAreaRequest request) {
        ProvinceAreaEntity e = objectMapper.convertValue(request, ProvinceAreaEntity.class);
        e = provinceAreaRepository.save(e);
        return objectMapper.convertValue(e, ProvinceAreaResponse.class);
    }

    @Override
    public ProvinceAreaResponse update(Long id, ProvinceAreaRequest request) {
        ProvinceAreaEntity e = provinceAreaRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.province_area.not_found")));
        ProvinceAreaEntity newE = objectMapper.convertValue(request, ProvinceAreaEntity.class);
        mapUtils.customMap(newE, e);
        e.setId(id);
        e = provinceAreaRepository.save(e);
        return objectMapper.convertValue(e, ProvinceAreaResponse.class);
    }

    @Override
    public List<ProvinceAreaTree> getTree() {
        List<ProvinceAreaTree> appParamDTOS;
        appParamDTOS = provinceAreaRepository.findAll().stream().map(appParamDTO -> objectMapper.convertValue(appParamDTO,
                ProvinceAreaTree.class)).collect(Collectors.toList());

        Map<Long, ProvinceAreaTree> mapTmp = new HashMap<>();
        //Save all nodes to a map
        for (ProvinceAreaTree current : appParamDTOS) {
            mapTmp.put(current.id, current);
        }

        for (ProvinceAreaTree current : appParamDTOS) {
            Long parentId = current.parentId;
            if (parentId != null) {
                ProvinceAreaTree parent = mapTmp.get(parentId);
                if (parent != null) {
                    parent.addChild(current);
                    mapTmp.put(parentId, parent);
                    mapTmp.put(current.id, current);
                }
            }
        }
        List<ProvinceAreaTree> provinceAreaTrees = new ArrayList<>();
        for (ProvinceAreaTree node : mapTmp.values()) {
            if (node.parentId == null || node.parentId == 0L) {
                provinceAreaTrees.add(node);
            }
        }
        return provinceAreaTrees;
    }

    @Override
    @Cacheable("province-area")
    public List<ProvinceAreaTreeShort> getTreeShort(SSoResponse sSoResponse) {
//        if (sSoResponse == null || sSoResponse.getSysUserId() == null) throw new BadRequestException(message.getMessage("message.unauthorized"));

        List<ProvinceAreaTreeShort> appParamDTOS;

//        Set<String> provinceCodeSet = sysUserRepository.getProvinceCodeFromSysUserId(appDomainCode, sSoResponse.getSysUserId());
        List<ProvinceAreaShortEntity> list2 = provinceAreaRepository.getAll3();

        if (list2.stream().anyMatch(objects -> objects.getParentId() != null &&  objects.getParentId().equals(KV1_ADDRESS_ID)))
            list2.add(new ProvinceAreaShortEntity(KV1_ADDRESS_ID, "KV1", "Khu vực 1", 1L, "KV1"));

        if (list2.stream().anyMatch(objects ->  objects.getParentId() != null &&  objects.getParentId().equals(KV2_ADDRESS_ID)))
            list2.add(new ProvinceAreaShortEntity(KV2_ADDRESS_ID, "KV2", "Khu vực 2", 1L, "KV2"));

        if (list2.stream().anyMatch(objects ->  objects.getParentId() != null &&  objects.getParentId().equals(KV3_ADDRESS_ID)))
            list2.add(new ProvinceAreaShortEntity(KV3_ADDRESS_ID, "KV3", "Khu vực 3", 1L, "KV3"));

        appParamDTOS = list2.stream().map(appParamDTO -> {
            ProvinceAreaTreeShort provinceAreaTreeShort = new ProvinceAreaTreeShort();
            provinceAreaTreeShort.id = appParamDTO.getId();
            provinceAreaTreeShort.code = appParamDTO.getCode();
            provinceAreaTreeShort.name = appParamDTO.getName();
            provinceAreaTreeShort.parentId = appParamDTO.getParentId();

            return provinceAreaTreeShort;
        }).collect(Collectors.toList());

        Map<Long, ProvinceAreaTreeShort> mapTmp = new HashMap<>();
        //Save all nodes to a map
        for (ProvinceAreaTreeShort current : appParamDTOS) {
            mapTmp.put(current.id, current);
        }

        for (ProvinceAreaTreeShort current : appParamDTOS) {
            Long parentId = current.parentId;
            if (parentId != null) {
                ProvinceAreaTreeShort parent = mapTmp.get(parentId);
                if (parent != null) {
                    parent.addChild(current);
                    mapTmp.put(parentId, parent);
                    mapTmp.put(current.id, current);
                }
            }
        }
        List<ProvinceAreaTreeShort> provinceAreaTrees = new ArrayList<>();
        for (ProvinceAreaTreeShort node : mapTmp.values()) {
            if (node.parentId == null || node.parentId == 0L) {
                provinceAreaTrees.add(node);
            }
        }
        return provinceAreaTrees;
    }

    public List<ProvinceAreaEntity> recruitmentChildren(ProvinceAreaEntity provinceAreaEntity, List<ProvinceAreaEntity> result) {
        List<ProvinceAreaEntity> listProvinceAreaResponses = provinceAreaRepository.findByParentId(provinceAreaEntity.getId());
        if (listProvinceAreaResponses.size() == 0)
            result.add(provinceAreaEntity);
        else {
            for (ProvinceAreaEntity element : listProvinceAreaResponses)
                result = recruitmentChildren(element, result);
        }
        return result;
    }

    @Override
    public List<ProvinceAreaEntity> getChildren(Long addressId) {
        List<ProvinceAreaEntity> result = new ArrayList<>();
        ProvinceAreaEntity provinceAreaEntity = provinceAreaRepository.findById(addressId).orElse(new ProvinceAreaEntity());
        return recruitmentChildren(provinceAreaEntity, result);
    }

    @Override
    public ProvinceAreaEntity getParentByLevel(Long parentId, Long level) {
        ProvinceAreaEntity result = new ProvinceAreaEntity();
        ProvinceAreaEntity provinceAreaEntity = provinceAreaRepository.findById(parentId).orElse(new ProvinceAreaEntity());
        if (provinceAreaEntity.getGroupLevel() == level) {
            result = provinceAreaEntity;
        } else {
            getParentByLevel(provinceAreaEntity.getParentId(), level);
        }
        return result;
    }
    
    public Optional<ProvinceAreaEntity> findById(Long id) {
        return provinceAreaRepository.findById(id);
    }

}
