package com.fiuni.mytube_videos.service.viewinghistory;

import com.fiuni.mytube.domain.viewinghistory.ViewingHistoryDomain;
import com.fiuni.mytube.dto.viewinghistory.ViewingHistoryDTO;
import com.fiuni.mytube.dto.viewinghistory.ViewingHistoryResult;
import com.fiuni.mytube_videos.dao.user.IUserDao;
import com.fiuni.mytube_videos.dao.video.IVideoDao;
import com.fiuni.mytube_videos.dao.viewinghistory.IViewingHistoryDao;
import com.fiuni.mytube_videos.exception.BadRequestException;
import com.fiuni.mytube_videos.exception.ResourceNotFoundException;
import com.fiuni.mytube_videos.service.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private RedisCacheManager cacheManager;

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
        return domain;
    }

    // Crear nuevo historial de visualización
    @Override
    @Transactional
    @CachePut(value = "mytube_viewinghistory", key = "'viewinghistory_' + #result._id")
    public ViewingHistoryDTO create(ViewingHistoryDTO dto) {
        ViewingHistoryDomain domain = convertDtoToDomain(dto);

        // Asignar usuario y video
        domain.setUser(userDao.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + dto.getUserId() + " no encontrado")));
        domain.setVideo(videoDao.findById(dto.getVideoId())
                .orElseThrow(() -> new ResourceNotFoundException("Video con id " + dto.getVideoId() + " no encontrado")));

        // Asignar la fecha actual
        domain.setViewDate(new Date());

        // Guardar el historial de visualización
        ViewingHistoryDomain savedDomain = viewingHistoryDao.save(domain);

        // Retornar el DTO del historial de visualización creado
        return convertDomainToDto(savedDomain);
    }

    // Modificar historial de visualización existente
    @Override
    @Transactional
    @CachePut(value = "mytube_viewinghistory", key = "'viewinghistory_' + #dto._id")
    public ViewingHistoryDTO update(ViewingHistoryDTO dto) {
        // Buscar el historial de visualización en la base de datos
        ViewingHistoryDomain domain = viewingHistoryDao.findById(dto.get_id())
                .orElseThrow(() -> new ResourceNotFoundException("Historial de visualización con id " + dto.get_id() + " no encontrado"));

        // Verificar que el usuario y el video no cambien
        if (!domain.getUser().getId().equals(dto.getUserId())) {
            throw new BadRequestException("No puedes cambiar el usuario del historial de visualización");
        }

        if (!domain.getVideo().getId().equals(dto.getVideoId())) {
            throw new BadRequestException("No puedes cambiar el video del historial de visualización");
        }

        // Actualizar el historial con los valores del DTO
        ViewingHistoryDomain updatedDomain = convertDtoToDomain(dto);

        // Mantener el usuario y el video actuales
        updatedDomain.setUser(domain.getUser());
        updatedDomain.setVideo(domain.getVideo());

        // Guardar los cambios en la base de datos
        ViewingHistoryDomain savedDomain = viewingHistoryDao.save(updatedDomain);

        // Retornar el DTO del historial de visualización actualizado
        return convertDomainToDto(savedDomain);
    }


    // Obtener historial de visualización por ID con caché
    @Override
    @Cacheable(value = "mytube_viewinghistory", key = "'viewinghistory_' + #id")
    public ViewingHistoryDTO getById(Integer id) {
        ViewingHistoryDomain domain = viewingHistoryDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Historial de visualización con id " + id + " no encontrado"));
        return convertDomainToDto(domain);
    }

    // Obtener todos los historiales de visualización con paginación y cachear cada uno individualmente
    @Override
    @Transactional(readOnly = true)
    public ViewingHistoryResult getAll(Pageable pageable) {
        Page<ViewingHistoryDomain> page = viewingHistoryDao.findAll(pageable); // Ajuste para paginación
        ViewingHistoryResult result = new ViewingHistoryResult();

        // Convertir la lista de ViewingHistoryDomain a ViewingHistoryDTO
        List<ViewingHistoryDTO> viewingHistoryList = convertDomainListToDtoList(page.getContent());

        // Cachear cada historial de visualización individualmente
        for (ViewingHistoryDTO history : viewingHistoryList) {
            cacheManager.getCache("mytube_viewinghistory").put("viewinghistory_" + history.get_id(), history);
        }

        // Establecer la lista de historiales de visualización en el resultado
        result.setViewingHistories(viewingHistoryList);

        return result;
    }

    // Eliminar historial de visualización y evict del caché
    @Override
    @Transactional
    @CacheEvict(value = "mytube_viewinghistory", key = "'viewinghistory_' + #id")
    public void delete(Integer id) {
        ViewingHistoryDomain domain = viewingHistoryDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Historial de visualización con id " + id + " no encontrado"));
        viewingHistoryDao.delete(domain);
    }

    // Obtener historial de visualización por usuario con paginación
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "mytube_viewinghistory", key = "'user_viewinghistory_' + #userId")
    public ViewingHistoryResult getByUser(Pageable pageable, Integer userId) {
        Page<ViewingHistoryDomain> pageResult = viewingHistoryDao.findByUserId(pageable, userId);
        List<ViewingHistoryDomain> domains = pageResult.getContent();
        ViewingHistoryResult result = new ViewingHistoryResult();
        result.setViewingHistories(convertDomainListToDtoList(domains));
        return result;
    }

    // TODO BORRAR METODO
    @Override
    public ViewingHistoryResult getAll() {
        return null;
    }
}
