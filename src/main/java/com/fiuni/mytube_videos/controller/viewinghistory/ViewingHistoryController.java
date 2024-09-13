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

    // Guardar un nuevo historial de visualización
    @PostMapping
    public ResponseEntity<ViewingHistoryDTO> createViewingHistory(@RequestBody ViewingHistoryDTO viewingHistoryDTO) {
        ViewingHistoryDTO savedHistory = viewingHistoryService.save(viewingHistoryDTO);
        return new ResponseEntity<>(savedHistory, HttpStatus.CREATED);
    }

    // Obtener un historial de visualización por ID
    @GetMapping("/{id}")
    public ResponseEntity<ViewingHistoryDTO> getViewingHistoryById(@PathVariable Integer id) {
        ViewingHistoryDTO history = viewingHistoryService.getById(id);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    // Obtener todo el historial de visualizaciones
    @GetMapping
    public ResponseEntity<List<ViewingHistoryDTO>> getAllViewingHistory() {
        ViewingHistoryResult result = viewingHistoryService.getAll();
        return new ResponseEntity<>(result.getViewingHistories(), HttpStatus.OK);
    }

    // Actualizar un historial de visualización
    @PutMapping("/{id}")
    public ResponseEntity<ViewingHistoryDTO> updateViewingHistory(@PathVariable Integer id, @RequestBody ViewingHistoryDTO viewingHistoryDTO) {
        ViewingHistoryDTO existingHistory = viewingHistoryService.getById(id);
        if (existingHistory == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        viewingHistoryDTO.set_id(id);
        ViewingHistoryDTO updatedHistory = viewingHistoryService.save(viewingHistoryDTO);
        return new ResponseEntity<>(updatedHistory, HttpStatus.OK);
    }

    // Eliminar un historial de visualización
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteViewingHistory(@PathVariable Integer id) {
        ViewingHistoryDTO existingHistory = viewingHistoryService.getById(id);
        if (existingHistory == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        viewingHistoryService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Obtener el historial de visualización de un usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ViewingHistoryDTO>> getViewingHistoryByUser(@PathVariable Integer userId) {
        ViewingHistoryResult result = viewingHistoryService.getByUser(userId);
        return new ResponseEntity<>(result.getViewingHistories(), HttpStatus.OK);
    }
}
