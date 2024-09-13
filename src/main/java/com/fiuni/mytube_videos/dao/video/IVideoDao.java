package com.fiuni.mytube_videos.dao.video;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fiuni.mytube.domain.video.VideoDomain;

import java.util.Optional;

@Repository
public interface IVideoDao extends JpaRepository<VideoDomain, Integer> {

    // Buscar por id y que no esté eliminado
    Optional<VideoDomain> findByIdAndDeletedFalse(Integer id);

    // Buscar todos los videos que no estén eliminados, con paginación
    Page<VideoDomain> findAllByDeletedFalse(Pageable pageable);
}
