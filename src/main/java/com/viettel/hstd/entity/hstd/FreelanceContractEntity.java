package com.viettel.hstd.entity.hstd;

import lombok.AllArgsConstructor;
        import lombok.Getter;
        import lombok.NoArgsConstructor;
        import lombok.Setter;
        import org.hibernate.annotations.Where;

        import javax.persistence.*;

@Entity
@Table(name = "CONTRACT")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Where(clause = "DEL_FLAG = 0")
public class FreelanceContractEntity extends ContractBaseEntity {
        @Column(name = "TAX_NUMBER")
        private String taxNumber;
}
