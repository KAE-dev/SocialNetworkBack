package ru.rosbank.javaschool.crudapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rosbank.javaschool.crudapi.dto.PostResponseDto;
import ru.rosbank.javaschool.crudapi.dto.PostSaveRequestDto;
import ru.rosbank.javaschool.crudapi.entity.PostEntity;
import ru.rosbank.javaschool.crudapi.exception.BadRequestException;
import ru.rosbank.javaschool.crudapi.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository repository;


    public int getFirstId() {
        List<PostEntity> toSort = new ArrayList<>(repository.findAll());
        toSort.sort((o1, o2) -> -(o1.getId() - o2.getId()));
        List<PostResponseDto> collect = new ArrayList<>();
        long limit = 1;
        for (PostEntity postEntity : toSort) {
            if (limit-- == 0) {
                break;
            }
            PostResponseDto from = PostResponseDto.from(postEntity);
            collect.add(from);
        }
        return collect.get(0).getId();
    }

    public List<PostResponseDto> getPosts(int lastPost, int i) {
        List<PostEntity> toSort = new ArrayList<>(repository.findAll());
        toSort.sort((o1, o2) -> -(o1.getId() - o2.getId()));
        List<PostResponseDto> list = new ArrayList<>();
        long limit = i;
        long toSkip = lastPost;
        for (PostEntity postEntity : toSort) {
            if (toSkip > 0) {
                toSkip--;
                continue;
            }
            if (limit-- == 0) {
                break;
            }
            PostResponseDto from = PostResponseDto.from(postEntity);
            list.add(from);
        }
        return list;
    }

    public int getNewPostsNumber(int firstPostId) {
        Optional<PostEntity> firstPost = repository.findById(firstPostId);
        List<PostEntity> toSort = new ArrayList<>(repository.findAll());
        toSort.sort((o1, o2) -> -(o1.getId() - o2.getId()));
        List<Optional<PostEntity>> collect = new ArrayList<>();
        for (PostEntity postEntity : toSort) {
            Optional<PostEntity> value = Optional.of(postEntity);
            collect.add(value);
        }
        return collect.indexOf(firstPost);
    }


    public PostResponseDto save(PostSaveRequestDto dto) {
        return PostResponseDto.from(repository.save(PostEntity.from(dto)));
    }

    public void removeById(int id) {
        repository.deleteById(id);
    }

    public List<PostResponseDto> searchByContent(String q) {
        return repository.findAllByContentLike(q).stream()
                .map(PostResponseDto::from)
                .collect(Collectors.toList());
    }

    public PostResponseDto likeById(int id) {
        final PostEntity entity = repository.findById(id)
                .orElseThrow(BadRequestException::new);
        entity.setLikes(entity.getLikes() + 1);
        return PostResponseDto.from(entity);
    }

    public PostResponseDto dislikeById(int id) {
        final PostEntity entity = repository.findById(id)
                .orElseThrow(BadRequestException::new);
        entity.setLikes(entity.getLikes() - 1);
        return PostResponseDto.from(entity);
    }
}
