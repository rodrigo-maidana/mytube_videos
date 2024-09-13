package com.fiuni.mytube_videos.dao.viewinghistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fiuni.mytube.domain.viewinghistory.ViewingHistoryDomain;

import java.util.List;

@Repository
public interface IViewingHistoryDao extends JpaRepository<ViewingHistoryDomain, Integer> {
    List<ViewingHistoryDomain> findByUserId(Integer userId);
    // Métodos adicionales si es necesario
}
