package com.fiuni.mytube_videos.api.TransactionsTest;

import com.fiuni.mytube.domain.user.UserDomain;
import com.fiuni.mytube.dto.video.VideoDTO;
import com.fiuni.mytube_videos.api.dao.user.IUserDao;
import com.fiuni.mytube_videos.api.dao.video.IVideoDao;
import com.fiuni.mytube_videos.api.service.video.IVideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Slf4j
public class TestService {

    @Autowired
    private IVideoDao videoDao;

    @Autowired
    private IVideoService videoService;

    @Autowired
    private IUserDao iUserDao;

    // -------------------- ROLLBACK ------------------------

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void methodRollbackWithError(VideoDTO video) throws Exception {
        log.info("Creando video con rollback forzado, parámetros: {}", video);
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);

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
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);

        videoService.create(video); // Usar el servicio para crear el video
        // Simulamos un retraso antes del rollback
        Thread.sleep(5000);  // 5 segundos de retraso
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        log.info("Rollback realizado después del retraso.");
        isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción después del rollback?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);
    }

    // -------------------- LECTURA ------------------------

    @Transactional(readOnly = true)
    public VideoDTO methodReadOnly(Integer videoId) {
        log.info("Transacción de solo lectura");
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);

        return videoService.getById(videoId); // Usar el servicio para obtener el video
    }

    // -------------------- ESCRITURA ------------------------

    @Transactional
    public VideoDTO methodWrite(VideoDTO video) {
        log.info("Transacción de escritura");
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);

        return videoService.create(video); // Usar el servicio para crear el video
    }

    // -------------------- REQUIRED ------------------------

    @Transactional(propagation = Propagation.REQUIRED)
    public void methodRequiredDirect(VideoDTO video) {
        log.info("Llamada directa - Propagación REQUIRED.");
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);

        videoService.create(video); // Usar el servicio para crear el video
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void methodRequiredIndirect(VideoDTO video) {
        videoService.create(video);
        log.info("Llamada indirecta con transacción - Propagación REQUIRED.");
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);

        methodRequiredDirect(video);
    }

    public void methodRequiredIndirectNoTransaction(VideoDTO video) {
        videoService.create(video);
        log.info("Llamada indirecta sin transacción - Propagación REQUIRED");
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);

        methodRequiredDirect(video);
    }

    // -------------------- SUPPORTS ------------------------

    @Transactional(propagation = Propagation.SUPPORTS)
    public void methodSupportsDirect(VideoDTO video) {
        log.info("Llamada directa - Propagación SUPPORTS.");
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);

        videoService.create(video); // Usar el servicio para crear el video
    }

    @Transactional
    public void methodSupportsIndirect(VideoDTO video) {
        videoService.create(video);
        log.info("Llamada indirecta con transacción - Propagación SUPPORTS.");
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);

        methodSupportsDirect(video);
    }

    public void methodSupportsIndirectNoTransaction(VideoDTO video) {
        videoService.create(video);
        log.info("Llamada indirecta sin transacción - Propagación SUPPORTS.");
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);

        methodSupportsDirect(video);
    }

    // -------------------- NOT_SUPPORTED ------------------------

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void methodNotSupportsDirect(VideoDTO video) {
        log.info("Llamada directa - Propagación NOT_SUPPORTED.");

        videoService.create(video); // Usar el servicio para crear el video
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void methodNotSupportsIndirect(VideoDTO video) {
        videoService.create(video);
        log.info("Llamada indirecta con transacción - Propagación NOT_SUPPORTED.");
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción antes de llamar a methodNotSupportsDirect?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);

        methodNotSupportsDirect(video);
    }

    @Transactional(propagation = Propagation.NEVER)
    public void methodNotSupportsIndirectNoTransaction(VideoDTO video) {
        videoService.create(video);
        log.info("Llamada indirecta sin transacción - Propagación NOT_SUPPORTED.");
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción antes de llamar a methodNotSupportsDirect?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);

        methodNotSupportsDirect(video);
    }

    // -------------------- REQUIRES_NEW ------------------------

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void methodRequiresNewDirect(VideoDTO video) {
        log.info("Llamada directa - Propagación REQUIRES_NEW.");
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);

        videoService.create(video); // Usar el servicio para crear el video
    }

    @Transactional
    public void methodRequiresNewIndirect(VideoDTO video) {
        videoService.create(video);
        log.info("Llamada indirecta con transacción - Propagación REQUIRES_NEW.");
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción antes de llamar a methodRequiresNewDirect?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);

        methodRequiresNewDirect(video);
    }

    public void methodRequiresNewIndirectNoTransaction(VideoDTO video) {
        videoService.create(video);
        log.info("Llamada indirecta sin transacción - Propagación REQUIRES_NEW.");
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción antes de llamar a methodRequiresNewDirect?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);

        methodRequiresNewDirect(video);
    }

    // -------------------- NEVER ------------------------

    @Transactional(propagation = Propagation.NEVER)
    public void methodNeverDirect(VideoDTO video) {
        log.info("Llamada directa - Propagación NEVER.");
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);

        videoService.create(video); // Usar el servicio para crear el video
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void methodNeverIndirect(VideoDTO video) {
        videoService.create(video);
        log.info("Llamada indirecta con transacción - Propagación NEVER.");
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción antes de llamar a methodNeverDirect?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);

        methodNeverDirect(video);
    }

    public void methodNeverIndirectNoTransaction(VideoDTO video) {
        log.info("Llamada indirecta sin transacción - Propagación NEVER.");
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción antes de llamar a methodNeverDirect?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);

        methodNeverDirect(video);
    }

    // -------------------- MANDATORY ------------------------

    @Transactional(propagation = Propagation.MANDATORY)
    public void methodMandatoryDirect(VideoDTO video) {
        log.info("Llamada directa - Propagación MANDATORY.");
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);

        videoService.create(video); // Usar el servicio para crear el video
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void methodMandatoryIndirect(VideoDTO video) {
        videoService.create(video);
        log.info("Llamada indirecta con transacción - Propagación MANDATORY.");
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción antes de llamar a methodMandatoryDirect?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);

        methodMandatoryDirect(video);
    }

    @Transactional(propagation = Propagation.NEVER)
    public void methodMandatoryIndirectNoTransaction(VideoDTO video) {
        videoService.create(video);
        log.info("Llamada indirecta sin transacción - Propagación MANDATORY.");
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("¿Está activa una transacción antes de llamar a methodMandatoryDirect?: " + isTransactionActive + ", Nombre de la transacción: " + transactionName);

        methodMandatoryDirect(video);
    }
}
