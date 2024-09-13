package com.fiuni.mytube_videos.service.viewinghistory;

import com.fiuni.mytube.domain.viewinghistory.ViewingHistoryDomain;
import com.fiuni.mytube.dto.viewinghistory.ViewingHistoryDTO;
import com.fiuni.mytube.dto.viewinghistory.ViewingHistoryResult;
import com.fiuni.mytube_videos.dao.user.IUserDao;
import com.fiuni.mytube_videos.dao.video.IVideoDao;
import com.fiuni.mytube_videos.dao.viewinghistory.IViewingHistoryDao;
import com.fiuni.mytube_videos.service.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ViewingHistoryServiceImpl extends BaseServiceImpl<ViewingHistoryDTO, ViewingHistoryDomain, ViewingHistoryResult> implements IViewingHistoryService {

    @Autowired
    private IViewingHistoryDao viewingHistoryDao;

    @Autowired
    private IVideoDao videoDao;

    @Autowired
    private IUserDao userDao;

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
        domain.setUser(userDao.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserId())));
        domain.setVideo(videoDao.findByIdAndDeletedFalse(dto.getVideoId()).orElseThrow(() -> new RuntimeException("Video not found with ID: " + dto.getVideoId())));
        domain.setViewDate(new Date());
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

    @Override
    public void delete(Integer id) {
        ViewingHistoryDomain domain = viewingHistoryDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Viewing history not found with ID: " + id));
        viewingHistoryDao.delete(domain);
    }

    @Override
    public ViewingHistoryResult getByUser(Integer userId) {
        List<ViewingHistoryDomain> domains = viewingHistoryDao.findByUserId(userId);
        ViewingHistoryResult result = new ViewingHistoryResult();
        result.setViewingHistories(convertDomainListToDtoList(domains));
        return result;
    }
}
