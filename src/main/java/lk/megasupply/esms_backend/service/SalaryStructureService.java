package lk.megasupply.esms_backend.service;

import lk.megasupply.esms_backend.dto.SalaryStructureDto;
import lk.megasupply.esms_backend.dto.SalaryStructureRequestDto;

import java.util.List;

public interface SalaryStructureService {
    SalaryStructureDto saveOrUpdate(Long userId, SalaryStructureRequestDto requestDto);
    SalaryStructureDto getByUserId(Long userId);
    SalaryStructureDto getMySalaryStructure(String username);
    List<SalaryStructureDto> getAll();
}