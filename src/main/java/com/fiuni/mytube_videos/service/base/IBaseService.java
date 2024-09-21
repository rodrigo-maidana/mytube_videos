package com.fiuni.mytube_videos.service.base;

import com.fiuni.mytube.dto.base.BaseDTO;
import com.fiuni.mytube.dto.base.BaseResult;
import org.springframework.data.domain.Pageable;

public interface IBaseService<DTO extends BaseDTO, R extends BaseResult<DTO>> {
    public DTO getById(Integer id);

    public R getAll();

    public R getAll(Pageable pageable);

    void delete(Integer id);

    public abstract DTO create(DTO dto);

    public abstract DTO update(DTO dto);
}
