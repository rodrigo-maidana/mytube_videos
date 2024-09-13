package com.fiuni.mytube_videos.service.reaction;

import com.fiuni.mytube.dto.reaction.ReactionDTO;
import com.fiuni.mytube.dto.reaction.ReactionResult;
import com.fiuni.mytube_videos.dto.reaction.ReactionSummaryDTO;
import com.fiuni.mytube_videos.service.base.IBaseService;
import org.springframework.data.domain.Pageable;

public interface IReactionService extends IBaseService<ReactionDTO, ReactionResult> {
    ReactionResult getAll(Pageable pageable);

    ReactionSummaryDTO getSummaryByVideoId(Integer videoId);
}
