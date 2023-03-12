package com.jasmine.filemanager.data;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends CrudRepository<File, Long> {


    List<File> findAll();
    Optional<File> findFirstByOrderByIdDesc();

}
