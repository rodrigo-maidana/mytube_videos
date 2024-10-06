package com.fiuni.mytube_videos.api.service.reaction;

import com.fiuni.mytube.domain.reaction.ReactionDomain;
import com.fiuni.mytube.domain.reaction.ReactionType;
import com.fiuni.mytube.dto.reaction.ReactionDTO;
import com.fiuni.mytube.dto.reaction.ReactionResult;
import com.fiuni.mytube_videos.api.dao.reaction.IReactionDao;
import com.fiuni.mytube_videos.api.dao.user.IUserDao;
import com.fiuni.mytube_videos.api.dao.video.IVideoDao;
import com.fiuni.mytube_videos.api.dto.reaction.ReactionSummaryDTO;
import com.fiuni.mytube_videos.exception.ResourceNotFoundException;
import com.fiuni.mytube_videos.api.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private RedisCacheManager cacheManager;

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
        // Obtener usuario ID
        domain.setUser(userDao.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + dto.getUserId() + " no encontrado")));
        // Obtener video ID
        domain.setVideo(videoDao.findById(dto.getVideoId())
                .orElseThrow(() -> new ResourceNotFoundException("Video con id " + dto.getVideoId() + " no encontrado")));
        domain.setReactionType(ReactionType.valueOf(dto.getReactionType()));
        domain.setReactionDate(dto.getReactionDate() != null ? dto.getReactionDate() : null);
        return domain;
    }

    // Crear una reacción
    @Override
    @Transactional
    @CacheEvict(value = "mytube_reaction", key = "'reaction_summary_' + #domain.getVideo().getId()")
    public ReactionDTO create(ReactionDTO dto) {
        ReactionDomain domain = convertDtoToDomain(dto);

        // Asignar la fecha actual
        domain.setReactionDate(new Date());

        ReactionDomain savedDomain = reactionDao.save(domain);
        return convertDomainToDto(savedDomain);
    }

    // Actualizar una reacción existente
    @Override
    @Transactional
    public ReactionDTO update(ReactionDTO dto) {
        ReactionDomain domain = reactionDao.findById(dto.get_id())
                .orElseThrow(() -> new ResourceNotFoundException("Reacción con id " + dto.get_id() + " no encontrada"));
        ReactionDomain updatedDomain = convertDtoToDomain(dto);

        // Mantener usuario y video originales
        updatedDomain.setUser(domain.getUser());
        updatedDomain.setVideo(domain.getVideo());

        ReactionDomain savedDomain = reactionDao.save(updatedDomain);
        return convertDomainToDto(savedDomain);
    }

    // Obtener una reacción por ID
    @Override
    @Transactional(readOnly = true)
    public ReactionDTO getById(Integer id) {
        ReactionDomain domain = reactionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reacción con id " + id + " no encontrada"));
        return convertDomainToDto(domain);
    }

    // Obtener todas las reacciones con paginación y cachear cada una individualmente
    @Override
    @Transactional(readOnly = true)
    public ReactionResult getAll(Pageable pageable) {
        Page<ReactionDomain> page = reactionDao.findAll(pageable);
        ReactionResult result = new ReactionResult();

        // Convertir la lista de ReactionDomain a ReactionDTO
        List<ReactionDTO> reactionList = convertDomainListToDtoList(page.getContent());

        // Cachear cada reacción individualmente
        for (ReactionDTO reaction : reactionList) {
            cacheManager.getCache("mytube_reaction").put("reaction_" + reaction.get_id(), reaction);
        }

        // Establecer la lista de reacciones en el resultado
        result.setReactions(reactionList);
        return result;
    }

    // Obtener un resumen de las reacciones por ID de video
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "mytube_reaction", key = "'reaction_summary_' + #videoId")
    public ReactionSummaryDTO getSummaryByVideoId(Integer videoId) {
        // Verificar que el video exista
        videoDao.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video con id " + videoId + " no encontrado"));

        // Obtener el resumen de reacciones del DAO
        Map<String, Long> reactionSummary = reactionDao.getReactionSummaryByVideoId(videoId);

        // Crear el DTO de resumen de reacciones
        ReactionSummaryDTO summary = new ReactionSummaryDTO();
        summary.setLikes(reactionSummary.getOrDefault("likes", 0L).intValue());   // Usar 0L para Long
        summary.setDislikes(reactionSummary.getOrDefault("dislikes", 0L).intValue());  // Usar 0L para Long

        return summary;
    }


    // Eliminar una reacción y remover del caché
    @Override
    @Transactional
    @CacheEvict(value = "mytube_reaction", key = "'reaction_sumary' + #domain.getVideo().getId()")
    public void delete(Integer id) {
        ReactionDomain domain = reactionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reacción con id " + id + " no encontrada"));
        reactionDao.delete(domain);
    }
}
