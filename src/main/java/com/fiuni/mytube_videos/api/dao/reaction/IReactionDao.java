package com.fiuni.mytube_videos.api.dao.reaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fiuni.mytube.domain.reaction.ReactionDomain;

import java.util.List;
import java.util.Map;

@Repository
public interface IReactionDao extends JpaRepository<ReactionDomain, Integer> {

    @Query("SELECT SUM(CASE WHEN r.reactionType = 'LIKE' THEN 1 ELSE 0 END) AS likes, " +
            "SUM(CASE WHEN r.reactionType = 'DISLIKE' THEN 1 ELSE 0 END) AS dislikes " +
            "FROM ReactionDomain r WHERE r.video.id = :videoId")
    Map<String, Long> getReactionSummaryByVideoId(@Param("videoId") Integer videoId);
}
