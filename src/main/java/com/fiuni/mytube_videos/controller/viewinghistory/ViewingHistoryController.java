package com.fiuni.mytube_videos.controller.viewinghistory;

import com.fiuni.mytube.dto.viewinghistory.ViewingHistoryDTO;
import com.fiuni.mytube.dto.viewinghistory.ViewingHistoryResult;
import com.fiuni.mytube_videos.service.viewinghistory.IViewingHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/viewing-history")
public class ViewingHistoryController {

    @Autowired
    private IViewingHistoryService viewingHistoryService;

    // Crear un nuevo registro de historial de visualización
    @PostMapping
    public ResponseEntity<ViewingHistoryDTO> createViewingHistory(@RequestBody ViewingHistoryDTO viewingHistoryDTO) {
        ViewingHistoryDTO savedViewingHistory = viewingHistoryService.save(viewingHistoryDTO);
        return new ResponseEntity<>(savedViewingHistory, HttpStatus.CREATED);
    }

    // Obtener un historial de visualización por ID
    @GetMapping("/{id}")
    public ResponseEntity<ViewingHistoryDTO> getViewingHistoryById(@PathVariable Integer id) {
        ViewingHistoryDTO viewingHistory = viewingHistoryService.getById(id);
        return new ResponseEntity<>(viewingHistory, HttpStatus.OK);
    }

    // Obtener todos los historiales de visualización
    @GetMapping
    public ResponseEntity<List<ViewingHistoryDTO>> getAllViewingHistories() {
        ViewingHistoryResult result = viewingHistoryService.getAll();
        return new ResponseEntity<>(result.getViewingHistories(), HttpStatus.OK);
    }

    // Actualizar un historial de visualización
    @PutMapping("/{id}")
    public ResponseEntity<ViewingHistoryDTO> updateViewingHistory(@PathVariable Integer id, @RequestBody ViewingHistoryDTO viewingHistoryDTO) {
        ViewingHistoryDTO existingViewingHistory = viewingHistoryService.getById(id);
        if (existingViewingHistory == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        viewingHistoryDTO.set_id(id);
        ViewingHistoryDTO updatedViewingHistory = viewingHistoryService.save(viewingHistoryDTO);
        return new ResponseEntity<>(updatedViewingHistory, HttpStatus.OK);
    }

    // Eliminar un historial de visualización
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteViewingHistory(@PathVariable Integer id) {
        ViewingHistoryDTO existingViewingHistory = viewingHistoryService.getById(id);
        if (existingViewingHistory == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Implementa la eliminación si es necesario (puede ser lógica o física)
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
