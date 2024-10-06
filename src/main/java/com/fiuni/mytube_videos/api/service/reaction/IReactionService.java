package com.fiuni.mytube_videos.api.service.reaction;

import com.fiuni.mytube.dto.reaction.ReactionDTO;
import com.fiuni.mytube.dto.reaction.ReactionResult;
import com.fiuni.mytube_videos.api.dto.reaction.ReactionSummaryDTO;
import com.fiuni.mytube_videos.api.service.base.IBaseService;

public interface IReactionService extends IBaseService<ReactionDTO, ReactionResult> {

    // Metodo para obtener todas las reacciones de un video
    ReactionSummaryDTO getSummaryByVideoId(Integer videoId);
}
