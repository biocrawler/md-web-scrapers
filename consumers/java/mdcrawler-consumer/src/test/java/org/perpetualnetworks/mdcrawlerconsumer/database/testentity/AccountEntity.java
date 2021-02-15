package org.perpetualnetworks.mdcrawlerconsumer.database.testentity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

//@Entity(name = "ForeignKeyAssoAccountEntity")
@Table(name = "Account")
public class AccountEntity implements Serializable {

    private static final long serialVersionUID = -6790693372846798580L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer accountId;

    @Column(name = "acc_number", unique = true, nullable = false, length = 100)
    private String accountNumber;

    @ManyToOne
    private EmployeeEntity employee;

}
