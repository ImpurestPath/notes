package ru.rvr.notes.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Table(name = "tag")
public class Tag implements hasId<Long>{
    @Id
    @GeneratedValue
    @ApiModelProperty("Identifier of the tag. Must not be changed.")
    private Long id;

    @Column(unique = true)
    @ApiModelProperty("Name of the tag. Unique value.")
    @JsonProperty(required = true)
    @NotNull(message = "name cannot be null")
    private String name;
}
