package com.fiuni.mytube_videos.service.reaction;

import com.fiuni.mytube.domain.reaction.ReactionDomain;
import com.fiuni.mytube.domain.reaction.ReactionType;
import com.fiuni.mytube.domain.video.VideoDomain;
import com.fiuni.mytube.dto.reaction.ReactionDTO;
import com.fiuni.mytube.dto.reaction.ReactionResult;
import com.fiuni.mytube_videos.dao.reaction.IReactionDao;
import com.fiuni.mytube_videos.dao.user.IUserDao;
import com.fiuni.mytube_videos.dao.video.IVideoDao;
import com.fiuni.mytube_videos.dto.reaction.ReactionSummaryDTO;
import com.fiuni.mytube_videos.exception.ResourceNotFoundException;
import com.fiuni.mytube_videos.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReactionServiceImpl extends BaseServiceImpl<ReactionDTO, ReactionDomain, ReactionResult> implements IReactionService {

    @Autowired
    private IReactionDao reactionDao;

    @Autowired
    private IUserDao userDao;

    @Autowired
    private IVideoDao videoDao;

    @Override
    protected ReactionDTO convertDomainToDto(ReactionDomain domain) {
        ReactionDTO dto = new ReactionDTO();
        dto.set_id(domain.getId());
        dto.setUserId(domain.getUser().getId());
        dto.setVideoId(domain.getVideo().getId());
        dto.setReactionType(domain.getReactionType().name());
        dto.setReactionDate(domain.getReactionDate());
        return dto;
    }

    @Override
    protected ReactionDomain convertDtoToDomain(ReactionDTO dto) {
        ReactionDomain domain = new ReactionDomain();
        domain.setId(dto.get_id());

        // TODO: Asignar UserDomain y VideoDomain cuando los servicios estén disponibles
        domain.setUser(userDao.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + dto.getUserId() + " no encontrado")));
        domain.setVideo(videoDao.findById(dto.getVideoId())
                .orElseThrow(() -> new ResourceNotFoundException("Video con id " + dto.getVideoId() + " no encontrado")));
        // domain.setUser(user);
        // domain.setVideo(video);

        domain.setReactionType(ReactionType.valueOf(dto.getReactionType()));
        domain.setReactionDate(new Date());
        return domain;
    }

    @Override
    public ReactionDTO save(ReactionDTO dto) {
        ReactionDomain domain = convertDtoToDomain(dto);
        ReactionDomain savedDomain = reactionDao.save(domain);
        return convertDomainToDto(savedDomain);
    }

    @Override
    public ReactionDTO getById(Integer id) {
        ReactionDomain domain = reactionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reacción con id " + id + " no encontrado"));
        return convertDomainToDto(domain);
    }

    @Override
    public ReactionResult getAll(Pageable pageable) {
        Page<ReactionDomain> pageResult = reactionDao.findAll(pageable);
        List<ReactionDomain> domains = pageResult.getContent();
        ReactionResult result = new ReactionResult();
        result.setReactions(convertDomainListToDtoList(domains));
        return result;
    }

    @Override
    public ReactionSummaryDTO getSummaryByVideoId(Integer videoId) {
        // Verificar si el video existe
        VideoDomain video = videoDao.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video con id " + videoId + " no encontrado"));

        // Obtener el resumen de reacciones por video
        Map<String, Long> result = reactionDao.getReactionSummaryByVideoId(video.getId());

        ReactionSummaryDTO summary = new ReactionSummaryDTO();
        log.debug("Reactions summary: {}", result);
        summary.setLikes(result.getOrDefault("likes", 0L).intValue());
        log.debug("Likes: {}", summary.getLikes());
        summary.setDislikes(result.getOrDefault("dislikes", 0L).intValue());
        log.debug("Dislikes: {}", summary.getDislikes());

        return summary;
    }



    @Override
    public ReactionResult getAll() {
        return null;
    }

    @Override
    public void delete(Integer id) {
        ReactionDomain domain = reactionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reacción con id " + id + " no encontrado"));
        reactionDao.delete(domain);
    }

}
