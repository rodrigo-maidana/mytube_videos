package com.fiuni.mytube_videos.api.TransactionsTest;

import com.fiuni.mytube.dto.video.VideoDTO;
import com.fiuni.mytube_videos.api.dao.video.IVideoDao;
import com.fiuni.mytube_videos.api.service.video.IVideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
@Slf4j
public class TestService {

    @Autowired
    private IVideoDao videoDao;

    @Autowired
    private IVideoService videoService;

    // -------------------- ROLLBACK ------------------------

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void methodRollbackWithError(VideoDTO video) throws Exception {
        log.info("Creando video con rollback forzado, parámetros: {}", video);
        VideoDTO newVideo = videoService.create(video);
        // Simulamos un error para forzar el rollback
        if (true) {
            log.error("Forzando rollback. Stacktrace del error:");
            throw new Exception("Error simulado para rollback");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, timeout = 1)
    public void methodRollbackWithDelay(VideoDTO video) throws InterruptedException {
        log.info("Creando video con rollback forzado después de un tiempo, parámetros: {}", video);
        videoService.create(video); // Usar el servicio para crear el video
        // Simulamos un retraso antes del rollback
        Thread.sleep(5000);  // 5 segundos de retraso
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        log.info("Rollback realizado después del retraso.");
    }

    // -------------------- LECTURA ------------------------

    @Transactional(readOnly = true)
    public VideoDTO methodReadOnly(Integer videoId) {
        log.info("Transacción de solo lectura");
        return videoService.getById(videoId); // Usar el servicio para obtener el video
    }

    // -------------------- ESCRITURA ------------------------

    @Transactional
    public VideoDTO methodWrite(VideoDTO video) {
        log.info("Transacción de escritura");
        return videoService.create(video); // Usar el servicio para crear el video
    }

    // -------------------- REQUIRED ------------------------

    @Transactional(propagation = Propagation.REQUIRED)
    public void methodRequiredDirect(VideoDTO video) {
        log.info("Llamada directa - Propagación REQUIRED.");
        videoService.create(video); // Usar el servicio para crear el video
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void methodRequiredIndirect(VideoDTO video) {
        log.info("Llamada indirecta con transacción - Propagación REQUIRED.");
        methodRequiredDirect(video);
    }

    public void methodRequiredIndirectNoTransaction(VideoDTO video) {
        log.info("Llamada indirecta sin transacción - Propagación REQUIRED.");
        methodRequiredDirect(video);
    }

    // -------------------- SUPPORTS ------------------------

    @Transactional(propagation = Propagation.SUPPORTS)
    public void methodSupportsDirect(VideoDTO video) {
        log.info("Llamada directa - Propagación SUPPORTS.");
        videoService.create(video); // Usar el servicio para crear el video
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void methodSupportsIndirect(VideoDTO video) {
        log.info("Llamada indirecta con transacción - Propagación SUPPORTS.");
        methodSupportsDirect(video);
    }

    public void methodSupportsIndirectNoTransaction(VideoDTO video) {
        log.info("Llamada indirecta sin transacción - Propagación SUPPORTS.");
        methodSupportsDirect(video);
    }

    // -------------------- NOT_SUPPORTED ------------------------

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void methodNotSupportsDirect(VideoDTO video) {
        log.info("Llamada directa - Propagación NOT_SUPPORTED.");
        videoService.create(video); // Usar el servicio para crear el video
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void methodNotSupportsIndirect(VideoDTO video) {
        log.info("Llamada indirecta con transacción - Propagación NOT_SUPPORTED.");
        methodNotSupportsDirect(video);
    }

    public void methodNotSupportsIndirectNoTransaction(VideoDTO video) {
        log.info("Llamada indirecta sin transacción - Propagación NOT_SUPPORTED.");
        methodNotSupportsDirect(video);
    }

    // -------------------- REQUIRES_NEW ------------------------

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void methodRequiresNewDirect(VideoDTO video) {
        log.info("Llamada directa - Propagación REQUIRES_NEW.");
        videoService.create(video); // Usar el servicio para crear el video
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void methodRequiresNewIndirect(VideoDTO video) {
        log.info("Llamada indirecta con transacción - Propagación REQUIRES_NEW.");
        methodRequiresNewDirect(video);
    }

    public void methodRequiresNewIndirectNoTransaction(VideoDTO video) {
        log.info("Llamada indirecta sin transacción - Propagación REQUIRES_NEW.");
        methodRequiresNewDirect(video);
    }

    // -------------------- NEVER ------------------------

    @Transactional(propagation = Propagation.NEVER)
    public void methodNeverDirect(VideoDTO video) {
        log.info("Llamada directa - Propagación NEVER.");
        videoService.create(video); // Usar el servicio para crear el video
    }

    @Transactional(propagation = Propagation.NEVER)
    public void methodNeverIndirect(VideoDTO video) {
        log.info("Llamada indirecta con transacción - Propagación NEVER.");
        methodNeverDirect(video);
    }

    public void methodNeverIndirectNoTransaction(VideoDTO video) {
        log.info("Llamada indirecta sin transacción - Propagación NEVER.");
        methodNeverDirect(video);
    }

    // -------------------- MANDATORY ------------------------

    @Transactional(propagation = Propagation.MANDATORY)
    public void methodMandatoryDirect(VideoDTO video) {
        log.info("Llamada directa - Propagación MANDATORY.");
        videoService.create(video); // Usar el servicio para crear el video
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void methodMandatoryIndirect(VideoDTO video) {
        log.info("Llamada indirecta con transacción - Propagación MANDATORY.");
        methodMandatoryDirect(video);
    }

    public void methodMandatoryIndirectNoTransaction(VideoDTO video) {
        log.info("Llamada indirecta sin transacción - Propagación MANDATORY.");
        methodMandatoryDirect(video);
    }
}
