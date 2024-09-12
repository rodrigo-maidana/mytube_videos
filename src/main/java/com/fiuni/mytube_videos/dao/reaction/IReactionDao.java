package com.fiuni.mytube_videos.dao.reaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fiuni.mytube.domain.reaction.ReactionDomain;

@Repository
public interface IReactionDao extends JpaRepository<ReactionDomain, Integer> {
    // Métodos adicionales si es necesario
}
