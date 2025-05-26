package ready_to_marry.userservice.common.util;

public final class MaskingUtils {
    private MaskingUtils() {
        // 유틸 클래스이므로 인스턴스 생성 방지
    }

    /**
     * 전화번호 부분 마스킹
     * - 하이픈 포함: 010-1234-5678 → 010-****-5678
     * - 하이픈 없음: 01012345678 → 010****5678
     * - 짧은 번호도 일부 마스킹 적용 (예: 0101 → ***1)
     * - 국가번호(+82 등) 포함도 지원
     * - 형식이 이상하더라도 최대한 보호
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.isBlank()) return "";

        // 하이픈 포함 케이스
        if (phone.contains("-")) {
            String[] parts = phone.split("-");
            StringBuilder masked = new StringBuilder();

            for (int i = 0; i < parts.length; i++) {
                if (i != 0) masked.append("-");

                // 가운데 블록 마스킹 (마지막 블록 제외)
                if (i == parts.length - 2) {
                    masked.append("*".repeat(parts[i].length()));
                } else {
                    masked.append(parts[i]);
                }
            }

            return masked.toString();
        }

        // 하이픈 없는 케이스
        String raw = phone.startsWith("+") ? phone.substring(1) : phone;
        String prefix = phone.startsWith("+") ? "+" : "";

        int len = raw.length();

        if (len <= 1) {
            return phone;
        } else if (len <= 4) {
            return prefix + "*".repeat(len - 1) + raw.substring(len - 1);
        } else if (len <= 7) {
            String start = raw.substring(0, 2);
            String end = raw.substring(len - 2);
            String mid = "*".repeat(len - 4);
            return prefix + start + mid + end;
        } else {
            String start = raw.substring(0, 3);
            String mid = "*".repeat(len - 7);
            String end = raw.substring(len - 4);
            return prefix + start + mid + end;
        }
    }
}