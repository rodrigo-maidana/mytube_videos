package com.fiuni.mytube_videos.dao.viewinghistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fiuni.mytube.domain.viewinghistory.ViewingHistoryDomain;

@Repository
public interface IViewingHistoryDao extends JpaRepository<ViewingHistoryDomain, Integer> {
    // Métodos adicionales si es necesario
}
