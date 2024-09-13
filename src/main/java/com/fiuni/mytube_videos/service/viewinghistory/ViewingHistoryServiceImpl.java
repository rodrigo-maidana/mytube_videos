package com.fiuni.mytube_videos.service.viewinghistory;

import com.fiuni.mytube.domain.viewinghistory.ViewingHistoryDomain;
import com.fiuni.mytube.dto.viewinghistory.ViewingHistoryDTO;
import com.fiuni.mytube.dto.viewinghistory.ViewingHistoryResult;
import com.fiuni.mytube_videos.dao.user.IUserDao;
import com.fiuni.mytube_videos.dao.video.IVideoDao;
import com.fiuni.mytube_videos.dao.viewinghistory.IViewingHistoryDao;
import com.fiuni.mytube_videos.exception.ResourceNotFoundException;
import com.fiuni.mytube_videos.service.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        // Asignar UserDomain y VideoDomain
        domain.setUser(userDao.findById(dto.getUserId()).orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + dto.getUserId() + " no encontrado")));
        domain.setVideo(videoDao.findByIdAndDeletedFalse(dto.getVideoId()).orElseThrow(() -> new ResourceNotFoundException("Video con id " + dto.getVideoId() + " no encontrado")));
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
                .orElseThrow(() -> new ResourceNotFoundException("Historial de visualización con id " + id + " no encontrado"));
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
                .orElseThrow(() -> new ResourceNotFoundException("Historial de visualización con id " + id + " no encontrado"));
        viewingHistoryDao.delete(domain);
    }

    @Override
    public ViewingHistoryResult getByUser(Pageable pageable, Integer userId) {
        Page<ViewingHistoryDomain> pageResult = viewingHistoryDao.findByUserId(pageable, userId);
        List<ViewingHistoryDomain> domains = pageResult.getContent();
        ViewingHistoryResult result = new ViewingHistoryResult();
        result.setViewingHistories(convertDomainListToDtoList(domains));
        return result;
    }


    @Override
    public ViewingHistoryResult getAll(Pageable pageable) {
        return null;
    }
}
