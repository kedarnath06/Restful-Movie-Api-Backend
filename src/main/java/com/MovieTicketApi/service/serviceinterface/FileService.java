package com.MovieTicketApi.service.serviceinterface;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface FileService {

    String uploadFile(String path, MultipartFile file) throws IOException;//used for adding poster file

    InputStream getResouceFile(String path, String fileName) throws FileNotFoundException;//This 2 methods is important to deal with the file


}
