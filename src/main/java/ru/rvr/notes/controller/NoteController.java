package ru.rvr.notes.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import ru.rvr.notes.entity.Note;
import ru.rvr.notes.entity.Tag;
import ru.rvr.notes.repository.NoteRepository;
import ru.rvr.notes.repository.TagRepository;

import javax.naming.directory.InvalidAttributesException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("notes")
@Slf4j
@Api(value = "Controller to work with notes")
public class NoteController {

    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;

    public NoteController(NoteRepository noteRepository, TagRepository tagRepository) {
        this.noteRepository = noteRepository;
        this.tagRepository = tagRepository;
    }

    /**
     * Find all notes
     *
     * @return list of notes
     */
    @GetMapping(value = "", produces = "application/json")
    @ApiOperation("Get all notes")
    public List<Note> getAll() {
        log.debug("Sending all notes");
        return noteRepository.getAll();
    }

    /**
     * Filter notes by tag
     * @param id id of the tag
     * @return list of notes
     * @throws InvalidAttributesException when tag isn't exist
     */
    @GetMapping(value = "/tag/{id}", produces = "application/json")
    @ApiOperation("Get all notes with specified tag id")
    public List<Note> getByTag(@PathVariable @NotNull Long id) throws InvalidAttributesException {
        Tag byId = tagRepository.getById(id);
        if (byId == null) {
            throw new InvalidAttributesException("No tag with this id");
        }
        log.debug("Sending notes filtered by tag");
        return noteRepository.getByTag(byId);
    }

    /**
     *
     * Filter notes that start after specified datetime
     * @param since start LocalDateTime to filter
     * @return list of notes
     */
    @GetMapping(value = "/since/{since}", produces = "application/json")
    @ApiOperation("Get all notes with specified tag id")
    public List<Note> getSinceDateTime(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull LocalDateTime since) {
        log.debug("Sending notes filtered by date");
        return noteRepository.getSinceDateTime(since);
    }

    @GetMapping(value = "/search/{query}", produces = "application/json")
    @ApiOperation("Get all notes with specified tag id")
    public List<Note> getSinceDateTime(@PathVariable @NotBlank @NotNull String query) {
        log.debug("Sending notes filtered by search query");
        return noteRepository.getBySearch(query);
    }


    /**
     * Saves new tag. Generates new id and sets createdAt in any case.
     *
     * @param note parsed from json Note
     * @return saved note
     * @throws InvalidAttributesException error during tags saving
     */
    @PutMapping(value = "", consumes = "application/json", produces = "application/json")
    @ApiOperation("Add new note")
    public Note addNote(@RequestBody Note note) throws InvalidAttributesException {
        log.debug("Adding note");
        note = new Note(
                note.getName(),
                note.getContent(),
                LocalDateTime.now(),
                tagRepository.getPersistedTagsFromList(note.getTags())
        );
        Note savedNote = noteRepository.save(note);
        log.info(String.format("Added note with id %d", savedNote.getId()));
        return savedNote;
    }

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    @ApiOperation("Update note with assigned id. Requires id.")
    public Note updateNote(@RequestBody Note note) throws InvalidAttributesException {
        if (note.getId() == null) {
            throw new InvalidAttributesException("Id not set. Cannot find needed note");
        }
        Note byId = noteRepository.getById(note.getId());
        if (byId == null) {
            // TODO: change to 404
            throw new InvalidAttributesException("No note with this id");
        }
        log.debug(String.format("Updating note with id %d", note.getId()));
        Note noteToUpdate = new Note(note, byId.getCreatedAt(), tagRepository.getPersistedTagsFromList(note.getTags()));
        return noteRepository.save(noteToUpdate);
    }


    @ApiOperation("Remove note with assigned id. Requires id.")
    @DeleteMapping(value = "",consumes = "application/json")
    public ResponseEntity removeNote(@RequestBody Note note) throws InvalidAttributesException {
        if (note.getId() == null) {
            throw new InvalidAttributesException("Id not set. Cannot find needed note");
        }
        Note byId = noteRepository.getById(note.getId());
        if (byId == null) {
            // TODO: change to 404
            throw new InvalidAttributesException("No note with this id");
        }
        log.debug(String.format("Updating note with id %d", note.getId()));
        noteRepository.remove(byId);
        return new ResponseEntity(HttpStatus.OK);
    }


    /**
     * Handler that allows to get original exception from transaction exception and send needed status
     */
    @ExceptionHandler({TransactionSystemException.class})
    protected ResponseEntity<Object> handlePersistenceException(final Exception ex, final WebRequest request) {
        log.info(ex.getClass().getName());
        Throwable cause = ((TransactionSystemException) ex).getRootCause();
        if (cause instanceof ConstraintViolationException) {
            ConstraintViolationException consEx = (ConstraintViolationException) cause;
            final List<String> errors = new ArrayList<>();
            for (final ConstraintViolation<?> violation : consEx.getConstraintViolations()) {
                errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            return new ResponseEntity<>(errors, new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ex, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({InvalidAttributesException.class})
    protected ResponseEntity<Object> handleAttributeException(final Exception ex, final WebRequest request) {
        log.info(ex.getClass().getName());
        return new ResponseEntity<>(ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
