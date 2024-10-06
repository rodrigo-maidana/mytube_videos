package com.fiuni.mytube_videos.api.service.viewinghistory;

import com.fiuni.mytube.dto.viewinghistory.ViewingHistoryDTO;
import com.fiuni.mytube.dto.viewinghistory.ViewingHistoryResult;
import com.fiuni.mytube_videos.api.service.base.IBaseService;
import org.springframework.data.domain.Pageable;

public interface IViewingHistoryService extends IBaseService<ViewingHistoryDTO, ViewingHistoryResult> {
    ViewingHistoryResult getByUser(Pageable page, Integer userId);
    // Métodos adicionales si es necesario
}
