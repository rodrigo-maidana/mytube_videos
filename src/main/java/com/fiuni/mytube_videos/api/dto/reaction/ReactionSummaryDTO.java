package com.fiuni.mytube_videos.api.dto.reaction;

import com.fiuni.mytube.dto.base.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReactionSummaryDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    private int likes;
    private int dislikes;
}
