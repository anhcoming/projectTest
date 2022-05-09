package com.viettel.hstd.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.viettel.hstd.constant.*;
import com.viettel.hstd.core.custom.CustomLocalDateTimeDeserializer;
import com.viettel.hstd.core.custom.CustomLocalDateTimeSerializer;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;
import com.viettel.hstd.core.utils.GsonDateDeSerializer;
import com.viettel.hstd.core.utils.GsonLocalDateTimeDeserializer;
import com.viettel.hstd.entity.hstd.ContractEntity;
import com.viettel.hstd.entity.hstd.InterviewSessionCvEntity;
import com.viettel.hstd.entity.hstd.ResignSessionContractEntity;
import com.viettel.hstd.entity.hstd.TerminateContractEntity;
import com.viettel.hstd.serializer.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Configuration
public class ConfigurationBean {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDate.class, new CustomLocalDateDeserializer());
        module.addSerializer(LocalDate.class, new CustomLocalDateSerializer());
        module.addDeserializer(LocalDateTime.class, new CustomLocalDateTimeDeserializer());
        module.addSerializer(LocalDateTime.class, new CustomLocalDateTimeSerializer());

        setUpCustomObjectMapperModule(module);

        objectMapper.registerModule(module);
        return objectMapper;
    }

    private void setUpCustomObjectMapperModule(SimpleModule module) {
        module.addSerializer(Gender.class, new GenderSerializer());
        module.addDeserializer(Gender.class, new GenderDeserializer());
        module.addSerializer(Attitude.class, new AttitudeSerializer());
        module.addDeserializer(Attitude.class, new AttitudeDeserializer());
        module.addSerializer(AcceptJobStatus.class, new AcceptJobStatusSerializer());
        module.addDeserializer(AcceptJobStatus.class, new AcceptJobStatusDeserializer());
        module.addSerializer(ContractDuration.class, new ContractDurationSerializer());
        module.addDeserializer(ContractDuration.class, new ContractDurationDeserializer());
        module.addSerializer(ContractType.class, new ContractTypeSerializer());
        module.addDeserializer(ContractType.class, new ContractTypeDeserializer());
        module.addSerializer(ResignStatus.class, new ResignStatusSerializer());
        module.addDeserializer(ResignStatus.class, new ResignStatusDeserializer());
        module.addSerializer(ResignPassStatus.class, new ResignPassStatusSerializer());
        module.addDeserializer(ResignPassStatus.class, new ResignPassStatusDeserializer());
        module.addSerializer(Operation.class, new OperationSerializer());
        module.addDeserializer(Operation.class, new OperationDeserializer());
        module.addSerializer(SearchType.class, new SearchTypeSerializer());
        module.addDeserializer(SearchType.class, new SearchTypeDeserializer());
        module.addSerializer(ResignVofficeStatus.class, new ResignVofficeStatusSerializer());
        module.addDeserializer(ResignVofficeStatus.class, new ResignVofficeStatusDeserializer());
        module.addSerializer(NewContractStatus.class, new NewContractStatusSerializer());
        module.addDeserializer(NewContractStatus.class, new NewContractStatusDeserializer());
        module.addSerializer(ResignType.class, new ResignTypeSerializer());
        module.addDeserializer(ResignType.class, new ResignTypeDeserializer());
        module.addSerializer(OtpType.class, new OtpTypeSerializer());
        module.addDeserializer(OtpType.class, new OtpTypeDeserializer());
        module.addSerializer(VOfficeSignType.class, new VOfficeSignTypeSerializer());
        module.addDeserializer(VOfficeSignType.class, new VOfficeSignTypeDeserializer());
        module.addSerializer(InterviewResult.class, new InterviewResultSerializer());
        module.addDeserializer(InterviewResult.class, new InterviewResultDeserializer());
        module.addSerializer(KpiGrade.class, new KpiGradeSerializer());
        module.addDeserializer(KpiGrade.class, new KpiGradeDeserializer());
        module.addSerializer(InsuranceStatus.class, new InsuranceStatusSerializer());
        module.addDeserializer(InsuranceStatus.class, new InsuranceStatusDeserializer());
        module.addSerializer(AttachmentDocumentStatus.class, new AttachmentDocumentStatusSerializer());
        module.addDeserializer(AttachmentDocumentStatus.class, new AttachmentDocumentStatusDeserializer());
        module.addSerializer(ContractImportCategory.class, new ContractImportCategorySerializer());
        module.addDeserializer(ContractImportCategory.class, new ContractImportCategoryDeserializer());
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<ResignSessionContractEntity, ResignSessionContractEntity>() {
            @Override
            protected void configure() {
                skip(destination.getContractEntity());
                skip(destination.getResignSessionEntity());
            }
        });

        modelMapper.addMappings(new PropertyMap<InterviewSessionCvEntity, InterviewSessionCvEntity>() {
            @Override
            protected void configure() {
                skip(destination.getInterviewSessionEntity());
                skip(destination.getCvEntity());
            }
        });
        return modelMapper;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new GsonDateDeSerializer());
        builder.registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeDeserializer());
        return builder.create();
    }
}
