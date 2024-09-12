package com.fiuni.mytube_videos.service.reaction;

import com.fiuni.mytube.domain.reaction.ReactionDomain;
import com.fiuni.mytube.domain.reaction.ReactionType;
import com.fiuni.mytube.dto.reaction.ReactionDTO;
import com.fiuni.mytube.dto.reaction.ReactionResult;
import com.fiuni.mytube_videos.dao.reaction.IReactionDao;
import com.fiuni.mytube_videos.service.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReactionServiceImpl extends BaseServiceImpl<ReactionDTO, ReactionDomain, ReactionResult> implements IReactionService {

    @Autowired
    private IReactionDao reactionDao;

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
        // domain.setUser(user);
        // domain.setVideo(video);

        domain.setReactionType(ReactionType.valueOf(dto.getReactionType()));
        domain.setReactionDate(dto.getReactionDate());
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
                .orElseThrow(() -> new RuntimeException("Reaction not found with ID: " + id));
        return convertDomainToDto(domain);
    }

    @Override
    public ReactionResult getAll() {
        List<ReactionDomain> domains = reactionDao.findAll();
        ReactionResult result = new ReactionResult();
        result.setReactions(convertDomainListToDtoList(domains));
        return result;
    }
}
