package com.fiuni.mytube_videos.api.service.video;

import com.fiuni.mytube.domain.video.VideoDomain;
import com.fiuni.mytube.domain.video.VideoVisibility;
import com.fiuni.mytube.dto.video.VideoDTO;
import com.fiuni.mytube.dto.video.VideoResult;
import com.fiuni.mytube_videos.api.dao.channel.IChannelDao;
import com.fiuni.mytube_videos.api.dao.user.IUserDao;
import com.fiuni.mytube_videos.api.dao.video.IVideoDao;
import com.fiuni.mytube_videos.exception.BadRequestException;
import com.fiuni.mytube_videos.exception.ResourceNotFoundException;
import com.fiuni.mytube_videos.api.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class VideoServiceImpl extends BaseServiceImpl<VideoDTO, VideoDomain, VideoResult> implements IVideoService {

    @Autowired
    private IVideoDao videoDao;

    @Autowired
    private IUserDao userDao;

    @Autowired
    private IChannelDao channelDao;

    @Autowired
    private RedisCacheManager cacheManager;

    @Override
    protected VideoDTO convertDomainToDto(VideoDomain domain) {
        VideoDTO dto = new VideoDTO();
        dto.set_id(domain.getId());
        dto.setUserId(domain.getUser().getId());
        dto.setChannelId(domain.getChannel().getId());
        dto.setTitle(domain.getTitle());
        dto.setDescription(domain.getDescription());
        dto.setVideoUrl(domain.getVideoUrl());
        dto.setThumbnailUrl(domain.getThumbnailUrl());
        dto.setFormat(domain.getFormat());
        dto.setTags(domain.getTags());
        dto.setUploadDate(domain.getUploadDate());
        dto.setVisibility(domain.getVisibility().name());
        dto.setDuration(domain.getDuration());
        return dto;
    }

    @Override
    protected VideoDomain convertDtoToDomain(VideoDTO dto) {
        VideoDomain domain = new VideoDomain();
        domain.setId(dto.get_id());
        domain.setTitle(dto.getTitle());
        domain.setDescription(dto.getDescription());
        domain.setVideoUrl(dto.getVideoUrl());
        domain.setThumbnailUrl(dto.getThumbnailUrl());
        domain.setFormat(dto.getFormat());
        domain.setTags(dto.getTags());

        // Asignar visibilidad (si es inválida, lanzará la excepción automáticamente)
        try {
            domain.setVisibility(VideoVisibility.valueOf(dto.getVisibility()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("La visibilidad del video es inválida");
        }

        domain.setDuration(dto.getDuration());

        return domain;
    }

    // Crear nuevo video
    @Transactional
    @Override
    @CachePut(value = "mytube_videos", key = "'video_' + #result._id")
    public VideoDTO create(VideoDTO dto) {
        VideoDomain domain = convertDtoToDomain(dto);

        // Asignar usuario y canal
        domain.setUser(userDao.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + dto.getUserId() + " no encontrado")));
        domain.setChannel(channelDao.findById(dto.getChannelId())
                .orElseThrow(() -> new ResourceNotFoundException("Canal con id " + dto.getChannelId() + " no encontrado")));

        //Asigmanos miniatura o la generamos si no existe
        domain.setThumbnailUrl(generateThumbnail(dto));

        // Asignar fecha de subida
        domain.setUploadDate(new Date());
        domain.setDeleted(false);

        // Guardar el video en la base de datos
        VideoDomain savedDomain = videoDao.save(domain);

        // Retornar el DTO del video creado
        return convertDomainToDto(savedDomain);
    }

    // Modificar video existente
    @Transactional
    @Override
    @CachePut(value = "mytube_videos", key = "'video_' + #dto.get_id()")
    public VideoDTO update(VideoDTO dto) {
        // Buscar el video en la base de datos
        VideoDomain domain = videoDao.findById(dto.get_id())
                .orElseThrow(() -> new ResourceNotFoundException("Video con id " + dto.get_id() + " no encontrado"));

        // Verificar que el usuario y el canal no cambien
        if (!domain.getUser().getId().equals(dto.getUserId())) {
            throw new BadRequestException("No puedes cambiar el usuario de un video");
        }

        if (!domain.getChannel().getId().equals(dto.getChannelId())) {
            throw new BadRequestException("No puedes cambiar el canal de un video");
        }

        // Actualizar el video con los valores del DTO
        VideoDomain updatedDomain = convertDtoToDomain(dto);

        //Asigmanos miniatura o la generamos si no existe
        updatedDomain.setThumbnailUrl(generateThumbnail(dto));

        // Mantener el usuario y el canal actuales
        updatedDomain.setUser(domain.getUser());
        updatedDomain.setChannel(domain.getChannel());
        updatedDomain.setDeleted(false);


        // Guardar los cambios en la base de datos
        VideoDomain savedDomain = videoDao.save(updatedDomain);

        // Retornar el DTO del video actualizado
        return convertDomainToDto(savedDomain);
    }

    // Obtener video por ID
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "mytube_videos", key = "'video_' + #id")
    public VideoDTO getById(Integer id) {
        VideoDomain domain = videoDao.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video con id " + id + " no encontrado"));
        return convertDomainToDto(domain);
    }

    // Obtener todos los videos con paginación
    @Override
    @Transactional(readOnly = true)
    public VideoResult getAll(Pageable pageable) {
        Page<VideoDomain> page = videoDao.findAllByDeletedFalse(pageable);
        VideoResult result = new VideoResult();

        // Convertir la lista de VideoDomain a VideoDTO
        List<VideoDTO> videoList = convertDomainListToDtoList(page.getContent());

        // Cachear cada video individualmente
        for (VideoDTO video : videoList) {
            cacheManager.getCache("mytube_videos").put("video_" + video.get_id(), video);
        }

        // Establecer la lista de videos en el resultado
        result.setVideos(videoList);

        return result;
    }

    // Soft delete de video
    @Override
    @Transactional
    @CacheEvict(value = "mytube_videos", key = "'video_' + #id")
    public void delete(Integer id) {
        VideoDomain domain = videoDao.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video con id " + id + " no encontrado"));
        domain.setDeleted(true);
        videoDao.save(domain);
    }

    public String generateThumbnail(VideoDTO videoDto) {
        // Verificar si el DTO no es nulo
        if (videoDto == null) {
            throw new BadRequestException("El VideoDTO es nulo");
        }

        // Si ya tiene un thumbnailUrl, devolver ese
        if (videoDto.getThumbnailUrl() != null && !videoDto.getThumbnailUrl().isEmpty()) {
            return videoDto.getThumbnailUrl();
        }

        // Si no tiene un thumbnail cargado, generar uno a partir del videoUrl
        String videoUrl = videoDto.getVideoUrl();

        // Verifica si la URL del video es válida y pertenece a YouTube
        if (videoUrl == null || !videoUrl.contains("youtube.com/watch?v=")) {
            throw new BadRequestException("La URL del video es inválida o no es un video de YouTube");
        }

        // Extraer la ID del video a partir de la URL
        String videoId = videoUrl.substring(videoUrl.indexOf("v=") + 2);

        // Crear la URL del thumbnail
        String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";

        // Actualizar el thumbnailUrl en el VideoDTO
        videoDto.setThumbnailUrl(thumbnailUrl);

        return thumbnailUrl;
    }


}
