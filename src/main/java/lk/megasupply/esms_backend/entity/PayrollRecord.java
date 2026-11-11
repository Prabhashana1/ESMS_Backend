package lk.megasupply.esms_backend.entity;

import lk.megasupply.esms_backend.enums.PayrollStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payroll_records",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "payroll_year", "payroll_month"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "payroll_year", nullable = false)
    private int payrollYear;

    @Column(name = "payroll_month", nullable = false)
    private int payrollMonth;


    private int totalWorkingDays;
    private Double presentDays;
    private Double paidLeaveDays;
    private Double noPayDays;
    private Double totalOvertimeHours;


    @Builder.Default
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal basicOrDailyEarnings = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal overtimePay = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal repairAllowance = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal otherAllowances = BigDecimal.ZERO;


    @Builder.Default
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal grossSalary = BigDecimal.ZERO;


    @Builder.Default
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal noPayDeduction = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal epfLiableSalary = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal epfEmployee8 = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal salaryAdvanceDeduction = BigDecimal.ZERO;


    @Builder.Default
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal epfEmployer12 = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal etfEmployer3 = BigDecimal.ZERO;


    @Builder.Default
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal netSalary = BigDecimal.ZERO;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayrollStatus status = PayrollStatus.DRAFT;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime generatedAt;
}