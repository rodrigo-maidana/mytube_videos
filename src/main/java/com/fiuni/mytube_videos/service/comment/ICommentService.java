package com.fiuni.mytube_videos.service.comment;

import com.fiuni.mytube.dto.comment.CommentDTO;
import com.fiuni.mytube.dto.comment.CommentResult;
import com.fiuni.mytube_videos.service.base.IBaseService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ICommentService extends IBaseService<CommentDTO, CommentResult> {
    // Obtener todos los comentarios hijos de un comentario padre
    List<CommentDTO> getChildrenComments(Integer parentId);
}
