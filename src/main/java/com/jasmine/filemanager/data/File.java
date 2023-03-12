package com.jasmine.filemanager.data;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "file")
public class File {

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private Long id;

    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "content")
    private byte[] content;
}
