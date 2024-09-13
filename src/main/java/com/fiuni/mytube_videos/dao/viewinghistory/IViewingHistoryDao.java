package com.fiuni.mytube_videos.dao.viewinghistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fiuni.mytube.domain.viewinghistory.ViewingHistoryDomain;

import java.util.List;

@Repository
public interface IViewingHistoryDao extends JpaRepository<ViewingHistoryDomain, Integer> {
    Page<ViewingHistoryDomain> findByUserId(Pageable page, Integer userId);

    // Métodos adicionales si es necesario
}
