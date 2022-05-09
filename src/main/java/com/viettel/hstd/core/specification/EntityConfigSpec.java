package com.viettel.hstd.core.specification;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.viettel.hstd.constant.SearchType;
import com.viettel.hstd.constant.SearchType.*;
import com.viettel.hstd.core.constant.ConstantConfig;
import com.viettel.hstd.core.constant.FormatConstant;
import com.viettel.hstd.core.dto.SearchDTO.SearchCriteria;
import com.viettel.hstd.entity.hstd.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.viettel.hstd.constant.Operation.*;

import static com.viettel.hstd.constant.SearchType.*;

@AllArgsConstructor
@NoArgsConstructor
public class EntityConfigSpec<E> implements Specification<E> {
    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate(Root<E> root,
                                 CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder builder) {

        if (criteria == null) return builder.equal(builder.literal(1), 1);

        if (criteria.getField().contains("interviewSessionCvEntity.cvEntity.")) {
            String fullField = criteria.getField();
            String childField = fullField.substring(fullField.lastIndexOf(".") + 1);
            Join<E, CvEntity> join = root
                    .join("interviewSessionCvEntity", JoinType.LEFT)
                    .join("cvEntity", JoinType.INNER);
            return getPredicateAdvance(root, builder, join.get(childField));
        }

        if (criteria.getField().contains("resignSessionContractEntity.resignSessionEntity.")) {
            String fullField = criteria.getField();
            String childField = fullField.substring(fullField.lastIndexOf(".") + 1);
            Join<E, ResignSessionEntity> join = root
                    .join("resignSessionContractEntities", JoinType.LEFT)
                    .join("resignSessionEntity", JoinType.LEFT);
            criteriaQuery.distinct(true);
            return getPredicateAdvance(root, builder, join.get(childField));
        }

        if (criteria.getField().contains("lstEmployee.")) {
            String fullField = criteria.getField();
            String childField = fullField.substring(fullField.lastIndexOf(".") + 1);
            Join<E, EmployeeInterviewSessionEntity> join = root.join("employeeInterviewSessionEntities", JoinType.INNER);
//            Long value = criteria.getValue() == null ? null : Long.parseLong(criteria.getValue().toString());
            criteriaQuery.distinct(true);
            return getPredicateAdvance(root, builder, join.get(childField));
        }

        if (criteria.getField().contains("interviewSessionEntity.")) {
            String fullField = criteria.getField();
            String childField = fullField.substring(fullField.lastIndexOf(".") + 1);
            Join<E, InterviewSessionEntity> join = root.join("interviewSessionEntity", JoinType.INNER);
//            criteriaQuery.distinct(true);
//            return builder.equal(join.get(childField), value);
            return getPredicateAdvance(root, builder, join.get(childField));
        }

        if (criteria.getField().contains("resignSessionEntity.")) {
            String fullField = criteria.getField();
            String childField = fullField.substring(fullField.lastIndexOf(".") + 1);
            Join<E, ResignSessionEntity> join = root.join("resignSessionEntity", JoinType.INNER);
            return getPredicateAdvance(root, builder, join.get(childField));
        }

        if (criteria.getField().contains("contractEntity.")) {
            String fullField = criteria.getField();
            String childField = fullField.substring(fullField.lastIndexOf(".") + 1);
            Join<E, ContractEntity> join = root.join("contractEntity", JoinType.INNER);
            return getPredicateAdvance(root, builder, join.get(childField));
        }

        if (criteria.getField().contains("cvEntity.")) {
            String fullField = criteria.getField();
            String childField = fullField.substring(fullField.lastIndexOf(".") + 1);
            Join<E, CvEntity> join = root.join("cvEntity", JoinType.INNER);
//            Object value = handleValue();
//            criteriaQuery.distinct(true);
            return getPredicateAdvance(root, builder, join.get(childField));
        }

        if (criteria.getField().contains("interviewSessionCvEntities.")) {
            String fullField = criteria.getField();
            String childField = fullField.substring(fullField.lastIndexOf(".") + 1);
            Join<E, InterviewSessionCvEntity> join = root.join("interviewSessionCvEntities", JoinType.LEFT);
            criteriaQuery.distinct(true);
            return getPredicateAdvance(root, builder, join.get(childField));
        }

        if (criteria.getField().contains("resignSessionContractEntity.")) {
            String fullField = criteria.getField();
            String childField = fullField.substring(fullField.lastIndexOf(".") + 1);
            Join<E, ResignSessionContractEntity> join = root.join("resignSessionContractEntities", JoinType.LEFT);
            criteriaQuery.distinct(true);
            return getPredicateAdvance(root, builder, join.get(childField));
        }



        Expression path = handleField(root, builder);
        return getPredicateAdvance(root, builder, path);
    }

    private Expression handleField(Root<E> root, CriteriaBuilder builder) {
        if (criteria.getType() == STRING) {
            return builder.upper(root.get(criteria.getField()).as(String.class));
        } else {
            return root.get(criteria.getField());
        }
    }

    private Object handleValue() {
        switch(criteria.getType()) {
            case NUMBER: {
                return criteria.getValue() == null ? null : Long.parseLong(criteria.getValue().toString());
            }
            case DATETIME: {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FormatConstant.LOCAL_DATE_TIME_FORMAT);
                return LocalDateTime.parse(criteria.getValue().toString(), formatter);
            }
            case DATE: {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FormatConstant.LOCAL_DATE_FORMAT);
                return LocalDate.parse(criteria.getValue().toString(), formatter);
            }
            case FLOAT: {
                return criteria.getValue() == null ? null : Float.parseFloat(criteria.getValue().toString());
            }
            case BOOLEAN: {
                return criteria.getValue() == null ? null : Boolean.valueOf(criteria.getValue().toString()) ;
            }
            case HASH_MAP: {
                Gson gson = new Gson();
                return criteria.getValue() == null ? null : gson.fromJson(criteria.getValue().toString(), new TypeToken<Set>(){}.getType()) ;
            }
            default: {
                return criteria.getValue() == null ? null : criteria.getValue().toString().toUpperCase();
            }
        }
    }

    private Predicate getPredicateAdvance(Root<E> root, CriteriaBuilder builder, Expression path) {
        SearchType type = criteria.getType();
        Expression truePath = path == null ? handleField(root, builder) : path;
        Object trueValue = handleValue();
        if (type == DATETIME) {
            return handleOperation(builder, truePath, trueValue, LocalDateTime.class);
        } else if (type == NUMBER) {
            return handleOperation(builder, truePath, trueValue, Long.class);
        } else if (type == DATE) {
            return handleOperation(builder, truePath, trueValue, LocalDate.class);
        } else if (type == BOOLEAN) {
            return handleOperation(builder, truePath, trueValue, Boolean.class);
        }  else {
            return handleOperation(builder, truePath, trueValue, String.class);
        }
    }


    private <Y extends Comparable<? super Y>> Predicate handleOperation(CriteriaBuilder builder, Expression path, Object value, Class<Y> Y) {
        switch (criteria.getOperation()) {
            case GREATER:
                return builder.greaterThan(path, (Y) value);
            case LESS:
                return builder.lessThan(path, (Y)  value);
            case GREATER_EQUAL: {
                return builder.greaterThanOrEqualTo(path, (Y) value);
            }
            case LESS_EQUAL: {
                return builder.lessThanOrEqualTo(path, (Y) value);
            }
            case EQUAL: {
                if (criteria.getValue() != null) {
                    return builder.equal(path, value);
                } else {
                    return builder.isNull(path);
                }
            }
            case IN: {
                return path.in((Set) value);
            }
            case LIKE: {
                Expression<String> stringPath = builder.upper(path.as(String.class));
                if (criteria.getValue() != null) {
                    return builder.like(
                        stringPath, "%" + criteria.getValue().toString().toUpperCase() + "%");
                } else {
                    return builder.isNull(path);
                }
            }
            case NOT_LIKE: {
                Expression<String> stringPath = builder.upper(path.as(String.class));
                if (criteria.getValue() != null) {
                    return builder.notLike(
                        stringPath, "%" + criteria.getValue().toString().toUpperCase() + "%");
                } else {
                    return builder.isNull(path);
                }
            }
            case NOT: {
                if (criteria.getValue() != null) {
                    return builder.notEqual(path, value);
                } else {
                    return builder.isNotNull(path);
                }
            }
        }
        return null;
    }

}
