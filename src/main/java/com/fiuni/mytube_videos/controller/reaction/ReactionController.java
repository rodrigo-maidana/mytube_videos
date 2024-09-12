package com.fiuni.mytube_videos.controller.reaction;

import com.fiuni.mytube.dto.reaction.ReactionDTO;
import com.fiuni.mytube.dto.reaction.ReactionResult;
import com.fiuni.mytube_videos.service.reaction.IReactionService;
import org.springframework.beans.factory.annotation.Autowired;
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
        ReactionDTO savedReaction = reactionService.save(reactionDTO);
        return new ResponseEntity<>(savedReaction, HttpStatus.CREATED);
    }

    // Obtener una reacción por ID
    @GetMapping("/{id}")
    public ResponseEntity<ReactionDTO> getReactionById(@PathVariable Integer id) {
        ReactionDTO reaction = reactionService.getById(id);
        return new ResponseEntity<>(reaction, HttpStatus.OK);
    }

    // Obtener todas las reacciones
    @GetMapping
    public ResponseEntity<List<ReactionDTO>> getAllReactions() {
        ReactionResult result = reactionService.getAll();
        return new ResponseEntity<>(result.getReactions(), HttpStatus.OK);
    }

    // Actualizar una reacción
    @PutMapping("/{id}")
    public ResponseEntity<ReactionDTO> updateReaction(@PathVariable Integer id, @RequestBody ReactionDTO reactionDTO) {
        ReactionDTO existingReaction = reactionService.getById(id);
        if (existingReaction == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        reactionDTO.set_id(id);
        ReactionDTO updatedReaction = reactionService.save(reactionDTO);
        return new ResponseEntity<>(updatedReaction, HttpStatus.OK);
    }

    // Eliminar una reacción
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReaction(@PathVariable Integer id) {
        ReactionDTO existingReaction = reactionService.getById(id);
        if (existingReaction == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Implementa la eliminación si es necesario (puedes hacerlo lógica o física)
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
