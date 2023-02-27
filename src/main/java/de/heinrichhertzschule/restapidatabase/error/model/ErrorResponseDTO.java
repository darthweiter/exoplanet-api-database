package de.heinrichhertzschule.restapidatabase.error.model;


public record ErrorResponseDTO(String message, int code, String moreInfo) {
}
