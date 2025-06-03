package ready_to_marry.userservice.schedule.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * user_db.couple_schedule 테이블 매핑 엔티티
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "couple_schedule")
public class CoupleSchedule {
    // PK 일정 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id", updatable = false, nullable = false)
    private Long scheduleId;

    // 커플 ID (외래 키)
    @Column(name = "couple_id", nullable = false)
    private UUID coupleId;

    // 일정 제목
    @Column(name = "title", length = 50, nullable = false)
    private String title;

    // 일정 내용
    @Column(name = "content", length = 500, nullable = false)
    private String content;

    // 일정 날짜
    @Column(name = "date", nullable = false)
    private LocalDate date;

    // 일정 시각
    @Column(name = "time", nullable = false)
    private LocalTime time;
}
