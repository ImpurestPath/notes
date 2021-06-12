package ru.rvr.notes.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.rvr.notes.entity.Note;
import ru.rvr.notes.entity.Tag;
import ru.rvr.notes.repository.TagRepository;

import java.util.List;

@RestController
@RequestMapping("tags")
@Api(value = "Controller to wort with tags")
public class TagController {
    private final TagRepository tagRepository;

    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * Find all tags
     *
     * @return list of notes
     */
    @GetMapping(value = "", produces = "application/json")
    @ApiOperation("Get all tags")
    public List<Tag> getAll() {
        return tagRepository.getAll();
    }
}
