package az.aistgroup.security.jwt;

import az.aistgroup.exception.ErrorResponseCode;

public record TokenValidityResponse(boolean isValid, ErrorResponseCode code) {
}
