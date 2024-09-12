package com.fiuni.mytube_videos.controller.comment;

import com.fiuni.mytube.dto.comment.CommentDTO;
import com.fiuni.mytube.dto.comment.CommentResult;
import com.fiuni.mytube_videos.service.comment.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
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
        CommentDTO savedComment = commentService.save(commentDTO);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }

    // Obtener un comentario por ID
    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Integer id) {
        CommentDTO comment = commentService.getById(id);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    // Obtener todos los comentarios
    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllComments() {
        CommentResult result = commentService.getAll();
        return new ResponseEntity<>(result.getComments(), HttpStatus.OK);
    }

    // Actualizar un comentario
    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Integer id, @RequestBody CommentDTO commentDTO) {
        CommentDTO existingComment = commentService.getById(id);
        if (existingComment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        commentDTO.set_id(id);
        CommentDTO updatedComment = commentService.save(commentDTO);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    // Eliminar un comentario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer id) {
        CommentDTO existingComment = commentService.getById(id);
        if (existingComment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Implementa la eliminación lógica si es necesario (por ejemplo, marcando el comentario como eliminado)
        //existingComment.setDeleted(true);
        commentService.save(existingComment);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
