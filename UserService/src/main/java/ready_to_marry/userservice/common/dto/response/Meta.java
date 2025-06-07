package ready_to_marry.userservice.common.dto.response;

import lombok.*;

/**
 * 페이징 메타 정보
 *
 * - page          : 현재 페이지 번호 (0부터 시작)
 * - size          : 페이지당 데이터 개수
 * - totalElements : 전체 데이터 건수
 * - totalPages    : 전체 페이지 수
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meta {
    // 현재 페이지 번호
    private int page;

    // 페이지당 데이터 개수
    private int size;

    // 전체 데이터 건수
    private long totalElements;

    // 전체 페이지 수
    private int totalPages;
}