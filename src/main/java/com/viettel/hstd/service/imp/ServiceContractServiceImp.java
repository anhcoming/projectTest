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
import com.viettel.hstd.dto.hstd.ServiceContractDTO;
import com.viettel.hstd.dto.hstd.ServiceContractDTO.*;
import com.viettel.hstd.entity.hstd.CollaboratorContractEntity;
import com.viettel.hstd.entity.hstd.EmployerInfoEntity;
import com.viettel.hstd.entity.hstd.ServiceContractEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.EmployerInfoRepository;
import com.viettel.hstd.repository.hstd.ServiceContractRepository;
import com.viettel.hstd.security.AuthenticationFacade;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.ServiceContractService;
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
public class ServiceContractServiceImp implements ServiceContractService {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    Message message;
    @Autowired
    private ServiceContractRepository serviceContractRepository;
    @Autowired
    private EmployerInfoRepository employerInfoRepository;
    @Autowired
    AuthenticationFacade authenticationFacade;

    public Page<ServiceContractResponse> findPage(SearchDTO searchRequest) {

        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<ServiceContractEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<ServiceContractEntity> list;
        if (searchRequest.pagedFlag) {
            list = serviceContractRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = serviceContractRepository.findAll(p);
        }
        return list.map(obj ->
                this.objectMapper.convertValue(obj, ServiceContractResponse.class)
        );
    }

    @Override
    public ServiceContractResponse findOneById(Long id) {
        ServiceContractEntity contract = serviceContractRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return objectMapper.convertValue(contract, ServiceContractResponse.class);

    }

    @Override
    public Boolean delete(Long aLong) {
        return null;
    }

    @Autowired
    private FolderExtension folderExtension;

    private void updateContractFile(String fileName, Long id) {
        ServiceContractEntity entity = serviceContractRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        entity.setContractFile(fileName);
        entity = serviceContractRepository.save(entity);
    }

    @Override
    public FileDTO.FileResponse exportContract(Long id) {

        try {
            ServiceContractResponse contract = findOneById(id);
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
                    hm.put(key, map.get(key));
                }
            }
            String pathStore = folderExtension.getUploadFolder();
            File file = new ClassPathResource("template/HopDong_DichVu.docx").getFile();
            String path = file.getPath();
            ExportWord<ServiceContractDTO> exportWord = new ExportWord<>();
            String resultPath = exportWord.export(path, pathStore, "HopDongDichVu",
                    hm, ExportFileExtension.PDF, null, "serviceContract");
            file = new File(resultPath);
            String fileName = folderExtension.getFileName(resultPath);
            String contentType = Files.probeContentType(file.toPath());
            updateContractFile(fileName, id);
            byte[] data = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
            fileDTO.fileName = file.getName();
            fileDTO.filePath = fileName;
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
