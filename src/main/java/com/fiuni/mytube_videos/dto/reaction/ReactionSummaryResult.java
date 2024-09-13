package com.fiuni.mytube_videos.dto.reaction;

import com.fiuni.mytube.dto.base.BaseResult;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReactionSummaryResult extends BaseResult<ReactionSummaryDTO> {

    private static final long serialVersionUID = 1L;

    public List<ReactionSummaryDTO> getSummaries() {
        return get_dtos();
    }

    public void setSummaries(List<ReactionSummaryDTO> dtos) {
        set_dtos(dtos);
    }
}
