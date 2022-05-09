package com.viettel.hstd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.viettel.hstd.constant.*;
import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.dto.hstd.*;
import com.viettel.hstd.entity.hstd.VoLogEntity;
import com.viettel.hstd.repository.hstd.ResignSessionRepository;
import com.viettel.hstd.security.AuthenticationFacade;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.*;
import com.viettel.hstd.util.EncryptionUtils;
import com.viettel.hstd.util.FolderExtension;
import com.viettel.hstd.util.PassWordUtil;
import com.viettel.hstd.util.VOConfig;
import com.viettel.security.PassTranformer;
import com.viettel.voffice.ws_autosign.service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.BindingProvider;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController()
@RequestMapping("sign")
@Slf4j
@Tag(name = "sign")
@Transactional
public class VOfficeSignController {
    @Autowired
    private FolderExtension folderExtension;
    @Autowired
    private InterviewSessionCvService interviewSessionCvService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private TerminateContractService terminateContractService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private VOConfig voConfig;

    @Autowired
    private LaborContractService laborContractService;

    @Autowired
    private ResignSessionService resignSessionService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ResignSessionRepository resignSessionRepository;

    @Autowired
    private VofficeService vofficeService;
    @Autowired
    private Environment environment;
    @Autowired
    private AuthenticationFacade authenticationFacade;
    @Autowired
    private VoLogService voLogService;

    /*
        Trừ phần liên quan đến tái ký được xử lý ở VofficeServiceImp thì tất cả nhưng request gửi đến VO được xử lý ở đây
     */
    @PostMapping("signVoffice")
    public BaseResponse<String> returnSignReult(@RequestBody VOfficeSignDTO request) {
        SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();

        voLogService.logVOSent(request);

        if (request == null || request.signData == null || request.signData.size() == 0
                || request.userSign == null || request.userSign.size() == 0) {
            return new BaseResponse
                    .ResponseBuilder<String>()
                    .failed(null, "Dữ liệu đầu vào không đúng định dạng, vui lòng thử lại sau");
        }

        if (request.type.equals(VOfficeSignType.RESIGN_FORM_09) ||
                request.type.equals(VOfficeSignType.RESIGN_FORM_03) ||
                request.type.equals(VOfficeSignType.RESIGN_FORM_09_TCT) ||
                request.type.equals(VOfficeSignType.RESIGN_FORM_03_TCT) ||
                request.type.equals(VOfficeSignType.SEV_ALLOWANCE_MULTI)) {
            boolean result = vofficeService.sentVoffice(request);
            if (result) {
                return new BaseResponse
                        .ResponseBuilder<String>()
                        .success(null, "Trình ký thành công");
            } else {
                return new BaseResponse
                        .ResponseBuilder<String>()
                        .failed(null, "Trình ký file thất bại, vui lòng thử lại sau.");
            }

        }

        LocalDateTime localDateTime = LocalDateTime.now();
        int size = request.signData.size();
        for (int i = 0; i < size; i++) {
            String debtFileName = null, agrFileName = null;
            String transactionCode = "";
            Long id = request.signData.get(i).id;
            String filePath = request.signData.get(i).filePath;
            Map<String, Object> dataMap = request.signData.get(i).data;
            if (id == null || id <= 0) {
                if (request.type != VOfficeSignType.RESIGN_FORM_09_TCT && request.type != VOfficeSignType.RESIGN_FORM_03_TCT) {
                    continue;
                }

            }
            if ((filePath == null || filePath.trim().length() <= 0) &&
                    (request.signData.get(i).data == null || !request.signData.get(i).data.containsKey("attachments"))) {
                continue;
            }


            if (request.type.equals(VOfficeSignType.INTERVIEW_SESSION_CV)) {
                transactionCode = HSDTConstant.InterviewReportPrefix + id + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            } else if (VOfficeSignType.COLLABORATOR == request.type
                    || VOfficeSignType.FREELANCE == request.type
                    || VOfficeSignType.LABOR == request.type
                    || VOfficeSignType.PROBATIONARY == request.type
                    || VOfficeSignType.SERVICE == request.type) {
                transactionCode = "hsdt_contract_" + id + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            } else if (request.type == VOfficeSignType.TERMINATE) {
                transactionCode = "hsdt_terminate_" + id + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            } else if (request.type == VOfficeSignType.RESIGN_FORM_09) {
                transactionCode = HSDTConstant.BM09DocumentPrefix + id + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            } else if (request.type == VOfficeSignType.SEV_ALLOWANCE) {
                transactionCode = "hsdt_sevallowance_" + id + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            } else if (request.type == VOfficeSignType.BRAND_NEW_CONTRACT) {
                transactionCode = HSDTConstant.BrandNewContractDocumentPrefix + id + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            } else if (request.type == VOfficeSignType.RESIGN_FORM_03) {
                transactionCode = HSDTConstant.BM03DocumentPrefix + id + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            } else if (request.type == VOfficeSignType.RESIGN_FORM_09_TCT) {
                transactionCode = HSDTConstant.BM09TCTDocumentPrefix + dataMap.get("quarter") + "." + dataMap.get("year") + "_" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(localDateTime);
            } else if (request.type == VOfficeSignType.RESIGN_FORM_03_TCT) {
                transactionCode = HSDTConstant.BM03TCTDocumentPrefix + id + "_" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(localDateTime);
            } else if (request.type == VOfficeSignType.RESIGN_LABOR_FINAL) {
                transactionCode = HSDTConstant.ResignLaborContractPrefix + "_" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(localDateTime);
            } else {
                transactionCode = "hsdt_" + id + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            }

            String code = transactionCode; //mã văn bản
            try {
                //<editor-fold desc="Khoi tao tham so service voffice">
                // Goi service ben vOffice, chuyen cac tham so tuong ung
                Vo2AutoSignSystemImplService sv = new Vo2AutoSignSystemImplService(new URL(voConfig.ca_wsUrl));
                Vo2AutoSignSystemImpl service = sv.getVo2AutoSignSystemImplPort();

                // Set timeout params
                int connectionTimeOutInMs = 20000; // Thoi gian timeout 10s

                Map<String, Object> requestContext = ((BindingProvider) service).getRequestContext();
                requestContext.put("com.sun.xml.internal.ws.connect.timeout", connectionTimeOutInMs);
                requestContext.put("com.sun.xml.internal.ws.request.timeout", connectionTimeOutInMs);
                requestContext.put("com.sun.xml.ws.request.timeout", connectionTimeOutInMs);
                requestContext.put("com.sun.xml.ws.connect.timeout", connectionTimeOutInMs);

                // Truyen cac tham so cho webservice Voffice
                KttsVofficeCommInpuParam param = new KttsVofficeCommInpuParam();
                String appCodeEnc = EncryptionUtils.encrypt(voConfig.ca_appCode, EncryptionUtils.getKey());
                param.setAppCode(appCodeEnc); // tên app account đăng nhập
                // nhap tren giao dien trinh ky
                String appPassEnc1 = PassWordUtil.getInstance().encrypt(voConfig.ca_appPass);
                String appPassEnc2 = EncryptionUtils.encrypt(appPassEnc1, EncryptionUtils.getKey());

                param.setAppPass(appPassEnc2); // mật khẩu appaccount đăng nhập nhap tren giao dien trinh ky
                // Lay userName, pass vOffice
                PassTranformer.setInputKey(voConfig.ca_encrypt_key);// set key to encrypt password voffice

//                param.setAccountName(voConfig.userVoffice);
//                param.setAccountPass(PassTranformer.encrypt(voConfig.passVoffice));// ma hoa password

                String vofficeUser = "";
                String vofficePass = "";

                if (Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
                    log.info("VO using dev account");
                    vofficeUser = voConfig.userVoffice;
                    vofficePass = PassTranformer.encrypt(voConfig.passVoffice);
                } else {
                    log.info("VO using SSO {}", sSoResponse.getEmployeeCode());
                    vofficeUser = sSoResponse.getEmployeeCode();
//                    log.info("Decrypted drowssap {}",PassTranformer.decrypt(sSoResponse.getEncryptedPw()));
                    vofficePass = sSoResponse.getEncryptedPw();
                }

                param.setAccountName(vofficeUser);
                param.setAccountPass(vofficePass);// encrypted password

                param.setTransCode(transactionCode);
                param.setSender(voConfig.ca_sender); // tên hệ thống trình kí văn bản. -> QLDTKTTS
                param.setRegisterNumber(vofficeService.generateRegisterNumber(request, sSoResponse)); // ma bien ban trinh ky = Ma BB ben HCQT
                param.setDocTitle(vofficeService.generateDocumentTitle(request, code));
                param.setIsCanVanthuXetduyet(false);// Khong can van thu xet duyet
                param.setIsCanBanhanh(true);

                //param.setHinhthucVanban(7L);
                param.setHinhthucVanban(voConfig.docTypeId); // Test
                param.setAreaId(voConfig.areaId);
//                param.setHinhthucVanban(47L);
                //</editor-fold>
                List<FileAttachTranfer> lstFileAttach = new ArrayList<>();
                try {
                    if (filePath != null && filePath.trim().length() > 0) {
                        // Danh sach file
                        File file = null;
                        String fileName = "";
                        file = new File(fileService.decodePath(filePath));
                        fileName = fileService.getFileNameFromEncodePath(filePath);
                        // Lay file export pdf (attach hoac export tu man hinh bien ban trinh ky)
                        FileAttachTranfer ft = new FileAttachTranfer();
                        ft.setFileName(fileName);
                        ft.setAttachBytes(Files.readAllBytes(file.toPath()));
                        ft.setFileSign(1L);
                        ft.setPath("");
                        lstFileAttach.add(ft);
                        // Add attachment to listFileTransfer
                        lstFileAttach = vofficeService.addAttachments(lstFileAttach, request.attachmentList);
                    } else if (request.signData.get(i).data != null && request.signData.get(i).data.containsKey("attachments")) {
                        List<String> attachments = (List<String>) request.signData.get(i).data.get("attachments");
                        int length = attachments.size();
                        for (int j = 0; j < length; j++) {
                            try {
                                // Danh sach file
                                File file = new File(fileService.decodePath(attachments.get(j)));
                                String filePathAttachments = attachments.get(j);
                                String fileNameRaw = fileService.getFileNameFromEncodePath(filePathAttachments);
                                // Lay file export pdf (attach hoac export tu man hinh bien ban trinh ky)
                                FileAttachTranfer ft = new FileAttachTranfer();
                                ft.setFileName(fileNameRaw);
                                ft.setAttachBytes(Files.readAllBytes(file.toPath()));
                                ft.setPath("");

                                if (fileNameRaw.contains(HSDTConstant.RESIGNATION_LETTER_PREFIX) ||
                                        fileNameRaw.contains(HSDTConstant.DEBT_PREFIX) ||
                                        fileNameRaw.contains(HSDTConstant.AGREEMENT_PREFIX)) {
                                    ft.setFileSign(1L);
                                } else {
                                    ft.setFileSign(2L);
                                }

                                if (fileNameRaw.contains(HSDTConstant.RESIGNATION_LETTER_PREFIX)) {
                                    filePath = filePathAttachments;
                                } else if (fileNameRaw.contains(HSDTConstant.DEBT_PREFIX)) {
                                    debtFileName = filePathAttachments;
                                } else if (fileNameRaw.contains(HSDTConstant.AGREEMENT_PREFIX)) {
                                    agrFileName = filePathAttachments;
                                }

                                lstFileAttach.add(ft);
                                // Add attachment to listFileTransfer
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                log.debug("Đã có lỗi đọc file trong attachment sign ");
                            }
                        }
                        lstFileAttach = vofficeService.addAttachments(lstFileAttach, request.attachmentList);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    log.debug("Đã có lỗi đọc file xảy ra ở ");
                }
                TerminateContractDTO.TerminateContractResponse terminateContract = null;
                param.setLstFileAttach(lstFileAttach);

                List<String> listEmail = new ArrayList<>();

                // Lay danh sach user ky theo dinh nghia cua Voffice
                List<Vof2EntityUser> vofficeUserLstParam = new ArrayList<>();

                Long index = 1L;
                for (Vof2EntityUser entityUser2 : request.userSign) {
                    if (entityUser2.getStrEmail() != null && entityUser2.getStrEmail().trim().length() > 0) {
                        listEmail.add(entityUser2.getStrEmail());
                    }
                    entityUser2.setSignImageIndex(index++);//số thứ tự ký này lấy từ trên giao diện trình ký

                    if (index == request.userSign.size() + 1) {
                        entityUser2.setIsPublicText(1L); //Đơn vị ban hành
                    } else {
                        entityUser2.setIsPublicText(0L);
                    }

                    vofficeUserLstParam.add(entityUser2);

                    listEmail.add(entityUser2.getStrEmail());//add email nguoi ky
                }

                param.setAutoPromulgateText(1);//Tự động ban hành(2)
                param.setEmailPublishGroup(listEmail.get(listEmail.size() - 1));

                // truyen param danh sach user ky
                param.setLstUserVof2(vofficeUserLstParam);
                if (VOfficeSignType.TERMINATE.equals(request.type)) {
                    terminateContract = terminateContractService.findOneById(id);
                    if (terminateContract == null || terminateContract.status != TerminateStatusConstant.managerApproval) {
                        return new BaseResponse
                                .ResponseBuilder<String>()
                                .failed(null, "Bạn không có quyền thực hiện chức năng này");
                    }
                }

                // In ra json object lúc trình ký
                log.info("Json object send to VO");
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String json = ow.writeValueAsString(param);
                log.info(json);

                Long status = service.vo2RegDigitalDocByEmail(param);
                System.out.println("Ket qua goi trinh ky trancode " + transactionCode + " la : " + status);
                if (status == 1) {
                    if (VOfficeSignType.INTERVIEW_SESSION_CV == request.type) {
                        //<editor-fold desc="Cap nhat trang thai trinh ky">
                        InterviewSessionCvDTO.InterviewSessionCvResponse interviewCv = interviewSessionCvService.findOneById(id);
                        interviewCv.transCode = transactionCode;
                        interviewCv.isCallVoffice = true;
                        if (interviewCv.interviewSessionEntity != null) {
                            interviewCv.interviewSessionId = interviewCv.interviewSessionEntity.interviewSessionId;
                        }
                        interviewCv.interviewReportFile = filePath;
                        interviewSessionCvService.updateData(id, interviewCv);
                        //</editor-fold>
                    } else if (VOfficeSignType.COLLABORATOR == request.type
                            || VOfficeSignType.FREELANCE == request.type
                            || VOfficeSignType.LABOR == request.type
                            || VOfficeSignType.PROBATIONARY == request.type
                            || VOfficeSignType.SERVICE == request.type
                    ) {
                        ContractDTO.ContractRequest contractRequest = new ContractDTO.ContractRequest();
                        contractRequest.signedFile = "";
                        contractRequest.isCallVoffice = true;
                        contractRequest.transCode = transactionCode;
                        contractRequest.contractFile = filePath;
                        contractService.updateVofficeCalled(id, contractRequest);
                    } else if (VOfficeSignType.TERMINATE == request.type) {
                        //Cap nhat trang thai
                        terminateContractService.updateStatus(transactionCode, id, TerminateStatusConstant.vofficePending, filePath, debtFileName, agrFileName);
                    } else if (VOfficeSignType.SEV_ALLOWANCE == request.type) {
                        //Cap nhat trang thai
                        terminateContractService.updateStatus(transactionCode, id, TerminateStatusConstant.sevPending, filePath, null, null);
                    } else if (VOfficeSignType.RESIGN_FORM_09 == request.type) {
                        //Cap nhat trang thai
                        resignSessionService.updateResignStatusAndRelated(id, ResignStatus.SENT_BM_TO_VOFFICE1);
                        resignSessionService.updateTranscodeAndRelated(id, transactionCode);
                        resignSessionService.updateBMUnitFile(id, fileService.encodePath(filePath));
                    } else if (VOfficeSignType.RESIGN_FORM_03 == request.type) {
                        //Cap nhat trang thai
                        resignSessionService.updateResignStatusAndRelated(id, ResignStatus.SENT_BM_TO_VOFFICE1);
                        resignSessionService.updateTranscodeAndRelated(id, transactionCode);
                        resignSessionService.updateBMUnitFile(id, fileService.encodePath(filePath));
                    } else if (VOfficeSignType.RESIGN_FORM_09_TCT == request.type) {
                        handleAfterSentResignForm09TCT(i, dataMap, transactionCode, filePath);
                    } else if (VOfficeSignType.RESIGN_FORM_03_TCT == request.type) {
                        handleAfterSentResignForm03TCT(i, dataMap, transactionCode, filePath);
                    } else if (VOfficeSignType.RESIGN_LABOR_FINAL == request.type) {
                        handleResignLaborFinal(id, transactionCode);
                    } else if (VOfficeSignType.BRAND_NEW_CONTRACT == request.type) {
                        //Cap nhat trang thai
                        ContractDTO.ContractNewStatusRequest contractNewStatusRequest = new ContractDTO.ContractNewStatusRequest();
                        contractNewStatusRequest.newContractStatus = NewContractStatus.SENT_TO_VOFFICE;
                        contractNewStatusRequest.contractId = id;
                        contractService.updateNewContractStatus(contractNewStatusRequest);

                        ContractDTO.UpdateTransCodeRequest updateTransCodeRequest = new ContractDTO.UpdateTransCodeRequest();
                        updateTransCodeRequest.contractId = id;
                        updateTransCodeRequest.transCode = transactionCode;
                        contractService.updateTransCode(updateTransCodeRequest);
                    }
                    return new BaseResponse
                            .ResponseBuilder<String>()
                            .success(null, "Trình ký thành công");
                } else {
                    return new BaseResponse
                            .ResponseBuilder<String>()
                            .failed(null, "Có lỗi xảy ra. Mã lỗi " + status);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (e.getMessage().equals("UNAUTHEN")) {
                    System.out.println("Bạn chưa nhập mật khẩu Voffice");
                } else {
                    return new BaseResponse
                            .ResponseBuilder<String>()
                            .failed(null, "Trình ký file thất bại, vui lòng thử lại sau.");
                }
            }
        }

        return new BaseResponse
                .ResponseBuilder<String>()
                .failed(null, "Trình ký file thất bại, vui lòng thử lại sau.");
    }

    private void handleAfterSentResignForm09TCT(int i, Map<String, Object> dataMap, String transactionCode, String filePath) {
        int quarter = (Integer) dataMap.get("quarter");
        int year = (Integer) dataMap.get("year");

        LocalDate startDate = LocalDate.of(year, (quarter - 1) * 3, 1);
        LocalDate endDate = startDate.plusMonths(3).minusDays(1);

        Set<Long> resignSessionIdSet = resignSessionService.getResignIdSetForVO2(startDate, endDate);

        if (i == 0) {
            resignSessionService.updateResignStatusAndRelated(quarter, year, ResignType.LABOR, ResignStatus.RECEIVED_BM_09_VOFFICE_AND_SUCCESS, ResignStatus.HR_TCT_CREATED_BMTCT_FILE, ResignStatus.HR_TCT_SENT_TO_VOFFICE2);
            log.debug("Update Resign Status and related");
            resignSessionService.updateTranscodeAndRelated(quarter, year, ResignType.LABOR, transactionCode);
            log.debug("Update Resign Transcode and related");
        }

        updateAllFilePathOfVo2(i, dataMap, resignSessionIdSet, filePath);

    }

    private void handleAfterSentResignForm03TCT(int i, Map<String, Object> dataMap, String transactionCode, String filePath) {
        String startDateString = (String) dataMap.get("startDate");
        String endDateString = (String) dataMap.get("endDate");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate startDate = LocalDate.parse(startDateString, formatter);
        LocalDate endDate = LocalDate.parse(endDateString, formatter);

        Set<Long> resignSessionIdSet = resignSessionService.getResignIdSetForVO2(startDate, endDate);

        if (i == 0) {
            resignSessionService.updateResignStatusAndRelated(startDate, endDate, ResignType.PROBATIONARY, ResignStatus.HR_TCT_SENT_TO_VOFFICE2);
            log.debug("Update Resign Status and related");
            resignSessionService.updateTranscodeAndRelated(startDate, endDate, ResignType.PROBATIONARY, transactionCode);
            log.debug("Update Resign Transcode and related");
        }

        updateAllFilePathOfVo2(i, dataMap, resignSessionIdSet, filePath);

    }

    private void updateAllFilePathOfVo2(int i, Map<String, Object> dataMap, Set<Long> resignSessionIdSet, String filePath) {
        // Add filePath

        switch (i) {
            case 0: {
                resignSessionRepository.updateBMTCTPdfFilePathByResignIdSet(
                        resignSessionIdSet,
                        fileService.encodePath(filePath));
                break;
            }
            case 1: {
                resignSessionRepository.updateBMTCTDocxFilePathByResignIdSet(
                        resignSessionIdSet,
                        fileService.encodePath(filePath));
                break;
            }
            case 2: {
                resignSessionRepository.updateBmListEncodePathByResignIdSet(
                        resignSessionIdSet,
                        fileService.encodePath(filePath)
                );
            }
            case 3: {
                resignSessionRepository.updateReportPathByResignIdSet(
                        resignSessionIdSet,
                        fileService.encodePath(filePath)
                );
                break;
            }
        }
    }

    private void handleResignLaborFinal(Long contractId, String transactionCode) {
        ContractDTO.UpdateTransCodeRequest updateTransCodeRequest = new ContractDTO.UpdateTransCodeRequest();
        updateTransCodeRequest.transCode = transactionCode;
        updateTransCodeRequest.contractId = contractId;
        contractService.updateTransCode(updateTransCodeRequest);
        log.debug("Done updateTransCode");
        contractService.updateResignStatus(contractId, ResignStatus.HR_SENT_TO_VOFFICE3);
        log.debug("Done updateResignStatus");
    }
}
