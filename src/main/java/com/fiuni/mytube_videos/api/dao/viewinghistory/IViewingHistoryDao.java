package com.fiuni.mytube_videos.api.dao.viewinghistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fiuni.mytube.domain.viewinghistory.ViewingHistoryDomain;

import java.util.List;
import java.util.Optional;

@Repository
public interface IViewingHistoryDao extends JpaRepository<ViewingHistoryDomain, Integer> {

    // Buscar todos los historiales de visualización que no estén eliminados, con paginación
    Page<ViewingHistoryDomain> findByUserId(Pageable page, Integer userId);

}
