package com.fiuni.mytube_videos.controller.video;

import com.fiuni.mytube.dto.video.VideoDTO;
import com.fiuni.mytube.dto.video.VideoResult;
import com.fiuni.mytube_videos.service.video.IVideoService;
import com.fiuni.mytube_videos.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/videos")
public class VideoController {

    @Autowired
    private IVideoService videoService;

    // Guardar un video
    @PostMapping
    public ResponseEntity<VideoDTO> createVideo(@RequestBody VideoDTO videoDTO) {
        VideoDTO savedVideo = videoService.save(videoDTO);
        return new ResponseEntity<>(savedVideo, HttpStatus.CREATED);
    }

    // Obtener un video por ID
    @GetMapping("/{id}")
    public ResponseEntity<VideoDTO> getVideoById(@PathVariable Integer id) {
        VideoDTO video = videoService.getById(id);
        if (video == null) {
            throw new ResourceNotFoundException("Video con id " + id + " no encontrado");
        }
        return new ResponseEntity<>(video, HttpStatus.OK);
    }

    // Obtener todos los videos
    @GetMapping
    public ResponseEntity<List<VideoDTO>> getAllVideos(Pageable pageable) {
        VideoResult result = videoService.getAll(pageable);
        return new ResponseEntity<>(result.getVideos(), HttpStatus.OK);
    }

    // Actualizar un video
    @PutMapping("/{id}")
    public ResponseEntity<VideoDTO> updateVideo(@PathVariable Integer id, @RequestBody VideoDTO videoDTO) {
        VideoDTO existingVideo = videoService.getById(id);
        if (existingVideo == null) {
            throw new ResourceNotFoundException("Video con id " + id + " no encontrado");
        }
        videoDTO.set_id(id);
        VideoDTO updatedVideo = videoService.save(videoDTO);
        return new ResponseEntity<>(updatedVideo, HttpStatus.OK);
    }

    // Eliminar un video
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Integer id) {
        VideoDTO existingVideo = videoService.getById(id);
        if (existingVideo == null) {
            throw new ResourceNotFoundException("Video con id " + id + " no encontrado");
        }
        videoService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
