package ready_to_marry.userservice.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 1xxx: 비즈니스 오류

    // 2xxx: 인프라(시스템) 오류
    DB_SAVE_FAILURE(2101, "System error occurred while saving data to the database"),
    DB_DELETE_FAILURE(2102, "System error occurred while deleting data from the database"),
    DB_RETRIEVE_FAILURE(2103, "System error occurred while retrieving data from the database"),
    S3_UPLOAD_FAILURE(2104, "System error occurred while uploading image to S3");

    private final int code;
    private final String message;
}