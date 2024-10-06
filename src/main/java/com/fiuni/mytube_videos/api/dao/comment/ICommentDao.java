package com.fiuni.mytube_videos.api.dao.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fiuni.mytube.domain.comment.CommentDomain;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICommentDao extends JpaRepository<CommentDomain, Integer> {

    // Metodo para obtener un comentario por id que no esté eliminado
    Optional<CommentDomain> findByIdAndDeletedFalse(Integer id);

    // Metodo para obtener todos los comentarios que no estén eliminados (paginados)
    Page<CommentDomain> findAllByDeletedFalse(Pageable pageable);

    // Metodo para obtener todos los comentarios hijos de un comentario padre
    List<CommentDomain> findByParentCommentId(Integer parentId);

    // Metodo para obtener todos los hijos de un comentario padre que no estén eliminados
    List<CommentDomain> findByParentCommentIdAndDeletedFalse(Integer parentId);
}
