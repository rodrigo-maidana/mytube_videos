package com.fiuni.mytube_videos.service.video;

import com.fiuni.mytube.domain.video.VideoDomain;
import com.fiuni.mytube.domain.video.VideoVisibility;
import com.fiuni.mytube.dto.video.VideoDTO;
import com.fiuni.mytube.dto.video.VideoResult;
import com.fiuni.mytube_videos.dao.channel.IChannelDao;
import com.fiuni.mytube_videos.dao.user.IUserDao;
import com.fiuni.mytube_videos.dao.video.IVideoDao;
import com.fiuni.mytube_videos.exception.BadRequestException;
import com.fiuni.mytube_videos.exception.ResourceNotFoundException;
import com.fiuni.mytube_videos.service.base.BaseServiceImpl;
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
        if(dto.get_id() == null) {
            return convertDtoToDomainWithoutId(dto);
        }
        VideoDomain domain = videoDao.findById(dto.get_id()).orElse(null);

        if(domain == null) {
            domain = new VideoDomain();
            domain.setUser(userDao.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + dto.getUserId() + " no encontrado")));
            domain.setChannel(channelDao.findById(dto.getChannelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Canal con id " + dto.getChannelId() + " no encontrado")));

        }else {
            if(!domain.getUser().getId().equals(dto.getUserId())) {
                throw new BadRequestException("No puedes cambiar el usuario de un video");
            }

            if(!domain.getChannel().getId().equals(dto.getChannelId())) {
                throw new BadRequestException("No puedes cambiar el canal de un video");
            }
        }

        domain.setTitle(dto.getTitle());
        domain.setDescription(dto.getDescription());
        domain.setVideoUrl(dto.getVideoUrl());
        domain.setThumbnailUrl(dto.getThumbnailUrl());
        domain.setFormat(dto.getFormat());
        domain.setTags(dto.getTags());
        domain.setUploadDate(dto.getUploadDate());
        domain.setVisibility(VideoVisibility.valueOf(dto.getVisibility()));
        domain.setDuration(dto.getDuration());
        domain.setDeleted(domain.getDeleted());
        return domain;
    }


    // Guardar video
    @Override
    @Transactional
    @CachePut(value = "mytube_videos", key = "'video_' + #dto._id")
    public VideoDTO save(VideoDTO dto) {
        VideoDomain domain = convertDtoToDomain(dto);
        VideoDomain savedDomain = videoDao.save(domain);
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

    @Override
    public VideoResult getAll() {
        return null;
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

    // Verificar que el video existe
    public void checkVideoExists(Integer id) {
        videoDao.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video con id " + id + " no encontrado"));
    }
}
