package ru.rosbank.javaschool.crudapi.service;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.rosbank.javaschool.crudapi.dto.PostResponseDto;
import ru.rosbank.javaschool.crudapi.dto.PostSaveRequestDto;
import ru.rosbank.javaschool.crudapi.entity.PostEntity;
import ru.rosbank.javaschool.crudapi.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    private PostRepository repository;
    private PostService service;

    @BeforeEach
    void setUp() {
        repository = mock(PostRepository.class);
        service = new PostService(repository);
    }


    @Test
    void getPosts() {
        val post = new PostEntity(1, "something", null, false, 0);
        List<PostEntity> list = new ArrayList<>();
        list.add(post);
        when(repository.findAll()).thenReturn(list);
        val dto = new PostResponseDto(1, "something", null, 0);
        List<PostResponseDto> listDto = new ArrayList<>();
        listDto.add(dto);
        val result = service.getPosts(0, 1);
        assertEquals(result, listDto);


    }

    @Test
    void getFirstId() {
        val post = new PostEntity(1, "something", null, false, 0);
        List<PostEntity> list = new ArrayList<>();
        list.add(post);
        when(repository.findAll()).thenReturn(list);
        val result = service.getFirstId();
        assertEquals(1, result);
    }

    @Test
    void getNewPostsNumber() {
        val post = new PostEntity(0, "something", null, false, 0);
        val post1 = new PostEntity(1, "something", null, false, 0);
        val post2 = new PostEntity(2, "something", null, false, 0);
        List<PostEntity> list = new ArrayList<>();
        list.add(post);
        list.add(post1);
        list.add(post2);
        when(repository.findById(2)).thenReturn(Optional.of(post));
        when(repository.findAll()).thenReturn(list);
        val result = service.getNewPostsNumber(2);
        assertEquals(2, result);
    }

    @Test
    void save() {
        val dto = new PostSaveRequestDto(0, "something", "");
        val post = PostEntity.from(dto);
        when(repository.save(post)).thenReturn(post);
        val expected = PostResponseDto.from(post);
        val result = service.save(dto);
        assertEquals(result, expected);
    }

    @Test
    void searchByContent() {
        val post = new PostEntity(1, "something", null, false, 0);
        List<PostEntity> list = new ArrayList<>();
        list.add(post);
        when(repository.findAllByContentLike("something")).thenReturn(list);

        val dto = new PostResponseDto(1, "something", null, 0);
        List<PostResponseDto> listDto = new ArrayList<>();
        listDto.add(dto);
        List<PostResponseDto> result = service.searchByContent("something");
        assertIterableEquals(result, listDto);
    }

    @Test
    void likeById() {
        val post = new PostEntity(1, "something", null, false, 0);
        when(repository.findById(1)).thenReturn(Optional.of(post));

        val expected = new PostResponseDto(1, "something", null, 1);
        val result = service.likeById(1);
        assertEquals(result, expected);
    }

    @Test
    void dislikeById() {
        val post = new PostEntity(1, "something", null, false, 1);
        when(repository.findById(1)).thenReturn(Optional.of(post));
        val expected = new PostResponseDto(1, "something", null, 0);
        val result = service.dislikeById(1);
        assertEquals(result, expected);
    }
}