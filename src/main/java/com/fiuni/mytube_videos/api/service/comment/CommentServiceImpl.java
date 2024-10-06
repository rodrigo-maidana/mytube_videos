package com.fiuni.mytube_videos.api.service.comment;

import com.fiuni.mytube.domain.comment.CommentDomain;
import com.fiuni.mytube.dto.comment.CommentDTO;
import com.fiuni.mytube.dto.comment.CommentResult;
import com.fiuni.mytube_videos.api.dao.comment.ICommentDao;
import com.fiuni.mytube_videos.api.dao.user.IUserDao;
import com.fiuni.mytube_videos.api.dao.video.IVideoDao;
import com.fiuni.mytube_videos.exception.ResourceNotFoundException;
import com.fiuni.mytube_videos.api.service.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl extends BaseServiceImpl<CommentDTO, CommentDomain, CommentResult> implements ICommentService {

    @Autowired
    private ICommentDao commentDao;

    @Autowired
    private IUserDao userDao;

    @Autowired
    private IVideoDao videoDao;

    @Autowired
    private RedisCacheManager cacheManager;

    @Override
    protected CommentDTO convertDomainToDto(CommentDomain domain) {
        CommentDTO dto = new CommentDTO();
        dto.set_id(domain.getId());
        dto.setUserId(domain.getUser().getId());
        dto.setVideoId(domain.getVideo().getId());
        dto.setParentCommentId(domain.getParentComment() != null ? domain.getParentComment().getId() : null);
        dto.setContent(domain.getContent());
        dto.setCommentDate(domain.getCommentDate());
        return dto;
    }

    @Override
    protected CommentDomain convertDtoToDomain(CommentDTO dto) {
        CommentDomain domain = new CommentDomain();
        domain.setId(dto.get_id());

        // Asignar el usuario y video cuando los servicios estén disponibles
        domain.setUser(userDao.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + dto.getUserId() + " no encontrado")));
        domain.setVideo(videoDao.findById(dto.getVideoId())
                .orElseThrow(() -> new ResourceNotFoundException("Video con id " + dto.getVideoId() + " no encontrado")));

        if (dto.getParentCommentId() != null) {
            domain.setParentComment(commentDao.findByIdAndDeletedFalse(dto.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comentario padre con id " + dto.getParentCommentId() + " no encontrado")));
        }

        domain.setContent(dto.getContent());
        return domain;
    }

    // Crear un comentario
    @Override
    @Transactional
    @CachePut(value = "mytube_comments", key = "'comment_' + #result._id")
    public CommentDTO create(CommentDTO dto) {
        CommentDomain domain = convertDtoToDomain(dto);

        // Asignar la fecha actual y no marcar como eliminado
        domain.setCommentDate(new Date());
        domain.setDeleted(false);

        CommentDomain savedDomain = commentDao.save(domain);
        return convertDomainToDto(savedDomain);
    }

    // Actualizar un comentario existente
    @Override
    @Transactional
    @CachePut(value = "mytube_comments", key = "'comment_' + #dto._id")
    public CommentDTO update(CommentDTO dto) {
        CommentDomain domain = commentDao.findByIdAndDeletedFalse(dto.get_id())
                .orElseThrow(() -> new ResourceNotFoundException("Comentario con id " + dto.get_id() + " no encontrado"));

        CommentDomain updatedDomain = convertDtoToDomain(dto);

        // Mantener usuario, video y comentario padre originales
        updatedDomain.setUser(domain.getUser());
        updatedDomain.setVideo(domain.getVideo());
        updatedDomain.setParentComment(domain.getParentComment());

        CommentDomain savedDomain = commentDao.save(updatedDomain);
        return convertDomainToDto(savedDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDTO getById(Integer id) {
        // Intentar obtener el comentario del caché
        Cache cache = cacheManager.getCache("mytube_comments");
        CommentDTO cachedComment = cache != null ? cache.get("comment_" + id, CommentDTO.class) : null;

        if (cachedComment != null) {
            // Si el comentario está en caché, devolverlo directamente
            return cachedComment;
        }

        // Si no está en caché, obtenerlo de la base de datos
        CommentDomain domain = commentDao.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario con id " + id + " no encontrado"));

        CommentDTO commentDTO = convertDomainToDto(domain);

        // Almacenar el resultado en el caché
        if (cache != null) {
            cache.put("comment_" + id, commentDTO);
        }

        return commentDTO;
    }


    // Obtener todos los comentarios con paginación y cachear cada uno individualmente
    @Override
    @Transactional(readOnly = true)
    public CommentResult getAll(Pageable pageable) {
        Page<CommentDomain> page = commentDao.findAllByDeletedFalse(pageable);
        CommentResult result = new CommentResult();

        // Convertir la lista de CommentDomain a CommentDTO
        List<CommentDTO> commentList = convertDomainListToDtoList(page.getContent());

        // Cachear cada comentario individualmente
        for (CommentDTO comment : commentList) {
            cacheManager.getCache("mytube_comments").put("comment_" + comment.get_id(), comment);
        }

        // Establecer la lista de comentarios en el resultado
        result.setComments(commentList);

        return result;
    }

    // Obtener todos los comentarios hijos de un comentario padre
    @Transactional(readOnly = true)
    @Override
    public List<CommentDTO> getChildrenComments(Integer parentId) {
        // Buscar los comentarios hijos del comentario padre
        List<CommentDomain> childComments = commentDao.findByParentCommentIdAndDeletedFalse(parentId);

        // Convertir la lista de CommentDomain a CommentDTO
        List<CommentDTO> childCommentDTOs = convertDomainListToDtoList(childComments);

        // Cachear cada comentario hijo individualmente
        for (CommentDTO comment : childCommentDTOs) {
            cacheManager.getCache("mytube_comments").put("comment_" + comment.get_id(), comment);
        }

        return childCommentDTOs;
    }

    // Eliminar un comentario y evict del caché junto con sus hijos
    @Override
    @Transactional
    @CacheEvict(value = "mytube_comments", key = "'comment_' + #id")
    public void delete(Integer id) {
        // Buscar el comentario padre
        CommentDomain parentComment = commentDao.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario con id " + id + " no encontrado"));

        // Obtener todos los comentarios hijos
        List<CommentDomain> childComments = commentDao.findByParentCommentId(id);

        // Marcar todos los hijos como eliminados
        for (CommentDomain child : childComments) {
            child.setDeleted(true);
            commentDao.save(child);  // Guardar el estado de eliminado en la base de datos

            // Evict del caché de cada hijo
            cacheManager.getCache("mytube_comments").evict("comment_" + child.getId());
        }

        // Eliminar el comentario padre de la base de datos
        parentComment.setDeleted(true);
        commentDao.save(parentComment);
    }
}
