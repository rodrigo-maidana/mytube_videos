package com.fiuni.mytube_videos.api.service.comment;

import com.fiuni.mytube.dto.comment.CommentDTO;
import com.fiuni.mytube.dto.comment.CommentResult;
import com.fiuni.mytube_videos.api.service.base.IBaseService;

import java.util.List;

public interface ICommentService extends IBaseService<CommentDTO, CommentResult> {
    // Obtener todos los comentarios hijos de un comentario padre
    List<CommentDTO> getChildrenComments(Integer parentId);
}
