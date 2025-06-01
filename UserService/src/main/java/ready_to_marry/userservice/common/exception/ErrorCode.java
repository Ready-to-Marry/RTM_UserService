package ready_to_marry.userservice.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 1xxx: 비즈니스 오류
    INVALID_INVITE_CODE(1101, "Invite code does not exist or has expired"),
    CANNOT_CONNECT_TO_SELF(1102, "Cannot connect couple to self"),
    ALREADY_CONNECTED_SELF(1103, "Current user is already connected"),
    ALREADY_CONNECTED_PARTNER(1104, "Target user is already connected"),
    ALREADY_RELEASED(1105, "Couple already released or not connected"),

    // 2xxx: 인프라(시스템) 오류
    DB_SAVE_FAILURE(2101, "System error occurred while saving data to the database"),
    DB_DELETE_FAILURE(2102, "System error occurred while deleting data from the database"),
    DB_RETRIEVE_FAILURE(2103, "System error occurred while retrieving data from the database"),
    S3_UPLOAD_FAILURE(2104, "System error occurred while uploading image to S3"),
    INVITE_CODE_SAVE_FAILURE(2105, "System error occurred while saving invite code to redis"),
    INVITE_CODE_DELETE_FAILURE(2106, "System error occurred while deleting invite code from redis"),
    INVITE_CODE_RETRIEVE_FAILURE(2107, "System error occurred while retrieving invite code from redis"),
    INVITE_CODE_GENERATION_FAILURE(2108, "System error occurred while generating unique invite code after multiple attempts");

    private final int code;
    private final String message;
}