package com.viettel.hstd.controller;

import client.VofficeClient;
import client.entity.EntityFileAttachment;
import client.entity.EntityText;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.viettel.hstd.constant.*;
import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.dto.hstd.ContractDTO;
import com.viettel.hstd.dto.hstd.InterviewSessionCvDTO;
import com.viettel.hstd.dto.hstd.TerminateContractDTO;
import com.viettel.hstd.entity.hstd.ContractEntity;
import com.viettel.hstd.entity.hstd.ResignSessionEntity;
import com.viettel.hstd.entity.hstd.TerminateContractEntity;
import com.viettel.hstd.entity.hstd.VoLogEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.ContractRepository;
import com.viettel.hstd.repository.hstd.ResignSessionRepository;
import com.viettel.hstd.repository.hstd.VoLogRepository;
import com.viettel.hstd.service.inf.*;
import com.viettel.hstd.util.FolderExtension;
import com.viettel.hstd.util.VOConfig;
import com.viettel.hstd.util.VOUtils;
import com.viettel.security.PassTranformer;
import com.viettel.voffice.vo.ResultObj;
import com.viettel.voffice.ws_autosign.service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.BindingProvider;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController()
@RequestMapping("voffice")
@Tag(name = "voffice")
@Slf4j
public class VOfficeResultController {
    @Autowired
    private InterviewSessionCvService interviewSessionCvService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private FolderExtension folderExtension;
    @Autowired
    private VOConfig voConfig;

    @Autowired
    private ContractService contractService;

    @Autowired
    private TerminateContractService terminateContractService;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    ResignSessionService resignSessionService;

    @Autowired
    ResignSessionRepository resignSessionRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private LaborContractService laborContractService;

    @Autowired
    private VoLogRepository voLogRepository;

    @Autowired
    private VofficeService vofficeService;

    @Autowired
    private VoLogService voLogService;

    @PostMapping("ReveiceFromVOfficeImpl")
    public Long returnSignReult(@RequestBody String data) {
        try {
            System.out.println("nhay vao ham ReveiceFromVOfficeImpl");
            System.out.println("VO Callback " + data);

            voLogService.logVOReceive(data);

            //đoạn code nhận được thông báo notify từ hệ thống Voffice
            HashMap<String, String> params = VOUtils.parseXML(data);
            ResultObj voSign = new ResultObj();
            voSign.setActionDate(params.get("actionDate"));
            voSign.setSignStatus(params.get("signStatus"));
            voSign.setSignComment(params.get("signComment"));
            voSign.setLastSignEmail(params.get("lastSignEmail"));
            voSign.setAppCode(params.get("appCode"));
            voSign.setDocumentCode(params.get("registerNumber"));
            voSign.setPublishDate(params.get("publishDate"));
            voSign.setPublishOganizationCode(params.get("publishOganizationCode"));
            voSign.setTransCode(params.get("transCode"));
            voSign.setVoTextId(Long.parseLong(params.get("voTextId")));
            voSign.setWsdl(params.get("wsdl"));
            System.out.println("transCode " + voSign.getTransCode());
            //String fileName = null;
            //byte[] signedData = viewSignedDoc(voSign.getTransCode(), fileName);
            if (voSign.getSignStatus().equals("3") || voSign.getSignStatus().equals("5")) {//đã được ký
                log.info("Voffice sign successful with transcode: " + voSign.getTransCode());
                // Lấy file đã ký về
                FileAttachTranferList tranferList = viewSignedDoc(voSign.getTransCode());
                if (tranferList.getLstFileAttachTranfer().size() > 0) {
                    FileAttachTranfer tranfer = tranferList.getLstFileAttachTranfer().stream().filter(item -> item.getFileSign() == 1L).findFirst().orElse(null);
                    if (tranfer == null) {
                        log.error("Không tìm được file trình ký");
                        return null;
                    }
                    List<FileAttachTranfer> lstFileAttachTransfer = tranferList.getLstFileAttachTranfer();
                    String encodePath = tranfer.getFileName();
                    String fileName = tranfer.getFileName().replace("-", "/");
                    String fileRealPath = fileService.getUploadFolder() + fileService.getFilePrefix() + fileName;
                    String fileEncodedPath = fileService.encodePath(fileRealPath);

                    if (voSign.getTransCode().startsWith("hsdt_cv_")) {
                        InterviewSessionCvDTO.InterviewSessionCvResponse interviewCv = interviewSessionCvService.findByTransCode(voSign.getTransCode());
                        if (interviewCv != null) {
                            System.out.println("co interviewCv voi transCode la : " + voSign.getTransCode());
                            System.out.println("interviewCv.interviewSessionCvId la : " + interviewCv.interviewSessionCvId);
                            interviewCv.signedFile = fileEncodedPath;
//                            interviewSessionCvService.updateSignedFile(interviewCv.signedFile, interviewCv.interviewSessionCvId);
                            interviewSessionCvService.updateNewFileAfterVO(interviewCv.interviewSessionCvId, fileEncodedPath, fileEncodedPath);
                        }
                        Path path = Paths.get(fileRealPath);
                        Files.write(path, tranfer.getAttachBytes());
                    } else if (voSign.getTransCode().startsWith("hsdt_contract_")) {
                        ContractDTO.ContractResponse contract = contractService.findByTransCode(voSign.getTransCode());
                        if (contract != null) {
                            System.out.println("co contract voi transCode la : " + voSign.getTransCode());
                            System.out.println("contract.contractId la : " + contract.contractId);
                            contract.signedFileEncodePath = fileEncodedPath;
                            contractService.updateSignedFile(contract.signedFileEncodePath, contract.contractId);
                        }
                        Path path = Paths.get(fileRealPath);
                        Files.write(path, tranfer.getAttachBytes());
                    } else if (voSign.getTransCode().startsWith("hsdt_terminate_")) {
                        TerminateContractDTO.TerminateContractResponse response = terminateContractService.findByTransCode(voSign.getTransCode());
                        if (response != null) {
                            System.out.println("Ky thoa thuan cham dut hop dong transCode la : " + voSign.getTransCode());
                            System.out.println("TerminateContract.terminateContractId la : " + response.terminateContractId);
                            int size = tranferList.getLstFileAttachTranfer().size();
                            log.info("So luong file nhan ve la: {}", size);
                            log.debug("FileAttachTranfer: " + new Gson().toJson(tranferList.getLstFileAttachTranfer()));

                            FileAttachTranfer fileAttachResignationLetter = tranferList.getLstFileAttachTranfer().stream().filter(f -> f.getFileName().contains(HSDTConstant.RESIGNATION_LETTER_PREFIX)).findFirst().orElse(null);

                            FileAttachTranfer fileAttachDebt = tranferList.getLstFileAttachTranfer().stream().filter(f -> f.getFileName().contains(HSDTConstant.DEBT_PREFIX)).findFirst().orElse(null);

                            FileAttachTranfer fileAttachAgreement = tranferList.getLstFileAttachTranfer().stream().filter(f -> f.getFileName().contains(HSDTConstant.AGREEMENT_PREFIX)).findFirst().orElse(null);

                            if (fileAttachResignationLetter != null && fileAttachDebt != null && fileAttachAgreement != null) {
                                log.info("Xử lý file đơn xin chấm dứt hợp đồng: " + fileAttachResignationLetter.getFileName());

                                String resignationLetterFileNameRaw = fileAttachResignationLetter.getFileName();
                                String resignationLetterFilePath = vofficeService.writeFile(resignationLetterFileNameRaw, fileAttachResignationLetter.getAttachBytes());

                                log.info("Xử lý file xác nhận công nợ: " + fileAttachDebt.getFileName());
                                String debtFileNameRaw = fileAttachDebt.getFileName();
                                String debtFilePath = vofficeService.writeFile(debtFileNameRaw, fileAttachDebt.getAttachBytes());

                                log.info("Xử lý file thỏa thuận chấm dứt hợp đồng: " + fileAttachAgreement.getFileName());
                                String agreementFileNameRaw = fileAttachAgreement.getFileName();
                                String agreementFilePath = vofficeService.writeFile(agreementFileNameRaw, fileAttachAgreement.getAttachBytes());

                                if (resignationLetterFilePath != null && debtFilePath != null && agreementFilePath != null) {
                                    terminateContractService.updateSignedFile(resignationLetterFilePath, response.terminateContractId,
                                            TerminateStatusConstant.vofficeApproval, null, debtFilePath, agreementFilePath);
                                } else {
                                    log.error("Có lỗi xảy ra khi ghi vào vào server với  transCode: " + voSign.getTransCode());
                                }

                            }

                        } else {
                            System.out.println("Khong tim thay don cham dut hd voi transCode la : " + voSign.getTransCode());
                        }
                        Path path = Paths.get(fileRealPath);
                        Files.write(path, tranfer.getAttachBytes());
                    } else if (voSign.getTransCode().startsWith("hsdt_sevallowance_")) {
                        TerminateContractDTO.TerminateContractResponse response = terminateContractService.findByTransCodeSevAllowance(voSign.getTransCode());
                        if (response != null) {
                            System.out.println("Ky xac nhan cong no transCode la : " + voSign.getTransCode());
                            System.out.println("terminateContract.terminateContractId la : " + response.terminateContractId);
                            terminateContractService.updateSignedFile(fileEncodedPath, response.terminateContractId, TerminateStatusConstant.sevApproval, voSign.getDocumentCode(), null, null);
                        } else {
                            System.out.println("Khong tim thay don cham dut hd voi transCode la : " + voSign.getTransCode());
                        }
                        Path path = Paths.get(fileRealPath);
                        Files.write(path, tranfer.getAttachBytes());
                    } else if (voSign.getTransCode().startsWith(HSDTConstant.BM09DocumentPrefix)) {
                        handleBMResult(voSign, fileName, tranfer);
                    } else if (voSign.getTransCode().startsWith(HSDTConstant.BM03DocumentPrefix)) {
                        handleBMResult(voSign, fileName, tranfer);
                    } else if (voSign.getTransCode().startsWith(HSDTConstant.BM09TCTDocumentPrefix)) {
                        handleBMTCTResult(voSign, lstFileAttachTransfer);
                    } else if (voSign.getTransCode().startsWith(HSDTConstant.BM03TCTDocumentPrefix)) {
                        handleBMTCTResult(voSign, lstFileAttachTransfer);
                    } else if (voSign.getTransCode().startsWith(HSDTConstant.ResignLaborContractPrefix)) {
                        handleResignLaborFinalResult(voSign, lstFileAttachTransfer, true);
                    } else if (voSign.getTransCode().startsWith(HSDTConstant.BrandNewContractDocumentPrefix)) {
                        handleNewContractDocument(voSign, tranfer, fileEncodedPath, true);
                    } else if (voSign.getTransCode().startsWith(HSDTConstant.SevAllowanceMultiPrefix)) {
                        handleSevAllowanceMulti(voSign, tranfer, true);
                    }
                }
            } else {
                log.info("Voffice sign failed with transcode: " + voSign.getTransCode());
                if (voSign.getTransCode().startsWith("hsdt_terminate_")) {
                    TerminateContractDTO.TerminateContractResponse response = terminateContractService.findByTransCode(voSign.getTransCode());
                    if (response != null) {
                        System.out.println("Tu choi phe duyet thoa thuan cham dut hd voi transCode la : " + voSign.getTransCode());
                        System.out.println("terminateContract.terminateContractId la : " + response.terminateContractId);
                        terminateContractService.updateSignedFile(null, response.terminateContractId, TerminateStatusConstant.vofficeReject, null, null, null);
                    } else {
                        System.out.println("Khong tim thay don cham dut hd voi transCode la : " + voSign.getTransCode());
                    }
                } else if (voSign.getTransCode().startsWith("hsdt_sevallowance_")) {
                    TerminateContractDTO.TerminateContractResponse response = terminateContractService.findByTransCode(voSign.getTransCode());
                    if (response != null) {
                        System.out.println("Tu choi phe duyet thoa thuan cham dut hd voi transCode la : " + voSign.getTransCode());
                        System.out.println("terminateContract.terminateContractId la : " + response.terminateContractId);
                        terminateContractService.updateSignedFile(null, response.terminateContractId, TerminateStatusConstant.sevReject, null, null, null);
                    } else {
                        System.out.println("Khong tim thay don cham dut hd voi transCode la : " + voSign.getTransCode());
                    }
                } else if (voSign.getTransCode().startsWith(HSDTConstant.BM09DocumentPrefix)) {
                    TerminateContractDTO.TerminateContractResponse response = terminateContractService.findByTransCode(voSign.getTransCode());
                    if (response != null) {
                        System.out.println("Tu choi phe duyet thoa thuan cham dut hd voi transCode la : " + voSign.getTransCode());
                        System.out.println("terminateContract.terminateContractId la : " + response.terminateContractId);
                        terminateContractService.updateSignedFile(null, response.terminateContractId, TerminateStatusConstant.sevReject, null, null, null);
                    } else {
                        System.out.println("Khong tim thay don cham dut hd voi transCode la : " + voSign.getTransCode());
                    }
                } else if (voSign.getTransCode().startsWith(HSDTConstant.SevAllowanceMultiPrefix)) {
                    handleSevAllowanceMulti(voSign, null, false);
                } else if (voSign.getTransCode().startsWith(HSDTConstant.ResignLaborContractPrefix)) {
                    handleResignLaborFinalResult(voSign, null, false);
                } else if (voSign.getTransCode().startsWith(HSDTConstant.BrandNewContractDocumentPrefix)) {
                    handleNewContractDocument(voSign, null, null, false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 2L;
        }
        return 1L;
    }

    @PostMapping("getListVof2UserByMail")
    public BaseResponse<List<Vof2EntityUser>> getListVof2UserByMail(@RequestBody String email) {
        List<Vof2EntityUser> listUser = new ArrayList<>();
        try {
            System.out.println(voConfig.ca_wsUrl);
            Vo2AutoSignSystemImplService sv = new Vo2AutoSignSystemImplService(new URL(voConfig.ca_wsUrl));
            Vo2AutoSignSystemImpl service = sv.getVo2AutoSignSystemImplPort();
            //Set timeout params
            int connectionTimeOutInMs = 20000; //Thoi gian timeout 10s
            Map<String, Object> requestContext = ((BindingProvider) service).getRequestContext();
            requestContext.put("com.sun.xml.internal.ws.connect.timeout", connectionTimeOutInMs);
            requestContext.put("com.sun.xml.internal.ws.request.timeout", connectionTimeOutInMs);
            requestContext.put("com.sun.xml.ws.request.timeout", connectionTimeOutInMs);
            requestContext.put("com.sun.xml.ws.connect.timeout", connectionTimeOutInMs);
            listUser = service.getListVof2UserByMail(Arrays.asList(email));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BaseResponse
                .ResponseBuilder<List<Vof2EntityUser>>()
                .success(listUser);
    }

    public FileAttachTranferList viewSignedDoc(String transCode)
            throws MalformedURLException {
        Vo2AutoSignSystemImplService sv = new Vo2AutoSignSystemImplService(
                new URL(voConfig.ca_wsUrl));
        Vo2AutoSignSystemImpl service = sv.getVo2AutoSignSystemImplPort();
        FileAttachTranferList file = service.getFile(voConfig.ca_appCode, transCode, true);
        if (file == null) {
            return service.getFile("CTCT_WMS", transCode, true);
        }
        //File.separator
        return file;
    }

    public byte[] viewSignedDoc(String transCode, String fileName) {
        byte[] result = null;


        try {
            Vo2AutoSignSystemImplService sv = new Vo2AutoSignSystemImplService(new URL(voConfig.ca_wsUrl));
            Vo2AutoSignSystemImpl service = sv.getVo2AutoSignSystemImplPort();

            VofficeClient.builder(voConfig.ca_wsUrl);
            PassTranformer.setInputKey(voConfig.ca_encrypt_key);
            // password voffice
            String passVoffice = PassTranformer.encrypt(voConfig.passVoffice);

            VofficeClient client = VofficeClient.getInstance(voConfig.userVoffice, passVoffice);
            if (client != null) {
                String passEncrypt = PassTranformer.encrypt(voConfig.ca_appPass);
                TransactionBO tranBo = service.getTransInfor(voConfig.ca_appCode, passEncrypt, transCode);
                if (tranBo == null) {
                    return null;
                }
                Long textId = client.getTextId(voConfig.ca_wsUrl, transCode, voConfig.ca_appCode, passEncrypt);
                if (textId != null) {
                    EntityText et = client.getTextDetail(textId, 0);
                    if (et != null && et.getFileMainSign() != null) {
                        EntityFileAttachment eAtt = et.getFileMainSign().get(0);
                        if (eAtt != null) {
                            System.out.println("file revieve info:" + et.getContent());
                            result = client.downloadContentFile(eAtt.getFileAttachmentId(), eAtt.getFileName(),
                                    eAtt.getFilePath(), textId);
                            fileName = eAtt.getFileName();
                            System.out.println("file revieve info:" + result);
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("get file error:" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    private void handleBMResult(ResultObj voSign, String fileName, FileAttachTranfer tranfer) throws IOException {
        ResignSessionEntity resignSessionEntity = resignSessionRepository.findFirstByTranscode(voSign.getTransCode())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đợt tái ký"));
        log.info("Id của đợt tái ký này là : {}", resignSessionEntity.getResignSessionId());
        log.info("Tên của đợt tái ký này là : {}", resignSessionEntity.getName());
        resignSessionService.updateResignVofficeStatus(resignSessionEntity.getResignSessionId(), ResignVofficeStatus.RECEIVED);
        resignSessionService.updateResignStatusAndRelated(resignSessionEntity.getResignSessionId(), ResignStatus.RECEIVED_BM_09_VOFFICE_AND_SUCCESS);
//                        resignSessionService.updateBM09File(resignSessionEntity.getResignSessionId(), tranfer.getFileName(), tranfer.getAttachBytes());

//                        contractService.createTempContractFile(resignSessionEntity.getResignSessionId());
        Path path = Paths.get(folderExtension.getUploadFolder() + File.separator + fileName);
        Files.write(path, tranfer.getAttachBytes());
    }

    private void handleBMTCTResult(ResultObj voSign, List<FileAttachTranfer> transferList) {
        log.info("start handleBMTCTResult");
        List<ResignSessionEntity> resignSessionEntityList = resignSessionRepository.findByTranscode(voSign.getTransCode());

        if (resignSessionEntityList.size() == 0) {
            throw new NotFoundException("Không tìm thấy đợt tái ký voi transcode " + voSign.getTransCode());
        }

        Set<Long> resignIdSet = resignSessionEntityList.stream()
                .map(ResignSessionEntity::getResignSessionId)
                .collect(Collectors.toSet());

        resignSessionService.createTempContractFile(resignSessionEntityList);
        log.debug("Create temp contract file");
        resignSessionService.updateResignStatusAndRelated(resignIdSet, ResignStatus.TEMP_CONTRACT_CREATE);
        log.debug("Update resign status and related");
        transferList.stream().filter(i -> i.getFileSign() == 1L).forEach(transfer -> {
            String fileName = fileService.decodePath(transfer.getFileName());
            byte[] data = transfer.getAttachBytes();

            Path path = Paths.get(folderExtension.getUploadFolder() + File.separator + fileName);
            try {
                Files.write(path, data);
            } catch (IOException e) {
                log.error("Có vấn đề khi lưu lại file nhận từ Voffice");
                String stackTrace = ExceptionUtils.getStackTrace(e);
                log.error(stackTrace);
            }
        });
        log.debug("Update new voffice file");

    }

    private void handleResignLaborFinalResult(ResultObj voSign, List<FileAttachTranfer> transferList, boolean isSuccess) {
        ContractEntity contractEntity = contractRepository.findByTransCode(voSign.getTransCode());
        if (contractEntity == null) {
            log.error("Không có hợp đồng với transcode: " + voSign.getTransCode());
            return;
        }
        if (isSuccess) {
            contractEntity.setIsActive(true);
            contractEntity.setResignStatus(ResignStatus.NOT_IN_RESIGN_SESSION);

            FileAttachTranfer fileAttachTranfer = transferList.stream().filter(file -> file.getFileSign() == 1L).findFirst().orElse(null);
            if (fileAttachTranfer == null) {
                log.error("Không nhận được file trình ký từ vOffice với transcode: " + voSign.getTransCode());
                return;
            }
            try {
                String contractFileNameRaw = fileAttachTranfer.getFileName();
                // File name raw chưa có prefix
                byte[] dataFile = fileAttachTranfer.getAttachBytes();
                String contractFilePath = vofficeService.writeFile(contractFileNameRaw, dataFile);
                contractEntity.setSignedFileEncodePath(contractFilePath);
                contractEntity.setSignedFile(fileService.getFileNameFromEncodePathWithPrefix(contractFilePath));
                contractRepository.save(contractEntity);
                log.debug("Cập nhật file ký");
            } catch (IOException e) {
                log.error("Không thể ghi được file từ vOffice");
            }
        } else {
            contractEntity.setResignStatus(ResignStatus.RECEIVED_VOFFICE3_AND_FAIL);
            contractRepository.save(contractEntity);
        }

    }

    private void handleNewContractDocument(ResultObj voSign, FileAttachTranfer tranfer, String filePath, boolean isSuccess) throws IOException {
        ContractEntity contractEntity = contractRepository.findFirstByTransCode(voSign.getTransCode())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hợp đồng với transCode là " + voSign.getTransCode()));
        log.debug("Đang nhận được kết quả trình ký của hợp đồng với id " + contractEntity.getContractId());

        ContractDTO.ContractNewStatusRequest contractNewStatusRequest = new ContractDTO.ContractNewStatusRequest();
        contractNewStatusRequest.newContractStatus = NewContractStatus.RECEIVED_FROM_VOFFICE;
        contractNewStatusRequest.contractId = contractEntity.getContractId();
        contractService.updateNewContractStatus(contractNewStatusRequest);

        if (isSuccess) {
            ContractDTO.ContractChangeSignedFileRequest changeSignedFileRequest = new ContractDTO.ContractChangeSignedFileRequest();
            changeSignedFileRequest.contractId = contractEntity.getContractId();
            changeSignedFileRequest.signedFile = filePath;
            contractService.updateSignedFile(changeSignedFileRequest);

            String fileRealPath = fileService.decodePath(filePath);

            Path path = Paths.get(fileRealPath);
            Files.write(path, tranfer.getAttachBytes());
        }
    }

    private void handleSevAllowanceMulti(ResultObj voSign, FileAttachTranfer fileAttachTranfer, boolean isSuccess) {
        List<TerminateContractEntity> terminateContractEntities = terminateContractService.findBySevAllowanceMulti(voSign.getTransCode());
        if (terminateContractEntities == null || terminateContractEntities.isEmpty()) {
            return;
        }

        if (isSuccess) {
            String fileNameRaw = fileAttachTranfer.getFileName();
            try {
                String filePath = vofficeService.writeFile(fileNameRaw, fileAttachTranfer.getAttachBytes());
                terminateContractService.saveSevAllowanceMulti(true, terminateContractEntities, filePath);
            } catch (IOException exception) {
                log.error("Không ghi được file: " + fileNameRaw);
            }

        } else {
            terminateContractService.saveSevAllowanceMulti(false, terminateContractEntities, null);
        }
    }
}
