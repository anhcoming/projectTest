package com.viettel.hstd.service.imp;

import com.viettel.hstd.core.utils.EncryptionData;
import com.viettel.hstd.entity.hstd.EmailConfigEntity;
import com.viettel.hstd.repository.hstd.EmailConfigRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmailConfigServiceTest {

    @Autowired
    private EmailConfigRepository emailConfigRepository;

    @Test
    @DisplayName("Decrypt email")
    void decryptEmail() {

        List<EmailConfigEntity> emailConfigEntityList = emailConfigRepository.findAll();

        emailConfigEntityList.forEach(obj -> {
            System.out.println(obj.getEmail());
            System.out.println(EncryptionData.decrypt(obj.getPassword()));
        });

    }

}