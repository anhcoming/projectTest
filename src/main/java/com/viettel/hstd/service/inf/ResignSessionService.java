package com.viettel.hstd.service.inf;

import com.viettel.hstd.constant.ResignStatus;
import com.viettel.hstd.constant.ResignType;
import com.viettel.hstd.constant.ResignVofficeStatus;
import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.ResignSessionContractDTO;
import com.viettel.hstd.dto.hstd.ResignSessionDTO.*;
import com.viettel.hstd.entity.hstd.ResignSessionContractEntity;
import com.viettel.hstd.entity.hstd.ResignSessionEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ResignSessionService extends CRUDService<ResignSessionRequest, ResignSessionResponse, Long> {
    boolean updateResignVofficeStatus(Long resignStatusId, ResignVofficeStatus resignVofficeStatus);

    void updateBMUnitFile(Long resignStatusId, String bmUnitEncodePath);

    boolean updateResignStatus(Long resignSessionId, ResignStatus resignStatus);

    boolean addContractToVoffice2(ResignSessionContractDTO.ResignContractAddToVofficeLaborRequest request);
    boolean addContractToVoffice2(ResignSessionContractDTO.ResignContractAddToVofficeProbationaryRequest request);

    /**
     Update resignStatus for ResignSession, ResignContract and Contract
     */
    void updateResignStatusAndRelated(Long resignSessionId, ResignStatus resignStatus);
    void updateResignStatusAndRelated(Set<Long> resignSessionIdSet, ResignStatus resignStatus);
    void updateResignStatusAndRelated(int quarter, int year, ResignType resignType, ResignStatus startResignStatus, ResignStatus endResignStatus, ResignStatus resignStatus);
    void updateResignStatusAndRelated(LocalDate startDate, LocalDate endDate, ResignType resignType, ResignStatus resignStatus);

    void updateTranscodeAndRelated(Long resignSessionId, String transcode);
    void updateTranscodeAndRelated(Set<Long> resignSessionIdSet, String transcode);
    void updateTranscodeAndRelated(int quarter, int year, ResignType resignType, String transcode);
    void updateTranscodeAndRelated(LocalDate startDate, LocalDate endDate, ResignType resignType, String transcode);

    void createTempContractFile(List<ResignSessionEntity> resignEntityList);

    Set<Long> getResignIdSetForVO2(LocalDate startDate, LocalDate endDate);

    FileDTO.FileResponse exportBm07(Long resignId);
    FileDTO.FileResponse exportBm08(Long resignId);
}
