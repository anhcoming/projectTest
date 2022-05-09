package com.viettel.hstd.entity.listener;

import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.entity.hstd.CvEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.repository.hstd.CvRepository;
import com.viettel.hstd.repository.vps.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.mail.Session;
import javax.persistence.*;

@Configurable
public class CVListener {
    static private CvRepository cvRepository;
    static private EntityManager entityManager;

    @Autowired
    public void init(CvRepository cvRepository, EntityManager entityManager) {
        CVListener.cvRepository = cvRepository;
        CVListener.entityManager = entityManager;
    }
//
//    @PostPersist
//    @PostUpdate
//    private void checkDuplicateEmailPhoneNumber(CvEntity cvEntity) {
//        if (cvRepository.existsByEmail(cvEntity.getEmail())) {
//            throw new BadRequestException("Email đã tồn tại");
//        }
//
//        if (cvRepository.existsByPhoneNumber(cvEntity.getPhoneNumber())) {
//            throw new BadRequestException("Số điện thoại đã tồn tại");
//        }
//
//    }
}
