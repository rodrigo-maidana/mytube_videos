package com.fiuni.mytube_videos.service.video;

import com.fiuni.mytube.domain.video.VideoDomain;
import com.fiuni.mytube.domain.video.VideoVisibility;
import com.fiuni.mytube.dto.video.VideoDTO;
import com.fiuni.mytube.dto.video.VideoResult;
import com.fiuni.mytube_videos.dao.channel.IChannelDao;
import com.fiuni.mytube_videos.dao.user.IUserDao;
import com.fiuni.mytube_videos.dao.video.IVideoDao;
import com.fiuni.mytube_videos.service.base.BaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.annotation.*;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoServiceImpl extends BaseServiceImpl<VideoDTO, VideoDomain, VideoResult> implements IVideoService {

    @Autowired
    private IVideoDao videoDao;

   // @Autowired
    //RedisCacheManager cacheManager;
    private static final Logger logger = LoggerFactory.getLogger(VideoServiceImpl.class);

    //Dao temporal para asignar UserDomain
    @Autowired
    private IUserDao userDao;

    //Dao temporal para asignar ChannelDomain
    @Autowired
    private IChannelDao channelDao;

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
        // Omitir deleted
        return dto;
    }

    @Override
    protected VideoDomain convertDtoToDomain(VideoDTO dto) {
        VideoDomain domain = new VideoDomain();
        domain.setId(dto.get_id());

        // TODO: Asignar el UserDomain y ChannelDomain cuando los servicios estén disponibles
        // DAO temporal para asignar UserDomain
        domain.setUser(userDao.findById(dto.getUserId())
                .orElseThrow(
                        () -> new RuntimeException("User not found with ID: " + dto.getUserId()))); // Maneja cuando no se encuentra el usuario
        // DAO temporal para asignar ChannelDomain
        domain.setChannel(channelDao.findById(dto.getChannelId())
                .orElseThrow(
                        () -> new RuntimeException("Channel not found with ID: " + dto.getChannelId()))); // Maneja cuando no se encuentra el canal

        domain.setTitle(dto.getTitle());
        domain.setDescription(dto.getDescription());
        domain.setVideoUrl(dto.getVideoUrl());
        domain.setThumbnailUrl(dto.getThumbnailUrl());
        domain.setFormat(dto.getFormat());
        domain.setTags(dto.getTags());
        domain.setUploadDate(dto.getUploadDate());
        domain.setVisibility(VideoVisibility.valueOf(dto.getVisibility()));
        domain.setDuration(dto.getDuration());
        domain.setDeleted(false); // Asignar por defecto false
        return domain;
    }

    // Guardar video
    @Override
    //@CachePut(value = "mytube_videos", key = "'video_' + #dto._id")
    public VideoDTO save(VideoDTO dto) {
        VideoDomain domain = convertDtoToDomain(dto); // Convierte DTO a Domain
        VideoDomain savedDomain = videoDao.save(domain); // Guarda el Domain en la base de datos
        return convertDomainToDto(savedDomain); // Convierte el Domain guardado a DTO y lo retorna
    }

    // Obtener video por ID
    @Override
    //@Cacheable(value = "mytube_videos", key = "'video_' + #id")
    public VideoDTO getById(Integer id) {
        VideoDomain domain = videoDao.findByIdAndDeletedFalse(id)
                .orElseThrow(
                        () -> new RuntimeException("Video not found with ID: " + id)); // Maneja cuando no se encuentra el video
        return convertDomainToDto(domain); // Convierte y retorna el Domain a DTO
    }

    // Obtener todos los videos
    @Override
    public VideoResult getAll() {
        List<VideoDomain> domains = videoDao.findAllByDeletedFalse(); // Obtiene todos los videos que no estén eliminados
        VideoResult result = new VideoResult();
        List<VideoDTO> videoList = convertDomainListToDtoList(domains); // Convierte la lista de Domain a DTO
        //for( VideoDTO video:videoList){
        //    cacheManager.getCache("mytube_videos").put("video_" + video.get_id(), video);
        //}
        result.setVideos(videoList); // Convierte la lista de Domain a DTO y la asigna al resultado
        return result;
    }

    // Soft delete de video
    //@CacheEvict(value = "mytube_videos", key = "'video_' + #id")
    public void delete(Integer id) {
        VideoDomain domain = videoDao.findByIdAndDeletedFalse(id)
                .orElseThrow(
                        () -> new RuntimeException("Video not found with ID: " + id)); // Maneja cuando no se encuentra el video
        domain.setDeleted(true); // Asigna true al campo deleted
        videoDao.save(domain); // Guarda el Domain en la base de datos
    }
}
