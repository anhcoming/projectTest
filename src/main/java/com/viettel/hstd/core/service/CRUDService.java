package com.viettel.hstd.core.service;

import com.viettel.hstd.core.dto.SearchDTO;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public interface CRUDService<REQ, RES, ID> {

    default List<RES> findAll() {
        return new ArrayList<>();
    }

    default List<RES> findAll(ID parentId) {
        return new ArrayList<>();
    }

    default Page<RES> findPage(SearchDTO searchDTO) {return null;}

    RES findOneById(ID id);

    Boolean delete(ID id);

    default Boolean deleteAll(List<ID> listID) {return null;}

    default RES create(REQ request) {
        return null;
    }

    default RES update(ID id, REQ request) {
        return null;
    }

//    default Set<IdTable> mergeId(List<Long> oldIds, List<Long> newIds) {
//        Set<Long> mergeId = new HashSet<>();
//        mergeId.addAll(oldIds);
//        mergeId.addAll(newIds);
//        return mergeId.stream().map(id -> {
//            IdTable idTable = new IdTable(id);
//            if (oldIds.contains(id) && newIds.contains(id)) idTable.state = 1L; // để nguyên
//            if (!oldIds.contains(id) && newIds.contains(id)) idTable.state = 2L; // cần thêm
//            if (oldIds.contains(id) && !newIds.contains(id)) idTable.state = 0L; // cần xóa
//            return idTable;
//        }).collect(Collectors.toSet());
//    }
}
