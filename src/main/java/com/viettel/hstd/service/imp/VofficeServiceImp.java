package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.viettel.hstd.constant.*;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.dto.hstd.*;
import com.viettel.hstd.entity.hstd.InterviewSessionCvEntity;
import com.viettel.hstd.entity.hstd.TerminateContractEntity;
import com.viettel.hstd.entity.hstd.VoSignEntity;
import com.viettel.hstd.entity.vps.VhrFutureOrganizationEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.exception.UnauthorizedAccessException;
import com.viettel.hstd.repository.hstd.*;
import com.viettel.hstd.repository.vps.VhrFutureOrganizationRepository;
import com.viettel.hstd.security.AuthenticationFacade;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.VofficeService;
import com.viettel.hstd.util.*;
import com.viettel.security.PassTranformer;
import com.viettel.voffice.ws_autosign.service.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.xml.ws.BindingProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.service.inf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@Slf4j
public class VofficeServiceImp implements VofficeService {
    @Autowired
    private VOConfig voConfig;
    private final int CONNECTION_TIME_OUT_IN_MS = 2000;
    @Autowired
    private FolderExtension folderExtension;
    @Autowired
    private InterviewSessionCvService interviewSessionCvService;
    @Autowired
    private ContractService contractService;
    @Autowired
    private TerminateContractService terminateContractService;
    @Autowired
    private ObjectMapper objectMapper;
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
    private Message message;
    @Autowired
    private AuthenticationFacade authenticationFacade;
    @Autowired
    private Gson gson;
    @Autowired
    private VoLogService voLogService;

    @Autowired
    private InterviewSessionCvRepository interviewSessionCvRepository;

    @Autowired
    private VhrFutureOrganizationRepository vhrFutureOrganizationRepository;

    @Autowired
    VoSignRepository voSignRepository;

    @Autowired
    TerminateContractRepository terminateContractRepository;

    @Override
    @Transactional
    public boolean sentVoffice(VOfficeSignDTO request) {
        log.info("Start sent custom Voffice");
        SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();

        voLogService.logVOSent(request);

        try {
            Vo2AutoSignSystemImplService sv = new Vo2AutoSignSystemImplService(new URL(voConfig.ca_wsUrl));
            Vo2AutoSignSystemImpl service = sv.getVo2AutoSignSystemImplPort();

            // Set timeout params
            int connectionTimeOutInMs = CONNECTION_TIME_OUT_IN_MS; // Thoi gian timeout 10s
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

            String vofficeUser = "";
            String vofficePass = "";
            if (Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
                log.info("VO using dev account");
                vofficeUser = voConfig.userVoffice;
                vofficePass = PassTranformer.encrypt(voConfig.passVoffice);
            } else {
                log.info("VO using SSO {}", sSoResponse.getEmployeeCode());
                vofficeUser = sSoResponse.getEmployeeCode();
//                log.info("Decrypted drowssap {}",PassTranformer.decrypt(sSoResponse.getEncryptedPw()));
                vofficePass = sSoResponse.getEncryptedPw();
            }

            param.setAccountName(vofficeUser);
            param.setAccountPass(vofficePass);// ma hoa password

            String transactionCode = createTranscode(request);
            param.setTransCode(transactionCode);
            param.setDocTitle(vofficeService.generateDocumentTitle(request, transactionCode));
            param.setRegisterNumber(vofficeService.generateRegisterNumber(request, sSoResponse));
            param.setSender(voConfig.ca_sender); // tên hệ thống trình kí văn bản. -> QLDTKTTS
            param.setHinhthucVanban(voConfig.docTypeId); // Test
            param.setAreaId(voConfig.areaId);

            param.setCreateDate("");
            param.setMoneyTransfer(0L);
            param.setMoneyUnitID(0L);
//            param.setIsCanVanthuXetduyet(false);// Khong can van thu xet duyet
            param.setIsCanVanthuXetduyet(null);
//            param.setHinhthucVanban(7L);

            List<FileAttachTranfer> lstFileAttach = new ArrayList<>();
            switch (request.type) {
                case RESIGN_FORM_09:
                case RESIGN_FORM_03:
                case RESIGN_FORM_09_TCT:
                case RESIGN_FORM_03_TCT:
                    List<String> attachments = request.signData.stream().map(obj -> obj.filePath)
                            .collect(Collectors.toList());
                    int length = attachments.size();
                    for (int i = 0; i < length; i++) {
                        // Danh sach file
                        String fileName = attachments.get(i);
                        File file = new File(fileService.decodePath(attachments.get(i)));
                        // Lay file export pdf (attach hoac export tu man hinh bien ban trinh ky)
                        FileAttachTranfer ft = new FileAttachTranfer();
                        ft.setFileName(fileName.replace(" ", "_"));
                        ft.setAttachBytes(Files.readAllBytes(file.toPath()));
                        ft.setFileSign(1L);
                        ft.setPath("");
                        lstFileAttach.add(ft);
                    }
                    break;
                case SEV_ALLOWANCE_MULTI:
                    String filePath = request.signData.stream().findFirst().
                            orElseThrow(() -> new NotFoundException("Không tìm thấy file để trình ký")).filePath;
                    String filePathReal = fileService.decodePath(filePath);
                    File file = new File(fileService.decodePath(filePathReal));
                    FileAttachTranfer ft = new FileAttachTranfer();
                    ft.setFileName(fileService.getFileNameFromEncodePath(filePath));
                    ft.setAttachBytes(Files.readAllBytes(file.toPath()));
                    ft.setFileSign(1L);
                    ft.setPath("");
                    lstFileAttach.add(ft);
                    break;
            }
            lstFileAttach = vofficeService.addAttachments(lstFileAttach, request.attachmentList);
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

            // Nếu đã dùng id thì không dùng cái này
//            param.setEmailPublishGroup(listEmail.get(listEmail.size() - 1));
            // truyen param danh sach user ky
            param.setLstUserVof2(vofficeUserLstParam);
            // TODO: Uncomment khi xong
            Long status = service.vo2RegDigitalDocByEmail(param);
            VOStatusCode voStatusCode = VOStatusCode.of(Math.toIntExact(status));
//            Long status = 107L;

            // Todo: Delete this log after done
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(param);
            log.info(json);

            log.info("Ket qua goi trinh ky trancode {} la : {}", transactionCode, status);
            if (status == 1) {
                return handleSentResult(request, transactionCode);
            } else {
                throw new BadRequestException("Có lỗi xảy ra. Mã lỗi " + status + " " + voStatusCode.getVietnameseStringValue());
            }
        } catch (Exception ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            log.error(stackTrace);
        }
        return false;
    }

    private String createTranscode(VOfficeSignDTO request) {
        String transactionCode = "";

        LocalDateTime localDateTime = LocalDateTime.now();

        VOfficeSignDataDTO metaData = request.signData.stream().findFirst()
                .orElseThrow(() -> new NotFoundException("Không thấy dữ liệu để trình ký"));

        switch (request.type) {
            case RESIGN_FORM_09: {
                log.info("Create transcode for RESIGN_FORM_09");
                transactionCode = HSDTConstant.BM09DocumentPrefix + metaData.id + "_" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(localDateTime);
                break;
            }
            case RESIGN_FORM_03: {
                log.info("Create transcode for RESIGN_FORM_03");
                transactionCode = HSDTConstant.BM03DocumentPrefix + metaData.id + "_" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(localDateTime);
                break;
            }
            case RESIGN_FORM_09_TCT: {
                log.info("Create transcode for RESIGN_FORM_09_TCT");
                transactionCode = HSDTConstant.BM09TCTDocumentPrefix + metaData.data.get("quarter") + "." + metaData.data.get("year") + "_" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(localDateTime);
                break;
            }
            case RESIGN_FORM_03_TCT: {
                log.info("Create transcode for RESIGN_FORM_03_TCT");
                transactionCode = HSDTConstant.BM03TCTDocumentPrefix + metaData.data.get("startDate") + "." + metaData.data.get("endDate") + "_" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(localDateTime);
                break;
            }

            case SEV_ALLOWANCE_MULTI: {
                log.info("Create transcode for SEV_ALLOWANCE_MULTI");
                transactionCode = HSDTConstant.SevAllowanceMultiPrefix + "_" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(localDateTime);
                break;
            }
            default: {
                transactionCode = "";
                break;
            }
        }

        return transactionCode;
    }

    private boolean handleSentResult(VOfficeSignDTO request, String transactionCode) {
        switch (request.type) {
            case RESIGN_FORM_09:
            case RESIGN_FORM_03: {
                //Cap nhat trang thai
                Long id = request.signData.get(0).id;
                resignSessionService.updateResignStatusAndRelated(id, ResignStatus.SENT_BM_TO_VOFFICE1);
                resignSessionService.updateTranscodeAndRelated(id, transactionCode);
                resignSessionService.updateBMUnitFile(id, fileService.encodePath(request.signData.get(0).filePath));
                break;
            }
            case RESIGN_FORM_09_TCT: {
                List<String> filePathList = request.signData.stream().map(obj -> obj.filePath).collect(Collectors.toList());
                handleAfterSentResignForm09TCT(request.signData.get(0).data, transactionCode, filePathList);
                break;
            }
            case RESIGN_FORM_03_TCT: {
                List<String> filePathList = request.signData.stream().map(obj -> obj.filePath).collect(Collectors.toList());
                handleAfterSentResignForm03TCT(request.signData.get(0).data, transactionCode, filePathList);
                break;
            }
            case RESIGN_LABOR_FINAL: {
                Long id = request.signData.get(0).id;
                handleResignLaborFinal(id, transactionCode);
                break;
            }
            case SEV_ALLOWANCE_MULTI: {
                VOfficeSignDataDTO vOfficeSignDataDTO = request.signData.stream().findFirst().orElse(null);
                if (vOfficeSignDataDTO != null) {
                    handleSevAllowance(vOfficeSignDataDTO, transactionCode);
                }
                break;
            }
        }

        return true;
    }

    @Override
    public void receiveVoffice() {

    }

    private void handleAfterSentResignForm09TCT(Map<String, Object> dataMap, String transactionCode, List<String> filePathList) {
        int quarter = (Integer) dataMap.get("quarter");
        int year = (Integer) dataMap.get("year");

        LocalDate startDate = LocalDate.of(year, (quarter - 1) * 3, 1);
        LocalDate endDate = startDate.plusMonths(3).minusDays(1);

        Set<Long> resignSessionIdSet = resignSessionService.getResignIdSetForVO2(startDate, endDate);

        resignSessionService.updateResignStatusAndRelated(quarter, year, ResignType.LABOR, ResignStatus.RECEIVED_BM_09_VOFFICE_AND_SUCCESS, ResignStatus.HR_TCT_CREATED_BMTCT_FILE, ResignStatus.HR_TCT_SENT_TO_VOFFICE2);
        log.debug("Update Resign Status and related");
        resignSessionService.updateTranscodeAndRelated(quarter, year, ResignType.LABOR, transactionCode);
        log.debug("Update Resign Transcode and related");


        updateAllFilePathOfVo2(resignSessionIdSet, filePathList);

    }

    private void handleAfterSentResignForm03TCT(Map<String, Object> dataMap, String transactionCode, List<String> filePathList) {
        String startDateString = (String) dataMap.get("startDate");
        String endDateString = (String) dataMap.get("endDate");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate startDate = LocalDate.parse(startDateString, formatter);
        LocalDate endDate = LocalDate.parse(endDateString, formatter);

        Set<Long> resignSessionIdSet = resignSessionService.getResignIdSetForVO2(startDate, endDate);


        resignSessionService.updateResignStatusAndRelated(startDate, endDate, ResignType.PROBATIONARY, ResignStatus.HR_TCT_SENT_TO_VOFFICE2);
        log.debug("Update Resign Status and related");
        resignSessionService.updateTranscodeAndRelated(startDate, endDate, ResignType.PROBATIONARY, transactionCode);
        log.debug("Update Resign Transcode and related");


        updateAllFilePathOfVo2(resignSessionIdSet, filePathList);

    }

    private void updateAllFilePathOfVo2(Set<Long> resignSessionIdSet, List<String> filePathList) {
        // Add filePath
        for (int i = 0; i < filePathList.size(); i++) {
            switch (i) {
                case 0: {
                    resignSessionRepository.updateBMTCTPdfFilePathByResignIdSet(
                            resignSessionIdSet,
                            fileService.encodePath(filePathList.get(i)));
                    break;
                }
                case 1: {
                    resignSessionRepository.updateBMTCTDocxFilePathByResignIdSet(
                            resignSessionIdSet,
                            fileService.encodePath(filePathList.get(i)));
                    break;
                }
                case 2: {
                    resignSessionRepository.updateBmListEncodePathByResignIdSet(
                            resignSessionIdSet,
                            fileService.encodePath(filePathList.get(i))
                    );
                }
                case 3: {
                    resignSessionRepository.updateReportPathByResignIdSet(
                            resignSessionIdSet,
                            fileService.encodePath(filePathList.get(i))
                    );
                    break;
                }
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

    /* Input:
    - request.type

     */
    @Override
    public String generateDocumentTitle(VOfficeSignDTO request, String defaultName) {
        LocalDate localDate = LocalDate.now();
        if (request.type.equals(VOfficeSignType.INTERVIEW_SESSION_CV)) {
            return "Báo cáo tuyển dụng " + DateTimeFormatter.ofPattern("MM/yyyy").format(localDate);
        } else if (request.type.equals(VOfficeSignType.BRAND_NEW_CONTRACT)) {
            return "Hợp đồng thử việc ";
        } else if (request.type.equals(VOfficeSignType.RESIGN_FORM_03)) {
            return "Chuyển diện Hợp đồng thử việc ";
        } else if (request.type.equals(VOfficeSignType.RESIGN_FORM_09)) {
            return "Tái ký hợp đồng lao động ";
        } else if (request.type.equals(VOfficeSignType.RESIGN_FORM_03_TCT)) {
            return "Chuyển diện Hợp đồng thử việc Tổng công ty ";
        } else if (request.type.equals(VOfficeSignType.RESIGN_FORM_09_TCT)) {
            return "Tái ký Hợp đồng lao động Tổng công ty ";
        } else if (request.type.equals(VOfficeSignType.RESIGN_LABOR_FINAL)) {
            return "Hợp đồng lao động ";
        } else if (request.type.equals(VOfficeSignType.TERMINATE)) {
            return "Yêu cầu chấm dứt hợp đồng";
        } else if (request.type.equals(VOfficeSignType.SEV_ALLOWANCE) || request.type.equals(VOfficeSignType.SEV_ALLOWANCE_MULTI)) {
            return "Quyết định chấm dứt hợp đồng lao động";
        }
        return defaultName;
    }

    @Override
    public String generateRegisterNumber(VOfficeSignDTO request, SSoResponse sSoResponse) {
        VhrFutureOrganizationEntity vhrFutureOrganizationEntity = vhrFutureOrganizationRepository.findByOrganizationId(sSoResponse.getOrganizationId());
        String unitCode = vhrFutureOrganizationEntity.getCode();
        return "HD/" + unitCode + '-' + DateUtils.getNowTime("yyyyMMddHHmmss");
    }

    @Override
    public List<FileAttachTranfer> addAttachments(List<FileAttachTranfer> listFileTranfer, List<String> fileAttachmentPaths) {
        if (listFileTranfer == null) {
            listFileTranfer = new ArrayList<>();
        }
        if (fileAttachmentPaths == null || fileAttachmentPaths.isEmpty()) {
            return listFileTranfer;
        }

        for (String filePath : fileAttachmentPaths) {
            try {
                FileAttachTranfer fileAttachTranfer = new FileAttachTranfer();
                // Khôi phục fileName từ filePath
                String fileAttachmentName = fileService.getFileNameFromEncodePath(filePath);
                fileAttachTranfer.setFileName(fileAttachmentName);
                //Lấy file
                File fileAttach = new File(fileService.decodePath(filePath));
                // Path mặc định
                fileAttachTranfer.setPath("");
                if (fileAttach.exists()) {
                    fileAttachTranfer.setAttachBytes(Files.readAllBytes(fileAttach.toPath()));
                    fileAttachTranfer.setFileSign(2L);
                    listFileTranfer.add(fileAttachTranfer);
                } else {
                    throw new NotFoundException("Không tìm được file đính kèm: " + filePath);
                }
                // Ghi byte file vào fileTranfer
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new BadRequestException("Lỗi xảy ra khi đọc file từ server: " + filePath);
            }
        }

        return listFileTranfer;
    }

    @Override
    public String writeFile(String fileNameRaw, byte[] data) {
        // File name raw chưa có prefix
        if (fileNameRaw == null) {
            throw new NotFoundException("Không tìm thấy tên file");
        }
        String filePathReal = fileService.createFilePathReal(fileNameRaw);
        String filePath = fileService.encodePath(filePathReal);
        Path path = Paths.get(filePathReal);
        try {
            Files.write(path, data);
            return filePath;
        } catch (IOException e) {
            log.error("Không thể ghi được file: " + fileNameRaw);
        }
        return null;
    }

    @Transactional
    public void handleSevAllowance(VOfficeSignDataDTO vOfficeSignDataDTO, String transCode) {
        Map<String, Object> data = vOfficeSignDataDTO.data;
        Object attachments = data.get("attachments");
        if (attachments == null) {
            throw new NotFoundException("Không có đính kèm các bản ghi chấm dứt hợp đồng");
        }
        List<Integer> ids = (List<Integer>) attachments;
        List<Long> terminateContractIds = ids.stream().map(Integer::longValue).collect(Collectors.toList());
        List<TerminateContractEntity> terminateContractEntities = terminateContractRepository.
                findByTerminateContractIdInAndHasSevAllowance(terminateContractIds, TerminateStatusConstant.vofficeApproval);
        if (terminateContractEntities.isEmpty()) {
            throw new NotFoundException("Không tìm thấy bản ghi chấm dứt hợp đồng");
        }
        List<VoSignEntity> voSignEntities = new ArrayList<>();
        for (TerminateContractEntity terminateContract : terminateContractEntities) {
            VoSignEntity entity = new VoSignEntity();
            entity.setTypeSign(VOfficeSignType.SEV_ALLOWANCE_MULTI);
            entity.setIdData(terminateContract.getTerminateContractId());
            entity.setTransCode(transCode);
            voSignEntities.add(entity);
            terminateContract.setTransCodeSevAllowance(transCode);
            terminateContract.setMergeSevAllowance(true);
            terminateContract.setStatus(TerminateStatusConstant.sevPending);
            terminateContract.setSevAllowanceMultiPath(vOfficeSignDataDTO.filePath);
        }

        voSignRepository.saveAll(voSignEntities);
        terminateContractRepository.saveAll(terminateContractEntities);
    }
}
