package com.example.resume.cv.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "resumes")
@Getter @Setter
@NoArgsConstructor
public class ResumeDocument {
    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "korean")
    private String keyword;
}
