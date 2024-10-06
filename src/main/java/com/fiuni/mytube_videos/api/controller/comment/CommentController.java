package com.fiuni.mytube_videos.api.controller.comment;

import com.fiuni.mytube.dto.comment.CommentDTO;
import com.fiuni.mytube.dto.comment.CommentResult;
import com.fiuni.mytube_videos.api.service.comment.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private ICommentService commentService;

    // Crear un nuevo comentario
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDTO) {
        CommentDTO savedComment = commentService.create(commentDTO);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }

    // Obtener un comentario por ID
    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Integer id) {
        CommentDTO comment = commentService.getById(id);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    // Obtener todos los comentarios con paginación
    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllComments(Pageable pageable) {
        CommentResult result = commentService.getAll(pageable);
        return new ResponseEntity<>(result.getComments(), HttpStatus.OK);
    }

    // Obtener los comentarios hijos de un comentario padre
    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<CommentDTO>> getChildrenComments(@PathVariable Integer parentId) {
        List<CommentDTO> childComments = commentService.getChildrenComments(parentId);
        return new ResponseEntity<>(childComments, HttpStatus.OK);
    }

    // Actualizar un comentario
    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Integer id, @RequestBody CommentDTO commentDTO) {
        commentDTO.set_id(id);  // Asignar el ID para la actualización
        CommentDTO updatedComment = commentService.update(commentDTO);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    // Eliminar un comentario y sus hijos
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer id) {
        // Eliminar el comentario y todos sus hijos
        commentService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
