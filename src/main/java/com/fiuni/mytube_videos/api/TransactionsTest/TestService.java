package com.fiuni.mytube_videos.api.TransactionsTest;

import com.fiuni.mytube.domain.video.VideoDomain;
import com.fiuni.mytube_videos.api.dao.video.IVideoDao;
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


    // -------------------- ROLLBACK ------------------------

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void methodRollbackWithError(VideoDomain video) throws Exception {
        log.info("Creando video con rollback forzado, parámetros: {}", video);
        videoDao.save(video);
        // Simulamos un error para forzar el rollback
        if (true) {
            log.error("Forzando rollback. Stacktrace del error:");
            throw new Exception("Error simulado para rollback");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void methodRollbackWithDelay(VideoDomain video) throws InterruptedException {
        log.info("Creando video con rollback forzado después de un tiempo, parámetros: {}", video);
        videoDao.save(video);
        // Simulamos un retraso antes del rollback
        Thread.sleep(5000);  // 5 segundos de retraso
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        log.info("Rollback realizado después del retraso.");
    }


    // -------------------- LECTURA ------------------------

    @Transactional(readOnly = true)
    public VideoDomain methodReadOnly(Integer videoId) {
        log.info("Transacción de solo lectura");
        return videoDao.findById(videoId).orElse(null); // Operación de lectura
    }

    // -------------------- ESCRITURA ------------------------

    @Transactional
    public VideoDomain methodWrite(VideoDomain video) {
        log.info("Transacción de escritura");
        return videoDao.save(video); // Operación de escritura
    }

    // -------------------- REQUIRED ------------------------

    @Transactional(propagation = Propagation.REQUIRED)
    public void methodRequiredDirect(VideoDomain video) {
        log.info("Llamada directa - Propagación REQUIRED.");
        videoDao.save(video);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void methodRequiredIndirect(VideoDomain video) {
        log.info("Llamada indirecta con transacción - Propagación REQUIRED.");
        methodRequiredDirect(video);
    }

    public void methodRequiredIndirectNoTransaction(VideoDomain video) {
        log.info("Llamada indirecta sin transacción - Propagación REQUIRED.");
        methodRequiredDirect(video);
    }

    // -------------------- SUPPORTS ------------------------

    @Transactional(propagation = Propagation.SUPPORTS)
    public void methodSupportsDirect(VideoDomain video) {
        log.info("Llamada directa - Propagación SUPPORTS.");
        videoDao.save(video);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void methodSupportsIndirect(VideoDomain video) {
        log.info("Llamada indirecta con transacción - Propagación SUPPORTS.");
        methodSupportsDirect(video);
    }

    public void methodSupportsIndirectNoTransaction(VideoDomain video) {
        log.info("Llamada indirecta sin transacción - Propagación SUPPORTS.");
        methodSupportsDirect(video);
    }

    // -------------------- NOT_SUPPORTED ------------------------

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void methodNotSupportsDirect(VideoDomain video) {
        log.info("Llamada directa - Propagación NOT_SUPPORTED.");
        videoDao.save(video);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void methodNotSupportsIndirect(VideoDomain video) {
        log.info("Llamada indirecta con transacción - Propagación NOT_SUPPORTED.");
        methodNotSupportsDirect(video);
    }

    public void methodNotSupportsIndirectNoTransaction(VideoDomain video) {
        log.info("Llamada indirecta sin transacción - Propagación NOT_SUPPORTED.");
        methodNotSupportsDirect(video);
    }

    // -------------------- REQUIRES_NEW ------------------------

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void methodRequiresNewDirect(VideoDomain video) {
        log.info("Llamada directa - Propagación REQUIRES_NEW.");
        videoDao.save(video);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void methodRequiresNewIndirect(VideoDomain video) {
        log.info("Llamada indirecta con transacción - Propagación REQUIRES_NEW.");
        methodRequiresNewDirect(video);
    }

    public void methodRequiresNewIndirectNoTransaction(VideoDomain video) {
        log.info("Llamada indirecta sin transacción - Propagación REQUIRES_NEW.");
        methodRequiresNewDirect(video);
    }

    // -------------------- NEVER ------------------------

    @Transactional(propagation = Propagation.NEVER)
    public void methodNeverDirect(VideoDomain video) {
        log.info("Llamada directa - Propagación NEVER.");
        videoDao.save(video);
    }

    @Transactional(propagation = Propagation.NEVER)
    public void methodNeverIndirect(VideoDomain video) {
        log.info("Llamada indirecta con transacción - Propagación NEVER.");
        methodNeverDirect(video);
    }

    public void methodNeverIndirectNoTransaction(VideoDomain video) {
        log.info("Llamada indirecta sin transacción - Propagación NEVER.");
        methodNeverDirect(video);
    }

    // -------------------- MANDATORY ------------------------

    @Transactional(propagation = Propagation.MANDATORY)
    public void methodMandatoryDirect(VideoDomain video) {
        log.info("Llamada directa - Propagación MANDATORY.");
        videoDao.save(video);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void methodMandatoryIndirect(VideoDomain video) {
        log.info("Llamada indirecta con transacción - Propagación MANDATORY.");
        methodMandatoryDirect(video);
    }

    public void methodMandatoryIndirectNoTransaction(VideoDomain video) {
        log.info("Llamada indirecta sin transacción - Propagación MANDATORY.");
        methodMandatoryDirect(video);
    }
}
