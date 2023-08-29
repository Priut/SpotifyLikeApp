package com.mongodb_example.main.view.DTOs;

import org.springframework.hateoas.Link;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.Links;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicDTO {

    private Integer id;
    private String name;
    private Map<String,Link> _links;

}