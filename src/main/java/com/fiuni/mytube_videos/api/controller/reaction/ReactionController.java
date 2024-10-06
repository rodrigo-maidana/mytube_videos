package com.fiuni.mytube_videos.api.controller.reaction;

import com.fiuni.mytube.dto.reaction.ReactionDTO;
import com.fiuni.mytube.dto.reaction.ReactionResult;
import com.fiuni.mytube_videos.api.dto.reaction.ReactionSummaryDTO;
import com.fiuni.mytube_videos.api.service.reaction.IReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reactions")
public class ReactionController {

    @Autowired
    private IReactionService reactionService;

    // Crear una nueva reacción
    @PostMapping
    public ResponseEntity<ReactionDTO> createReaction(@RequestBody ReactionDTO reactionDTO) {
        ReactionDTO createdReaction = reactionService.create(reactionDTO);
        return new ResponseEntity<>(createdReaction, HttpStatus.CREATED);
    }

    // Obtener una reacción por ID
    @GetMapping("/{id}")
    public ResponseEntity<ReactionDTO> getReactionById(@PathVariable Integer id) {
        ReactionDTO reactionDTO = reactionService.getById(id);
        return new ResponseEntity<>(reactionDTO, HttpStatus.OK);
    }

    // Obtener todas las reacciones con paginación
    @GetMapping
    public ResponseEntity<List<ReactionDTO>> getAllReactions(Pageable pageable) {
        ReactionResult result = reactionService.getAll(pageable);
        return new ResponseEntity<>(result.getReactions(), HttpStatus.OK);
    }

    // Obtener el resumen de reacciones por video (likes/dislikes)
    @GetMapping("/video/{videoId}")
    public ResponseEntity<ReactionSummaryDTO> getReactionsByVideo(@PathVariable Integer videoId) {
        ReactionSummaryDTO dto = reactionService.getSummaryByVideoId(videoId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    // Eliminar una reacción por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReaction(@PathVariable Integer id) {
        reactionService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
