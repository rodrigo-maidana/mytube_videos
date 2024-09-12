package com.fiuni.mytube_videos.service.viewinghistory;

import com.fiuni.mytube.domain.viewinghistory.ViewingHistoryDomain;
import com.fiuni.mytube.dto.viewinghistory.ViewingHistoryDTO;
import com.fiuni.mytube.dto.viewinghistory.ViewingHistoryResult;
import com.fiuni.mytube_videos.dao.viewinghistory.IViewingHistoryDao;
import com.fiuni.mytube_videos.service.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViewingHistoryServiceImpl extends BaseServiceImpl<ViewingHistoryDTO, ViewingHistoryDomain, ViewingHistoryResult> implements IViewingHistoryService {

    @Autowired
    private IViewingHistoryDao viewingHistoryDao;

    @Override
    protected ViewingHistoryDTO convertDomainToDto(ViewingHistoryDomain domain) {
        ViewingHistoryDTO dto = new ViewingHistoryDTO();
        dto.set_id(domain.getId());
        dto.setUserId(domain.getUser().getId());
        dto.setVideoId(domain.getVideo().getId());
        dto.setViewDate(domain.getViewDate());
        return dto;
    }

    @Override
    protected ViewingHistoryDomain convertDtoToDomain(ViewingHistoryDTO dto) {
        ViewingHistoryDomain domain = new ViewingHistoryDomain();
        domain.setId(dto.get_id());

        // TODO: Asignar UserDomain y VideoDomain cuando los servicios estén disponibles
        // domain.setUser(user);
        // domain.setVideo(video);

        domain.setViewDate(dto.getViewDate());
        return domain;
    }

    @Override
    public ViewingHistoryDTO save(ViewingHistoryDTO dto) {
        ViewingHistoryDomain domain = convertDtoToDomain(dto);
        ViewingHistoryDomain savedDomain = viewingHistoryDao.save(domain);
        return convertDomainToDto(savedDomain);
    }

    @Override
    public ViewingHistoryDTO getById(Integer id) {
        ViewingHistoryDomain domain = viewingHistoryDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Viewing history not found with ID: " + id));
        return convertDomainToDto(domain);
    }

    @Override
    public ViewingHistoryResult getAll() {
        List<ViewingHistoryDomain> domains = viewingHistoryDao.findAll();
        ViewingHistoryResult result = new ViewingHistoryResult();
        result.setViewingHistories(convertDomainListToDtoList(domains));
        return result;
    }
}
