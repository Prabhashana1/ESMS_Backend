package lk.megasupply.esms_backend.entity;

import lk.megasupply.esms_backend.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
        name = "attendances",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "date"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    private LocalTime checkInTime;
    private LocalTime checkOutTime;

    private Double checkInLatitude;
    private Double checkInLongitude;
    private Double checkOutLatitude;
    private Double checkOutLongitude;

    @Builder.Default
    @Column(nullable = false)
    private boolean locationValid = false;

    @Builder.Default
    private Double workingHours = 0.0;

    @Builder.Default
    private Double overtimeHours = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;
}