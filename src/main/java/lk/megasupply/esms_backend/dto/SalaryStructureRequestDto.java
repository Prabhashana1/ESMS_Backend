package lk.megasupply.esms_backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SalaryStructureRequestDto {
    private String employmentType; // "PERMANENT" or "DAILY_WAGE"
    private BigDecimal basicSalary;
    private BigDecimal dailyRate;
    private BigDecimal hourlyOvertimeRate;
    private boolean epfEtfEligible;
    private int annualCasualLeaveQuota;
    private int annualMedicalLeaveQuota;
    private String bankName;
    private String bankBranch;
    private String accountNumber;
}