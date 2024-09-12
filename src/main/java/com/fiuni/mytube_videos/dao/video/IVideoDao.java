package com.fiuni.mytube_videos.dao.video;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fiuni.mytube.domain.video.VideoDomain;

@Repository
public interface IVideoDao extends JpaRepository<VideoDomain, Integer> {
}
