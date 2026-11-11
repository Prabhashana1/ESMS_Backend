package lk.megasupply.esms_backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SalaryStructureDto {
    private Long id;
    private Long userId;
    private String fullName;
    private String username;
    private String designation;
    private String employmentType;
    private BigDecimal basicSalary;
    private BigDecimal dailyRate;
    private BigDecimal hourlyOvertimeRate;
    private boolean epfEtfEligible;
    private int annualCasualLeaveQuota;
    private int annualMedicalLeaveQuota;
    private String bankName;
    private String bankBranch;
    private String accountNumber;
    private LocalDateTime updatedAt;
}