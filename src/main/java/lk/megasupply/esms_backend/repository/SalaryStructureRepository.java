package lk.megasupply.esms_backend.repository;

import lk.megasupply.esms_backend.entity.SalaryStructure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalaryStructureRepository extends JpaRepository<SalaryStructure, Long> {

    Optional<SalaryStructure> findByUserId(Long userId);

    Optional<SalaryStructure> findByUserUsername(String username);
}