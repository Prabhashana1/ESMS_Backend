package lk.megasupply.esms_backend.entity;

import lk.megasupply.esms_backend.enums.EmploymentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "salary_structures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmploymentType employmentType;

    @Builder.Default
    @Column(precision = 12, scale = 2)
    private BigDecimal basicSalary = BigDecimal.ZERO;

    @Builder.Default
    @Column(precision = 12, scale = 2)
    private BigDecimal dailyRate = BigDecimal.ZERO;

    @Builder.Default
    @Column(precision = 10, scale = 2)
    private BigDecimal hourlyOvertimeRate = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false)
    private boolean epfEtfEligible = true;

    @Builder.Default
    private int annualCasualLeaveQuota = 7;

    @Builder.Default
    private int annualMedicalLeaveQuota = 7;

    @Column(length = 100)
    private String bankName;

    @Column(length = 100)
    private String bankBranch;

    @Column(length = 255)
    private String accountNumber;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}