package tn.esprit.autoloc.web;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.autoloc.exception.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTests {

    @Test
    void resourceNotFoundReturnsNotFoundResponseAndTimestamp() {
        ResponseEntity<ErrorResponse> response = new GlobalExceptionHandler()
                .handleResourceNotFoundException(new ResourceNotFoundException("Client", "7"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatut());
        assertEquals("Client introuvable avec l'identifiant 7", response.getBody().getMessage());
        assertNotNull(response.getBody().getHorodatage());
    }

    @Test
    void illegalArgumentReturnsBadRequestResponse() {
        ResponseEntity<ErrorResponse> response = new GlobalExceptionHandler()
                .handleIllegalArgumentException(new IllegalArgumentException("Invalid input"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatut());
        assertEquals("Invalid input", response.getBody().getMessage());
        assertNotNull(response.getBody().getHorodatage());
    }
}
