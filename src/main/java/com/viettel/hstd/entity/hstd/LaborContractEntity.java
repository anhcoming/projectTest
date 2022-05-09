package com.viettel.hstd.entity.hstd;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.viettel.hstd.constant.ResignStatus;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "CONTRACT")
@Getter
@Setter
@Where(clause = "DEL_FLAG = 0 AND CONTRACT_TYPE = 4")
public class LaborContractEntity extends ContractBaseEntity {

}

