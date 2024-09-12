package com.fiuni.mytube_videos.service.video;

import com.fiuni.mytube.domain.video.VideoDomain;
import com.fiuni.mytube.domain.video.VideoVisibility;
import com.fiuni.mytube.dto.video.VideoDTO;
import com.fiuni.mytube.dto.video.VideoResult;
import com.fiuni.mytube_videos.dao.video.IVideoDao;
import com.fiuni.mytube_videos.service.base.BaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoServiceImpl extends BaseServiceImpl<VideoDTO, VideoDomain, VideoResult> implements IVideoService {

    @Autowired
    private IVideoDao videoDao;

    private static final Logger logger = LoggerFactory.getLogger(VideoServiceImpl.class);

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
        // Omitiendo el campo deleted
        return dto;
    }

    @Override
    protected VideoDomain convertDtoToDomain(VideoDTO dto) {
        VideoDomain domain = new VideoDomain();
        domain.setId(dto.get_id());

        // TODO: Asignar el UserDomain y ChannelDomain cuando los servicios estén disponibles
        // domain.setUser(user);
        // domain.setChannel(channel);

        domain.setTitle(dto.getTitle());
        domain.setDescription(dto.getDescription());
        domain.setVideoUrl(dto.getVideoUrl());
        domain.setThumbnailUrl(dto.getThumbnailUrl());
        domain.setFormat(dto.getFormat());
        domain.setTags(dto.getTags());
        domain.setUploadDate(dto.getUploadDate());
        domain.setVisibility(VideoVisibility.valueOf(dto.getVisibility()));
        domain.setDuration(dto.getDuration());
        domain.setDeleted(false); // Asignar por defecto false o según tu lógica
        return domain;
    }

    @Override
    public VideoDTO save(VideoDTO dto) {
        VideoDomain domain = convertDtoToDomain(dto); // Convierte DTO a Domain
        VideoDomain savedDomain = videoDao.save(domain); // Guarda el Domain en la base de datos
        return convertDomainToDto(savedDomain); // Convierte el Domain guardado a DTO y lo retorna
    }

    @Override
    public VideoDTO getById(Integer id) {
        VideoDomain domain = videoDao.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Video not found with ID: " + id)); // Maneja cuando no se encuentra el video
        return convertDomainToDto(domain); // Convierte y retorna el Domain a DTO
    }

    @Override
    public VideoResult getAll() {
        List<VideoDomain> domains = videoDao.findAll(); // Obtiene todos los videos de la base de datos
        VideoResult result = new VideoResult();
        result.setVideos(convertDomainListToDtoList(domains)); // Convierte la lista de Domain a DTO y la asigna al resultado
        logger.info("Lista de Videos retornada correctamente");
        return result;
    }
}
