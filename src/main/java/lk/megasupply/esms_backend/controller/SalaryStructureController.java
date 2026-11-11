package lk.megasupply.esms_backend.controller;

import lk.megasupply.esms_backend.dto.SalaryStructureDto;
import lk.megasupply.esms_backend.dto.SalaryStructureRequestDto;
import lk.megasupply.esms_backend.service.SalaryStructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/salary-structures")
@RequiredArgsConstructor
public class SalaryStructureController {

    private final SalaryStructureService salaryStructureService;

    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<SalaryStructureDto>> getAll() {
        return ResponseEntity.ok(salaryStructureService.getAll());
    }


    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<SalaryStructureDto> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(salaryStructureService.getByUserId(userId));
    }

    @PutMapping("/user/{userId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<SalaryStructureDto> saveOrUpdate(
            @PathVariable Long userId,
            @RequestBody SalaryStructureRequestDto requestDto) {
        return ResponseEntity.ok(salaryStructureService.saveOrUpdate(userId, requestDto));
    }


    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('OWNER', 'EMPLOYEE')")
    public ResponseEntity<SalaryStructureDto> getMySalaryStructure(Authentication authentication) {
        SalaryStructureDto dto = salaryStructureService.getMySalaryStructure(authentication.getName());
        if (dto == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }
}