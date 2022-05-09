package com.viettel.hstd.core.utils;

import com.viettel.hstd.core.entity.EntityBase;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class MapUtils {
    @Autowired
    ModelMapper modelMapper;

    public void customMap(EntityBase e1, EntityBase e2) {
        Boolean oldDelflag = e2.getDelFlag();
        LocalDateTime oldCreatedAt = e2.getCreatedAt();
        Long oldCreatedBy = e2.getCreatedBy();
        LocalDateTime oldUpdatedAt = e2.getUpdatedAt();
        Long oldUpdatedBy = e2.getUpdatedBy();
        modelMapper.map(e1, e2);
        if (e1.getDelFlag() == null) e2.setDelFlag(oldDelflag);
        if (e1.getCreatedAt() == null) e2.setCreatedAt(oldCreatedAt);
        if (e1.getCreatedBy() == null) e2.setCreatedBy(oldCreatedBy);
        if (e1.getUpdatedAt() == null) e2.setUpdatedAt(oldUpdatedAt);
        if (e1.getUpdatedBy() == null) e2.setUpdatedBy(oldUpdatedBy);
    }

}
