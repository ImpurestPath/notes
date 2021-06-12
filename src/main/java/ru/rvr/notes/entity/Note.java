package ru.rvr.notes.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.Immutable;

import javax.naming.directory.InvalidAttributesException;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "note")
@NoArgsConstructor
public class Note implements hasId<Long> {
    @Id
    @GeneratedValue
    @ApiModelProperty("Identifier of the note. Must not be changed.")
    private Long id;

    @ApiModelProperty("Name of the note. Used for search.")
    private String name;

    @JsonProperty(required = true)
    @ApiModelProperty("Content of the note. Used for search.")
    @NotNull(message = "Content cannot be null")
    private String content;

    @Setter
    @ApiModelProperty("The note creating time. Generated on server side.")
    @NotNull(message = "Created time cannot be null")
    private LocalDateTime createdAt;

    // Not using cascade because of own implementation of setting tags on creating in controller
    // But it's not deleting old tags for now
    @ManyToMany
    private List<Tag> tags = new ArrayList<>();


    /**
     * Constructor for creating note with tags validation
     */
    public Note(String name,
                @NotNull(message = "Content cannot be null") String content,
                @NotNull(message = "Created time cannot be null") LocalDateTime createdAt,
                List<Tag> tags) throws InvalidAttributesException {
        this.name = name;
        this.content = content;
        this.createdAt = createdAt;
        setTags(tags);
    }

    /**
     * Constructor for updating note
     */
    public Note(Note oldNote, LocalDateTime oldCreatedAt, List<Tag> newTags){
        this.id = oldNote.getId();
        this.name = oldNote.getName();
        this.content = oldNote.getContent();
        this.createdAt = oldCreatedAt;
        this.tags = newTags;
    }

    /**
     * Constructor only for jackson use
     */
    @JsonCreator
    protected Note(@JsonProperty("id") Long id,
                   @JsonProperty("name") String name,
                   @JsonProperty("content") @NotNull(message = "Content cannot be null") String content,
                   @JsonProperty("createdAt") @NotNull(message = "Created time cannot be null") LocalDateTime createdAt,
                   @JsonProperty("tags") List<Tag> tags) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.createdAt = createdAt;
        this.tags = tags;
    }


    /**
     * Replaces tags of note.
     * Checks persistence of tag by not null id.
     * If tag is not persisted throws {@link InvalidAttributesException}
     *
     * @param tags list of persisted tags
     */
    public void setTags(List<Tag> tags) throws InvalidAttributesException {
        for (Tag tag : tags) {
            if (tag.getId() == null)
                throw new InvalidAttributesException("Tag is not persisted");
        }
        this.tags = tags;
    }
}
