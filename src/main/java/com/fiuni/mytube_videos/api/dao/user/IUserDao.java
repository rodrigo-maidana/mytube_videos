package com.fiuni.mytube_videos.api.dao.user;

import com.fiuni.mytube.domain.user.UserDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserDao extends JpaRepository<UserDomain, Integer> {
    // Métodos adicionales si es necesario
}
