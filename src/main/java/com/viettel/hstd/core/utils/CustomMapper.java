package com.viettel.hstd.core.utils;

import com.viettel.hstd.constant.*;
import com.viettel.hstd.dto.hstd.EmployeeMonthlyReviewDTO.*;
import com.viettel.hstd.dto.hstd.LaborContractDTO;
import com.viettel.hstd.dto.hstd.ProbationaryContractDTO;
import com.viettel.hstd.dto.hstd.ResignSessionContractDTO;
import com.viettel.hstd.entity.hstd.CvEntity;
import com.viettel.hstd.entity.hstd.EmployeeMonthlyReviewEntity;
import com.viettel.hstd.entity.hstd.ProbationaryContractEntity;
import lombok.extern.slf4j.Slf4j;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

@Slf4j
public class CustomMapper {

    public static Map<String, String> convert(Object obj) {
        Map<String, String> map = new HashMap<>();
        for (Field field : getAllDeclaredField(obj)) {
            field.setAccessible(true);
            try {
                if (field.get(obj) != null) {
                    map.put(field.getName(), field.get(obj).toString());
                    if (field.get(obj) instanceof Date) {
                        Date temp = (Date) field.get(obj);
                        SimpleDateFormat formatter = new SimpleDateFormat("dd");
                        map.put(field.getName() + "_dd", formatter.format(temp));

                        formatter = new SimpleDateFormat("MM");
                        map.put(field.getName() + "_mm", formatter.format(temp));

                        formatter = new SimpleDateFormat("yyyy");
                        map.put(field.getName() + "_yyyy", formatter.format(temp));

                        formatter = new SimpleDateFormat("dd/MM/yyyy");
                        map.put(field.getName() + "_ddmmyyyy", formatter.format(temp));
                    } else if (field.get(obj) instanceof LocalDate) {
                        LocalDate temp = (LocalDate) field.get(obj);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");

                        map.put(field.getName() + "_dd", formatter.format(temp));

                        formatter = DateTimeFormatter.ofPattern("MM");
                        map.put(field.getName() + "_mm", formatter.format(temp));

                        formatter = DateTimeFormatter.ofPattern("yyyy");
                        map.put(field.getName() + "_yyyy", formatter.format(temp));

                        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        map.put(field.getName() + "_ddmmyyyy", formatter.format(temp));
                    } else if (field.get(obj) instanceof LocalDateTime) {
                        LocalDateTime temp = (LocalDateTime) field.get(obj);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");
                        map.put(field.getName() + "_dd", formatter.format(temp));

                        formatter = DateTimeFormatter.ofPattern("MM");
                        map.put(field.getName() + "_mm", formatter.format(temp));

                        formatter = DateTimeFormatter.ofPattern("yyyy");
                        map.put(field.getName() + "_yyyy", formatter.format(temp));

                        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        map.put(field.getName() + "_ddmmyyyy", formatter.format(temp));

                        formatter = DateTimeFormatter.ofPattern("HH");
                        map.put(field.getName() + "_hh", formatter.format(temp));

                        formatter = DateTimeFormatter.ofPattern("mm");
                        map.put(field.getName() + "_mm", formatter.format(temp));
                    } else if (field.get(obj) instanceof Attitude) {
                        Attitude attitude = (Attitude) field.get(obj);
                        map.put(field.getName() + "_string", attitude.getVietnameseStringValue());
                    } else if (field.get(obj) instanceof ContractDuration) {
                        ContractDuration contractDuration = (ContractDuration) field.get(obj);
                        map.put(field.getName() + "_string", contractDuration.getVietnameseStringValue());
                        map.put(field.getName() + "_int", contractDuration.getValue() + "");
                    } else if (field.get(obj) instanceof ContractType) {
                        ContractType contractType = (ContractType) field.get(obj);
                        map.put(field.getName() + "_string", contractType.getVietnameseStringValue());
                    } else if (field.get(obj) instanceof ResignPassStatus) {
                        ResignPassStatus resignPassStatus = (ResignPassStatus) field.get(obj);
                        map.put(field.getName() + "_fmt", resignPassStatus.getVietnameseStringValue());
                    } else if (field.get(obj) instanceof ResignStatus) {
                        ResignStatus resignStatus = (ResignStatus) field.get(obj);
                        map.put(field.getName() + "_fmt", resignStatus.getVietnameseStringValue());
                    } else if (field.get(obj) instanceof InterviewResult) {
                        InterviewResult interviewResult = (InterviewResult) field.get(obj);
                        map.put(field.getName() + "", interviewResult.getVietnameseStringValue());
                    } else if (field.get(obj) instanceof Float || field.get(obj) instanceof Integer
                            || field.get(obj) instanceof Long || field.get(obj) instanceof Double) {
                        Locale localeVN = new Locale("vi", "VN");
                        NumberFormat vn = NumberFormat.getInstance(localeVN);
                        map.put(field.getName() + "_fmt", vn.format(field.get(obj)));
                    } else if (field.get(obj) instanceof Gender) {
                        Gender gender = (Gender) field.get(obj);
                        map.put(field.getName(), gender.getVietnameseStringValue());
                        map.put("gender_string", gender.getVietnameseStringValue());
                    } else if (field.get(obj) instanceof List) {
                        List objectList = (List) field.get(obj);
                        if (objectList.size() > 0) {
                            if (objectList.get(0) instanceof EmployeeMonthlyReviewResponse) {
                                for (int i = 0; i < 12 - objectList.size(); i++) {
                                    EmployeeMonthlyReviewResponse employeeMonthlyReviewResponse = (EmployeeMonthlyReviewResponse) objectList.get(i);
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
                                    map.put("reviewGrade" + (i + 1), KpiGrade.UNKNOWN.getValue());
                                    map.put("reviewMonth" + (i + 1), "Tháng " + formatter.format(employeeMonthlyReviewResponse.month));
                                }
                                for (int i = 0; i < objectList.size(); i++) {
                                    EmployeeMonthlyReviewResponse employeeMonthlyReviewResponse = (EmployeeMonthlyReviewResponse) objectList.get(i);
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
                                    EmployeeMonthlyReviewResponse object = (EmployeeMonthlyReviewResponse) objectList.get(i);
                                    map.put("reviewGrade" + (i + (13 - objectList.size())), object.grade.getValue());
                                    map.put("reviewMonth" + (i + (13 - objectList.size())), "Tháng " + formatter.format(employeeMonthlyReviewResponse.month));
                                }
                            }
                        }
                    }
                } else {
                    map.put(field.getName(), "");
                }
            } catch (Exception e) {

            }
        }

        if (!map.containsKey("currentDate")) {
            Date currentDate = new Date();

            SimpleDateFormat formatter = new SimpleDateFormat("dd");
            map.put("currentDate_dd", formatter.format(currentDate));
            map.put("dd", formatter.format(currentDate));

            formatter = new SimpleDateFormat("MM");
            map.put("currentDate_mm", formatter.format(currentDate));
            map.put("mm", formatter.format(currentDate));

            formatter = new SimpleDateFormat("yyyy");
            map.put("currentDate_yyyy", formatter.format(currentDate));
            map.put("yyyy", formatter.format(currentDate));

            formatter = new SimpleDateFormat("dd/MM/yyyy");
            map.put("currentDate_ddmmyyyy", formatter.format(currentDate));

            formatter = new SimpleDateFormat("HH");
            map.put("currentDate_hh", formatter.format(currentDate));

        }
        return map;
    }

    private static Field[] getAllDeclaredField(Object obj) {

        List<Field> fieldList = new ArrayList<>(Arrays.asList(obj.getClass().getDeclaredFields()));

        if (obj.getClass() == ProbationaryContractDTO.ProbationaryContractResponse.class ||
                obj.getClass() == ProbationaryContractEntity.class ||
                obj.getClass() == LaborContractDTO.LaborContractResponse.class ||
                obj.getClass() == ResignSessionContractDTO.ExportBM09Response.class ||
                obj.getClass() == CvEntity.class ||
                obj.getClass() == ResignSessionContractDTO.ExportBM03Response.class) {
            fieldList.addAll(Arrays.asList(obj.getClass().getSuperclass().getDeclaredFields()));
        }

        Field[] array = new Field[fieldList.size()];
        return fieldList.toArray(array);
    }

}
