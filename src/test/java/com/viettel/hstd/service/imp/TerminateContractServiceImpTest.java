package com.viettel.hstd.service.imp;

import com.viettel.hstd.constant.HSDTConstant;
import com.viettel.hstd.constant.TerminateStatusConstant;
import com.viettel.hstd.core.utils.StringUtils;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.entity.hstd.TerminateContractEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.TerminateContractRepository;
import com.viettel.hstd.service.inf.FileService;
import com.viettel.hstd.service.inf.TerminateContractService;
import com.viettel.hstd.util.FileUtils;
import org.apache.xmlbeans.XmlException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TerminateContractServiceImpTest {

    @Autowired
    TerminateContractService terminateContractService;
    @Autowired
    TerminateContractRepository terminateContractRepository;
    @Autowired
    Environment environment;
    @Autowired
    FileService fileService;

    @Test
    void mergeTerminateContract() {
        if(Arrays.asList(environment.getActiveProfiles()).contains("dev")){
        List<Long> ids = new ArrayList<>();
        ids.add(1209L);
        ids.add(1255L);
//        FileDTO.FileResponse fileResponse = terminateContractService.mergeTerminateContract(ids);
        }
    }
}