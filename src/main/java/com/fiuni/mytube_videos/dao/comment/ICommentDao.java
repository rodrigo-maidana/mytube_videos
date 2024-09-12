package com.fiuni.mytube_videos.dao.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fiuni.mytube.domain.comment.CommentDomain;

@Repository
public interface ICommentDao extends JpaRepository<CommentDomain, Integer> {
    // Métodos adicionales si es necesario
}
