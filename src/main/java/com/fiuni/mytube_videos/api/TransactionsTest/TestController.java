package com.fiuni.mytube_videos.api.TransactionsTest;

import com.fiuni.mytube.dto.video.VideoDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private TestService testService;

    // -------------------- ROLLBACK CON ERROR ------------------------

    @PostMapping("/rollback-with-error")
    public ResponseEntity<String> rollbackWithError(@RequestBody VideoDTO video) {
        try {
            testService.methodRollbackWithError(video);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Se forzó un rollback debido a un error: " + e.getMessage());
        }
        return ResponseEntity.ok("Video creado, pero se forzó un rollback.");
    }

    // -------------------- ROLLBACK CON RETRASO ------------------------

    @PostMapping("/rollback-with-delay")
    public ResponseEntity<String> rollbackWithDelay(@RequestBody VideoDTO video) {
        try {
            testService.methodRollbackWithDelay(video);
        } catch (InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error durante el rollback diferido: " + e.getMessage());
        }
        return ResponseEntity.ok("Video creado, pero se forzó un rollback después de un retraso.");
    }

    // -------------------- LECTURA ------------------------

    @GetMapping("/read/{id}")
    public ResponseEntity<VideoDTO> readOnly(@PathVariable Integer id) {
        VideoDTO video = testService.methodReadOnly(id);
        return ResponseEntity.ok(video);
    }

    // -------------------- ESCRITURA ------------------------

    @PostMapping("/write")
    public ResponseEntity<VideoDTO> write(@RequestBody VideoDTO video) {
        VideoDTO savedVideo = testService.methodWrite(video);
        return ResponseEntity.ok(savedVideo);
    }

    // -------------------- REQUIRED ------------------------

    @PostMapping("/required-direct")
    public ResponseEntity<String> requiredDirect(@RequestBody VideoDTO video) {
        testService.methodRequiredDirect(video);
        return ResponseEntity.ok("Llamada directa con propagación REQUIRED exitosa.");
    }

    @PostMapping("/required-indirect")
    public ResponseEntity<String> requiredIndirect(@RequestBody VideoDTO video) {
        testService.methodRequiredIndirect(video);
        return ResponseEntity.ok("Llamada indirecta con transacción (REQUIRED) exitosa.");
    }

    @PostMapping("/required-indirect-no-tx")
    public ResponseEntity<String> requiredIndirectNoTransaction(@RequestBody VideoDTO video) {
        testService.methodRequiredIndirectNoTransaction(video);
        return ResponseEntity.ok("Llamada indirecta sin transacción (REQUIRED) exitosa.");
    }

    // -------------------- SUPPORTS ------------------------

    @PostMapping("/supports-direct")
    public ResponseEntity<String> supportsDirect(@RequestBody VideoDTO video) {
        testService.methodSupportsDirect(video);
        return ResponseEntity.ok("Llamada directa con propagación SUPPORTS exitosa.");
    }

    @PostMapping("/supports-indirect")
    public ResponseEntity<String> supportsIndirect(@RequestBody VideoDTO video) {
        testService.methodSupportsIndirect(video);
        return ResponseEntity.ok("Llamada indirecta con transacción (SUPPORTS) exitosa.");
    }

    @PostMapping("/supports-indirect-no-tx")
    public ResponseEntity<String> supportsIndirectNoTransaction(@RequestBody VideoDTO video) {
        testService.methodSupportsIndirectNoTransaction(video);
        return ResponseEntity.ok("Llamada indirecta sin transacción (SUPPORTS) exitosa.");
    }

    // -------------------- NOT_SUPPORTED ------------------------

    @PostMapping("/not-supported-direct")
    public ResponseEntity<String> notSupportedDirect(@RequestBody VideoDTO video) {
        testService.methodNotSupportsDirect(video);
        return ResponseEntity.ok("Llamada directa con propagación NOT_SUPPORTED exitosa.");
    }

    @PostMapping("/not-supported-indirect")
    public ResponseEntity<String> notSupportedIndirect(@RequestBody VideoDTO video) {
        testService.methodNotSupportsIndirect(video);
        return ResponseEntity.ok("Llamada indirecta con transacción (NOT_SUPPORTED) exitosa.");
    }

    @PostMapping("/not-supported-indirect-no-tx")
    public ResponseEntity<String> notSupportedIndirectNoTransaction(@RequestBody VideoDTO video) {
        testService.methodNotSupportsIndirectNoTransaction(video);
        return ResponseEntity.ok("Llamada indirecta sin transacción (NOT_SUPPORTED) exitosa.");
    }

    // -------------------- REQUIRES_NEW ------------------------

    @PostMapping("/requires-new-direct")
    public ResponseEntity<String> requiresNewDirect(@RequestBody VideoDTO video) {
        testService.methodRequiresNewDirect(video);
        return ResponseEntity.ok("Llamada directa con propagación REQUIRES_NEW exitosa.");
    }

    @PostMapping("/requires-new-indirect")
    public ResponseEntity<String> requiresNewIndirect(@RequestBody VideoDTO video) {
        testService.methodRequiresNewIndirect(video);
        return ResponseEntity.ok("Llamada indirecta con transacción (REQUIRES_NEW) exitosa.");
    }

    @PostMapping("/requires-new-indirect-no-tx")
    public ResponseEntity<String> requiresNewIndirectNoTransaction(@RequestBody VideoDTO video) {
        testService.methodRequiresNewIndirectNoTransaction(video);
        return ResponseEntity.ok("Llamada indirecta sin transacción (REQUIRES_NEW) exitosa.");
    }

    // -------------------- NEVER ------------------------

    @PostMapping("/never-direct")
    public ResponseEntity<String> neverDirect(@RequestBody VideoDTO video) {
        testService.methodNeverDirect(video);
        return ResponseEntity.ok("Llamada directa con propagación NEVER exitosa.");
    }

    @PostMapping("/never-indirect")
    public ResponseEntity<String> neverIndirect(@RequestBody VideoDTO video) {
        testService.methodNeverIndirect(video);
        return ResponseEntity.ok("Llamada indirecta con transacción (NEVER) exitosa.");
    }

    @PostMapping("/never-indirect-no-tx")
    public ResponseEntity<String> neverIndirectNoTransaction(@RequestBody VideoDTO video) {
        testService.methodNeverIndirectNoTransaction(video);
        return ResponseEntity.ok("Llamada indirecta sin transacción (NEVER) exitosa.");
    }

    // -------------------- MANDATORY ------------------------

    @PostMapping("/mandatory-direct")
    public ResponseEntity<String> mandatoryDirect(@RequestBody VideoDTO video) {
        testService.methodMandatoryDirect(video);
        return ResponseEntity.ok("Llamada directa con propagación MANDATORY exitosa.");
    }

    @PostMapping("/mandatory-indirect")
    public ResponseEntity<String> mandatoryIndirect(@RequestBody VideoDTO video) {
        testService.methodMandatoryIndirect(video);
        return ResponseEntity.ok("Llamada indirecta con transacción (MANDATORY) exitosa.");
    }

    @PostMapping("/mandatory-indirect-no-tx")
    public ResponseEntity<String> mandatoryIndirectNoTransaction(@RequestBody VideoDTO video) {
        testService.methodMandatoryIndirectNoTransaction(video);
        return ResponseEntity.ok("Llamada indirecta sin transacción (MANDATORY) exitosa.");
    }
}
