package com.fiuni.mytube_videos.service.base;

import com.fiuni.mytube.dto.base.BaseDTO;
import com.fiuni.mytube.dto.base.BaseResult;

public interface IBaseService<DTO extends BaseDTO, R extends BaseResult<DTO>> {
    public DTO save(DTO dto);

    public DTO getById(Integer id);

    public R getAll();

    void delete(Integer id);
}
