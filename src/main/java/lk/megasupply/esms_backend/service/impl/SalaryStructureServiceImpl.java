package lk.megasupply.esms_backend.service.impl;

import lk.megasupply.esms_backend.dto.SalaryStructureDto;
import lk.megasupply.esms_backend.dto.SalaryStructureRequestDto;
import lk.megasupply.esms_backend.entity.SalaryStructure;
import lk.megasupply.esms_backend.entity.User;
import lk.megasupply.esms_backend.enums.EmploymentType;
import lk.megasupply.esms_backend.exception.BadRequestException;
import lk.megasupply.esms_backend.exception.ResourceNotFoundException;
import lk.megasupply.esms_backend.repository.SalaryStructureRepository;
import lk.megasupply.esms_backend.repository.UserRepository;
import lk.megasupply.esms_backend.service.SalaryStructureService;
import lk.megasupply.esms_backend.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryStructureServiceImpl implements SalaryStructureService {

    private final SalaryStructureRepository salaryStructureRepository;
    private final UserRepository userRepository;
    private final EncryptionUtil encryptionUtil;

    @Override
    @Transactional
    public SalaryStructureDto saveOrUpdate(Long userId, SalaryStructureRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // 1. Employment Type Validation
        EmploymentType type;
        try {
            type = EmploymentType.valueOf(dto.getEmploymentType().toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Invalid employment type. Use PERMANENT or DAILY_WAGE.");
        }

        BigDecimal basic = dto.getBasicSalary() != null ? dto.getBasicSalary() : BigDecimal.ZERO;
        BigDecimal daily = dto.getDailyRate() != null ? dto.getDailyRate() : BigDecimal.ZERO;
        BigDecimal otRate = dto.getHourlyOvertimeRate() != null ? dto.getHourlyOvertimeRate() : BigDecimal.ZERO;

        // 2. Negative Values Check
        if (basic.compareTo(BigDecimal.ZERO) < 0 || daily.compareTo(BigDecimal.ZERO) < 0 || otRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Salary and overtime rates cannot be negative.");
        }
        if (dto.getAnnualCasualLeaveQuota() < 0 || dto.getAnnualMedicalLeaveQuota() < 0) {
            throw new BadRequestException("Leave quotas cannot be negative.");
        }

        // 3. Conditional Validation (PERMANENT vs DAILY_WAGE)
        if (type == EmploymentType.PERMANENT) {
            if (basic.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Basic salary must be greater than 0 for Permanent employees.");
            }
            daily = BigDecimal.ZERO;
        } else if (type == EmploymentType.DAILY_WAGE) {
            if (daily.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Daily rate must be greater than 0 for Daily Wage employees.");
            }
            basic = BigDecimal.ZERO;
        }

        SalaryStructure structure = salaryStructureRepository.findByUserId(userId)
                .orElse(new SalaryStructure());

        structure.setUser(user);
        structure.setEmploymentType(type);
        structure.setBasicSalary(basic);
        structure.setDailyRate(daily);
        structure.setHourlyOvertimeRate(otRate);
        structure.setEpfEtfEligible(dto.isEpfEtfEligible());
        structure.setAnnualCasualLeaveQuota(dto.getAnnualCasualLeaveQuota());
        structure.setAnnualMedicalLeaveQuota(dto.getAnnualMedicalLeaveQuota());
        structure.setBankName(dto.getBankName());
        structure.setBankBranch(dto.getBankBranch());

        if (dto.getAccountNumber() != null && !dto.getAccountNumber().trim().isEmpty()) {
            structure.setAccountNumber(encryptionUtil.encrypt(dto.getAccountNumber().trim()));
        } else {
            structure.setAccountNumber(null);
        }

        SalaryStructure saved = salaryStructureRepository.save(structure);
        return mapToDto(saved, false);
    }

    @Override
    @Transactional(readOnly = true)
    public SalaryStructureDto getByUserId(Long userId) {
        SalaryStructure structure = salaryStructureRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Salary structure not found for user id: " + userId));
        return mapToDto(structure, false);
    }

    @Override
    @Transactional(readOnly = true)
    public SalaryStructureDto getMySalaryStructure(String username) {
        SalaryStructure structure = salaryStructureRepository.findByUserUsername(username).orElse(null);
        if (structure == null) {
            return null;
        }
        return mapToDto(structure, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalaryStructureDto> getAll() {
        List<SalaryStructure> list = salaryStructureRepository.findAll();
        List<SalaryStructureDto> dtoList = new ArrayList<>();

        for (SalaryStructure s : list) {
            dtoList.add(mapToDto(s, false));
        }
        return dtoList;
    }

    private SalaryStructureDto mapToDto(SalaryStructure structure, boolean maskAccount) {
        SalaryStructureDto dto = new SalaryStructureDto();
        dto.setId(structure.getId());
        dto.setUserId(structure.getUser().getId());
        dto.setFullName(structure.getUser().getFullName());
        dto.setUsername(structure.getUser().getUsername());
        dto.setDesignation(structure.getUser().getDesignation().name());
        dto.setEmploymentType(structure.getEmploymentType().name());
        dto.setBasicSalary(structure.getBasicSalary());
        dto.setDailyRate(structure.getDailyRate());
        dto.setHourlyOvertimeRate(structure.getHourlyOvertimeRate());
        dto.setEpfEtfEligible(structure.isEpfEtfEligible());
        dto.setAnnualCasualLeaveQuota(structure.getAnnualCasualLeaveQuota());
        dto.setAnnualMedicalLeaveQuota(structure.getAnnualMedicalLeaveQuota());
        dto.setBankName(structure.getBankName());
        dto.setBankBranch(structure.getBankBranch());
        dto.setUpdatedAt(structure.getUpdatedAt());

        // Decrypt Bank Account Number
        if (structure.getAccountNumber() != null && !structure.getAccountNumber().isEmpty()) {
            String plainAccount = encryptionUtil.decrypt(structure.getAccountNumber());
            dto.setAccountNumber(maskAccount ? maskAccountNumber(plainAccount) : plainAccount);
        }

        return dto;
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber.length() <= 4) {
            return accountNumber;
        }
        String stars = "*".repeat(accountNumber.length() - 4);
        return stars + accountNumber.substring(accountNumber.length() - 4);
    }
}