package org.perpetualnetworks.mdcrawlerconsumer.database.testentity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

//@Entity(name = "ForeignKeyAssoEntity")
@Table(name = "Employee")
public class EmployeeEntity implements Serializable {

    private static final long serialVersionUID = -1798070786993154676L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer employeeId;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "first_name", unique = false, nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", unique = false, nullable = false, length = 100)
    private String lastName;

    @OneToMany(cascade= CascadeType.ALL)
    @JoinColumn(name="employee_id")
    private Set<AccountEntity> accounts;

    //Getters and setters
}
