package com.viettel.hstd.entity.hstd;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "CONTRACT")
@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@Where(clause = "DEL_FLAG = 0")
public class ContractEntity extends ContractBaseEntity {
}
