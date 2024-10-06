package com.fiuni.mytube_videos.api.dao.channel;

import com.fiuni.mytube.domain.channel.ChannelDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IChannelDao extends JpaRepository<ChannelDomain, Integer> {
    // Métodos adicionales si es necesario
}
