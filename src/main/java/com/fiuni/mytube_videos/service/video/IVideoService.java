package com.fiuni.mytube_videos.service.video;

import com.fiuni.mytube.dto.video.VideoDTO;
import com.fiuni.mytube.dto.video.VideoResult;
import com.fiuni.mytube_videos.service.base.IBaseService;
import org.springframework.data.domain.Pageable;

public interface IVideoService extends IBaseService<VideoDTO, VideoResult> {
}
