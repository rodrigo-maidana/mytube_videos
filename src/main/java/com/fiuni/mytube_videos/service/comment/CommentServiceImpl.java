package com.fiuni.mytube_videos.service.comment;

import com.fiuni.mytube.domain.comment.CommentDomain;
import com.fiuni.mytube.dto.comment.CommentDTO;
import com.fiuni.mytube.dto.comment.CommentResult;
import com.fiuni.mytube_videos.dao.comment.ICommentDao;
import com.fiuni.mytube_videos.service.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl extends BaseServiceImpl<CommentDTO, CommentDomain, CommentResult> implements ICommentService {

    @Autowired
    private ICommentDao commentDao;

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

        // TODO: Asignar UserDomain y VideoDomain cuando los servicios estén disponibles
        // domain.setUser(user);
        // domain.setVideo(video);

        // Asignar ParentComment si está presente
        // domain.setParentComment(parentComment);

        domain.setContent(dto.getContent());
        domain.setCommentDate(dto.getCommentDate());
        domain.setDeleted(false); // Asignar como no eliminado por defecto
        return domain;
    }

    @Override
    public CommentDTO save(CommentDTO dto) {
        CommentDomain domain = convertDtoToDomain(dto);
        CommentDomain savedDomain = commentDao.save(domain);
        return convertDomainToDto(savedDomain);
    }

    @Override
    public CommentDTO getById(Integer id) {
        CommentDomain domain = commentDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + id));
        return convertDomainToDto(domain);
    }

    @Override
    public CommentResult getAll() {
        List<CommentDomain> domains = commentDao.findAll();
        CommentResult result = new CommentResult();
        result.setComments(convertDomainListToDtoList(domains));
        return result;
    }

    @Override
    public void delete(Integer id) {

    }
}
