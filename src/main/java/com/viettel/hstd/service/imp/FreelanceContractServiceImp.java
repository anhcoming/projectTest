package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.constant.ExportFileExtension;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.CustomMapper;
import com.viettel.hstd.core.utils.ExportWord;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.CollaboratorContractDTO;
import com.viettel.hstd.dto.hstd.FreelanceContractDTO.*;
import com.viettel.hstd.entity.hstd.CollaboratorContractEntity;
import com.viettel.hstd.entity.hstd.EmployerInfoEntity;
import com.viettel.hstd.entity.hstd.FreelanceContractEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.EmployerInfoRepository;
import com.viettel.hstd.repository.hstd.FreelanceContractRepository;
import com.viettel.hstd.security.AuthenticationFacade;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.FreelanceContractService;
import com.viettel.hstd.util.FolderExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class FreelanceContractServiceImp implements FreelanceContractService {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    Message message;
    @Autowired
    private FreelanceContractRepository freelanceContractRepository;
    @Autowired
    private EmployerInfoRepository employerInfoRepository;
    @Autowired
    AuthenticationFacade authenticationFacade;

    @Override
    public Page<FreelanceContractResponse> findPage(SearchDTO searchRequest) {
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<FreelanceContractEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<FreelanceContractEntity> list;
        if (searchRequest.pagedFlag) {
            list = freelanceContractRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = freelanceContractRepository.findAll(p);
        }
        return list.map(obj ->
                this.objectMapper.convertValue(obj, FreelanceContractResponse.class)
        );
    }

    @Override
    public FreelanceContractResponse findOneById(Long id) {
        FreelanceContractEntity contract = freelanceContractRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return objectMapper.convertValue(contract, FreelanceContractResponse.class);

    }

    @Override
    public Boolean delete(Long aLong) {
        return null;
    }

    @Autowired
    private FolderExtension folderExtension;

    private void updateContractFile(String fileName, Long id) {
        FreelanceContractEntity entity = freelanceContractRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        entity.setContractFile(fileName);
        entity = freelanceContractRepository.save(entity);
    }

    @Override
    public FileDTO.FileResponse exportContract(Long id) {
        try {
            FreelanceContractResponse contract = findOneById(id);
            Map<String, String> map = CustomMapper.convert(contract);
            //<editor-fold desc="Them thong tin nguoi ky hop dong">
            SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
            Long unitId = 0L;
            if (sSoResponse.getOrganizationId() != null && sSoResponse.getOrganizationId() > 0) {
                unitId = sSoResponse.getOrganizationId();
                if (unitId > 0) {
                    EmployerInfoEntity employer = employerInfoRepository.findByUnitId(unitId);
                    if (employer != null) {
                        Map<String, String> employerMap = CustomMapper.convert(employer);
                        map.putAll(employerMap);
                    }
                }
            }
            //</editor-fold>
            HashMap<String, String> hm = new HashMap<>();
            for (String key : map.keySet()) {
                if (map.get(key) != null) {
                    hm.put(key, map.get(key).toString());
                }
            }
            String pathStore = folderExtension.getUploadFolder();
            File file = new ClassPathResource("template/HopDong_GiaoKhoan.docx").getFile();
            String path = file.getPath();
            ExportWord<CollaboratorContractDTO> exportWord = new ExportWord<>();
            String resultPath = exportWord.export(path, pathStore, "HopDongGiaoKhoan",
                    hm, ExportFileExtension.PDF, null, "freelance");
            file = new File(resultPath);
            String contentType = Files.probeContentType(file.toPath());
            updateContractFile(file.getName(), id);
            byte[] data = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
            fileDTO.fileName = file.getName();
            fileDTO.filePath = file.getName();
            fileDTO.size = file.length();
            fileDTO.type = contentType;
            fileDTO.data = new String(data, StandardCharsets.US_ASCII);
            return fileDTO;
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
        return null;
    }
}
